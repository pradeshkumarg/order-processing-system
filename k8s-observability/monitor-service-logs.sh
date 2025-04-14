#!/bin/bash

# Function to monitor logs from a specific service
monitor_service_logs() {
  local service=$1
  echo "=== Monitoring logs from $service ==="
  kubectl exec -n observability $(kubectl get pods -n observability -l app=clickhouse -o name | head -1) -- clickhouse-client -q "SELECT timestamp, container_name, message FROM logs.logs WHERE container_name = '$service' ORDER BY timestamp DESC LIMIT 5"
  echo ""
}

# Monitor logs from all services
monitor_service_logs "order-service"
monitor_service_logs "inventory-service"
monitor_service_logs "notification-service"
monitor_service_logs "analytics-service"

# Monitor log count by service
echo "=== Log count by service ==="
kubectl exec -n observability $(kubectl get pods -n observability -l app=clickhouse -o name | head -1) -- clickhouse-client -q "SELECT container_name, count() AS count FROM logs.logs WHERE container_name IN ('order-service', 'inventory-service', 'notification-service', 'analytics-service') GROUP BY container_name ORDER BY count DESC"
