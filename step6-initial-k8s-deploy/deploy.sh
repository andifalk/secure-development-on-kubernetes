#!/bin/sh

kubectl apply -f ./k8s/deploy-root.yaml
kubectl apply -f ./k8s/service.yaml
