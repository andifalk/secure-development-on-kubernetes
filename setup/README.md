# Requirements and Setup

## Java JDK

You need a Java JDK version 17 or higher

## Kubernetes

In general, you should be able to run all demos on current Kubernetes cluster versions
at least supporting pod security contexts. For supporting Pod Security Admission you need at least Kubernetes version 1.23.

### Local

For local Kubernetes provisioning you may use one of the following:
* [K3s](https://k3s.io) that runs on Linux systems (without using a VM)
* [Minikube](https://minikube.sigs.k8s.io) as a cross-platform solution running on Linux, macOS, and Windows.
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) for Windows, macOS and Linux (needs Licensing for commercial use)
* [Rancher Desktop](https://rancherdesktop.io/) for Windows, macOS and Linux (free alternative for Docker Desktop)

For installation just follow the instructions on the [K3s](https://k3s.io) or [Minikube](https://minikube.sigs.k8s.io) web sites.

#### Minikube

To start Minikube just type:

```shell
minikube start
```

If you want to use local images to be deployed to minikube
then you need to point the docker registry to the one inside minikube.

```shell
eval $(minikube docker-env)
```

With a _docker ps_ command you can check if you are using the intended docker registry.

```shell
docker ps
```

You can stop Minikube again using:

```shell
minikube stop
```

For full details please consult the [minikube docs](https://minikube.sigs.k8s.io/docs/)

#### K3s

For Linux users the easiest way to provision a Kubernetes locally is done as follows:

```shell
curl -sfL https://get.k3s.io | sh -
```

After waiting for a short time (takes maybe about 30 seconds) you have a Kubernetes cluster ready to use.
Just try this to make sure it works:

```bash
sudo k3s kubectl get nodes
```

Unfortunately as of now this requires root privileges. Currently, K3s provides Rootless support only as an _experimental_ feature.

To stop the Kubernetes server just type:

```bash
k3s-killall.sh
```

To stop it and get rid of the installation just type:

```bash
k3s-uninstall.sh
```

### Managed Kubernetes Cluster

To use all features of an enterprise grade Kubernetes cluster you have to go into the cloud and use
one of the well-known providers:

* Microsoft Azure with [AKS](https://azure.microsoft.com/en-us/services/kubernetes-service)
* Amazon AWS with [EKS](https://aws.amazon.com/eks)
* Google Cloud with [GKE](https://cloud.google.com/kubernetes-engine)

#### Google GKE

The [gke-provisioning](../gke-provisioning) directory contains
scripts to create a kubernetes cluster on Google cloud GKE.
There is also a script to update the cluster to enable pod security policy.

To use the scripts you must have the Google cloud cli installed and be logged in
to GCP.

```bash
gcloud auth login
gcloud config set project [project]
```

Please update the provided scripts according to your google cloud project and the target zone
you want to use before executing these!