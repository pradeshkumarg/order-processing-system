#!/bin/bash

set -e

echo "Running load tests..."

# Function to check if a service is ready
check_service_ready() {
  local service=$1
  local port=$2
  local namespace=$3
  local max_retries=10
  local retry_count=0
  
  echo "Checking if $service is ready..."
  while [ $retry_count -lt $max_retries ]; do
    if kubectl port-forward -n $namespace svc/$service $port:$port > /dev/null 2>&1 & 
    then
      PID=$!
      sleep 1
      if curl -s http://localhost:$port/actuator/health > /dev/null; then
        kill $PID
        echo "$service is ready!"
        return 0
      fi
      kill $PID
    fi
    retry_count=$((retry_count + 1))
    echo "Retry $retry_count/$max_retries: $service is not ready yet..."
    sleep 2
  done
  
  echo "$service is not ready after $max_retries retries!"
  return 1
}

# Step 1: Check if all services are ready
echo "Step 1: Checking if all services are ready..."
check_service_ready "order-service" 8081 "order-processing-system"
check_service_ready "inventory-service" 8082 "order-processing-system"

# Step 2: Set up port forwarding for the services
echo "Step 2: Setting up port forwarding for the services..."
kubectl port-forward -n order-processing-system svc/order-service 8081:8081 > /dev/null 2>&1 &
ORDER_PF_PID=$!
kubectl port-forward -n order-processing-system svc/inventory-service 8082:8082 > /dev/null 2>&1 &
INVENTORY_PF_PID=$!

# Wait for port forwarding to be established
sleep 2

# Step 3: Run load tests
echo "Step 3: Running load tests..."

# Create 10 orders in parallel
echo "Creating 10 orders in parallel..."
for i in {1..10}; do
  curl -s -X POST -H "Content-Type: application/json" -d "{\"customerId\": \"customer-$i\", \"items\": [{\"productId\": \"product-$i\", \"quantity\": $i}]}" http://localhost:8081/api/orders > /dev/null &
done

# Wait for all requests to complete
wait

# Step 4: Verify the results
echo "Step 4: Verifying the results..."
ORDER_COUNT=$(curl -s http://localhost:8081/api/orders/count)
echo "Order count: $ORDER_COUNT"

if [ "$ORDER_COUNT" -gt 0 ]; then
  echo "Load test successful!"
else
  echo "Load test failed!"
  exit 1
fi

# Step 5: Check logs in ClickHouse
echo "Step 5: Checking logs in ClickHouse..."
LOG_COUNT=$(kubectl exec -n observability $(kubectl get pods -n observability -l app=clickhouse -o name | head -1) -- clickhouse-client -q "SELECT count() FROM logs.logs WHERE message LIKE '%Created order%'")
echo "Log count for created orders in ClickHouse: $LOG_COUNT"

# Step 6: Clean up
echo "Step 6: Cleaning up..."
kill $ORDER_PF_PID
kill $INVENTORY_PF_PID

echo "Load tests completed successfully!"
