#!/bin/bash

# Exit on error
set -e

echo "Deploying Grafana Loki for Kubernetes logging..."

# Create namespace if it doesn't exist
echo "Creating logging namespace if it doesn't exist..."
kubectl create namespace logging --dry-run=client -o yaml | kubectl apply -f -

# Add Grafana Helm repository
echo "Adding Grafana Helm repository..."
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update

# Deploy Loki stack using Helm
echo "Deploying Loki stack..."
helm upgrade --install loki grafana/loki-stack \
  --namespace logging \
  --values k8s-logging/loki-values.yaml \
  --wait

# Set up port forwarding for Grafana
echo "Setting up port forwarding for Grafana on port 3000..."
kubectl port-forward --namespace logging service/loki-grafana 3000:80 &
GRAFANA_PID=$!
echo "Grafana port forwarding started with PID: $GRAFANA_PID"

# Set up port forwarding for Loki
echo "Setting up port forwarding for Loki on port 3100..."
kubectl port-forward --namespace logging service/loki-gateway 3100:80 &
LOKI_PID=$!
echo "Loki port forwarding started with PID: $LOKI_PID"

echo "Logging setup complete!"
echo "Grafana UI: http://localhost:3000 (default credentials: admin/admin)"
echo "Loki API: http://localhost:3100"
echo ""
echo "To stop port forwarding, run: kill $GRAFANA_PID $LOKI_PID"
echo "To save these PIDs for later, you can run:"
echo "echo \"$GRAFANA_PID $LOKI_PID\" > k8s-logging/port-forward.pid"
