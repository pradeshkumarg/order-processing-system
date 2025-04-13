#!/bin/bash

echo "Starting infrastructure services (Zookeeper, Kafka, PostgreSQL)..."
docker-compose -f docker/docker-compose.yml up -d

echo "Waiting for services to start..."
sleep 1

echo "Infrastructure services are running!"
echo "Kafka is available at localhost:9092"
echo "PostgreSQL is available at localhost:5432"
echo "Kafka UI is available at http://localhost:8080"
