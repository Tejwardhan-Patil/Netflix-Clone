# Use the official Go image as the base image
FROM golang:1.20-alpine as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Go module files and download dependencies
COPY go.mod go.sum ./
RUN go mod download

# Copy the source code to the container
COPY . .

# Build the Go application
RUN go build -o transcoding_service ./src

# Create a minimal final image
FROM alpine:3.18

# Set up the working directory
WORKDIR /root/

# Copy the compiled binary from the builder stage
COPY --from=builder /app/transcoding_service .

# Copy the config file
COPY config.yaml .

# Expose the port
EXPOSE 8080

# Command to run the executable
CMD ["./transcoding_service"]