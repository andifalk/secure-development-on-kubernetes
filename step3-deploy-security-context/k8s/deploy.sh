#!/bin/sh

kubectl apply -f ./deploy-security-context.yaml
kubectl apply -f ./service-security-context.yaml
