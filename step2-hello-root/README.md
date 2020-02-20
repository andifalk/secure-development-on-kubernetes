# Default Docker Container

This demo builds a standard docker image from the demo application just using a standard _Dockerfile_.
For details on the demo application see [hello spring boot application](../step1-hello-spring-boot).

## Java Base Images

* [OpenJDK](https://hub.docker.com/_/openjdk)
* [AdoptJDK](https://hub.docker.com/_/adoptopenjdk)
* [Google Distroless](https://github.com/GoogleContainerTools/distroless)
* [Amazon Corretto](https://hub.docker.com/_/amazoncorretto)

## Standard Dockerfile

```dockerfile
FROM openjdk:11.0.6-jre-buster
COPY step2-hello-root-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT java -jar /app.jar
```

## Runs with Root by default

When using defaults for building a container image the container will run using
the __root__ user by default.

You can prove this by using these commands:

```bash
docker container run --rm --detach --name hello-root \
-p 8080:8080 andifalk/hello-root:latest
docker exec hello-root whoami
```

This should return the following user information (it really is root)

```bash
root
```

You should also be able to reach the dockerized application via [localhost:8080](http://localhost:8080).

Finally stop the running container by using the following command:

```bash
docker stop hello-root
```

## Linux capabilities

Docker runs with a balanced set of capabilities between security and usuablity of containers.
You can print the default capabilities by using this command:

```shell
docker container run --rm -it alpine sh -c 'apk add -U libcap; capsh --print'
```

If you even run the container in privileged mode (you should usually never do that)
then you get full privileged root access with all linux capabilities set:

```shell
docker container run --privileged --rm -it alpine sh -c 'apk add -U libcap; capsh --print'
```

In privileged mode you can for example list and change partition tables:

```shell
docker container run --privileged --rm -it alpine sh -c 'apk add -U libcap; capsh --print; fdisk -l'
```

Usually you even don't need the default capabilities defined by docker.
A common use case is to run a container listening on a [privileged tcp port (below 1024)](https://www.w3.org/Daemon/User/Installation/PrivilegedPorts.html), e.g. using a http server.  
For this you just need the capability _CAP_NET_BIND_SERVICE_:

```shell
docker container run --cap-drop=ALL --cap-add=net_bind_service --rm -it alpine sh -c 'apk add -U libcap; capsh --print'
```

## Check image for Vulnerabilities

Now we can check our image for vulnerabilities with critical severities using this command:

```bash
trivy --severity CRITICAL andifalk/hello-root:latest
```

## Next

[Next: Rootless Container](../step3-hello-rootless)
