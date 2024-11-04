# Base image for Java 17 
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the build file (jar file) from the host machine to the working directory in the container
COPY target/VideoService.jar /app/VideoService.jar

# Expose the port on which the service will run
EXPOSE 8080

# Define environment variables like database URLs, credentials, etc
ENV SPRING_PROFILES_ACTIVE=prod

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/VideoService.jar"]