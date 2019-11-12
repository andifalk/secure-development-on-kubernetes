#!/bin/sh

kubectl apply -f ./deploy-rootless.yaml
kubectl apply -f ./service.yaml
