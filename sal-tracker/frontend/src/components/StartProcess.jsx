"use client";
import { useState, useEffect } from "react";
import { Box, Button, Typography, LinearProgress } from "@mui/material";
import CompletedJobs from "./CompletedJobs";
import withVideoProcessing from "./withVideoProcessing";

const StartProcess = ({ filename, color, threshold, status, setStatus, error, jobId, start }) => {
  const [completedJobs, setCompletedJobs] = useState([]);

  useEffect(() => {
    if (!jobId) return;

    // intervally call backend to fetch progess of job
    const interval = setInterval(async () => {
      try {
        const res = await fetch(`http://localhost:3000/process/${jobId}/status`);
        const data = await res.json();

        // if job is done, stop calling 
        if (data.status === "done") {
          setCompletedJobs((prev) => [...prev, { jobId, filename }]);
          setStatus("done");
          clearInterval(interval);
        } else if (data.status === "error") {
          // Handle backend reporting an error
          setStatus("idle");
          clearInterval(interval);
          console.error("Job failed:", data.error);
        }
      } catch (err) {
        console.error("Failed to check job status:", err);
        setStatus("idle"); // Reset to idle so user can retry
        clearInterval(interval);
      }
    }, 2000);

    return () => clearInterval(interval);
  }, [jobId, filename, setStatus]);

  return (
    <Box sx={{ marginTop: 6, textAlign: "center" }}>
      {/* processing button. if status is processing, button is disabled */}
      <Button
        variant="contained"
        onClick={() => start(filename, color, threshold)}
        disabled={status === "processing"}
        sx={{ backgroundColor: "lightblue", color: "black" }}
      >
        {status === "processing" ? "Processing..." : "Start Process"}
      </Button>

      {/* If job is still processing, render MUI progress bar */}
      {status === "processing" && (
        <Box sx={{ width: "100%", mt: 2 }}>
          <LinearProgress />
        </Box>
      )}


      {/* Return error if error in job processing */}
      {error && (
        <Typography sx={{ mt: 2, color: "red" }}>{error}</Typography>
      )}

      {status === "done" && (
        <Box sx={{ mt: 2 }}>
          <Typography>✅ Process complete!</Typography>
        </Box>
      )}

      {/* Render list of completed jobs */}
      <CompletedJobs jobs={completedJobs} />
    </Box>
  );
};

export default withVideoProcessing(StartProcess);
