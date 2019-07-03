#!/bin/sh

# please replace "pa-afa-kubernetes" with your own project on GCP

gcloud auth login
gcloud config set project pa-afa-kubernetes
gcloud container clusters get-credentials "demo-gke"
