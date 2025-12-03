package io.github.jameson789.app;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ImageProcessorTest {

    @Test
    public void testNullImageReturnsNull() {
        ImageProcessor processor = new ImageProcessor(0xFF0000, 50);
        CentroidResult result = processor.processImage(null);
        assertNull(result, "Null image should return null");
    }

    @Test
    public void testNoMatchingPixelsReturnsNull() {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        fillColor(image, Color.BLUE);

        ImageProcessor processor = new ImageProcessor(0xFF0000, 10); // Red target, blue image
        CentroidResult result = processor.processImage(image);
        assertNull(result, "No red in image → should return null");
    }

    @Test
    public void testAllMatchingPixelsReturnsCenter() {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        fillColor(image, new Color(255, 0, 0)); // Pure red

        ImageProcessor processor = new ImageProcessor(0xFF0000, 255);
        CentroidResult result = processor.processImage(image);

        assertNotNull(result);
        assertEquals(4, result.x(), 1); // Center (approx)
        assertEquals(4, result.y(), 1);
    }

    @Test
    public void testSinglePixelMatch() {
        BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        fillColor(image, Color.BLACK);
        image.setRGB(2, 3, 0xFF0000); // One red pixel

        ImageProcessor processor = new ImageProcessor(0xFF0000, 10);
        CentroidResult result = processor.processImage(image);

        assertNotNull(result);
        assertEquals(2, result.x());
        assertEquals(3, result.y());
    }

    @Test
    public void testBinarizedImageWithNullInputReturnsNullOrFails() {
        ImageProcessor processor = new ImageProcessor(0xFF0000, 50);
        assertThrows(NullPointerException.class, () -> {
            processor.getBinarizedImage(null);
        });
    }

    @Test
    public void testBinarizedImageAllMatch() {
        BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        fillColor(image, new Color(255, 0, 0)); // red

        ImageProcessor processor = new ImageProcessor(0xFF0000, 255);
        BufferedImage binary = processor.getBinarizedImage(image);

        assertNotNull(binary);
        assertEquals(Color.WHITE.getRGB(), binary.getRGB(2, 2)); // middle pixel should be white
    }

    @Test
    public void testBinarizedImageNoneMatch() {
        BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        fillColor(image, new Color(0, 255, 0)); // green

        ImageProcessor processor = new ImageProcessor(0xFF0000, 10); // red target, tight threshold
        BufferedImage binary = processor.getBinarizedImage(image);

        assertNotNull(binary);
        assertEquals(Color.BLACK.getRGB(), binary.getRGB(2, 2)); // middle pixel should be black
    }

    @Test
    public void testBinarizedImageSinglePixelMatch() {
        BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        fillColor(image, Color.BLACK);
        image.setRGB(3, 3, 0xFF0000); // one red pixel

        ImageProcessor processor = new ImageProcessor(0xFF0000, 10);
        BufferedImage binary = processor.getBinarizedImage(image);

        assertNotNull(binary);
        assertEquals(Color.WHITE.getRGB(), binary.getRGB(3, 3));
        assertEquals(Color.BLACK.getRGB(), binary.getRGB(0, 0));
    }

    @Test
    public void testChoosesLargestGroup() {
        BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        fillColor(image, Color.BLACK);
        // Create a small red group (1 pixel)
        image.setRGB(0, 0, 0xFF0000);
        // Create a larger red group (3 pixels)
        image.setRGB(2, 2, 0xFF0000);
        image.setRGB(3, 2, 0xFF0000);
        image.setRGB(2, 3, 0xFF0000);

        ImageProcessor processor = new ImageProcessor(0xFF0000, 10);
        CentroidResult result = processor.processImage(image);

        assertNotNull(result);
        assertEquals(2, result.x());
        assertEquals(2, result.y()); // Center of the larger group
    }

    @Test
    public void testCentroidForUniformImage() {
        BufferedImage image = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        fillColor(image, new Color(255, 0, 0)); // red

        ImageProcessor processor = new ImageProcessor(0xFF0000, 255);
        CentroidResult result = processor.processImage(image);

        assertNotNull(result);
        assertEquals(1, result.x()); // integer division: (0+1+2+3)/4 = 1
        assertEquals(1, result.y());
    }

    @Test
    public void testSinglePixelInCorner() {
        BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        fillColor(image, Color.BLACK);
        image.setRGB(4, 4, 0xFF0000); // bottom-right corner

        ImageProcessor processor = new ImageProcessor(0xFF0000, 10);
        CentroidResult result = processor.processImage(image);

        assertNotNull(result);
        assertEquals(4, result.x());
        assertEquals(4, result.y());
    }

    @Test
    public void testThresholdBoundary() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, new Color(250, 5, 5).getRGB());

        // Slightly off red, close enough if threshold is high
        ImageProcessor processorLow = new ImageProcessor(0xFF0000, 5);
        ImageProcessor processorHigh = new ImageProcessor(0xFF0000, 100);

        assertNull(processorLow.processImage(image));
        assertNotNull(processorHigh.processImage(image));
    }

    private void fillColor(BufferedImage image, Color color) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, color.getRGB());
            }
        }
    }
}
