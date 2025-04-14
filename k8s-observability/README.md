# Kubernetes Observability Stack

This directory contains the configuration files for the Vector + ClickHouse + Grafana logging stack, a fully open-source and self-hosted solution for collecting, storing, and visualizing logs from Kubernetes services.

## Components

- **Vector**: Collects logs from all pods in the cluster and forwards them to ClickHouse
- **ClickHouse**: Stores logs in a high-performance columnar database, providing fast query performance even with large volumes of logs
- **Grafana**: Visualizes logs with interactive dashboards, making it easy to monitor and troubleshoot services

## Files

- `vector-config.yaml`: Vector configuration for collecting logs from all Kubernetes pods
- `clickhouse-config.yaml`: ClickHouse configuration and deployment
- `grafana-with-dashboards.yaml`: Combined Grafana configuration with pre-configured dashboards
- `apply-grafana-with-dashboards.sh`: Script to apply the Grafana configuration and set up port forwarding
- `monitor-service-logs.sh`: Script to quickly check logs from all services via command line
- `dashboards/service-logs-dashboard.json`: Dashboard JSON for visualizing service logs
- `stop-observability.sh`: Script to shut down all observability components
- `README.md`: This documentation file

## Setup

1. Create the observability namespace:
   ```bash
   kubectl create namespace observability
   ```

2. Deploy ClickHouse:
   ```bash
   kubectl apply -f k8s-observability/clickhouse-config.yaml
   ```

3. Create the logs database in ClickHouse:
   ```bash
   kubectl exec -n observability $(kubectl get pods -n observability -l app=clickhouse -o name | head -1) -- clickhouse-client -q "CREATE DATABASE IF NOT EXISTS logs"
   
   kubectl exec -n observability $(kubectl get pods -n observability -l app=clickhouse -o name | head -1) -- clickhouse-client -q "CREATE TABLE IF NOT EXISTS logs.logs (timestamp DateTime, namespace String, pod_name String, container_name String, level String, message String, app String, host String, service String, raw_message String) ENGINE = MergeTree() PARTITION BY toYYYYMM(timestamp) ORDER BY (timestamp, namespace, pod_name, container_name)"
   ```

4. Deploy Vector:
   ```bash
   kubectl apply -f k8s-observability/vector-config.yaml
   ```

5. Deploy Grafana with dashboards:
   ```bash
   kubectl apply -f k8s-observability/grafana-with-dashboards.yaml
   ```

This will set up the complete logging stack with pre-configured dashboards for all services.

## Accessing Grafana

1. Set up port forwarding:
   ```bash
   kubectl port-forward svc/grafana -n observability 3000:3000
   ```

2. Open Grafana in your browser:
   ```
   http://localhost:3000
   ```

3. Log in with the following credentials:
   - Username: `admin`
   - Password: `admin`

4. Navigate to the "Service Logs Dashboard" to view logs from all services.

The dashboard includes panels for:
- Inventory Service Logs
- Order Service Logs
- Notification Service Logs
- Analytics Service Logs

## Monitoring Logs

You can use the `monitor-service-logs.sh` script to quickly check logs from all services via the command line:

```bash
./k8s-observability/monitor-service-logs.sh
```

This is useful for quick debugging without accessing the Grafana UI.

## Shutting Down

To shut down all observability components, run the stop script:

```bash
./k8s-observability/stop-observability.sh
```

This will delete all resources created for the observability stack.

## Troubleshooting

If you encounter issues with the logging stack, try the following:

1. Check if Vector is running:
   ```bash
   kubectl get pods -n observability -l app=vector
   ```

2. Check if ClickHouse is running:
   ```bash
   kubectl get pods -n observability -l app=clickhouse
   ```

3. Check if Grafana is running:
   ```bash
   kubectl get pods -n observability -l app=grafana
   ```

4. Check if logs are being collected:
   ```bash
   kubectl exec -n observability $(kubectl get pods -n observability -l app=clickhouse -o name | head -1) -- clickhouse-client -q "SELECT count() FROM logs.logs"
   ```

5. Check if the dashboard is available:
   ```bash
   kubectl exec -n observability $(kubectl get pods -n observability -l app=grafana -o name | head -1) -- curl -s http://localhost:3000/api/dashboards/uid/service-logs-dashboard
   ```

## Benefits Over Loki

- **Performance**: ClickHouse provides better query performance for large volumes of logs
- **Scalability**: The stack can handle millions of log entries without performance degradation
- **Flexibility**: SQL-based querying allows for complex log analysis
- **Integration**: Easy integration with existing Kubernetes services
- **Open Source**: Fully open-source solution with no vendor lock-in
