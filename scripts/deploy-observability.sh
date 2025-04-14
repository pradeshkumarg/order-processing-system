#!/bin/bash

# This script deploys the Vector + ClickHouse + Grafana observability stack

# Apply the observability stack
kubectl apply -f k8s-observability/observability-stack.yaml

# Wait for ClickHouse to be ready
echo "Waiting for ClickHouse to be ready..."
kubectl wait --for=condition=ready pod -l app=clickhouse -n observability --timeout=300s

# Wait for Vector to be ready
echo "Waiting for Vector to be ready..."
kubectl wait --for=condition=ready pod -l app=vector -n observability --timeout=300s

# Wait for Grafana to be ready
echo "Waiting for Grafana to be ready..."
kubectl wait --for=condition=ready pod -l app=grafana -n observability --timeout=300s

# Set up port forwarding for Grafana
echo "Setting up port forwarding for Grafana..."
kubectl port-forward svc/grafana -n observability 3000:3000 &
GRAFANA_PID=$!

# Set up port forwarding for ClickHouse
echo "Setting up port forwarding for ClickHouse..."
kubectl port-forward svc/clickhouse -n observability 8123:8123 &
CLICKHOUSE_PID=$!

echo "Observability stack deployed successfully!"
echo "Grafana is available at http://localhost:3000 (admin/admin)"
echo "ClickHouse is available at http://localhost:8123 (default/clickhouse)"
echo ""
echo "To stop port forwarding, run:"
echo "kill $GRAFANA_PID $CLICKHOUSE_PID"
