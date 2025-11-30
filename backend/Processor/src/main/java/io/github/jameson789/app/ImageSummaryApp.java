package io.github.jameson789.app;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

/**
 * The Image Summary Application.
 *
 * This application takes three command-line arguments: 1. The path to an input
 * image file (for example, "image.png"). 2. A target hex color in the format
 * RRGGBB (for example, "FF0000" for red). 3. An integer threshold for
 * binarization.
 *
 * The application performs the following steps:
 *
 * 1. Loads the input image. 2. Parses the target color from the hex string into
 * a 24-bit integer. 3. Binarizes the image by comparing each pixel's Euclidean
 * color distance to the target color. A pixel is marked white (1) if its
 * distance is less than the threshold; otherwise, it is marked black (0). 4.
 * Converts the binary array back to a BufferedImage and writes the binarized
 * image to disk as "binarized.png". 5. Finds connected groups of white pixels
 * in the binary image. Pixels are connected vertically and horizontally (not
 * diagonally). For each group, the size (number of pixels) and the centroid
 * (calculated using integer division) are computed. 6. Writes a CSV file named
 * "groups.csv" containing one row per group in the format "size,x,y".
 * Coordinates follow the convention: (x:0, y:0) is the top-left, with x
 * increasing to the right and y increasing downward.
 *
 * Usage: java ImageSummaryApp <input_image> <hex_target_color> <threshold>
 */
public class ImageSummaryApp {

    public static void main(String[] args) {
        if (args.length < 4) {
            throw new IllegalArgumentException(
                    "Usage: java ImageSummaryApp <input_video> <hex_target_color> <threshold> <task_id>");
        }

        String videoPath = args[0];
        String hexTargetColor = args[1];
        String taskId = args[3];
        int targetColor;
        int threshold;

        try {
            targetColor = Integer.parseInt(hexTargetColor, 16);
            threshold = Integer.parseInt(args[2]);
        } catch (Exception e) {
            System.err.println("Error parsing color or threshold.");
            return;
        }

        String resultDir = System.getenv("RESULT_PATH");
        if (resultDir == null || resultDir.isBlank()) {
            resultDir = "../results"; // fallback for local dev
        }

        String baseName = new File(videoPath).getName();
        int dotIndex = baseName.lastIndexOf('.');
        if (dotIndex > 0) {
            baseName = baseName.substring(0, dotIndex);
        }
        String outputFileName = baseName + "_" + taskId + ".csv";
        File outputFile = new File(resultDir, outputFileName);

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath);
                PrintWriter writer = new PrintWriter(outputFile)) {

            grabber.start();
            Java2DFrameConverter converter = new Java2DFrameConverter();
            ImageProcessor processor = new ImageProcessor(targetColor, threshold);

            double fps = grabber.getFrameRate();
            // Convert microseconds to seconds
            double durationInSeconds = grabber.getLengthInTime() / 1000000.0;
            System.out.printf("Video duration: %.2f seconds%n", durationInSeconds);

            // Process one frame per second
            for (int second = 0; second < (int) durationInSeconds; second++) {
                // Calculate frame number and cast to int
                int frameNumber = (int) (second * fps);
                grabber.setFrameNumber(frameNumber);

                var frame = grabber.grabImage();
                if (frame != null) {
                    BufferedImage image = converter.getBufferedImage(frame);
                    CentroidResult result = processor.processImage(image);

                    if (result != null) {
                        writer.printf("%d,%d,%d%n", second, result.x(), result.y());
                    }
                }
            }

            grabber.stop();
            System.out.println("Processing complete. Output: " + outputFileName);

        } catch (Exception e) {
            System.err.println("Error processing video.");
            e.printStackTrace();
        }
    }
}