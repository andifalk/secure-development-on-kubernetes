# RBAC

Let's check how the authorization using RBAC (Role Based Access Control) works here.

## Authentication

Kubernetes has the notion of users and service account to access resources.
A user is associated with a key and certificate to authenticate API requests.
The most common technique to authenticate requests is through X.509 certificates.

## Authorization with RBAC

Once an API request is authenticated, the next step is to determine whether the operation is allowed or not.

For authorizing a request, Kubernetes Role Based Access Control (RBAC) looks at three aspects:
1.the username of the requester
2.the requested action
3.the object affected by the action

The username is extracted from the token embedded in the header, the action is
one of the HTTP verbs like _GET_, _POST_, _PUT_, _DELETE_ mapped to CRUD operations, and the object is one of the valid
Kubernetes objects such as a _pod_ or a _service_.

The requester can be of type:

* user
* group
* service-account

You can evaluate what you are allowed to perform in Kubernetes using:

```shell
kubectl auth can-i create pods
```

This command is limited to ask from a users' prospective.
If you want to ask the other way around you may use _kubectl-who_can_:

```shell
kubectl-who_can create pods
```

or evaluate role bindings for a subject using _rbac-lookup_:

```shell
kubectl rbac-lookup default -k serviceaccount -o wide
```

## Service accounts

While you use X.509 certificates for authenticating external requests, service accounts are meant to authenticate processes running within the cluster.
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