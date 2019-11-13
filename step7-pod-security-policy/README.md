# Safe K8s Deployment

This deploys the demo application to Kubernetes using cluster-wide pod security policy
to enforce that the docker container must run unprivileged using non-root user.

For details on the demo application see [hello spring boot application](../step1-hello-spring-boot).
  
## Deploy the application

The corresponding container image is pulled 
from [andifalk/hello-rootless-jib](https://cloud.docker.com/repository/registry-1.docker.io/andifalk/hello-rootless-jib) 
docker hub repository.

The application is deployed using the following deployment yaml file _k8s/deploy.yaml_:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: hello-rootless
  name: hello-rootless
spec:
  replicas: 1
  selector:
    matchLabels:
      app: hello-rootless
  template:
    metadata:
      labels:
        app: hello-rootless
    spec:
      serviceAccountName: no-root-policy-serviceaccount
      containers:
        - image: andifalk/hello-rootless-jib:latest
          name: hello-rootless
          resources:
            limits:
              cpu: "1"
              memory: "512Mi"
            requests:
              cpu: "0.5"
              memory: "256Mi"
          readinessProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 5
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

### Add and authorize policy 

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

```bash
kubectl apply -f ./no-root-policy-serviceaccount.yaml
kubectl apply -f ./no-root-policy-role.yaml
kubectl apply -f ./no-root-policy-role-binding.yaml
```

Now we can update the kubernetes cluster to enable pod security policy admission controller:

```bash
./gke-provisioning/update-gke-pod-security.sh
```

After the update is finished (takes some minutes) we can deploy our application:

```bash
kubectl apply -f ./deploy.yaml
kubectl apply -f ./service.yaml
```

Now this should successfully be deployed as it is compliant and authorized for the new pod security policy.

You may notice that the previously deployed applications will not work any more as none of these authorize against
this new policy (even if these would be compliant).