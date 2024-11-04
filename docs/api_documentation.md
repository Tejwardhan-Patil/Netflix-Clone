# API Documentation

## User Service API

### POST /users/register

- Description: Registers a new user.
- Request Body:
  
  ```json
  {
    "username": "string",
    "email": "string",
    "password": "string"
  }
  ```

- Response:
  - 201 Created
  - 400 Bad Request

### POST /users/login

- Description: Authenticates a user.
- Request Body:

  ```json
  {
    "email": "string",
    "password": "string"
  }
  ```

- Response:
  - 200 OK
  - 401 Unauthorized

## Video Service API

### GET /videos/{id}

- Description: Retrieves video details by ID.
- Response:

  ```json
  {
    "id": "string",
    "title": "string",
    "description": "string",
    "url": "string"
  }
  ```

### POST /videos/upload

- Description: Uploads a new video.
- Request Body:

  ```json
  {
    "title": "string",
    "description": "string",
    "file": "binary"
  }
  ```

- Response:
  - 201 Created

## Recommendation Service API

### GET /recommendations/{userId}

- Description: Fetches personalized video recommendations for a user.
- Response:
  
  ```json
  [
    {
      "videoId": "string",
      "title": "string",
      "description": "string"
    }
  ]
  ```

## Analytics Service API

### GET /analytics/{videoId}

- Description: Retrieves analytics for a video.
- Response:

  ```json
  {
    "videoId": "string",
    "views": "integer",
    "likes": "integer",
    "watchTime": "integer"
  }
  ```

## Transcoding Service API

### POST /transcode

- Description: Requests video transcoding for various formats.
- Request Body:

  ```json
  {
    "videoId": "string",
    "formats": ["1080p", "720p"]
  }
  ```

- Response:
  - 200 OK

## Gateway API

- The Gateway handles routing and load balancing between the different microservices, acting as a central point for all requests.
