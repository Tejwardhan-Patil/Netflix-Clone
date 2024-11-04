# Netflix Clone

## Overview

This project is a Netflix clone designed to replicate the core functionalities of Netflix, including user management, video streaming, recommendations, analytics, and transcoding. The system is built using a microservices architecture, with each service implemented in a language that best suits its role, ensuring scalability, maintainability, and performance. 

The backend services are distributed across multiple languages: Kotlin for user services, Java for video services, Python for recommendations, Scala for analytics, and Go for transcoding. The frontend is developed using React, with mobile applications available for Android (Kotlin) and iOS (Swift). The project also includes DevOps configurations for continuous integration and deployment.

## Features

- **User Service (Kotlin with Spring Boot)**:
  - Handles user registration, authentication, and profile management.
  - REST API for user operations.
  - Integrated with a relational database for persistent storage.

- **Video Service (Java with Spring Boot)**:
  - Manages video content, including uploading, metadata, and streaming.
  - REST API for video operations.
  - Integrated with a scalable video storage solution.

- **Recommendation Service (Python with Flask)**:
  - Provides personalized video recommendations based on user behavior.
  - REST API for fetching recommendations.
  - Utilizes machine learning models for prediction.

- **Analytics Service (Scala with Akka HTTP)**:
  - Collects and processes user interaction data for insights and reporting.
  - Real-time analytics on user behavior and video performance.
  - REST API for analytics data retrieval.

- **Transcoding Service (Go)**:
  - Handles video transcoding to different formats and resolutions.
  - REST API for managing transcoding jobs.
  - Optimized for high-performance processing.

- **API Gateway (Kotlin with Spring Boot)**:
  - Centralized entry point for all client requests.
  - Load balancing and routing to appropriate backend services.
  - Configurable routing rules and security settings.

- **Event Handling (Scala with Kafka)**:
  - Manages communication between services using an event-driven architecture.
  - Kafka-based event bus for high-throughput message handling.
  - Ensures eventual consistency and resilience across microservices.

- **CQRS and Hexagonal Architecture**:
  - Command and Query Responsibility Segregation (CQRS) pattern implemented in video services.
  - Commands handled by Kotlin-based services and queries managed by Go-based services.
  - Separation of concerns for scalability and maintainability.

- **Frontend (React with JavaScript)**:
  - Responsive web interface for browsing and watching videos.
  - Integrated with Redux for state management.
  - API services for interacting with backend microservices.

- **Mobile Applications**:
  - **Android App (Kotlin)**: Native Android application with user authentication, video playback, and offline capabilities.
  - **iOS App (Swift)**: Native iOS application with a similar feature set as the Android app.

- **DevOps and CI/CD**:
  - Dockerized services for consistent and scalable deployments.
  - GitHub Actions for continuous integration and deployment.
  - Deployment scripts for automating the setup of services.

- **Documentation**:
  - Detailed system architecture documentation.
  - API documentation for all microservices.
  - Change log and contribution guidelines.

