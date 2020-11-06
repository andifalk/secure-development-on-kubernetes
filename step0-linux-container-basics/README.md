# Linux and Container Basics

## setuid and setgid

When executing a file, usually the process that gets started inherits your user ID.
If the file has the setuid bit set, the process will have the user ID of the file’s owner instead.

```shell
cp /bin/sleep ./mysleep
ls -l mysleep
sudo ./mysleep 100
ps ajf
```

Now with setuid bit set:

```shell
chmod +s mysleep
ls -l mysleep
sudo ./mysleep 100
```

This bit is typically used to give a program privileges that it needs but that are not
usually extended to regular users.
Because setuid provides a dangerous pathway to privilege escalation, some container
image security scanners report on the presence of files with the setuid bit set.

## Linux capabilities

Back in the old days the only way in Linux has been to either execute a process in privileged (_root_) or unprivileged mode (all other users).
 
With linux capabilities you can now break down privileges used by executing processes/threads to just grant the least
privileges required to successfully run a thread.

Just look up the detailed docs for linux capabilities by

```shell
man capabilities
```

| Capability          | Description                                                                     |
|---------------------|---------------------------------------------------------------------------------|
|CAP_CHOWN            | Make arbitrary changes to file UIDs and GIDs                                    |
|CAP_NET_ADMIN        | Perform network operations like modify routing tables                           |
|CAP_NET_BIND_SERVICE | Bind a socket to Internet domain privileged ports (port numbers less than 1024) |
|CAP_NET_RAW          | use RAW and PACKET sockets        |
|CAP_SETUID           | Make arbitrary manipulations of process UIDs |
|CAP_SYS_ADMIN        | Perform system admin operations like _mount_, _swapon_, _sethostname_ or perform privileged _syslog_ |
|CAP_SYS_BOOT         | Use reboot   |
|CAP_SYS_CHROOT       | Use chroot   |
|CAP_SYS_TIME         | Set system clock |
|CAP_SYSLOG           | Perform privileged syslog operations  |

If you want to query capabilities for a process use this command

```shell
getpcaps <pid>
```

You should use this for root processes, processes for non-root users usually do not have 
any capabilities set.

You may query capabilities for a file:

```shell
getcap ping
```

You may also set capabilities for a file:

```shell
sudo setcap 'cap_net_raw+p' ./myping
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

In privileged mode you can for example list and change partition tables or directly mount file systems:

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

## Privilege Escalation

_Privilege escalation_ happens when a user is extending his/her privileges beyond the 
privileges hi/she was supposed to have and to take actions that the user shouldn’t be permitted to take. 
To escalate privileges, attackers takes advantage of a system vulnerability or misconfiguration.

Usually, the attacker starts as a non-privileged user and wants to gain root privileges on the 
machine. A common method of escalating privileges is to look for software that’s already running 
as root and then take advantage of known vulnerabilities in this software.

Even when running a container as a non-root user, there is potential for privilege escalation 
based on the Linux permissions mechanisms in cases such as

* Container images including with a setuid binary
* Additional capabilities granted to a container running as a non-root user

You can also prohibit such privilege escalation in docker by adding
_--security-opt="no-new-privileges:true"_ to your _docker run_ command.

See the [docker run security section](https://docs.docker.com/engine/reference/run/#security-configuration) for more details. 

## Linux Namespaces

CGroups (see next section) control the resources that a process can use, namespaces control what a process can see.

Linux currently provides the following namespaces:

* Unix Timesharing System (UTS): This namespace is responsible for the hostname and domain names.
* Process IDs
* Mount points
* Network
* User and group IDs
* Inter-process communications (IPC)
* Control groups (cgroups)

You can easily see all namespaces on your machine using the _lsns_ command.
Try also to run this command using root, then you will see more details.

By using the tool _unshare_ you may run a process with some namespaces unshared from the parent 
(i.e. simulating a linux container).

So let's try to use isolating the hostname:

```shell
sudo unshare --uts sh
hostname
hostname isolatedhost
hostname
``` 

Now in the new shell we have our own hostname isolated by the _UTS_ namespace.
If you open a new terminal you will see that the host still has the original hostname.

This basically is the isolation mechanism used by linux containers (combined with linux capabilities).

## Linux CGroups

Docker uses the Linux cgroups (one of the linux namespaces) to limit resource usage of containers.

To limit the container to use a maximum of 200MiB and only one half of a cpu use this command:

```shell
docker container run --cpu-shares=0.5 --memory=200m --rm --detach --name hello-root -p 8080:8080 andifalk/hello-root:latest
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

## Looking inside containers

Just run the following container:

```shell
docker run --name myapp --rm andifalk/hello-root:latest
```

There are different possibilities to look inside a running container.

### Docker exec

By using the _docker exec_ command you can just open a shell inside the container (if bash is installed in the container):

```shell
docker exec -it myapp bash
```

### Nsenter

Another option is the _nsenter_ tool that basically is intended 
to run a program with namespaces of other processes.

First we need to check for the pid of the container process using the container identifier using _docker inspect_.
Then we can use _nsenter_ to open a shell inside the container.

```shell
docker inspect --format "{{ .State.Pid }}" myapp
nsenter --target 17274 --mount --uts --ipc --net --pid
```

## Next

[Next: Application Security](../step1-hello-spring-boot)
