#!/bin/sh

# create kubernetes cluster using encryption at rest and application level encryption

gcloud beta container --project "pa-afa-kubernetes" clusters create "demo-gke" --zone "europe-west3-a" \
--no-enable-basic-auth --cluster-version "1.24.5-gke.600" --release-channel "regular" --machine-type "e2-standard-2" \
--image-type "COS_CONTAINERD" --disk-type "pd-balanced" --disk-size "100" --metadata disable-legacy-endpoints=true \
--scopes "https://www.googleapis.com/auth/devstorage.read_only","https://www.googleapis.com/auth/logging.write","https://www.googleapis.com/auth/monitoring","https://www.googleapis.com/auth/servicecontrol","https://www.googleapis.com/auth/service.management.readonly","https://www.googleapis.com/auth/trace.append" \
--max-pods-per-node "110" --num-nodes "3" --logging=SYSTEM,WORKLOAD --monitoring=SYSTEM --enable-ip-alias \
--network "projects/pa-afa-kubernetes/global/networks/default" \
--subnetwork "projects/pa-afa-kubernetes/regions/europe-west3/subnetworks/default" --no-enable-intra-node-visibility \
--default-max-pods-per-node "110" --enable-network-policy --no-enable-master-authorized-networks \
--addons HorizontalPodAutoscaling,HttpLoadBalancing,GcePersistentDiskCsiDriver --enable-autoupgrade --enable-autorepair \
--max-surge-upgrade 1 --max-unavailable-upgrade 0 \
--database-encryption-key "projects/pa-afa-kubernetes/locations/europe-west3/keyRings/demo-gke/cryptoKeys/demo-gke" \
--enable-shielded-nodes --node-locations "europe-west3-a"
