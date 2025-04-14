#!/bin/bash

# This script checks the status of the Vector + ClickHouse + Grafana observability stack

# Check if the observability namespace exists
if ! kubectl get namespace observability &>/dev/null; then
  echo "❌ Observability namespace not found. Please deploy the stack first."
  exit 1
fi

# Check ClickHouse
echo "Checking ClickHouse..."
CLICKHOUSE_PODS=$(kubectl get pods -n observability -l app=clickhouse -o name)
if [ -z "$CLICKHOUSE_PODS" ]; then
  echo "❌ ClickHouse pods not found"
else
  echo "✅ ClickHouse pods found: $CLICKHOUSE_PODS"
  
  # Check ClickHouse readiness
  CLICKHOUSE_READY=$(kubectl get pods -n observability -l app=clickhouse -o jsonpath='{.items[0].status.containerStatuses[0].ready}')
  if [ "$CLICKHOUSE_READY" == "true" ]; then
    echo "✅ ClickHouse is ready"
  else
    echo "❌ ClickHouse is not ready"
  fi
fi

# Check Vector
echo "Checking Vector..."
VECTOR_PODS=$(kubectl get pods -n observability -l app=vector -o name)
if [ -z "$VECTOR_PODS" ]; then
  echo "❌ Vector pods not found"
else
  echo "✅ Vector pods found: $VECTOR_PODS"
  
  # Check Vector readiness
  VECTOR_READY_COUNT=$(kubectl get pods -n observability -l app=vector -o jsonpath='{.items[0].status.containerStatuses[0].ready}')
  if [ "$VECTOR_READY_COUNT" == "true" ]; then
    echo "✅ Vector is ready"
  else
    echo "❌ Vector is not ready"
  fi
fi

# Check Grafana
echo "Checking Grafana..."
GRAFANA_PODS=$(kubectl get pods -n observability -l app=grafana -o name)
if [ -z "$GRAFANA_PODS" ]; then
  echo "❌ Grafana pods not found"
else
  echo "✅ Grafana pods found: $GRAFANA_PODS"
  
  # Check Grafana readiness
  GRAFANA_READY=$(kubectl get pods -n observability -l app=grafana -o jsonpath='{.items[0].status.containerStatuses[0].ready}')
  if [ "$GRAFANA_READY" == "true" ]; then
    echo "✅ Grafana is ready"
  else
    echo "❌ Grafana is not ready"
  fi
fi

# Check if logs are being collected
echo "Checking if logs are being collected..."
if kubectl exec -n observability $(kubectl get pods -n observability -l app=clickhouse -o name | cut -d/ -f2) -- clickhouse-client -u default --password clickhouse -q "SELECT count() FROM logs.logs" &>/dev/null; then
  LOG_COUNT=$(kubectl exec -n observability $(kubectl get pods -n observability -l app=clickhouse -o name | cut -d/ -f2) -- clickhouse-client -u default --password clickhouse -q "SELECT count() FROM logs.logs")
  echo "✅ Found $LOG_COUNT logs in ClickHouse"
  
  if [ "$LOG_COUNT" -gt 0 ]; then
    echo "Sample logs:"
    kubectl exec -n observability $(kubectl get pods -n observability -l app=clickhouse -o name | cut -d/ -f2) -- clickhouse-client -u default --password clickhouse -q "SELECT timestamp, namespace, pod_name, container_name, level, message FROM logs.logs LIMIT 5 FORMAT Pretty"
  fi
else
  echo "❌ Could not query logs from ClickHouse"
fi

echo "Observability stack check completed."
