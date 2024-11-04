# Python image from the Docker Hub
FROM python:3.9-slim

# Set environment variables
ENV PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1

# Set the working directory inside the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

# Install any dependencies specified in requirements.txt
RUN pip install --upgrade pip && \
    pip install -r requirements.txt

# Expose the port on which the Flask app will run
EXPOSE 5000

# Set the Flask app environment variables
ENV FLASK_APP=src/recommendation_controller.py \
    FLASK_ENV=production

# Run the Flask app
CMD ["flask", "run", "--host=0.0.0.0"]