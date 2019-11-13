# Secure Development on Kubernetes

This repository contains presentation slides and the complete live demo code 
for the talk _Secure Development on Kubernetes_.

## Presentation

[Presentation Slides (PDF)](https://github.com/andifalk/secure-development-on-kubernetes/raw/master/secure_kubernetes_presentation.pdf)

## Setup

In general you should be able to run all demos on current Kubernetes cluster versions
at least supporting pod security contexts.

### Local Kubernetes

For local Kubernetes provisioning you may use [K3s](https://k3s.io) that runs on Linux systems (without using a VM) 
or [Minikube](https://minikube.sigs.k8s.io) as a cross-platform solution running on Linux, macOS, and Windows.

For installation just follow the instructions on the [K3s](https://k3s.io) or [Minikube](https://minikube.sigs.k8s.io) 
web sites.

For Linux users the easiest way to provision a Kubernetes locally is done as follows:

```bash
curl -sfL https://get.k3s.io | sh -
```
After waiting for a short time (takes maybe about 30 seconds) you have a Kubernetes cluster ready to use.
Just try this to make sure it works:

```bash
sudo k3s kubectl get nodes
```

Unfortunately as of now this requires root privileges. Rootless support is 
currently only provided as _experimental_ feature. 

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
one of the well known providers:

* Microsoft Azure with AKS
* Amazon AWS with EKS
* Google Cloud with GKE

#### Google GKE

The [gke-provisioning](gke-provisioning) directory contains
scripts to create a kubernetes cluster on google cloud.
There is also a script to update the cluster to enable pod security policy.

To use the scripts you must have google cloud cli installed and be logged in
to GCP.

```bash
gcloud auth login
gcloud config set project [project]
```

### Trivy for Image Scan

As part of the demos we will also scan our container images for OS and Application vulnerabilities
using an open source tool named [Trivy](https://github.com/aquasecurity/trivy).

For installation instructions just browse to the [Trivy](https://github.com/aquasecurity/trivy) website.

## Demos

### Iteration 1: Application Security

* [Hello Spring Boot](step1-hello-spring-boot)

### Iteration 2: Container Security

* [Root Container](step2-hello-root)
* [Rootless Container](step3-hello-rootless)
* [Rootless Container with JIB](step4-hello-rootless-jib)

### Iteration 3: Kubernetes Security

* [Initial Unsafe Kubernetes Deployment](step5-initial-unsafe-deploy)
* [Safe Kubernetes Deployment (Pod Security Context)](step3-deploy-security-context)
* [Safe Kubernetes Deployment (Pod Security Policy)](step7-pod-security-policy)
