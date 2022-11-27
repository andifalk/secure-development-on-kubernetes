# Linux and Container Basics

## Prerequisites

To try out the demos of the following sections you need a Linux system, i.e., using the Ubuntu desktop or server. The easiest way is to install Linux as a separate VM (e.g. using VirtualBox, VMWare or Parallels).

In addition to that the following tools have to be installed:

* Tools for getting and setting Linux capabilities, in Ubuntu/Debian just install it using `sudo apt install libcap2-bin`

## File permissions

In Linux, everything is a file, like:

* Binary application code
* Data
* Configuration
* Logs
* Devices

Permissions on such files determine which users are allowed to access those files and what actions they
can perform on the files.

Each file and directory has three user based permission groups:

* __u - owner__ – The _Owner_ permissions apply only to the owner of the file or directory, they will not impact the actions of other users.
* __g - group__ – The _Group_ permissions apply only to the group that has been assigned to the file or directory, they will not affect the actions of other users.
* __all users__ – The _All Users_ permissions apply to all other users on the system, this is the permission group that you want to watch the most.

The Permission Types that are used are:

* __r__ – Read
* __w__ – Write
* __x__ – Execute

The permissions are displayed as: `-rwxrwxrwx 1 owner:group`.
Using `ls -l test.txt` would result in the following:

```bash
-rw-r--r-- 1 myuser mygroup test.txt
```

1. The first character is the special permission flag 
2. The following set of three characters (rwx) is for the _owner_ permissions
3. The second set of three characters (rwx) is for the _group_ permissions
4. The third set of three characters (rwx) is for the _all users_ permissions
5. Following the grouping the number displays the number of hard links to the file
6. The last piece is the _owner_ and _group_ assignment

The file owner and group can be changed using the `chown` command. By performing `chown myuser:mygroup test.txt` the owner of the file _test.txt_ would be _myuser_ and the group would be set to _mygroup_.

The file permissions are edited by using the command `chmod`. You can assign the permissions explicitly or by using a binary reference.
You may add the _read_ and _write_ permission to the group using `chmod g+rw text.txt`. To remove the same permissions for all other users you would type `chmod o-rw text.txt`.

You may also specify the complete file permissions using a binary reference instead:
The numbers are a binary representation of the rwx string.

* r (read) = 4
* w (write) = 2
* x (execute) = 1

So you could also perform `chmod 644 test.txt` instead.

### Special permissions using setuid and setgid

When executing a file, usually the process that gets started inherits your user ID.
If the file has the setuid/setgid bit set, the process will have the user/group ID of the file’s owner/group instead.

We will try that using the `sleep` command. Because we will change permissions first we will copy the binary to our own one experiment with. To check the installation path of the `sleep` file perform a `which sleep`. 
With this path perform the copy command:

```bash
cp /bin/sleep ./mysleep
```

Now let's check the file permissions for the _mysleep_ file:

```bash
ls -l mysleep
```

This should return something like this:

```bash
-rwxr-xr-x 1 afa afa 39256 Nov 27 18:15 mysleep
```

Normally, when you execute a file, the process that gets started inherits your user ID.
If you now execute it as root user in a terminal with

```bash
sudo ./mysleep 20
```

And then execute this in another terminal:

```bash
ps ajf
```

Then this will run with the root user id:

```bash
PPID     PID    PGID     SID TTY        TPGID STAT   UID   TIME COMMAND
 735453  810647  810647  735453 pts/0     810647 S+       0   0:00  \_ sudo ./mysleep 20
 810647  810648  810647  735453 pts/0     810647 S+       0   0:00      \_ ./mysleep 20
```

Now with setuid bit set:

```bash
chmod +s mysleep
```

Check again with 

```bash
ls -l mysleep
```


```bash
-rwsr-sr-x 1 afa afa 39256 Nov 27 18:15 mysleep
```

If you now execute this in one terminal:

```bash
sudo ./mysleep 20
```

And execute this in another terminal:

```bash
ps ajf
```

Then you will see that even when executing as root the command is run using the other user id:

```bash
PPID     PID    PGID     SID TTY        TPGID STAT   UID   TIME COMMAND
 735453  810637  810637  735453 pts/0     810637 S+       0   0:00  \_ sudo ./mysleep 20
 810637  810638  810637  735453 pts/0     810637 S+    1000   0:00      \_ ./mysleep 20
```

This bit is typically used to give a program privileges that it needs but are not
usually extended to regular users.
Because _setuid_ provides a dangerous pathway to privilege escalation, some container
image security scanners report on the presence of files with the _setuid_ bit set.

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

```bash
getcap /usr/bin/ping
```

This will return

```bash
/usr/bin/ping = cap_net_raw+ep
```

As you can see the `ping` command requires the `net_raw` capability to access the network socket.

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

```bash
docker container run --privileged --rm -it alpine sh -c 'apk add -U libcap; capsh --print'
```

This (among other stuff) prints out the standard capabilities set by docker

```bash
...
Bounding set =cap_chown,cap_dac_override,cap_fowner,cap_fsetid,cap_kill,cap_setgid,cap_setuid,cap_setpcap,cap_net_bind_service,cap_net_raw,cap_sys_chroot,cap_mknod,cap_audit_write,cap_setfcap
...
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
privileges hi/she was supposed to have. Then a user can take actions that shouldn’t be permitted to take. 
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
Just open a new terminal and check the hostname, then you will see that the host still has the original name.

This basically is the isolation mechanism used by linux containers (combined with linux capabilities).

## Linux CGroups

Docker uses the Linux _cgroups_ (one of the linux namespaces) to limit resource usage of containers.

To limit the container to use a maximum of 200MiB and only one half of a cpu use this command:

```shell
docker container run --cpu-shares=0.5 --memory=200m --rm --detach --name hello-root -p 8080:8080 andifalk/hello-root:latest
```

You will recognize that the spring boot application start up is much slower in this container due to less cpu power.

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
