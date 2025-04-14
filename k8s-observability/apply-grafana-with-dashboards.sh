#!/bin/bash

# Apply the Grafana configuration with dashboards
kubectl apply -f k8s-observability/grafana-with-dashboards.yaml

# Wait for Grafana to start
echo "Waiting for Grafana to start..."
kubectl rollout status deployment/grafana -n observability

# Set up port forwarding to access Grafana
echo "Setting up port forwarding to access Grafana..."
echo "You can access Grafana at http://localhost:3000"
echo "Username: admin"
echo "Password: admin"
kubectl port-forward svc/grafana -n observability 3000:3000
