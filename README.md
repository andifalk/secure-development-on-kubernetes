# Kubernetes und Container - Aber Sicher!

Slides und Demos zum Talk "Kubernetes und Container - Aber Sicher!"

## Presentation

[Presentation Slides (Online)](https://andifalk.github.io/kubernetes-container-aber-sicher)

## Demos

* [Initial Spring Boot Application](initial-spring-boot-app/README.md)
* [Unsafe Kubernetes Deployment](initial-unsafe-deploy/README.md)
* [Safe Kubernetes Deployment (Pod Security Context)](deploy-security-context/README.md)
* [Safe Kubernetes Deployment (Pod Security Policy)](deploy-pod-security-policy/README.md)

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

