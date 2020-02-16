#!/bin/sh

helm repo add stable https://kubernetes-charts.storage.googleapis.com
helm repo update
helm install prom-operator stable/prometheus-operator

kubectl --namespace default get pods -l "release=prom-operator"
kubectl port-forward $(kubectl get pods --selector=app=grafana --output=jsonpath="{.items..metadata.name}") 3000

