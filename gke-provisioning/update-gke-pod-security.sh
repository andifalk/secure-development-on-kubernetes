#!/bin/sh

# update kubernetes cluster to enable pod security policy

gcloud beta container clusters update "demo-gke" --zone "europe-west3-a" --enable-pod-security-policy

