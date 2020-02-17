# Safe K8s Deployment using Pod Security Policy

This deploys the demo application to Kubernetes using cluster-wide pod security policy
to enforce that the docker container must run unprivileged using non-root user.

For details on the demo application see [hello spring boot application](../step1-hello-spring-boot).

## RBAC

Before diving into the pod security policy let's check how the authorization using RBAC (Role Based Access Control) works.

### Authentication

Kubernetes has the notion of users and service account to access resources.
A user is associated with a key and certificate to authenticate API requests.
The most common technique to authenticate requests is through X.509 certificates.

### Authorization

Once an API request is authenticated, the next step is to determine whether the operation is allowed or not.

For authorizing a request, Kubernetes looks at three aspects:
1.the username of the requester
2.the requested action
3.and the object affected by the action

The username is extracted from the token embedded in the header, the action is 
one of the HTTP verbs like _GET_, _POST_, _PUT_, _DELETE_ mapped to CRUD operations, and the object is one of the valid 
Kubernetes objects such as a _pod_ or a _service_.

The requester can be of type:

* user
* group
* serviceaccount

### Service accounts

While X.509 certificates are used for authenticating external requests, service accounts are meant to authenticate processes running within the cluster.
Service accounts are associated with pods that make internal calls to the API server.

Every Kubernetes installation has a service account called __default__ that is automatically associated with every running pod.

By issuing the following command you can check which role bindings and effective roles are bound to
the _default_ service account:

```shell
kubectl get rolebindings,clusterrolebindings --all-namespaces  \
  -o custom-columns='KIND:kind,NAMESPACE:metadata.namespace,NAME:metadata.name,SERVICE_ACCOUNTS:subjects[?(@.kind=="ServiceAccount")].name' | grep default
```  

You can list all service accounts using this:

```shell
kubectl get serviceAccounts
```

Such service account is pointing to a secret that is mounted inside every pod.
This secret contains the token expected by the API Server:

```shell
kubectl get secret
```
  
To see the authorization in action we will spin up a special container with a shell:

```shell
kubectl run -i --tty --rm curl-tns --image=radial/busyboxplus:curl --generator=run-pod/v1
```  

Now let us explore the _serviceaccount_ folder with the mounted secret as token
and then set some environment variables for

* CA_CERT: Pointing to the certificate for authentication
* TOKEN: Pointing to the token for the authorization
* NAMESPACE: The target kubernetes namespace

```shell
ls /var/run/secrets/kubernetes.io/serviceaccount

CA_CERT=/var/run/secrets/kubernetes.io/serviceaccount/ca.crt
TOKEN=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)
NAMESPACE=$(cat /var/run/secrets/kubernetes.io/serviceaccount/namespace)
```  

Now we can try to reach the API server of kubernetes with a request:

```shell
curl --cacert $CA_CERT -H "Authorization: Bearer $TOKEN" "https://kubernetes/api/v1/namespaces/$NAMESPACE/services/"
```  

This should return a similar response to this:

```json
{
  "kind": "Status",
  "apiVersion": "v1",
  "metadata": {
  },
  "status": "Failure",
  "message": "services is forbidden: User \"system:serviceaccount:default:default\" cannot list resource \"services\" in API group \"\" in the namespace \"default\"",
  "reason": "Forbidden",
  "details": {
    "kind": "services"
  },
  "code": 403
}
```

Kubernetes follows the convention of the _least privilege_ principle 
which means that by default no user or service account has any permissions.

This is why we get the 403 status and are not authorized for this request.

Now exit this container and let's fix this issue.

To authorize the _default_ service account to list all the services via the Kubernetes API server
usually you would create a special role just to view services and bind this role to the service account.

To make it easier for now we just bind our service account to the predefined _view_ cluster role.

```shell
kubectl create rolebinding default-view \
  --clusterrole=view \
  --serviceaccount=default:default \
  --namespace=default
```

You may look into the created role binding:

```shell
kubectl get rolebindings -oyaml
```

Now restart the same container again as before an retry the same steps inside the container as before.

```shell
kubectl run -i --tty --rm curl-tns --image=radial/busyboxplus:curl --generator=run-pod/v1
```  

Then set again the required environment variables:

```shell
CA_CERT=/var/run/secrets/kubernetes.io/serviceaccount/ca.crt
TOKEN=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)
NAMESPACE=$(cat /var/run/secrets/kubernetes.io/serviceaccount/namespace)
```  

Now again try to reach the API server of kubernetes with a request:

