# Kubernetes Deployment Guide

This guide explains how to deploy the Order Processing System to a Kubernetes cluster.

## Prerequisites

- Kubernetes cluster (local like Minikube or cloud-based)
- kubectl configured to connect to your cluster
- Docker installed
- Container registry access (Docker Hub, ACR, GCR, etc.)

## Deployment Steps

### 1. Build and Push Docker Images

Update the `build-and-push-images.sh` script with your container registry details, then run:

```bash
./build-and-push-images.sh
```

### 2. Update Image References (if needed)

If you're not using a local registry, update the image references in the Kubernetes manifests:

```bash
# Example for Docker Hub
find kubernetes -name "*.yaml" -exec sed -i 's|image: \(.*\):latest|image: your-username/\1:latest|g' {} \;
```

### 3. Deploy to Kubernetes

Run the deployment script:

```bash
./deploy-to-kubernetes.sh
```

### 4. Verify Deployment

Check that all pods are running:

```bash
kubectl get pods -n order-processing-system
```

### 5. Access the Application

If you're using Minikube, you can access the services using:

```bash
# Get the URL for the order service
minikube service order-service -n order-processing-system --url

# Get the URL for the inventory service
minikube service inventory-service -n order-processing-system --url

# Get the URL for the analytics service
minikube service analytics-service -n order-processing-system --url

# Get the URL for the Kafka UI
minikube service kafka-ui -n order-processing-system --url
```

If you're using an Ingress controller, add the following to your hosts file:

```
127.0.0.1 order-processing.local
```

Then access the application at:
- http://order-processing.local/orders
- http://order-processing.local/inventory
- http://order-processing.local/analytics
- http://order-processing.local/kafka-ui

## Scaling the Application

To scale a service, use:

```bash
kubectl scale deployment order-service -n order-processing-system --replicas=3
```

## Monitoring and Logs

View logs for a service:

```bash
kubectl logs -f deployment/order-service -n order-processing-system
```

## Cleanup

To remove all resources:

```bash
kubectl delete namespace order-processing-system
```
