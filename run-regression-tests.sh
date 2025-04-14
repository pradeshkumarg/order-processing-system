#!/bin/bash

set -e

echo "Running regression tests..."

# Function to wait for namespace deletion with timeout
wait_for_namespace_deletion() {
  local namespace=$1
  local timeout=$2
  local start_time=$(date +%s)
  local end_time=$((start_time + timeout))

  echo "Waiting for $namespace namespace to be deleted (timeout: ${timeout}s)..."
  while kubectl get namespace $namespace 2>/dev/null; do
    current_time=$(date +%s)
    if [ $current_time -gt $end_time ]; then
      echo "Timeout waiting for $namespace namespace to be deleted. Forcing cleanup..."
      kubectl get namespace $namespace -o json | jq '.spec.finalizers = []' | kubectl replace --raw "/api/v1/namespaces/$namespace/finalize" -f -
      break
    fi
    echo "Still waiting for $namespace namespace to be deleted..."
    sleep 2
  done
}

# Step 1: Uninstall existing deployments
echo "Uninstalling existing deployments..."
helm uninstall order-processing-system --wait=false || true
helm uninstall observability-stack --wait=false || true

# Step 2: Delete namespaces
echo "Deleting namespaces..."
kubectl delete namespace order-processing-system --wait=false || true
kubectl delete namespace observability --wait=false || true

# Step 3: Wait for namespaces to be deleted (with 30s timeout)
wait_for_namespace_deletion "order-processing-system" 10
wait_for_namespace_deletion "observability" 10

# Step 4: Create namespaces
echo "Creating namespaces..."
kubectl create namespace order-processing-system

# Step 5: Install order-processing-system chart
echo "Installing order-processing-system chart..."
helm install order-processing-system ./helm-charts/order-processing-system

# Step 6: Wait for order-processing-system to be ready
echo "Waiting for order-processing-system to be ready..."

# Wait for infrastructure components first
echo "Waiting for infrastructure components..."
kubectl rollout status deployment/zookeeper -n order-processing-system --timeout=30s
kubectl rollout status deployment/kafka -n order-processing-system --timeout=30s
kubectl rollout status deployment/postgres -n order-processing-system --timeout=30s

# Wait for application services in parallel
echo "Waiting for application services..."
kubectl rollout status deployment/order-service -n order-processing-system --timeout=30s &
kubectl rollout status deployment/inventory-service -n order-processing-system --timeout=30s &
kubectl rollout status deployment/notification-service -n order-processing-system --timeout=30s &
kubectl rollout status deployment/analytics-service -n order-processing-system --timeout=30s &
wait

# Step 7: Install observability-stack chart
echo "Installing observability-stack chart..."
helm install observability-stack ./helm-charts/observability-stack

# Step 8: Wait for observability-stack to be ready
echo "Waiting for observability-stack to be ready..."
kubectl rollout status deployment/clickhouse -n observability --timeout=30s
kubectl rollout status daemonset/vector -n observability --timeout=30s &
kubectl rollout status deployment/grafana -n observability --timeout=30s &
wait

# Step 9: Run tests
echo "Running tests..."

# Test 1: Check if all services are running
echo "Test 1: Checking if all services are running..."
kubectl get pods -n order-processing-system | grep -v "Running\|Completed" && { echo "Some pods are not running"; exit 1; } || echo "All pods are running"

# Test 2: Check if logs are being collected in ClickHouse
echo "Test 2: Checking if logs are being collected in ClickHouse..."
LOG_COUNT=$(kubectl exec -n observability $(kubectl get pods -n observability -l app=clickhouse -o name | head -1) -- clickhouse-client -q "SELECT count() FROM logs.logs")
echo "Log count in ClickHouse: $LOG_COUNT"
if [ "$LOG_COUNT" -gt 0 ]; then
  echo "Logs are being collected in ClickHouse"
else
  echo "No logs found in ClickHouse"
  exit 1
fi

# Test 3: Generate some test logs and verify they are collected
echo "Test 3: Generating test logs..."
kubectl exec -n order-processing-system $(kubectl get pods -n order-processing-system -l app=order-service -o name | head -1) -- sh -c 'echo "This is a test log from order-service at $(date)" > /proc/1/fd/1'
kubectl exec -n order-processing-system $(kubectl get pods -n order-processing-system -l app=inventory-service -o name | head -1) -- sh -c 'echo "This is a test log from inventory-service at $(date)" > /proc/1/fd/1'
sleep 5

# Verify the test logs were collected
echo "Verifying test logs were collected..."
TEST_LOG_COUNT=$(kubectl exec -n observability $(kubectl get pods -n observability -l app=clickhouse -o name | head -1) -- clickhouse-client -q "SELECT count() FROM logs.logs WHERE message LIKE '%This is a test log%'")
echo "Test log count in ClickHouse: $TEST_LOG_COUNT"
if [ "$TEST_LOG_COUNT" -gt 0 ]; then
  echo "Test logs were collected in ClickHouse"
else
  echo "Test logs were not found in ClickHouse"
  exit 1
fi

echo "Regression tests completed successfully!"
