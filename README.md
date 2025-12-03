# Salamander Tracker – Full Stack Video Processing System

A full stack project combining:

- **Next.js + React** frontend  
- **Node.js + Express** backend  
- **Java (Maven) video processor**  
- **Dockerized** deployment workflow  
- **Centroid detection** of salamanders in MP4 videos  
- **CSV output** for coordinate tracking  
- **Live preview** using color threshold + binarization  

This README documents the backend, frontend, Docker, job queue, and how to run everything.

---

## Features

### **Frontend (Next.js + React)**
- Video listing page (`/videos`)
- Preview page with:
  - Original frame preview
  - Live binarized preview
  - Color picker for target color
  - Threshold slider
- Start processing button
- Job status polling (every 2s)
- Completed job download list

---

## Backend (Node + Express)

### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/videos` | List all video files |
| GET | `/thumbnail/:filename` | Get first-frame JPEG thumbnail |
| POST | `/process/:filename` | Start processing a video with Java JAR |
| GET | `/process/:jobId/status` | Poll job status |
| POST | `/api/upload` | Upload MP4 videos |

---

## Java Processor

- Built with Maven
- Generates:
  - Largest centroid per second
  - Writes full CSV file
- Interface:
  ```
  java -jar processor.jar <videoPath> <hexColor> <threshold> <jobId> <csvOutputName>
  ```
- CSV saved into:
  ```
  /results/<filename>_<jobId>.csv
  ```

---

## Docker Setup

### Volumes:
- `/videos` → stores uploaded videos
- `/results` → generated CSV files
- Backend container mounts the Java jar

### Environment variables:
```
VIDEO_PATH=./videos
RESULT_PATH=./results
JAR_PATH=./Processor/target/centroidFinderVideo-jar-with-dependencies.jar
```

---

## 🧠 How Job Tracking Works

### 1️⃣ Start Process  
User presses **Start Process** → frontend sends request:

```
POST /process/<filename>?targetColor=FF0000&threshold=120
```

Backend:
- Generates UUID jobId
- Saves: `jobStatus[jobId] = "processing"`
- Spawns Java process

### 2️⃣ Java finishes  
Backend marks job:

```
jobStatus[jobId] = {
  status: "done",
  csv: "<file_jobId>.csv"
}
```

### 3️Frontend polls status  
Every 2 seconds:

```
GET /process/:jobId/status
```

When `"done"` → shows completed job list + download button.

---

## Project Structure

```
root
│── Processor/               # Java JAR + Maven
│── backend/                 # Express server
│   ├── routes/
│   ├── videos/
│   ├── results/
│── frontend/                # Next.js app
│   ├── app/
│   ├── components/
│   ├── preview/
│   ├── videos/
│── docker/
```

---

## Running Locally

### 1. Build Java JAR
```
cd Processor
mvn clean package
```

### 2. Start backend
```
cd backend
npm install
npm run start
```

### 3. Start frontend
```
cd frontend
npm install
npm run dev
```

---

## Technologies Used

- **Next.js 13 (App Router)**
- **Material UI**
- **React hooks** (useState, useEffect, useParams, useRef)
- **Node.js Express**
- **Java + Maven**
- **FFmpeg + JCodec**
- **Docker + Volumes**
- **UUID job tracking**
- **Canvas API for binarization**

---

## Downloading CSV Output

After processing completes, frontend generates:

```
http://localhost:3000/results/<videoName>_<jobId>.csv
```

You may download via browser or API.

---
