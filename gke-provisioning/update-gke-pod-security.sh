#!/bin/sh

# please replace "pa-afa-kubernetes" with your own project on GCP

gcloud auth login
gcloud config set project pa-afa-kubernetes

# update kubernetes cluster to enable pod security policy
gcloud beta container clusters update "demo-gke" --enable-pod-security-policy

