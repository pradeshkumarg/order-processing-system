# Order Processing System Helm Chart

This Helm chart deploys the Order Processing System microservices architecture to a Kubernetes cluster.

## Prerequisites

- Kubernetes 1.19+
- Helm 3.2.0+
- PV provisioner support in the underlying infrastructure (for PostgreSQL persistence)

## Installing the Chart

To install the chart with the release name `order-processing`:

```bash
helm install order-processing ./helm-charts/order-processing-system
```

The command deploys the Order Processing System on the Kubernetes cluster with default configuration. The [Parameters](#parameters) section lists the parameters that can be configured during installation.

## Uninstalling the Chart

To uninstall/delete the `order-processing` deployment:

```bash
helm uninstall order-processing
```

## Parameters

### Global Parameters

| Name                       | Description                                     | Value                    |
| -------------------------- | ----------------------------------------------- | ------------------------ |
| `global.namespace`         | Namespace to deploy all resources               | `order-processing-system` |
| `global.imageRegistry`     | Global Docker image registry                    | `""`                     |
| `global.imagePullPolicy`   | Global Docker image pull policy                 | `IfNotPresent`           |
| `global.imagePullSecrets`  | Global Docker registry secret names as array    | `[]`                     |

### Infrastructure Parameters

#### Zookeeper Parameters

| Name                                  | Description                                     | Value                          |
| ------------------------------------- | ----------------------------------------------- | ------------------------------ |
| `infrastructure.zookeeper.enabled`    | Enable Zookeeper deployment                     | `true`                         |
| `infrastructure.zookeeper.image.repository` | Zookeeper image repository                | `confluentinc/cp-zookeeper`    |
| `infrastructure.zookeeper.image.tag` | Zookeeper image tag                             | `7.3.0`                        |
| `infrastructure.zookeeper.resources` | Zookeeper resource requests/limits              | Check `values.yaml` file       |
| `infrastructure.zookeeper.service.type` | Zookeeper service type                       | `ClusterIP`                    |
| `infrastructure.zookeeper.service.port` | Zookeeper service port                       | `2181`                         |

#### Kafka Parameters

| Name                                  | Description                                     | Value                          |
| ------------------------------------- | ----------------------------------------------- | ------------------------------ |
| `infrastructure.kafka.enabled`        | Enable Kafka deployment                         | `true`                         |
| `infrastructure.kafka.image.repository` | Kafka image repository                        | `confluentinc/cp-kafka`        |
| `infrastructure.kafka.image.tag`      | Kafka image tag                                 | `7.3.0`                        |
| `infrastructure.kafka.resources`      | Kafka resource requests/limits                  | Check `values.yaml` file       |
| `infrastructure.kafka.service.type`   | Kafka service type                              | `ClusterIP`                    |
| `infrastructure.kafka.service.ports`  | Kafka service ports                             | Check `values.yaml` file       |
| `infrastructure.kafka.env`            | Kafka environment variables                     | Check `values.yaml` file       |

#### Kafka UI Parameters

| Name                                  | Description                                     | Value                          |
| ------------------------------------- | ----------------------------------------------- | ------------------------------ |
| `infrastructure.kafkaUI.enabled`      | Enable Kafka UI deployment                      | `true`                         |
| `infrastructure.kafkaUI.image.repository` | Kafka UI image repository                   | `provectuslabs/kafka-ui`       |
| `infrastructure.kafkaUI.image.tag`    | Kafka UI image tag                              | `latest`                       |
| `infrastructure.kafkaUI.resources`    | Kafka UI resource requests/limits               | Check `values.yaml` file       |
| `infrastructure.kafkaUI.service.type` | Kafka UI service type                           | `ClusterIP`                    |
| `infrastructure.kafkaUI.service.port` | Kafka UI service port                           | `8080`                         |
| `infrastructure.kafkaUI.env`          | Kafka UI environment variables                  | Check `values.yaml` file       |

#### PostgreSQL Parameters

| Name                                  | Description                                     | Value                          |
| ------------------------------------- | ----------------------------------------------- | ------------------------------ |
| `infrastructure.postgres.enabled`     | Enable PostgreSQL deployment                    | `true`                         |
| `infrastructure.postgres.image.repository` | PostgreSQL image repository                | `postgres`                     |
| `infrastructure.postgres.image.tag`   | PostgreSQL image tag                            | `latest`                       |
| `infrastructure.postgres.resources`   | PostgreSQL resource requests/limits             | Check `values.yaml` file       |
| `infrastructure.postgres.service.type` | PostgreSQL service type                        | `ClusterIP`                    |
| `infrastructure.postgres.service.port` | PostgreSQL service port                        | `5432`                         |
| `infrastructure.postgres.env`         | PostgreSQL environment variables                | Check `values.yaml` file       |
| `infrastructure.postgres.persistence.enabled` | Enable PostgreSQL persistence           | `true`                         |
| `infrastructure.postgres.persistence.size` | PostgreSQL PVC size                        | `1Gi`                          |
| `infrastructure.postgres.persistence.storageClass` | PostgreSQL storage class           | `""`                           |

### Application Services Parameters

#### Order Service Parameters

| Name                                  | Description                                     | Value                          |
| ------------------------------------- | ----------------------------------------------- | ------------------------------ |
| `services.orderService.enabled`       | Enable Order Service deployment                 | `true`                         |
| `services.orderService.image.repository` | Order Service image repository               | `order-service`                |
| `services.orderService.image.tag`     | Order Service image tag                         | `latest`                       |
| `services.orderService.replicas`      | Number of Order Service replicas                | `1`                            |
| `services.orderService.resources`     | Order Service resource requests/limits          | Check `values.yaml` file       |
| `services.orderService.service.type`  | Order Service service type                      | `ClusterIP`                    |
| `services.orderService.service.port`  | Order Service service port                      | `8081`                         |
| `services.orderService.env`           | Order Service environment variables             | Check `values.yaml` file       |

#### Inventory Service Parameters

| Name                                  | Description                                     | Value                          |
| ------------------------------------- | ----------------------------------------------- | ------------------------------ |
| `services.inventoryService.enabled`   | Enable Inventory Service deployment             | `true`                         |
| `services.inventoryService.image.repository` | Inventory Service image repository       | `inventory-service`            |
| `services.inventoryService.image.tag` | Inventory Service image tag                     | `latest`                       |
| `services.inventoryService.replicas`  | Number of Inventory Service replicas            | `1`                            |
| `services.inventoryService.resources` | Inventory Service resource requests/limits      | Check `values.yaml` file       |
| `services.inventoryService.service.type` | Inventory Service service type               | `ClusterIP`                    |
| `services.inventoryService.service.port` | Inventory Service service port               | `8082`                         |
| `services.inventoryService.env`       | Inventory Service environment variables         | Check `values.yaml` file       |

#### Notification Service Parameters

| Name                                  | Description                                     | Value                          |
| ------------------------------------- | ----------------------------------------------- | ------------------------------ |
| `services.notificationService.enabled` | Enable Notification Service deployment         | `true`                         |
| `services.notificationService.image.repository` | Notification Service image repository | `notification-service`         |
| `services.notificationService.image.tag` | Notification Service image tag               | `latest`                       |
| `services.notificationService.replicas` | Number of Notification Service replicas       | `1`                            |
| `services.notificationService.resources` | Notification Service resource requests/limits | Check `values.yaml` file      |
| `services.notificationService.service.type` | Notification Service service type         | `ClusterIP`                    |
| `services.notificationService.service.port` | Notification Service service port         | `8083`                         |
| `services.notificationService.env`    | Notification Service environment variables      | Check `values.yaml` file       |

#### Analytics Service Parameters

| Name                                  | Description                                     | Value                          |
| ------------------------------------- | ----------------------------------------------- | ------------------------------ |
| `services.analyticsService.enabled`   | Enable Analytics Service deployment             | `true`                         |
| `services.analyticsService.image.repository` | Analytics Service image repository       | `analytics-service`            |
| `services.analyticsService.image.tag` | Analytics Service image tag                     | `latest`                       |
| `services.analyticsService.replicas`  | Number of Analytics Service replicas            | `1`                            |
| `services.analyticsService.resources` | Analytics Service resource requests/limits      | Check `values.yaml` file       |
| `services.analyticsService.service.type` | Analytics Service service type               | `ClusterIP`                    |
| `services.analyticsService.service.port` | Analytics Service service port               | `8084`                         |
| `services.analyticsService.env`       | Analytics Service environment variables         | Check `values.yaml` file       |

## Configuration and Installation Details

### Dependency Management

This Helm chart includes a dependency management system that ensures services start in the correct order. The dependencies are managed using Helm hooks and annotations:

1. **Infrastructure Services**:
   - Zookeeper starts first
   - Kafka starts after Zookeeper is ready
   - PostgreSQL starts in parallel with Kafka
   - Kafka UI starts after Kafka is ready

2. **Application Services**:
   - Order Service, Inventory Service, Notification Service, and Analytics Service start after all infrastructure services are ready

The dependency mechanism works as follows:

1. Readiness check jobs run to verify each infrastructure component is ready
2. A dependency controller job orchestrates the startup sequence
3. Services are annotated with their dependencies

You can enable or disable this feature by setting `dependencies.enabled` to `true` or `false` in the values.yaml file.

```yaml
dependencies:
  enabled: true  # Enable dependency management
```

### Building and Pushing Docker Images

Before deploying to a production environment, you need to build and push the Docker images for the services:

```bash
# Build the Docker images
docker build -t order-service:latest ./order-service
docker build -t inventory-service:latest ./inventory-service
docker build -t notification-service:latest ./notification-service
docker build -t analytics-service:latest ./analytics-service

# Tag the images for your registry
docker tag order-service:latest your-registry/order-service:latest
docker tag inventory-service:latest your-registry/inventory-service:latest
docker tag notification-service:latest your-registry/notification-service:latest
docker tag analytics-service:latest your-registry/analytics-service:latest

# Push the images to your registry
docker push your-registry/order-service:latest
docker push your-registry/inventory-service:latest
docker push your-registry/notification-service:latest
docker push your-registry/analytics-service:latest
```

Then update the `values.yaml` file to use your registry:

```yaml
global:
  imageRegistry: "your-registry"
```

### Using Environment Variables

The chart allows you to specify environment variables for each service. You can modify these in the `values.yaml` file or override them when installing the chart:

```bash
helm install order-processing ./helm-charts/order-processing-system \
  --set services.orderService.env.SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:29092
```

### Persistence

The PostgreSQL database uses a PersistentVolumeClaim to store data. You can disable this by setting `infrastructure.postgres.persistence.enabled` to `false`.

### Resource Requests and Limits

Each component has default resource requests and limits defined in the `values.yaml` file. You can override these based on your cluster's capacity.

## Upgrading the Chart

To upgrade the chart:

```bash
helm upgrade order-processing ./helm-charts/order-processing-system
```

## Uninstalling the Chart

To uninstall/delete the `order-processing` deployment:

```bash
helm uninstall order-processing
```

This removes all the Kubernetes components associated with the chart and deletes the release.
