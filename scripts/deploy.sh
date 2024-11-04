#!/bin/bash

# Function to deploy a microservice
deploy_service() {
  local service_name=$1
  local dockerfile=$2
  local port=$3

  echo "Deploying $service_name..."

  # Build Docker image
  docker build -t "$service_name" -f "$dockerfile" .

  # Run the Docker container
  docker run -d -p "$port":"$port" --name "$service_name" "$service_name"

  echo "$service_name deployed on port $port."
}

# Deploy UserService (Kotlin with Spring Boot)
deploy_service "user-service" "../UserService/Dockerfile" 8081

# Deploy VideoService (Java with Spring Boot)
deploy_service "video-service" "../VideoService/Dockerfile" 8082

# Deploy RecommendationService (Python with Flask)
deploy_service "recommendation-service" "../RecommendationService/Dockerfile" 8083

# Deploy AnalyticsService (Scala with Akka HTTP)
deploy_service "analytics-service" "../AnalyticsService/Dockerfile" 8084

# Deploy TranscodingService (Go)
deploy_service "transcoding-service" "../TranscodingService/Dockerfile" 8085

# Deploy EventBus (Scala with Kafka)
deploy_service "event-bus" "../EventBus/Dockerfile" 9092

# Deploy API Gateway (Kotlin with Spring Boot)
deploy_service "api-gateway" "../Gateway/Dockerfile" 8080

# Deploy Frontend (React)
echo "Deploying frontend..."
cd ../frontend
npm install
npm run build
docker build -t "frontend" -f Dockerfile .
docker run -d -p 3000:3000 --name "frontend" "frontend"
echo "Frontend deployed on port 3000."

echo "All services deployed successfully."