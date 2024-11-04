# Build the Go application
FROM golang:1.18-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the go.mod and go.sum files first for dependency management
COPY go.mod go.sum ./

# Download and cache dependencies
RUN go mod download

# Copy the rest of the application code
COPY . .

# Build the Go application
RUN go build -o video-query-service ./src

# Create a minimal final image
FROM alpine:latest

# Set the working directory in the final container
WORKDIR /app

# Copy the compiled Go binary from the builder stage
COPY --from=builder /app/video-query-service /app/video-query-service

# Expose the application port 
EXPOSE 8082

# Set the entrypoint to run the Go application
ENTRYPOINT ["/app/video-query-service"]