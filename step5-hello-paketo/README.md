# Non-Root Docker Container using Paketo

This demo again builds a container image from the demo application.
For details on the demo application see [hello spring boot application](../step1-hello-spring-boot).

But this time instead of using a _Dockerfile_ or JIB we will just try [spring boot tooling](https://docs.spring.io/spring-boot/docs/current/reference/html/container-images.html#container-images.buildpacks) together with [paketo buildpacks](https://github.com/paketo-buildpacks/spring-boot) to build the container image.

__Important note__: Paketo buildpacks are still missing ARM64 support (Apple M1/M2 Mac hardware). See [GitHub issue: Add support for arm64](https://github.com/paketo-buildpacks/stacks/issues/51) for details.

```groovy
plugins {
    id 'com.google.cloud.tools.jib' version '2.6.0'
}

jib {
    to {
        image = 'andifalk/hello-rootless-jib:latest'
    }
    container {
        user = 1002
    }
}
```
  
You can prove this by using these commands:

```shell
docker container run --rm --detach --name hello-rootless-jib \
-p 8080:8080 andifalk/hello-rootless-jib:latest
docker exec hello-rootless-jib whoami
```

This time this should report an error as in the [distroless image](https://github.com/GoogleContainerTools/distroless), as used by JIB as default, there even is no shell installed and so no _whoami_ command is possible.

You should also be able to reach the dockerized application again via [localhost:8080](http://localhost:8080).

Finally, stop the running container by using the following command:

```shell
docker stop hello-rootless-jib
```

## Check image for Vulnerabilities

Now we can check our image for vulnerabilities with high and critical severities using this command:

```shell
trivy i --clear-cache
trivy i --severity HIGH,CRITICAL andifalk/hello-rootless-jib:latest
```

## Next

[Next: Initial Unsafe K8s Deploy](../step5-initial-k8s-deploy)
