![Java CI](https://github.com/andifalk/secure-development-on-kubernetes/workflows/Java%20CI/badge.svg)

# Secure Development on Kubernetes

This repository contains presentation slides and the complete live demo code for the talk _Secure Development on Kubernetes_.

## Table of Contents

* [Presentation Slides](#presentation)
* [Requirements and Setup](#setup)
  * [Local Kubernetes](#local-kubernetes)
  * [Managed Kubernetes](#managed-kubernetes-cluster)
  * [Trivy for Image Scanning](#trivy-for-image-scan)
  * [KubeAudit for K8s Security Audits](kubeaudit-for-kubernetes-security-audits)
* [Demos](#demos)
  * [Iteration 1: Application Security](#iteration-1-application-security)  
  * [Iteration 2: Container Security](#iteration-2-container-security)  
  * [Iteration 3: Kubernetes Security](#iteration-3-kubernetes-security)

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

#### Minikube

To start Minikube just type:

```shell
minikube start
```

You can stop Minikube again using:

```shell
minikube stop
```

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

* Microsoft Azure with [AKS](https://azure.microsoft.com/en-us/services/kubernetes-service)
* Amazon AWS with [EKS](https://aws.amazon.com/eks)
* Google Cloud with [GKE](https://cloud.google.com/kubernetes-engine)

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

### Kubeaudit for Kubernetes Security Audits

As part of the demos we will also check our Kubernetes for security issues like container running
with root rights using an open source tool named [Kubeaudit](https://github.com/Shopify/kubeaudit).

For installation instructions just browse to the [Kubeaudit](https://github.com/Shopify/kubeaudit) website.

## Demos

### Iteration 1: Application Security

* [Hello Spring Boot](step1-hello-spring-boot)

### Iteration 2: Container Security

* [Root Container](step2-hello-root)
* [Rootless Container](step3-hello-rootless)
* [Rootless Container with JIB](step4-hello-rootless-jib)

### Iteration 3: Kubernetes Security

* [Initial Unsafe Kubernetes Deployment](step5-initial-k8s-deploy)
* [Safe Kubernetes Deployment (Pod Security Context)](step6-pod-security-context)
* [Safe Kubernetes Deployment (Pod Security Policy)](step7-pod-security-policy)
