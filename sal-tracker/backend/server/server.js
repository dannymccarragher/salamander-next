import express from "express";
import router from "./router/router.js";
import dotenv from "dotenv";
import cors from "cors";
import path from "path";
import { fileURLToPath } from "url";

dotenv.config();

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// API routes
app.use("/", router);

// Serve CSV results as static files
const resultDir = process.env.RESULT_PATH || path.join(__dirname, "results");
app.use("/process", express.static(resultDir));

app.listen(PORT, () => {
  console.log(`Backend listening on http://localhost:${PORT}`);
});
