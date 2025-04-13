#!/bin/bash

echo "Running tests for specific modules..."

# Run tests for order-service
echo "Running tests for order-service..."
cd order-service
mvn test -Dsurefire.printSummary=true -Dsurefire.useFile=false -Dsurefire.showSuccess=true
cd ..

# Run tests for inventory-service
echo "Running tests for inventory-service..."
cd inventory-service
mvn test -Dsurefire.printSummary=true -Dsurefire.useFile=false -Dsurefire.showSuccess=true
cd ..

# Run tests for common module
echo "Running tests for common module..."
cd common
mvn test -Dsurefire.printSummary=true -Dsurefire.useFile=false -Dsurefire.showSuccess=true
cd ..

echo ""
echo "Tests completed!"
