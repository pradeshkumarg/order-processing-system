#!/bin/bash

echo "Stopping all services in Kubernetes..."

# Delete application services first
echo "Stopping application services..."
kubectl delete -f kubernetes/services/order-service.yaml --ignore-not-found
kubectl delete -f kubernetes/services/inventory-service.yaml --ignore-not-found
kubectl delete -f kubernetes/services/notification-service.yaml --ignore-not-found
kubectl delete -f kubernetes/services/analytics-service.yaml --ignore-not-found

# Delete infrastructure resources
echo "Stopping infrastructure services..."
kubectl delete -f kubernetes/infrastructure/postgres.yaml --ignore-not-found
kubectl delete -f kubernetes/infrastructure/kafka.yaml --ignore-not-found
kubectl delete -f kubernetes/infrastructure/zookeeper.yaml --ignore-not-found

echo "All services stopped!"
