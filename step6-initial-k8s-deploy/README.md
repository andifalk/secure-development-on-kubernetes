# Initial Unsafe K8s Deployment

This deploys the demo application to Kubernetes using a standard kubernetes yaml file
running the container using root user.

For details on the demo application see [hello spring boot application](../step1-hello-spring-boot).
  
## Deploy the application

The corresponding container image is pulled from [andifalk/hello-root](https://cloud.docker.com/repository/registry-1.docker.io/andifalk/hello-root) docker hub repository.

The application is deployed using the following deployment yaml file _k8s/deploy.yaml_:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: hello-root
  name: hello-root
spec:
  replicas: 1
  selector:
    matchLabels:
      app: hello-root
  template:
    metadata:
      labels:
        app: hello-root
    spec:
      containers:
      - image: andifalk/hello-root:latest
        name: hello-root
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
      restartPolicy: Always
```

Just deploy it by typing ```kubectl apply -f ./deploy.yaml``` in directory _k8s_.

## Static analysis of Deployment

Please note that the container is running as root by default and kubernetes
also does not prohibit this by default!

Now you can prove that this container does run with root by using a tool like [kubeaudit](https://github.com/Shopify/kubeaudit).

```shell
kubeaudit nonroot -n default
```

This should result in an output similar to this:

```shell
INFO[0000] Not running inside cluster, using local config
ERRO[0000] RunAsNonRoot is not set in ContainerSecurityContext, which results in root user being allowed!  Container=hello-root...
ERRO[0000] RunAsNonRoot is not set in ContainerSecurityContext, which results in root user being allowed!  Container=hello-root...
```

An alternative tool for this is _popeye_, just run it against your current cluster:

```shell
popeye
```

It is also possible to check directly your deployment yaml file:

```shell
kube-score score ./deploy.yaml
```

This will show an output similar to this one:

```shell
[CRITICAL] Container Security Context
        · hello-root -> Container has no configured security context
            Set securityContext to run the container in a more secure context.
[CRITICAL] Container Resources
        · hello-root -> CPU limit is not set
            Resource limits are recommended to avoid resource DDOS. Set resources.limits.cpu
        · hello-root -> Memory limit is not set
            Resource limits are recommended to avoid resource DDOS. Set resources.limits.memory
```

You may also check that the user of the running container is not root using (check your pod name before):

```shell
kubectl get pods
...
kubectl exec hello-root-59f59fb9b8-878rk -it -- whoami
```

__Note:__ If you have deployed the JIB container image then the base image is a _distroless_ image meaning that
no shell and no _whoami_ command is inside the container. Therefore, you cannot use the command above.

## Next

[Next: K8s Pod Security Context](../step7-pod-security-context)
