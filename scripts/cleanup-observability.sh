#!/bin/bash

# This script cleans up the Vector + ClickHouse + Grafana observability stack

# Confirm with the user
echo "This will delete the entire observability stack including all data in ClickHouse."
read -p "Are you sure you want to continue? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
  echo "Cleanup cancelled."
  exit 0
fi

# Delete the observability stack
echo "Deleting observability stack..."
kubectl delete -f k8s-observability/observability-stack.yaml

# Delete any PVCs that might be left behind
echo "Deleting persistent volume claims..."
kubectl delete pvc -n observability --all

# Wait for namespace to be deleted
echo "Waiting for namespace to be deleted..."
kubectl wait --for=delete namespace/observability --timeout=300s

echo "Observability stack cleanup completed."
