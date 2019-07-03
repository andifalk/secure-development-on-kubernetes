#!/bin/sh

# please replace "pa-afa-kubernetes" with your own project on GCP

gcloud auth login
gcloud config set project pa-afa-kubernetes
gcloud beta container clusters delete "demo-gke"

