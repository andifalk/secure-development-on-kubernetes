# Non-Root Docker Container using Google JIB

This demo again builds an improved docker image from the demo application.
For details on the demo application see [hello spring boot application](../step1-hello-spring-boot).

But this time instead of using a _Dockerfile_ we will use [Google JIB](https://github.com/GoogleContainerTools/jib) to build the container image.
With JIB you even can build a container image without a docker daemon installed on your machine. Additionally
building images repeatedly is much faster as JIB optimizes this to the typical development flow so that only the application code changes
and no dependencies.

This time we configure a non-root user in the _gradle.build_ file to build a container image that will run using
without the root user.

```groovy
plugins {
    ...
    id "com.google.cloud.tools.jib" version "2.0.0"
}
...
jib {
    to {
        image = 'andifalk/hello-rootless-jib:latest'
    }
    container {
        user = 1002
    }
}
...
```
  
You can prove this by using these commands:

```bash
docker container run --rm --detach --name hello-rootless-jib \
-p 8080:8080 andifalk/hello-rootless-jib:latest
docker exec hello-rootless-jib whoami
```

This time this should report an error as in the [distroless image](https://github.com/GoogleContainerTools/distroless) 
, as used by JIB as default, there is no shell and no _whoami_ command installed.

You should also be able to reach the dockerized application 
via http://localhost:8080.

Finally stop the running container by using the following command:

```bash
docker stop hello-rootless-jib
```

## Check image for Vulnerabilities

Now we can check our image for vulnerabilities with high and critical severities 
using this command:

```bash
trivy --clear-cache --severity HIGH,CRITICAL andifalk/hello-rootless-jib:latest
```

## Next

[Next: Initial Unsafe K8s Deploy](../step5-initial-k8s-deploy)
