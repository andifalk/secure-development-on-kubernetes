# Safe K8s Deployment

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

To achieve the best results for resource limiting you have to use Java 11. Earlier
versions of Java (9, 10) provide special command line arguments to enable the same functionality.  
Java 8, 7 or earlier do have issues regarding resource limits !

Please note that the container is running using a non-root user now and kubernetes
also does enforce a non-root user now!

## Deploy the application using Pod Security Context

Now to deploy our application use these commands:

```bash
kubectl apply -f ./deploy-rootless.yaml
kubectl apply -f ./service.yaml
```

Now this should successfully be deployed as the container is non-root and therefore is compliant to the pod security context.
