#!/bin/sh

kubectl apply -f ./deploy-pod-security-policy-serviceaccount.yaml
kubectl apply -f ./no-root-policy-role.yaml
kubectl apply -f ./deploy-pod-security-policy-rolebinding.yaml
