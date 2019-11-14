# Non-Root Docker Container

This demo builds an improved docker image from the demo application just using a standard _Dockerfile_.
For details on the demo application see [hello spring boot application](../step1-hello-spring-boot).

This time we configure a non-root user in the _Dockerfile_ to build a container image that will run using
without the root user.

```dockerfile
FROM openjdk:11-jre-slim
COPY step3-hello-rootless-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
RUN addgroup --system --gid 1002 app && adduser --system --uid 1002 --gid 1002 appuser
USER 1002
ENTRYPOINT java -jar /app.jar
```
  
You can prove this by using these commands:

```bash
docker container run --rm --detach --name hello-rootless \
-p 8080:8080 andifalk/hello-rootless:latest
docker exec hello-rootless whoami
```

This should return the following user information (it should not be root any more)

```bash
appuser
```

You should also be able to reach the dockerized application 
via http://localhost:8080.

Finally stop the running container by using the following command:

```bash
docker stop hello-rootless
```

## Check image for Vulnerabilities

Now we can check our image for vulnerabilities with high and critical severities 
using this command:

```bash
trivy --clear-cache --severity HIGH,CRITICAL andifalk/hello-rootless:latest
```
