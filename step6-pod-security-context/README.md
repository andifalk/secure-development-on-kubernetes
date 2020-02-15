# Safe K8s Deployment using Pod Security Context

This deploys the demo application to Kubernetes using pod security context
to enforce that the docker container must run unprivileged using non-root user.

For details on the demo application see [hello spring boot application](../step1-hello-spring-boot).
  
## Deploy the application

The corresponding container image is pulled 
from [andifalk/hello-rootless-jib](https://cloud.docker.com/repository/registry-1.docker.io/andifalk/hello-rootless-jib) docker hub repository.

The application is deployed using the following deployment yaml file _k8s/deploy-rootless.yaml_:

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
      securityContext:
        runAsNonRoot: true
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
        securityContext:
          readOnlyRootFilesystem: true
          allowPrivilegeEscalation: false
          privileged: false
          runAsNonRoot: true
          capabilities:
            drop:
              - ALL
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

Please note that the container is not allowed to run as root any more!

There is also a specification for resource limits 
(the container is only allowed to access the given cpu and memory).

For java this only works with container aware JDK versions like OpenJDK 8u192 or above.
To achieve the best results for resource limiting you have to use Java 11. 
With using older java versions the java vm inside the container will just grab the whole memory and
cpu resources of the host system and will probably be just killed by Kubernetes. 

Please note that the container is running using a non-root user now and kubernetes
also does enforce a non-root user now!

## Deploy the application using Pod Security Context

Now to deploy our application use these commands:

```shell
kubectl apply -f ./deploy-rootless.yaml
kubectl apply -f ./service.yaml
```

This deploys the rootless image build using the openjdk base image.

If you want to use the JIB image using the distroless image instead then use these commands:

```shell
kubectl apply -f ./deploy-rootless-jib.yaml
kubectl apply -f ./service.yaml
```

Now this should successfully be deployed as the container is non-root and therefore is compliant to the pod security context.

Now you can prove that this container does not run with root by using [kubeaudit](https://github.com/Shopify/kubeaudit) again.

```bash
kubeaudit nonroot -n default
```

This time only the info line should appear in the result:
```
INFO[0000] Not running inside cluster, using local config 
```

You can also check for a lot of other security relevant things by using:

```bash
kubeaudit all -n default
```

You may also check that the user of the running container is not root using (check your pod name before):

```shell
kubectl exec hello-rootless-59f59fb9b8-878rk -it -- whoami
```

__Note:__ If you have deployed the JIB container image then the base image is a _distroless_ image meaning that
no shell and no _whoami_ command is inside the container. Therefore you cannot use the command above.

## Next

[Next: K8s Pod Security Policy](../step7-pod-security-policy)
