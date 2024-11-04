# System Architecture

## Overview

The Netflix Clone project is built using a microservices architecture. Each service is responsible for a specific domain of the application, and all services communicate with each other through REST APIs or event-driven mechanisms. The backend services are containerized using Docker and orchestrated via a CI/CD pipeline for continuous integration and delivery.

## Services

### 1. User Service (Kotlin with Spring Boot)

- Manages user-related operations such as registration, authentication, and profile management.
- Contains the following layers:
  - **Controllers**: REST API controllers for user operations.
  - **Services**: Business logic for user management.
  - **Repositories**: Data access layer with SQL and JSON schema for persistence.

### 2. Video Service (Java with Spring Boot)

- Manages video metadata and streaming URLs.
- Contains the following layers:
  - **Controllers**: REST API controllers for video operations.
  - **Services**: Business logic for handling video-related actions.
  - **Repositories**: Data access layer for video storage.

### 3. Recommendation Service (Python with Flask)

- Provides personalized recommendations based on user interactions.
- Layers include:
  - **Controllers**: REST API controllers for retrieving recommendations.
  - **Services**: Business logic for generating and updating recommendations.
  - **Repositories**: Data layer to store recommendation data.

### 4. Analytics Service (Scala with Akka HTTP)

- Collects and processes usage data for analytical insights.
- Contains:
  - **Controllers**: REST API controllers for fetching analytics data.
  - **Services**: Handles the business logic for analytics.
  - **Repositories**: Manages storage of analytical information.

### 5. Transcoding Service (Go)

- Manages video transcoding for different formats and resolutions.
- Layers include:
  - **Controllers**: REST API endpoints for managing transcoding requests.
  - **Services**: Business logic for transcoding videos.
  - **Repositories**: Data access for storing transcoded video data.

## API Gateway (Kotlin with Spring Boot)

- Acts as the central entry point for all microservices.
- Provides routing, load balancing, and security features for the system.

## Event-Driven Architecture

- **EventBus** (Scala with Kafka): Facilitates communication between services using events. Produces and consumes events to ensure real-time updates and asynchronous processing.

## CQRS and Hexagonal Architecture

- The **VideoCommandService** (Kotlin) handles command operations, while the **VideoQueryService** (Go) manages query requests.
- This separation of concerns allows the system to scale efficiently, handling both commands and queries independently.

## Frontend

- Built using React, the frontend communicates with the backend services via REST API calls.
- State management is handled using Redux for efficient data flow.

## Mobile Applications

- Native Android (Kotlin) and iOS (Swift) apps, with features like user login, video playback, and recommendation browsing.

## DevOps

- CI/CD pipelines are implemented using GitHub Actions, and Docker is used for containerizing each service. Deployment scripts automate service provisioning and updates.
