# Initial Unsafe K8s Deployment

This deploys the demo application to Kubernetes using unsafe defaults.

For details on the demo application see [initial demo application](../step1-initial-spring-boot-app).
  
## Deploy the application

The corresponding container image is pulled 
from [andifalk/initial-unsafe-deploy](https://cloud.docker.com/repository/registry-1.docker.io/andifalk/initial-unsafe-deploy) docker hub repository.

The application is deployed using a minimal (and quite unsafe) deployment yaml file _k8s/deploy-initial.yaml_:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: initial-unsafe-deploy
  name: initial-unsafe-deploy
spec:
  replicas: 1
  selector:
    matchLabels:
      app: initial-unsafe-deploy
  template:
    metadata:
      labels:
        app: initial-unsafe-deploy
    spec:
      containers:
        - image: andifalk/initial-unsafe-deploy:latest
          name: initial-unsafe-deploy
          imagePullPolicy: Always
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 5
            periodSeconds: 5    
      restartPolicy: Always
```

Please note that the container runs with complete root rights as root user!

You can prove this by using these commands:

```bash
docker container run --rm --detach --name unsafe-deploy \
--publish 8080:8080 andifalk/initial-unsafe-deploy:latest
docker exec unsafe-deploy whoami
```

This should return the following user information (it really is root)

```bash
root
```

You should also be able to reach the dockerized application 
via http://localhost:8080.

Finally stop the running container by using the following command:

```bash
docker stop unsafe-deploy
```

## Deploy the application using unsafe defaults

Now to deploy our application use these commands:

```bash
kubectl apply -f ./deploy-unsafe.yaml
kubectl apply -f ./service-unsafe.yaml
```

Now this should successfully be deployed (even if the container is running as root which is bad).
