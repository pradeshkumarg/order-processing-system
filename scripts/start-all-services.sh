#!/bin/bash

echo "Starting all services in Kubernetes using Helm..."

# Check if Helm is installed
if ! command -v helm &> /dev/null; then
    echo "Error: Helm is not installed. Please install Helm first."
    exit 1
fi

# Delete namespace if it exists
echo "Cleaning up existing namespace if it exists..."
kubectl delete namespace order-processing-system --ignore-not-found

# Wait for namespace to be fully deleted
echo "Waiting for namespace to be fully deleted..."
while kubectl get namespace order-processing-system &> /dev/null; do
    echo "Namespace still exists, waiting..."
    sleep 2
done

# Create namespace
echo "Creating namespace..."
kubectl create namespace order-processing-system

# Install or upgrade the Helm chart
echo "Installing/upgrading the Order Processing System Helm chart..."
helm upgrade --install order-processing ./helm-charts/order-processing-system -n order-processing-system

echo "Checking service status..."

# Check pod status
echo "Checking pod status..."
kubectl get pods -n order-processing-system

echo "\nNote: Some pods may not be in the 'Ready' state yet."
echo "This is expected in a development environment where images might not be available."
echo "In a production environment, you would need to build and push the service images first."

# Check the status of the deployment
echo "Checking deployment status..."
helm status order-processing

echo "All services are being deployed to Kubernetes!"
echo "You can access services via port-forwarding:"
echo "  kubectl port-forward -n order-processing-system svc/kafka 9092:9092 29092:29092"
echo "  kubectl port-forward -n order-processing-system svc/postgres 5432:5432"
echo "  kubectl port-forward -n order-processing-system svc/kafka-ui 8080:8080"
echo "  kubectl port-forward -n order-processing-system svc/order-service 8081:8081"
echo "  kubectl port-forward -n order-processing-system svc/inventory-service 8082:8082"
echo "  kubectl port-forward -n order-processing-system svc/notification-service 8083:8083"
echo "  kubectl port-forward -n order-processing-system svc/analytics-service 8084:8084"

echo "To run the API tests:"
echo "  cd api-tests && ./run-api-tests.sh"
