# Utility Scripts

This directory contains utility scripts for managing and testing the Order Processing System.

## Service Management Scripts

- **start-all-services.sh**: Starts all services (both infrastructure and application) in Kubernetes
- **stop-all-services.sh**: Stops all services (both application and infrastructure) in Kubernetes

## Testing and Reporting Scripts

- **generate-aggregate-coverage.sh**: Generates aggregate code coverage reports across all modules

## Usage

### Starting All Services

```bash
./start-all-services.sh
```

### Stopping All Services

```bash
./stop-all-services.sh
```

### Generating Coverage Reports

```bash
./generate-aggregate-coverage.sh
```
