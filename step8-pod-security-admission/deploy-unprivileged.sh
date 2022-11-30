#!/bin/sh

kubectl apply -f ./k8s/deploy-unprivileged.yaml --namespace=restricted
