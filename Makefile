.PHONY: build run stop clean

build:
    ./mvnw clean package -DskipTests
    docker-compose -f docker/docker-compose.yml -f docker/docker-compose.apps.yml build

run:
    docker-compose -f docker/docker-compose.yml -f docker/docker-compose.apps.yml up -d

stop:
    docker-compose -f docker/docker-compose.yml -f docker/docker-compose.apps.yml down

clean:
    docker-compose -f docker/docker-compose.yml -f docker/docker-compose.apps.yml down -v
    ./mvnw clean