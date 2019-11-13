#!/bin/sh

kubectl apply -f ./k8s/no-root-policy-serviceaccount.yaml
kubectl apply -f ./k8s/no-root-policy-role-binding.yaml
kubectl apply -f ./k8s/deploy.yaml
kubectl apply -f ./k8s/service.yaml
