#!/bin/bash

echo "Stopping infrastructure services..."
docker-compose -f docker/docker-compose.yml down

echo "Infrastructure services stopped!"
