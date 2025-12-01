import { Router } from "express";
import controller from "../controllers/controller.js";

const router = Router();

router.get("/api/videos", controller.getVideos);
router.get("/thumbnail/:filename", controller.getThumbnail);
router.post("/process/:filename", controller.startVideoProcess);
router.get("/process/:jobId/status", controller.getJobStatus);
router.post("/api/upload", controller.upload.single("video"), controller.UploadVideo);

export default router;
