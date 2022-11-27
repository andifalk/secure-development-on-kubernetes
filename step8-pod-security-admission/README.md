# Safe K8s Deployment using Pod Security Admission

## Introduction

[Pod Security Admission](https://kubernetes.io/docs/concepts/security/pod-security-admission/) is the predecessor of the [Pod Security Policies](https://kubernetes.io/docs/concepts/security/pod-security-policy/) and has been introduced as Alpha feature in Kubernetes version 1.22. 
Starting with Kubernetes version 1.25 [Pod Security Admission](https://kubernetes.io/docs/concepts/security/pod-security-admission/) is regarded stable and [Pod Security Policies](https://kubernetes.io/docs/concepts/security/pod-security-policy) have been removed.
Please check this [blog post on kuberneetes.io](https://kubernetes.io/blog/2021/04/06/podsecuritypolicy-deprecation-past-present-and-future/) and the alpha documentation for [pod security admission](https://kubernetes.io/docs/concepts/security/pod-security-admission/) in Kubernetes version 1.22 for details.


Pod Security Admission is a Kubernetes admission controller that lets you apply Pod Security Standards toPods running on your cluster. Pod Security Standards are predefined security policies. These policies range from being highly permissive to highly restrictive.

You can apply one of these Pod Security Standards:

* __Privileged__: An unrestricted policy that provides the widest level of permissions. Allows for known privilege escalations.
* __Baseline__: A minimally restrictive policy that allows the default, minimally specified Pod configuration. Prevents known privilege escalations.
* __Restricted__: A highly restrictive policy that follows Pod hardening best practices.

You can use the PodSecurity admission controller to apply Pod Security Standards in the following modes:

* __Enforce__: Policy violations reject Pod creation. An audit event is added to the audit log.
* __Audit__: Policy violations trigger adding an audit event to the audit log. Pod creation is allowed.
* __Warn__: Policy violations trigger a user-facing warning. Pod creation is allowed.

To use the Pod Security admission controller, you must apply specific Pod Security Standards in specific modes to specific namespaces. You can do this by using namespace labels.

## Lab

In this lab, you will do the following:

* Create two new namespaces
* Apply specific security policies to each namespace
* Test the configured policies by using different deployments

For details on the demo application see [hello spring boot application](../step1-hello-spring-boot).

### Prepare the namespaces

Create the following namespaces using this commands:

```bash
kubectl create ns baseline
kubectl create ns restricted
```

Now apply the following Pod Security Standards:

* __baseline__: Apply `baseline` standard to `baseline` namespace in the `warn` mode
* __restricted__: Apply `restricted` standard to `restricted` namespace in the `enforce` mode

```bash
kubectl label --overwrite ns baseline pod-security.kubernetes.io/warn=baseline
kubectl label --overwrite ns restricted pod-security.kubernetes.io/enforce=restricted
```

These commands achieve the following result:

* Workloads in the `baseline` namespace that violate the `baseline` policy are allowed, and the client displays a warning message.
* Workloads in the `restricted` namespace that violate the `restricted` policy are rejected, and the cluster adds a corresponding entry to the audit logs.

Verify that the labels were added:

```bash
kubectl get ns --show-labels
```

The output should be similar to the following (other existing namespaces are omitted here):

```bash
baseline-ns       Active   74s   kubernetes.io/metadata.name=baseline-ns,pod-security.kubernetes.io/warn=baseline
restricted-ns     Active   18s   kubernetes.io/metadata.name=restricted-ns,pod-security.kubernetes.io/enforce=restricted
```

### Testing the configured policies

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

Please note that the all security context settings have been removed here as the same will be enforced by the namespace-wide pod security standard instead later.


