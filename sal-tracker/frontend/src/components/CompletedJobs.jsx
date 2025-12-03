"use client";
import { Box, Typography, List, ListItem, Button, Link } from "@mui/material";

// display completed processing jobs
export default function CompletedJobs({ jobs }) {
  // Don't render if no jobs
  if (!jobs.length) return null;

  const backendUrl = process.env.NEXT_PUBLIC_BACKEND_URL;

  console.log(backendUrl);

  return (
    <Box sx={{ marginTop: 6 }}>

      {/* Title */}
      <Typography variant="h6">Completed Jobs</Typography>

      {/* Jobs list */}
      <List>
        {jobs.map(({ jobId, filename }) => (
          <ListItem
            key={jobId}
            sx={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
            }}
          >
            {/* Job info */}
            <Typography>{"Video: " + filename + "  -  Job ID: " + jobId}</Typography>

            {/* Download link */}
            <Link
              href={`${backendUrl}/process/${filename.slice(0, filename.length - 4)}_${jobId}.csv`}
              // removes file extension
              download={`${filename.replace(/\.[^/.]+$/, "")}.csv`}
              sx={{ textDecoration: "none" }}
            >
              <Button variant="outlined" size="small">
                Download
              </Button>
            </Link>
          </ListItem>
        ))}
      </List>
    </Box>
  );
}
