#!/bin/bash

echo "Starting all services in Kubernetes..."

# Create namespace if it doesn't exist
kubectl get namespace order-processing-system > /dev/null 2>&1 || kubectl apply -f kubernetes/namespace.yaml

# Apply infrastructure configurations
echo "Starting infrastructure services (Zookeeper, Kafka, PostgreSQL)..."
kubectl apply -f kubernetes/infrastructure/zookeeper.yaml
kubectl apply -f kubernetes/infrastructure/kafka.yaml
kubectl apply -f kubernetes/infrastructure/postgres.yaml

echo "Waiting for infrastructure services to start..."
sleep 10

# Apply application configurations
echo "Starting application services (Order, Inventory, Notification, Analytics)..."
kubectl apply -f kubernetes/services/order-service.yaml
kubectl apply -f kubernetes/services/inventory-service.yaml
kubectl apply -f kubernetes/services/notification-service.yaml
kubectl apply -f kubernetes/services/analytics-service.yaml

echo "Waiting for application services to start..."
sleep 10

echo "All services are running in Kubernetes!"
echo "You can access services via port-forwarding:"
echo "  kubectl port-forward -n order-processing-system svc/kafka 9092:9092 29092:29092"
echo "  kubectl port-forward -n order-processing-system svc/postgres 5432:5432"
echo "  kubectl port-forward -n order-processing-system svc/kafka-ui 8080:8080"
echo "  kubectl port-forward -n order-processing-system svc/order-service 8081:8081"
echo "  kubectl port-forward -n order-processing-system svc/inventory-service 8082:8082"
echo "  kubectl port-forward -n order-processing-system svc/notification-service 8083:8083"
echo "  kubectl port-forward -n order-processing-system svc/analytics-service 8084:8084"