```shell
curl --cacert $CA_CERT -H "Authorization: Bearer $TOKEN" "https://kubernetes/api/v1/namespaces/$NAMESPACE/services/"
```  

This time the request should succeed and should return a similar response like this:

```json
{
  "kind": "ServiceList",
  "apiVersion": "v1",
  "metadata": {
    "selfLink": "/api/v1/namespaces/default/services/",
    "resourceVersion": "18755"
  },
  "items": [
  ]
}
```

__Note:__ Parts of this paragraph have been taken from a tutorial series at [TheNewStack](https://thenewstack.io/kubernetes-access-control-exploring-service-accounts).

## Deploy the application

The corresponding container image is pulled from [andifalk/hello-rootless-jib](https://cloud.docker.com/repository/registry-1.docker.io/andifalk/hello-rootless-jib) docker hub repository.

The application is deployed using the following deployment yaml file _k8s/deploy.yaml_:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: hello-rootless-with-policy
  name: hello-rootless-with-policy
spec:
  replicas: 1
  selector:
    matchLabels:
      app: hello-rootless-with-policy
  template:
    metadata:
      labels:
        app: hello-rootless-with-policy
    spec:
      serviceAccountName: no-root-policy-serviceaccount
      automountServiceAccountToken: false
      containers:
        - image: andifalk/hello-rootless-jib:latest
          name: hello-rootless-with-policy
          resources:
            limits:
              cpu: "1"
              memory: "512Mi"
            requests:
              cpu: "0.5"
              memory: "256Mi"
          readinessProbe:
            httpGet:
              path: /
              port: 8080
            initialDelaySeconds: 5
            periodSeconds: 5
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 5  
          volumeMounts:
            - name: tmp-volume
              mountPath: /tmp
      restartPolicy: Always
      volumes:
        - name: tmp-volume
          emptyDir: {}
```

Please note that the all security context settings have been removed here as the same will be enforced by a 
cluster-wide pod security policy instead later.

## Deploy the application using Pod Security Policy

### Add a Security policy

Before enabling the Pod Security Policy admission controller there has to be
at least one Pod Security Policy in place that our Pod must authorize against.

Otherwise if we would enable the admission controller beforehand the Pod would
be declined to run completely.

We will add a new policy prohibiting privileged containers with root access completely:

```yaml
apiVersion: policy/v1beta1
kind: PodSecurityPolicy
metadata:
  name: no-root-policy
  annotations:
    seccomp.security.alpha.kubernetes.io/allowedProfileNames: '*'
spec:
  readOnlyRootFilesystem: true
  privileged: false
  allowPrivilegeEscalation: false
  requiredDropCapabilities:
    - ALL
  volumes:
    - '*'
  hostNetwork: true
  hostPorts:
    - min: 0
      max: 65535
  hostIPC: true
  hostPID: true
  runAsUser:
    rule: 'MustRunAsNonRoot'
  seLinux:
    rule: 'RunAsAny'
  supplementalGroups:
    rule: 'RunAsAny'
  fsGroup:
    rule: 'RunAsAny'
```

To add this policy just execute:

```bash
kubectl apply -f ./no-root-policy.yaml
```

Then we have to create a corresponding role for the policy.

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: no-root-policy-role
  namespace: default
rules:
  - apiGroups: ['policy']
    resources: ['podsecuritypolicies']
    verbs:     ['use']
    resourceNames:
      - no-root-policy
```

After this we have to create a new service account (which we will use in our deployment later).

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: no-root-policy-serviceaccount
  namespace: default
```

Now we can finally create a role binding between the policy role and the service account:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: no-root-policy-role-binding
  namespace: default
roleRef:
  kind: Role
  name: no-root-policy-role
  apiGroup: rbac.authorization.k8s.io
subjects:
  - kind: ServiceAccount
    name: no-root-policy-serviceaccount
    namespace: default
```

Now this has to be applied to K8s:

```shell
kubectl apply -f ./no-root-policy-serviceaccount.yaml
kubectl apply -f ./no-root-policy-role.yaml
kubectl apply -f ./no-root-policy-role-binding.yaml
```

Now we can update the kubernetes cluster to enable pod security policy admission controller:

```shell
./gke-provisioning/update-gke-pod-security.sh
```

After the update is finished (takes some minutes) we can deploy our application:

```shell
kubectl apply -f ./deploy.yaml
kubectl apply -f ./service.yaml
```

Now this should successfully be deployed as it is compliant and authorized for the new pod security policy.

## Deploy the application without Pod Security Policy

If you deploy the application via _deploy-without-policy.yaml_ you will notice that this application deployment denied due to the pod security policy.
