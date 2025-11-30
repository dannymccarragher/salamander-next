FROM node:20-slim

# Install Java and ffmpeg
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk ffmpeg && \
    apt-get clean

# Set working directory
WORKDIR /app

# Copy all files into the container
COPY . .

# Install Node dependencies from /server
WORKDIR /app/server
RUN npm install

# Environment variables for paths (used inside code)
ENV VIDEO_PATH=/videos
ENV RESULT_PATH=/results

# Expose the API port
EXPOSE 3000

# Run the Node app using your dev script
CMD ["npm", "run", "dev"]
