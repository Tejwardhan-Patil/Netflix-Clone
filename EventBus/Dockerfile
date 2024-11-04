# Use an official Scala runtime as a parent image
FROM hseeberger/scala-sbt:11.0.16_1.8.0_1.7.2 AS build

# Set working directory inside the container
WORKDIR /app

# Copy the build definition and source code to the container
COPY build.sbt ./
COPY src ./src

# Compile and build the Scala project
RUN sbt clean compile assembly

# Second stage: Create a lightweight image with the compiled jar and Kafka
FROM openjdk:11-jre-slim

# Set environment variables for Kafka
ENV KAFKA_BROKER="kafka:9092"
ENV SCALA_JAR_PATH="/app/target/scala-2.13/eventbus-assembly.jar"

# Install Kafka
RUN apt-get update && \
    apt-get install -y curl && \
    curl -s https://downloads.apache.org/kafka/3.1.0/kafka_2.13-3.1.0.tgz | tar -xvz -C /opt && \
    ln -s /opt/kafka_2.13-3.1.0 /opt/kafka

# Set Kafka home
ENV KAFKA_HOME="/opt/kafka"

# Expose the necessary Kafka ports
EXPOSE 9092 2181

# Copy the compiled jar file from the first stage
COPY --from=build /app/target/scala-2.13/eventbus-assembly.jar /app/eventbus.jar

# Set the entry point to start Kafka and the Scala application
CMD ["/bin/bash", "-c", "$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties & java -jar /app/eventbus.jar"]