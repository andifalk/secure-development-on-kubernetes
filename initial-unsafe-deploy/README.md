# Initial Unsafe K8s Deployment

This is the initial demo application that will be used for showing
all security patterns when deploying and running this in Kubernetes.

This application provides two REST APIs:

* Greetings API
  * GET [http://localhost:8080](http://localhost:8080): Shows greeting with configured default values
  * GET [http://localhost:8080?message=test](http://localhost:8080?message=test): Shows greeting with custom message
  * GET [http://localhost:8080/admin](http://localhost:8080/admin): Shows the administrative section (only accessible by admin user)
* Actuator API
  * Exposes all available [actuator endpoints](http://localhost:8080/actuator) of Spring Boot (including sensitive ones)
  
All APIs are secured by requiring either basic authentication or form based login.

Login credentials are: 

* Standard user: _user_ / _secret_
* Admin user: _admin_ / _secret_  
  
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

## Sample command client requests

### Httpie

This should give a 401:

```bash
http localhost:8080
```

This should return the default greeting:

```bash
http --auth user:secret localhost:8080
```

This should return a custom greeting:

```bash
http --auth user:secret localhost:8080 "message==Test"
```

### Curl

This should return the default greeting:

```bash
curl --user user:secret http://localhost:8080
```

This should return a custom greeting:

```bash
curl --user user:secret http://localhost:8080\?message\=Test
```
