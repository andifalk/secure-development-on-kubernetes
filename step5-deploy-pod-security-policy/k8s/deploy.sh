#!/bin/sh

kubectl apply -f ./deploy-pod-security-policy.yaml
kubectl apply -f ./service-pod-security-policy.yaml