## Directory Structure
```bash
Root Directory
├── README.md
├── LICENSE
├── .gitignore
├── package.json

Backend Services (Microservices)
├── UserService/
│   ├── src/
│   │   ├── domain/
│   │   │   ├── User.kt
│   │   ├── controllers/
│   │   │   ├── UserController.kt
│   │   ├── services/
│   │   │   ├── UserService.kt
│   │   ├── repositories/
│   │   │   ├── UserRepository.kt
│   ├── Dockerfile
│   ├── application.yml
├── VideoService/
│   ├── src/
│   │   ├── domain/
│   │   │   ├── Video.java
│   │   ├── controllers/
│   │   │   ├── VideoController.java
│   │   ├── services/
│   │   │   ├── VideoService.java
│   │   ├── repositories/
│   │   │   ├── VideoRepository.java
│   ├── Dockerfile
│   ├── application.yml
├── RecommendationService/
│   ├── src/
│   │   ├── domain/
│   │   │   ├── recommendation.py
│   │   ├── controllers/
│   │   │   ├── recommendation_controller.py
│   │   ├── services/
│   │   │   ├── recommendation_service.py
│   │   ├── repositories/
│   │   │   ├── recommendation_repository.py
│   ├── Dockerfile
│   ├── config.py
├── AnalyticsService/
│   ├── src/
│   │   ├── domain/
│   │   │   ├── Analytics.scala
│   │   ├── controllers/
│   │   │   ├── AnalyticsController.scala
│   │   ├── services/
│   │   │   ├── AnalyticsService.scala
│   │   ├── repositories/
│   │   │   ├── AnalyticsRepository.scala
│   ├── Dockerfile
│   ├── application.conf
├── TranscodingService/
│   ├── src/
│   │   ├── domain/
│   │   │   ├── transcoding.go
│   │   ├── controllers/
│   │   │   ├── transcoding_controller.go
│   │   ├── services/
│   │   │   ├── transcoding_service.go
│   │   ├── repositories/
│   │   │   ├── transcoding_repository.go
│   ├── Dockerfile
│   ├── config.yaml
├── Gateway/
│   ├── src/
│   │   ├── GatewayApplication.kt
│   ├── Dockerfile
│   ├── application.yml

Event Handling (Event-Driven Architecture)
├── EventBus/
│   ├── src/
│   │   ├── producers/
│   │   │   ├── UserActionProducer.scala
│   │   ├── consumers/
│   │   │   ├── RecommendationConsumer.scala
│   ├── Dockerfile
│   ├── application.conf

CQRS and Hexagonal Architecture
├── VideoCommandService/
│   ├── src/
│   │   ├── commands/
│   │   │   ├── AddVideoCommand.kt
│   │   ├── controllers/
│   │   │   ├── VideoCommandController.kt
│   │   ├── services/
│   │   │   ├── VideoCommandService.kt
│   │   ├── repositories/
│   │   │   ├── VideoCommandRepository.kt
│   ├── Dockerfile
│   ├── application.yml
├── VideoQueryService/
│   ├── src/
│   │   ├── queries/
│   │   │   ├── fetch_video_query.go
│   │   ├── controllers/
│   │   │   ├── video_query_controller.go
│   │   ├── services/
│   │   │   ├── video_query_service.go
│   │   ├── repositories/
│   │   │   ├── video_query_repository.go
│   ├── Dockerfile
│   ├── config.yaml

React Frontend
├── public/
│   ├── index.html
├── src/
│   ├── components/
│   │   ├── VideoPlayer.js
│   │   ├── RecommendationList.js
│   ├── redux/
│   │   ├── store.js
│   │   ├── reducers.js
│   ├── services/
│   │   ├── api.js
│   ├── App.js
│   ├── index.js
├── package.json

Mobile
├── android/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/netflixclone/app/
│   │   │   │   ├── activities/
│   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   ├── LoginActivity.kt
│   │   │   │   │   ├── MovieDetailActivity.kt
│   │   │   │   ├── adapters/
│   │   │   │   │   ├── MovieAdapter.kt
│   │   │   │   │   ├── CategoryAdapter.kt
│   │   │   │   ├── fragments/
│   │   │   │   │   ├── HomeFragment.kt
│   │   │   │   │   ├── ProfileFragment.kt
│   │   │   │   │   ├── BrowseFragment.kt
│   │   │   │   ├── models/
│   │   │   │   │   ├── Movie.kt
│   │   │   │   │   ├── User.kt
│   │   │   │   ├── services/
│   │   │   │   │   ├── ApiService.kt
│   │   │   │   │   ├── AuthService.kt
│   │   │   │   ├── utils/
│   │   │   │   │   ├── NetworkUtils.kt
│   │   │   │   │   ├── Extensions.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_login.xml
│   │   │   │   │   ├── fragment_home.xml
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── colors.xml
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── ic_launcher_background.xml
│   │   │   │   ├── mipmap/
│   │   │   │   └── AndroidManifest.xml
│   ├── gradle.properties
│   ├── settings.gradle
│   └── build.gradle
├── iOS/
│   ├── AppDelegate.swift
│   ├── SceneDelegate.swift
│   ├── Info.plist
│   ├── Assets.xcassets
│   │   ├── Images.xcassets
│   │   ├── AppIcon.appiconset
│   ├── Main.storyboard
│   ├── LaunchScreen.storyboard
│   ├── Models/
│   │   ├── Movie.swift
│   │   ├── User.swift
│   ├── Controllers/
│   │   ├── MainViewController.swift
│   │   ├── LoginViewController.swift
│   │   ├── MovieDetailViewController.swift
│   │   ├── ProfileViewController.swift
│   ├── Views/
│   │   ├── MovieCollectionViewCell.swift
│   │   ├── CustomButton.swift
│   │   ├── CustomTextField.swift
│   ├── ViewModels/
│   │   ├── MovieViewModel.swift
│   │   ├── UserViewModel.swift
│   ├── Networking/
│   │   ├── APIClient.swift
│   │   ├── APIRouter.swift
│   ├── Utilities/
│   │   ├── Constants.swift
│   │   ├── Extensions.swift
│   ├── Resources/
│   │   ├── Localizable.strings
│   │   ├── Fonts/
│   └── Supporting Files/
│       ├── Info.plist
│       ├── Configuration.plist
├── Podfile

DevOps
├── docker/
│   ├── Dockerfile_UserService
│   ├── Dockerfile_VideoService
│   ├── Dockerfile_RecommendationService
│   ├── Dockerfile_AnalyticsService
│   ├── Dockerfile_TranscodingService
├── .github/
│   ├── workflows/
│   │   ├── ci_cd_pipeline.yml
├── scripts/
│   ├── deploy.sh

Documentation
├── docs/
│   ├── system_architecture.md
│   ├── api_documentation.md

Configurations
├── .eslintrc
├── .prettierrc
├── .babelrc