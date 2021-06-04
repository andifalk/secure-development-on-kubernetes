# Non-Root Docker Container

This demo builds an improved docker image from the demo application just using a standard _Dockerfile_.
For details on the demo application see [hello spring boot application](../step1-hello-spring-boot).

This time we configure a non-root user in the _Dockerfile_ to build a container image that will run using
without the root user.

```dockerfile
FROM openjdk:11.0.9-jre-slim-buster
COPY step3-hello-rootless-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
RUN addgroup --system --gid 1002 app && adduser --system --uid 1002 --gid 1002 appuser
USER 1002
ENTRYPOINT java -jar /app.jar
```

Regarding the group-id (gid) and user-id (uid) you should use one above '1000' to avoid using any system user.
If you want to be really on the safe side you even leave out all local users (reserved numbers up to 10000) by choosing a number above '10000' (reserved for remote users).

You can prove that the container now does not run with root any more by using these commands:

```shell
docker container run --rm --detach --name hello-rootless \
-p 8080:8080 andifalk/hello-rootless:latest
docker exec hello-rootless whoami
```

This should return the following user information (it should not be root any more)

```shell
appuser
```

To prevent any privilege escalation it is also best practice restricting this by the additional command option _--security-opt=no-new-privileges_ 
for _docker run_:

```shell
docker container run --security-opt=no-new-privileges --rm --detach --name hello-rootless \
-p 8080:8080 andifalk/hello-rootless:latest
```

You should also be able to reach the dockerized application via [localhost:8080](http://localhost:8080).

Finally, stop the running container by using the following command:

```shell
docker stop hello-rootless
```

## Check image for Vulnerabilities

Now we can check our image for vulnerabilities with high and critical severities using this command:

```shell
trivy i --clear-cache
trivy i --severity HIGH,CRITICAL andifalk/hello-rootless:latest
```

## Next

[Next: Rootless JIB Container](../step4-hello-rootless-jib)
