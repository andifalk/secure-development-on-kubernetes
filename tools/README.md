# Helpful Tools for K8s Security

## Static Code Analysis

### Kube-Score

[Kube-Score](https://github.com/zegl/kube-score) is a tool that performs static code analysis of your Kubernetes object definitions (i.e., your YAML files).
You can install it from [Kube-Score](https://github.com/zegl/kube-score).

Now you can just verify e.g., a deployment definition like this:

```shell
kube-score score ./deploy.yaml
```

### Checkov

[Chekov](https://github.com/bridgecrewio/checkov) is an open source static code analysis tool for infrastructure-as-code. It scans cloud infrastructure provisioned using Terraform, Terraform plan, Cloudformation, Kubernetes, Dockerfile and more.

### KICS

[KICS](https://kics.io/) is an open source solution for static code analysis of Infrastructure as Code. KICS finds security vulnerabilities, compliance issues, and infrastructure misconfigurations in Infrastructure as Code solutions like Terraform, Kubernetes, Docker, Ansible or Helm.

## Image Scanning

## Docker Scan

Starting with [Docker Desktop](https://docs.docker.com/desktop/) version 2.3.6.0 or [Docker Engine](https://docs.docker.com/engine/) on Linux version 20.10.6 Docker has [image scan capabilities](https://docs.docker.com/engine/scan/) built-in using functionality provided by Snyk.

## Trivy

As part of the demos we will also scan our container images for OS and Application vulnerabilities
using an open source tool named [Trivy](https://github.com/aquasecurity/trivy).

For installation instructions just browse to the [Trivy](https://github.com/aquasecurity/trivy) website.

Trivy is very easy to use locally and inside your CI/CD system. 

### Harbour Registry

[Harbour Registry](https://goharbor.io) is an open source registry that secures artifacts with policies and role-based access control, __ensures images are scanned and free from vulnerabilities__, and signs images as trusted. For image scanning you may integrate with one of the [supported scanners](https://goharbor.io/docs/2.2.0/install-config/harbor-compatibility-list/#scanner-adapters) like [Trivy](https://github.com/aquasecurity/trivy), [Clair](https://github.com/quay/clair) or [Anchore](https://anchore.com/).

### Snyk

or a commercial tool like [Snyk](https://snyk.io).

## Kubeaudit for Kubernetes Security Audits

As part of the demos we will also check our Kubernetes for security issues like container running
with root rights using an open source tool named [Kubeaudit](https://github.com/Shopify/kubeaudit).

For installation instructions just browse to the [Kubeaudit](https://github.com/Shopify/kubeaudit) website.

## Popeye – A Kubernetes Cluster Sanitizer

Popeye is a utility that scans live Kubernetes cluster and reports potential issues with deployed resources and configurations.
Just head to the [Popeye website](https://github.com/derailed/popeye) to install it.

With that you just _Popeye_ a cluster using your current kubeconfig environment by typing:

```shell
popeye
```

## Who-Can for Auditing RBAC

[Kubernetes' Role Based Access (RBAC)](https://kubernetes.io/docs/reference/access-authn-authz/rbac/) is not easy. A recommended helpful tool for auditing RBAC configuration is [AqueSecurity Who-Can](https://github.com/aquasecurity/kubectl-who-can).  
Just follow the instructions on the [Who-Can website](https://github.com/aquasecurity/kubectl-who-can) to install this.

After installing, you may for example just check who can create pods:

```shell
kubectl who-can create pods
```

## Look up role bindings with RBAC lookup

[RBAC Lookup](https://github.com/FairwindsOps/rbac-lookup) is a CLI that allows you to easily find Kubernetes roles and cluster roles bound to any user, service account, or group name.

With RBAC lookup you can just query for example the role bindings of the _default_ service account:

```shell
kubectl rbac-lookup default -k serviceaccount -o wide
```
