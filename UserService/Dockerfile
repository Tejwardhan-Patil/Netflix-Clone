# Base image with JDK and Kotlin support
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the build files from the current directory to the container
COPY ./build/libs/UserService-0.0.1-SNAPSHOT.jar /app/UserService.jar

# Expose port 8080 for the application
EXPOSE 8080

# Define environment variables for configuration
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS=""

# Set the command to run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/UserService.jar"]