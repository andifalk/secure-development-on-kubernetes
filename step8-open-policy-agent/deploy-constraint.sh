#!/bin/sh

kubectl apply -f ./k8s/gatekeeper-template.yaml
kubectl apply -f ./k8s/gatekeeper-constraint.yaml

