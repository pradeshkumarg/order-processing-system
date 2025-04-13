# Utility Scripts

This directory contains utility scripts for managing and testing the Order Processing System.

## Helm Deployment Scripts

- **start-all-services.sh**: Deploys all services using the Helm chart
- **stop-all-services.sh**: Uninstalls all services using Helm

## Testing and Reporting Scripts

- **generate-aggregate-coverage.sh**: Generates aggregate code coverage reports across all modules

## Usage

### Deploying with Helm

```bash
./start-all-services.sh
```

This script will:
1. Check if Helm is installed
2. Install or upgrade the Order Processing System Helm chart
3. Display the status of the deployment
4. Show instructions for accessing the services

### Uninstalling with Helm

```bash
./stop-all-services.sh
```

This script will:
1. Check if Helm is installed
2. Check if the Order Processing System is deployed
3. Uninstall the Helm release if it exists
4. Verify that all pods have been removed

### Generating Coverage Reports

```bash
./generate-aggregate-coverage.sh
```

This script will generate an aggregate code coverage report across all modules.
