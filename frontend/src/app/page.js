"use client";
import {
  Box,
  Container,
  Typography,
  Paper,
  Button,
  Grid,
  Card,
  CardContent,
  Divider,
} from "@mui/material";
import MovieIcon from "@mui/icons-material/Movie";
import AnalyticsIcon from "@mui/icons-material/Analytics";
import AutoFixHighIcon from "@mui/icons-material/AutoFixHigh";
import DownloadIcon from "@mui/icons-material/Download";
import Link from "next/link";
import NavBar from "@/components/navBar";

export default function Home() {
  const features = [
    {
      icon: <MovieIcon sx={{ fontSize: 40 }} />,
      title: "Upload Video",
      description: "Select your video file from the available list",
    },
    {
      icon: <AutoFixHighIcon sx={{ fontSize: 40 }} />,
      title: "Set Parameters",
      description: "Choose target color and threshold for tracking",
    },
    {
      icon: <AnalyticsIcon sx={{ fontSize: 40 }} />,
      title: "Process",
      description: "Our system analyzes centroid movement frame by frame",
    },
    {
      icon: <DownloadIcon sx={{ fontSize: 40 }} />,
      title: "Get Results",
      description: "Download CSV file with precise movement coordinates",
    },
  ];

  return (
    <Box sx={{ bgcolor: "#f5f5f5", minHeight: "100vh" }}>
      <NavBar />
      {/* Hero Section */}
      <Box
        sx={{
          bgcolor: "primary.main",
          color: "white",
          py: 8,
          mb: 6,
        }}
      >
        <Container maxWidth="lg">
          <Grid container spacing={4} alignItems="center">
            <Grid item xs={12} md={6}>
              <Typography
                variant="h2"
                component="h1"
                gutterBottom
                sx={{
                  fontWeight: 700,
                  fontSize: { xs: "2.5rem", md: "3.5rem" },
                }}
              >
                Salamander Video Processor
              </Typography>
              <Typography
                variant="h5"
                sx={{
                  marginBottom: 4,
                  opacity: 0.9,
                }}
              > 
                Track and analyze object movement in videos with precision
              </Typography>
              <Button
                variant="contained"
                size="large"
                component={Link}
                href="/videos"
                sx={{
                  bgcolor: "white",
                  color: "primary.main",
                  "&:hover": {
                    bgcolor: "grey.100",
                  },
                }}
              >
                Start Processing
              </Button>
            </Grid>
            <Grid item xs={12} md={6}>
              {/* Add an illustration or animation here */}
            </Grid>
          </Grid>
        </Container>
      </Box>

      {/* How It Works Section */}
      {/* md breakpoint is 900 px */}
      <Container maxWidth="md" sx={{ mb: 8 }}>
        {" "}
        {/* Changed from sm to md */}
        <Typography
          variant="h3"
          component="h2"
          align="center"
          gutterBottom
          sx={{
            fontWeight: 600,
            mb: 6,
          }}
        >
          How It Works
        </Typography>
        <Box
          sx={{
            display: "grid",
            gridTemplateColumns: "repeat(2, 1fr)",
            gap: 4,
            maxWidth: "900px",
            margin: "0 auto",
          }}
        >
          {features.map((step, index) => (
            <Card
              key={index}
              sx={{
                height: "280px",
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                justifyContent: "center",
                padding: 3, 
                textAlign: "center",
                transition: "0.3s",
                "&:hover": {
                  //Hover element on each card
                  transform: "translateY(-5px)",
                  boxShadow: 3,
                },
              }}
            >
              <Box
                sx={{
                  color: "primary.main",
                  marginBottom: 3,
                }}
              >
                {step.icon}
              </Box>
              <Typography
                variant="h6"
                component="h3"
                gutterBottom
                sx={{ fontSize: "1.2rem" }}
              >
                {step.title}
              </Typography>
              <Typography
                color="text.secondary"
                sx={{
                  fontSize: "1rem", 
                  maxWidth: "95%", 
                }}
              >
                {step.description}
              </Typography>
            </Card>
          ))}
        </Box>
      </Container>

      {/* Footer */}
      <Box
        component="footer"
        sx={{
          bgcolor: "primary.main",
          color: "white",
          padding: 3,
          marginTop: "auto",
        }}
      >
        <Container maxWidth="lg">
          <Typography align="center">
            Salamander Video Processing Tool © {new Date().getFullYear()}
          </Typography>
        </Container>
      </Box>
    </Box>
  );
}
