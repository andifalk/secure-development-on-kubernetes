#!/bin/sh

kubectl apply -f ./k8s/deploy-privileged.yaml --namespace=restricted
