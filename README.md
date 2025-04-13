# Order Processing System

Event-driven microservices architecture demonstrating order processing with Kafka. Includes order, inventory, notification, and analytics services with comprehensive testing and documentation.

## Architecture Overview

This project demonstrates an event-driven microservices architecture for processing orders in an e-commerce system. The services communicate asynchronously via Kafka events, ensuring loose coupling and scalability. Each service has its own responsibility and can be developed, deployed, and scaled independently.

## Services

- **Order Service (Port 8081)**: Accepts new orders via REST API and publishes order-created events to Kafka.
- **Inventory Service (Port 8082)**: Subscribes to order-created events, checks/updates inventory, and publishes inventory-updated or inventory-failed events.
- **Notification Service (Port 8083)**: Listens to inventory events and sends mock notifications.
- **Analytics Service (Port 8084)**: Consumes all events for real-time analytics and reporting.

Each service includes:
- REST API with proper error handling
- Swagger/OpenAPI documentation
- Comprehensive unit tests with >95% code coverage
- Conditional Kafka configuration for testing
- H2 in-memory database for testing

## Prerequisites

- Java 17
- Maven
- Docker and Docker Compose

## Building the Project

Build all modules:

```bash
mvn clean package -DskipTests
```

## Running the Application

### 1. Start Infrastructure Services

Start Zookeeper, Kafka, and PostgreSQL:

```bash
./start-infrastructure.sh
```

### 2. Run the Services

You can run each service individually using Maven:

```bash
# Run Order Service
cd order-service
mvn spring-boot:run

# Run Inventory Service
cd inventory-service
mvn spring-boot:run

# Run Notification Service
cd notification-service
mvn spring-boot:run

# Run Analytics Service
cd analytics-service
mvn spring-boot:run
```

Or use the provided scripts for the compiled JAR files:

```bash
./run-order-service.sh
./run-inventory-service.sh
```

### 3. Access API Documentation

Each service provides Swagger UI for API documentation and testing:

- Order Service: http://localhost:8081/swagger-ui
- Inventory Service: http://localhost:8082/swagger-ui
- Notification Service: http://localhost:8083/swagger-ui
- Analytics Service: http://localhost:8084/swagger-ui

The OpenAPI specifications are available at:

- Order Service: http://localhost:8081/api-docs
- Inventory Service: http://localhost:8082/api-docs
- Notification Service: http://localhost:8083/api-docs
- Analytics Service: http://localhost:8084/api-docs

### 4. Testing the Services

#### Order Service

Create a new order:

```bash
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{
      "productId": "PROD-001",
      "quantity": 2,
      "unitPrice": 99.99
  }'
```

Get all orders:

```bash
curl http://localhost:8081/api/orders
```

#### Inventory Service

Create or update inventory:

```bash
curl -X POST http://localhost:8082/api/inventory \
  -H "Content-Type: application/json" \
  -d '{
      "productId": "PROD-001",
      "quantity": 10
  }'
```

Get inventory for a product:

```bash
curl http://localhost:8082/api/inventory/PROD-001
```

Get all inventory:

```bash
curl http://localhost:8082/api/inventory
```

### 4. Stopping the Application

Stop the infrastructure services:

```bash
./stop-infrastructure.sh
```

## Kafka Topics

- order-created
- inventory-updated
- inventory-failed

## Kafka UI

Access the Kafka UI at: http://localhost:8080

## Running Tests

### Running All Tests with Coverage

To run all tests across all modules and generate coverage reports:

```bash
./run-tests-with-coverage.sh
```

This will:
1. Run all tests in all modules
2. Generate individual JaCoCo coverage reports for each module
3. Display a summary of test results
4. Show the locations of the coverage reports

### Running Tests for Specific Modules

If you encounter issues with the main test script, you can run tests for specific modules:

```bash
./run-specific-tests.sh
```

This script runs tests for each module individually, which can help identify where issues might be occurring.

### Generating an Aggregate Coverage Report

To generate an aggregate coverage report for all modules:

```bash
./generate-aggregate-coverage.sh
```

This will create a single coverage report that combines data from all modules.

### Viewing Coverage Reports

After running the tests with coverage, you can view the coverage reports by opening the HTML files in your browser:

- Order Service: `order-service/target/site/jacoco/index.html`
- Inventory Service: `inventory-service/target/site/jacoco/index.html`
- Common Module: `common/target/site/jacoco/index.html`
- Notification Service: `notification-service/target/site/jacoco/index.html`
- Analytics Service: `analytics-service/target/site/jacoco/index.html`
- Aggregate Report: `target/site/jacoco-aggregate/index.html`

## Project Structure

```
├── analytics-service     # Analytics and reporting service
├── common               # Shared code and event definitions
├── docker               # Docker Compose configuration
├── inventory-service    # Inventory management service
├── notification-service # Notification handling service
├── order-service        # Order processing service
├── next-steps.md        # Future enhancements and roadmap
├── pom.xml              # Parent POM for the project
└── README.md            # This file
```

## Event Flow

1. **Order Creation**:
   - User creates an order via Order Service REST API
   - Order Service publishes an `order-created` event to Kafka

2. **Inventory Processing**:
   - Inventory Service consumes the `order-created` event
   - Checks if there's sufficient inventory
   - If successful, publishes an `inventory-updated` event
   - If failed, publishes an `inventory-failed` event

3. **Notification Handling**:
   - Notification Service consumes both `inventory-updated` and `inventory-failed` events
   - Sends appropriate notifications based on the event type

4. **Analytics Processing**:
   - Analytics Service consumes all events
   - Updates analytics data and provides reporting endpoints

## Future Enhancements

See [next-steps.md](next-steps.md) for planned enhancements and improvements.
