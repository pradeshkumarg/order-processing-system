#!/bin/bash

echo "Running tests with coverage for all modules..."

# Clean and run tests with JaCoCo coverage
mvn clean test jacoco:report -Dsurefire.printSummary=true -Dsurefire.useFile=false -Dsurefire.showSuccess=true

# Display summary of test results
echo ""
echo "Test Summary:"
echo "============="
find . -name "TEST-*.xml" | xargs grep "tests=" | awk -F'tests=\"|\"' '{sum+=$2} END {print "Total Tests Run: " sum}'
find . -name "TEST-*.xml" | xargs grep "failures=" | awk -F'failures=\"|\"' '{sum+=$2} END {print "Total Failures: " sum}'
find . -name "TEST-*.xml" | xargs grep "errors=" | awk -F'errors=\"|\"' '{sum+=$2} END {print "Total Errors: " sum}'
find . -name "TEST-*.xml" | xargs grep "skipped=" | awk -F'skipped=\"|\"' '{sum+=$2} END {print "Total Skipped: " sum}'

# Display coverage report locations
echo ""
echo "Coverage Reports:"
echo "================"
echo "Order Service: file://$(pwd)/order-service/target/site/jacoco/index.html"
echo "Inventory Service: file://$(pwd)/inventory-service/target/site/jacoco/index.html"
echo "Common Module: file://$(pwd)/common/target/site/jacoco/index.html"
echo "Notification Service: file://$(pwd)/notification-service/target/site/jacoco/index.html"
echo "Analytics Service: file://$(pwd)/analytics-service/target/site/jacoco/index.html"

echo ""
echo "To view a coverage report, copy and paste the URL into your browser."
