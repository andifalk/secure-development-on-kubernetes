![License](https://img.shields.io/badge/License-Apache%20License%202.0-brightgreen.svg)
![Java CI](https://github.com/andifalk/secure-development-on-kubernetes/workflows/Java%20CI/badge.svg)
[![Release](https://img.shields.io/github/release/andifalk/secure-development-on-kubernetes.svg?style=flat)](https://github.com/andifalk/secure-development-on-kubernetes/releases)

# Secure Development on Kubernetes

This repository contains all the associated code labs for the deep dive session on _Secure Development on Kubernetes_.

## Table of Contents

* [Requirements and Setup](#requirements-and-setup)
* [Helpful tools for K8s Security](#helpful-tools-for-k8s-security)  
* [Labs](#labs)
  * [Iteration 1: Application Security](#iteration-1-application-security)  
  * [Iteration 2: Container Security](#iteration-2-container-security)  
  * [Iteration 3: Kubernetes Security](#iteration-3-kubernetes-security)
  
## Requirements and Setup

Please check the [Requirements and Setup](setup/README.md) section first before looking into the [Labs](#labs).

## Helpful Tools for K8s Security

For helpful tools see [here](tools/README.md).

## Labs

Please follow the [corresponding tutorial](https://andifalk.gitbook.io/secure-kubernetes-development/) for the labs.

### Introduction

* [Linux & Container Basics](step0-linux-container-basics)

#### Iteration 1: Application Security

* [Hello Spring Boot](step1-hello-spring-boot)

#### Iteration 2: Container Security

* [Root Container](step2-hello-root)
* [Rootless Container](step3-hello-rootless)
* [Rootless Container with JIB](step4-hello-rootless-jib)

#### Iteration 3: Kubernetes Security

* [Initial Unsafe Kubernetes Deployment](step5-initial-k8s-deploy)
* [Safe Kubernetes Deployment (Pod Security Context)](step6-pod-security-context)
* [Safe Kubernetes Deployment (Pod Security Policy, _deprecated_)](step7-pod-security-policy)
* [Safe Kubernetes Deployment (Open Policy Agent)](step8-open-policy-agent)

## Feedback

Any feedback on this hands-on workshop is highly appreciated.

Please either send an email to _andreas.falk(at)novatec-gmbh.de_ or contact me via Twitter (_@andifalk_).

## License

Apache 2.0 licensed

[1]:http://www.apache.org/licenses/LICENSE-2.0.txt