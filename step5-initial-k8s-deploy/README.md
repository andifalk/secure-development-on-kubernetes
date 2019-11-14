# Initial Unsafe K8s Deployment

This deploys the demo application to Kubernetes using a standard kubernetes yaml file
running the container using root user.

For details on the demo application see [hello spring boot application](../step1-hello-spring-boot).
  
## Deploy the application

The corresponding container image is pulled 
from [andifalk/hello-root](https://cloud.docker.com/repository/registry-1.docker.io/andifalk/hello-root) docker hub repository.

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
        livenessProbe:
          httpGet:
            path: /
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
      restartPolicy: Always
```

Just deploy it by typing ```kubectl apply -f ./deploy.yaml``` in directory _k8s_.

Please note that the container is running as root by default and kubernetes
also does not prohibit this by default!

Now you can prove that this container does run with root by using a tool like [kubeaudit](https://github.com/Shopify/kubeaudit).

```bash
kubeaudit nonroot -n default
```

This should result in an output similar to this:
```
INFO[0000] Not running inside cluster, using local config 
ERRO[0000] RunAsNonRoot is not set in ContainerSecurityContext, which results in root user being allowed!  Container=hello-root...
ERRO[0000] RunAsNonRoot is not set in ContainerSecurityContext, which results in root user being allowed!  Container=hello-root...
```
