# Use an official OpenJDK runtime as the base image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the build files from the local machine to the container
COPY ./build/libs/Gateway-0.0.1-SNAPSHOT.jar /app/gateway.jar

# Expose the application port 
EXPOSE 8080

# Set environment variables 
ENV SPRING_PROFILES_ACTIVE=prod

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/gateway.jar"]