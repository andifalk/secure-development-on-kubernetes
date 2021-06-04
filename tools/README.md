# Helpful Tools for K8s Security

## Kube-Score for Static Code Analysis

Kube-score is a tool that performs static code analysis of your Kubernetes object definitions (i.e., your YAML files).
You can install it from [Kube-Score](https://github.com/zegl/kube-score).

Now you can just verify e.g., a deployment definition like this:

```shell
kube-score score ./deploy.yaml
```

## Trivy for Image Scan

As part of the demos we will also scan our container images for OS and Application vulnerabilities
using an open source tool named [Trivy](https://github.com/aquasecurity/trivy).

For installation instructions just browse to the [Trivy](https://github.com/aquasecurity/trivy) website.

Trivy is very easy to use locally and inside your CI/CD system. If you want to have a more enterprise grade tool
you may look for the [Harbour Registry](https://goharbor.io) (including the Clair image scanner), or a commercial tool like [Snyk](https://snyk.io).

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
