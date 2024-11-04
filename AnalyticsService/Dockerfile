# Use an official Scala image as the base image
FROM openjdk:17-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the build file and source code to the working directory
COPY build.sbt /app/
COPY src /app/src

# Install sbt (Scala Build Tool)
RUN apk add --no-cache bash curl && \
    curl -L -o sbt-1.5.5.tgz https://github.com/sbt/sbt/releases/download/v1.5.5/sbt-1.5.5.tgz && \
    tar -xvzf sbt-1.5.5.tgz && \
    rm sbt-1.5.5.tgz && \
    mv sbt /usr/local/sbt && \
    ln -s /usr/local/sbt/bin/sbt /usr/local/bin/sbt

# Build the application using sbt
RUN sbt compile

# Expose the port Akka HTTP will run on
EXPOSE 8080

# Run the Scala application
CMD ["sbt", "run"]