# Use a lightweight Python image
FROM python:3.12-slim

# Set the working directory
WORKDIR /app

# Copy requirements and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy the function code
COPY . .

# Expose the port Knative will use
EXPOSE 8080

# Command to run the function
CMD ["python", "-m", "parliament", "/app"]
