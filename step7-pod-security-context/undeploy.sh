#!/bin/sh

kubectl delete svc hello-security-ctx
kubectl delete deployment hello-security-ctx
kubectl delete deployment hello-security-ctx-deny
