# Secure Development on Kubernetes

This repository contains presentation slides and the complete live demo code 
for the talk _Secure Development on Kubernetes_.

## Presentation

[Presentation Slides (PDF)](https://andifalk.github.io/kubernetes-container-aber-sicher)

## Demos

* [Initial Spring Boot Application](step1-initial-spring-boot-app)
* [Unsafe Kubernetes Deployment](step2-initial-unsafe-deploy)
* [Safe Kubernetes Deployment (Pod Security Context)](step3-deploy-security-context)
* [Safe Kubernetes Deployment (Pod Security Policy)](step4-deploy-pod-security-policy)

## Provisioning

The [gke-provisioning](gke-provisioning) directory contains
scripts to create a kubernetes cluster on google cloud.
There is also a script to update the cluster to enable pod security policy.

To use the scripts you must have google cloud cli installed and be logged in
to GCP.

```bash
gcloud auth login
gcloud config set project [project]
```

