# API Tests for Order Processing System

This directory contains Jest tests for testing the Order Processing System API in a Kubernetes environment.

## Prerequisites

- Node.js (v14 or later)
- npm or yarn
- Access to the Order Processing System services in Kubernetes
- kubectl configured for Kubernetes tests

## Installation

```bash
# Install dependencies
npm install
```

## Running Tests

```bash
# Run tests
npm test

# Run tests in watch mode
npm run test:watch

# Run tests with shorter timeouts
npm run test:fast
```

## Test Files

- **k8s-tests.test.js**: Tests the API using port forwarding and Jest.
- **jest.config.js**: Configuration for Jest tests.

## Test Coverage

The tests cover:

1. **Inventory Service**
   - Creating inventory
   - Getting inventory by product ID
   - Verifying inventory updates

2. **Order Service**
   - Creating orders
   - Getting all orders

3. **Notification Service**
   - Getting all notifications
   - Sending test notifications

4. **End-to-End Flow**
   - Creating inventory
   - Creating an order
   - Verifying that inventory is updated via Kafka messaging

## Troubleshooting

If you encounter connectivity issues with your Kubernetes cluster:

1. Check that your kubectl context is set correctly:
   ```bash
   kubectl config current-context
   ```

2. Verify that the pods are running:
   ```bash
   kubectl get pods -n order-processing-system
   ```

3. Check the logs of the services:
   ```bash
   kubectl logs -n order-processing-system deployment/order-service
   kubectl logs -n order-processing-system deployment/inventory-service
   kubectl logs -n order-processing-system deployment/notification-service
   ```

4. If using port forwarding, make sure no other processes are using the required ports:
   ```bash
   lsof -i :8081
   lsof -i :8082
   lsof -i :8083
   ```
