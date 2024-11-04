# Use an official openjdk image as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the compiled JAR file from the build context to the container
COPY build/libs/VideoCommandService-0.0.1-SNAPSHOT.jar /app/VideoCommandService.jar

# Expose the port on which the service will run
EXPOSE 8082

# Define the environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Run the JAR file when the container starts
ENTRYPOINT ["java", "-jar", "/app/VideoCommandService.jar"]