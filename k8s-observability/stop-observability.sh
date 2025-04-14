#!/bin/bash

echo "Shutting down observability stack..."

# Delete Grafana resources
echo "Deleting Grafana resources..."
kubectl delete deployment grafana -n observability
kubectl delete service grafana -n observability
kubectl delete configmap grafana-datasources -n observability
kubectl delete configmap grafana-dashboard-provisioning -n observability
kubectl delete configmap grafana-dashboards -n observability

# Delete Vector resources
echo "Deleting Vector resources..."
kubectl delete daemonset vector -n observability
kubectl delete configmap vector-config -n observability
kubectl delete serviceaccount vector -n observability
kubectl delete clusterrole vector -n observability
kubectl delete clusterrolebinding vector -n observability

# Delete ClickHouse resources
echo "Deleting ClickHouse resources..."
kubectl delete deployment clickhouse -n observability
kubectl delete service clickhouse -n observability
kubectl delete configmap clickhouse-config -n observability

# Optionally delete the namespace (uncomment if needed)
# echo "Deleting observability namespace..."
# kubectl delete namespace observability

echo "Observability stack shutdown complete!"
