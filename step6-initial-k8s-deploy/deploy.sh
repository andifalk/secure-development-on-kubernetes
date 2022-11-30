#!/bin/sh

kubectl apply -f ./k8s/deploy.yaml
kubectl apply -f ./k8s/service.yaml
