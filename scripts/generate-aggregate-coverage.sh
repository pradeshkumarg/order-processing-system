#!/bin/bash

echo "Generating aggregate coverage report..."

# Create a directory for the aggregate report
mkdir -p target/site/jacoco-aggregate

# Use JaCoCo's report-aggregate goal to create an aggregate report
mvn jacoco:report-aggregate -Djacoco.outputDirectory=target/site/jacoco-aggregate

echo ""
echo "Aggregate Coverage Report:"
echo "========================="
echo "file://$(pwd)/target/site/jacoco-aggregate/index.html"
echo ""
echo "To view the aggregate coverage report, copy and paste the URL into your browser."
