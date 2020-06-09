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
FROM openjdk:11.0.7-jre-slim-buster
COPY step2-hello-root-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT java -jar /app.jar
```

## Build the Docker Image

To build the docker image just run

```shell
./gradlew clean build docker
```

To push the docker image to a docker registry please specify your target registry
in _gradle.properties_ before performing the next command:

```shell
./gradlew clean build dockerPush
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

Finally, stop the running container by using the following command:

```bash
docker stop hello-root
```

## Linux capabilities

Back in the old days the only way in Linux has been to either execute a process in privileged (_root_) or unprivileged mode (all other users).
 
With linux capabilities you can now break down privileges used by executing processes/threads to just grant the least
privileges required to successfully run a thread.

Just look up the detailed docs for linux capabilities by

```shell
man capabilities
```

Docker runs with a balanced set of capabilities between security and usability of containers.
You can print the default capabilities set by docker by using this command:

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
A common use case is to run a container listening on a [privileged tcp port (below 1024)](https://www.w3.org/Daemon/User/Installation/PrivilegedPorts.html), 
e.g. using a http server.  
For this you just need the capability _CAP_NET_BIND_SERVICE_:

```shell
docker container run --cap-drop=ALL --cap-add=net_bind_service --rm -it alpine sh -c 'apk add -U libcap; capsh --print'
```

For more details on docker security consult the [docker security docs](https://docs.docker.com/engine/security/security).

## Linux CGroups

Docker uses the Linux cgroups to limit resource usage of containers.

To limit the container to use a maximum of 200MiB and only one half of a cpu use this command:

```shell
docker container run --cpu-shares=0.5 --memory=256MB --rm --detach --name hello-root -p 8080:8080 andifalk/hello-root:latest
```

You will recognize that the spring boot application startup is much slower in this container due to less cpu power.

You can always check the state of the app by issuing the logs:

```shell
docker logs hello-root
```

To see the actual resource consumption of the container use the docker stats command:

```shell
docker stats hello-root
```

All details on limiting resources can be found in [docker resource constraints](https://docs.docker.com/config/containers/resource_constraints).

## Check image for Vulnerabilities

Now let's check our image for vulnerabilities of critical and high severity using these commands:

```shell
 trivy i --clear-cache
 trivy i --severity=HIGH,CRITICAL andifalk/hello-root:latest
```

You only need the first command to clear the cache when using images with _latest_ tag. 

__Note__: It is a good practice to always use specific version tags instead of the _latest_ tag. For demo purposes, this just makes things easier.

## Next

[Next: Rootless Container](../step3-hello-rootless)
