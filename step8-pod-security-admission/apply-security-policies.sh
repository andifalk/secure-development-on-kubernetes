#!/bin/sh

kubectl label --overwrite ns baseline pod-security.kubernetes.io/warn=baseline
kubectl label --overwrite ns restricted pod-security.kubernetes.io/enforce=restricted
