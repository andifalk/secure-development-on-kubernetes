#!/bin/sh

kubectl apply -f ./k8s/no-root-policy.yaml
kubectl apply -f ./k8s/no-root-policy-role.yaml
