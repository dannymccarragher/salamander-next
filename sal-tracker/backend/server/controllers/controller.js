import * as fs from "fs/promises"; 
import path from "path";
import ffmpeg from "fluent-ffmpeg";
import { spawn } from "child_process";
import { randomUUID } from "crypto";
import multer from "multer";
import { fileURLToPath } from "url";
import { dirname } from "path";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

// In-memory job tracking
const jobStatus = {};

// Directories from env or defaults
const VIDEO_DIR =
  process.env.VIDEO_PATH || path.join(__dirname, "../videos");

// Java jar location relative to repo root
const JAR_PATH =
  process.env.JAR_PATH ||
  path.join(__dirname, "../../../Processor/target/centroidFinderVideo-jar-with-dependencies.jar")

// return list of all videos within video directory
const getVideos = async (req, res) => {
  try {
    const files = await fs.readdir(VIDEO_DIR);
    const videoURLs = files.map((file) => `${file}`);
    res.json(videoURLs);
  } catch (err) {
    console.error("Error reading video directory:", err);
    res.status(500).json({ error: "Failed to read video directory" });
  }
};

// return thumbnail of first frame at first second
const getThumbnail = async (req, res) => {
  try {
    const thumbnailId = req.params.filename;
    const videoPath = path.join(VIDEO_DIR, thumbnailId);

    try {
      await fs.access(videoPath);
    } catch {
      return res.status(404).json({ error: "Video not found." });
    }

    res.setHeader("Content-Type", "image/jpeg");

    ffmpeg(videoPath)
      .inputOptions(["-ss 0"])
      .outputOptions(["-frames:v 1"])
      .format("mjpeg")
      .on("error", (err) => {
        console.error("FFmpeg error:", err.message);
      })
      .pipe(res, { end: true });
  } catch (err) {
    console.error("Error generating thumbnail:", err);
    res.status(500).json({ error: "Error generating thumbnail" });
  }
};

// Start video processing using child_process
const startVideoProcess = async (req, res) => {
  const { filename } = req.params;
  const { targetColor, threshold } = req.query;

  // Check if params are missing
  if (!targetColor || !threshold) {
    return res
      .status(400)
      .json({ error: "Missing targetColor or threshold query parameter." });
  }

  // Validate hex color
  const hexColorRegex = /^[0-9A-Fa-f]{6}$/;
  if (!hexColorRegex.test(targetColor)) {
    return res.status(400).json({
      error: "targetColor must be a valid 6-digit hex code (e.g., FF0000)",
    });
  }

  // Validate threshold number
  const thresholdNum = Number(threshold);
  if (Number.isNaN(thresholdNum) || thresholdNum < 0 || thresholdNum > 255) {
    return res
      .status(400)
      .json({ error: "Threshold must be a number between 0 and 255." });
  }

  try {
    const files = await fs.readdir(VIDEO_DIR);

    if (!files.includes(filename)) {
      return res.status(404).json({ error: "Video file not found on the server." });
    }

    const jobId = randomUUID();
    jobStatus[jobId] = { status: "processing" };

    const videoPath = path.join(VIDEO_DIR, filename); // inside container we mount /videos

    const child = spawn(
      "java",
      [
        "-jar",
        JAR_PATH,
        videoPath,
        targetColor,
        thresholdNum.toString(),
        jobId,
      ],
      {
        stdio: ["ignore", "pipe", "pipe"],
      }
    );

    child.stdout.on("data", (data) => {
      const text = data.toString();
      console.log(`[JAR OUTPUT ${jobId}]: ${text.trim()}`);
    });

    child.on("exit", (code) => {
      jobStatus[jobId] = { status: code === 0 ? "done" : "error" };
    });

    child.on("error", (err) => {
      jobStatus[jobId] = {
        status: "error",
        error: `Error processing video: ${err.message}`,
      };
    });

    res.status(202).json({ jobId });
  } catch (err) {
    console.error("Error starting job:", err);
    res.status(500).json({ error: "Error starting job" });
  }
};

// Logic for getting job status
const getJobStatus = (req, res) => {
  const { jobId } = req.params;
  const job = jobStatus[jobId];

  if (!job) {
    return res.status(404).json({ error: "Job ID not found" });
  }

  if (job.status === "processing") {
    return res.status(200).json({ status: "processing" });
  } else if (job.status === "done") {
    return res.status(200).json({ status: "done" });
  } else if (job.status === "error") {
    return res.status(200).json({ status: "error", error: job.error });
  }

  return res.status(500).json({ error: "Error fetching job status" });
};

// Store multer upload in Video Path
const storage = multer.diskStorage({
  destination: path.resolve(VIDEO_DIR),
  filename: (req, file, cb) => {
    cb(null, file.originalname);
  },
});

const upload = multer({ storage });

const UploadVideo = (req, res) => {
  res.send(`Uploaded to /videos/${req.file.filename}`);
};

export default {
  getVideos,
  getThumbnail,
  startVideoProcess,
  getJobStatus,
  UploadVideo,
  upload,
};
