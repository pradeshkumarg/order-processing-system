#!/bin/bash

echo "Stopping all services in Kubernetes using Helm..."

# Check if Helm is installed
if ! command -v helm &> /dev/null; then
    echo "Error: Helm is not installed. Please install Helm first."
    exit 1
fi

# Check if the namespace exists
if kubectl get namespace order-processing-system &> /dev/null; then
    # Check if the release exists
    if helm status order-processing -n order-processing-system &> /dev/null; then
        # Uninstall the Helm release
        echo "Uninstalling the Order Processing System Helm chart..."
        helm uninstall order-processing -n order-processing-system

        echo "Checking for remaining resources..."
    else
        echo "The Order Processing System Helm release is not found, but the namespace exists."
        echo "Cleaning up any resources in the namespace..."
    fi

    # Check if any pods are still running
    echo "Checking if any pods are still running..."
    kubectl get pods -n order-processing-system

    # Delete the namespace
    echo "Deleting the namespace..."
    kubectl delete namespace order-processing-system --ignore-not-found

    echo "All services have been stopped!"
else
    echo "The Order Processing System namespace does not exist. Nothing to stop."
fi
