# Safe K8s Deployment

This deploys the demo application to Kubernetes using pod security context
to enforce that the docker container must run unprivileged using non-root user.

For details on the demo application see [initial demo application](../initial-spring-boot-app/README.md).
  
## Deploy the application

The corresponding container image is pulled 
from [andifalk/deploy-security-context](https://cloud.docker.com/repository/registry-1.docker.io/andifalk/deploy-security-context) docker hub repository.

The application is deployed using the following deployment yaml file _k8s/deploy-security-context.yaml_:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: deploy-security-context
  name: deploy-security-context
spec:
  replicas: 1
  selector:
    matchLabels:
      app: deploy-security-context
  template:
    metadata:
      labels:
        app: deploy-security-context
    spec:
      securityContext:
        runAsNonRoot: true
      containers:
        - image: andifalk/deploy-security-context:latest
          name: deploy-security-context
          imagePullPolicy: Always
          resources:
            limits:
              cpu: "1"
              memory: "768Mi"
            requests:
              cpu: "0.5"
          securityContext:
            allowPrivilegeEscalation: false
            privileged: false
            runAsNonRoot: true
            capabilities:
              drop:
                - ALL
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 5
            periodSeconds: 5    
      restartPolicy: Always
```

Please note that the container is not allowed to run as root any more!

There is also a specification for resource limits 
(the container is only allowed to access the given cpu and memory).

To achieve the best results for resource limiting you have to use Java 11. Earlier
versions of Java (9, 10) provide special command line arguments to enable the same functionality.  
Java 8, 7 or earlier do have issues regarding resource limits !

Now you can prove that this container does NOT run with root by using these commands:

```bash
docker container run --rm --detach --name deploy-sec-ctx \
--publish 8080:8080 andifalk/deploy-security-context:latest
docker exec deploy-sec-ctx whoami
```

This should return the following user information (it really is NO root any more)

```bash
appuser
```

You should also be able to reach the dockerized application 
via http://localhost:8080.

Finally stop the running container by using the following command:

```bash
docker stop deploy-sec-ctx
```

## Deploy the application using Pod Security Context

Now to deploy our application use these commands:

```bash
kubectl apply -f ./deploy-security-context.yaml
kubectl apply -f ./service-security-context.yaml
```

Now this should successfully be deployed as the container is non-root and therefore is compliant to the pod security context.
