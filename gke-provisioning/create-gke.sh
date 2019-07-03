#!/bin/sh

# create kubernetes cluster using encryption at rest and application level encryption
gcloud beta container --project "pa-afa-kubernetes" clusters create "demo-gke" --zone "europe-west3-a" \
--no-enable-basic-auth --cluster-version "1.12.8-gke.10" --machine-type "n1-standard-2" --image-type "COS" \
--disk-type "pd-standard" --disk-size "100" \
--scopes "https://www.googleapis.com/auth/devstorage.read_only","https://www.googleapis.com/auth/logging.write","https://www.googleapis.com/auth/monitoring","https://www.googleapis.com/auth/servicecontrol","https://www.googleapis.com/auth/service.management.readonly","https://www.googleapis.com/auth/trace.append" \
--num-nodes "3" --enable-cloud-logging --enable-cloud-monitoring --no-enable-ip-alias \
--network "projects/pa-afa-kubernetes/global/networks/default" --subnetwork "projects/pa-afa-kubernetes/regions/europe-west3/subnetworks/default" \
--addons HorizontalPodAutoscaling,HttpLoadBalancing --enable-autoupgrade --enable-autorepair \
--database-encryption-key "projects/pa-afa-kubernetes/locations/europe-west3/keyRings/demo-gke/cryptoKeys/demo-gke"


