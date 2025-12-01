package io.github.jameson789.app;

import java.awt.image.BufferedImage;

/**
 * An implementation of the ImageBinarizer interface that uses color distance
 * to determine whether each pixel should be black or white in the binary image.
 * 
 * The binarization is based on the Euclidean distance between a pixel's color and a reference target color.
 * If the distance is less than the threshold, the pixel is considered white (1);
 * otherwise, it is considered black (0).
 * 
 * The color distance is computed using a provided ColorDistanceFinder, which defines how to compare two colors numerically.
 * The targetColor is represented as a 24-bit RGB integer in the form 0xRRGGBB.
 */
public class DistanceImageBinarizer implements ImageBinarizer {
    private final ColorDistanceFinder distanceFinder;
    private final int threshold;
    private final int targetColor;

    /**
     * Constructs a DistanceImageBinarizer using the given ColorDistanceFinder,
     * target color, and threshold.
     * 
     * The distanceFinder is used to compute the Euclidean distance between a pixel's color and the target color.
     * The targetColor is represented as a 24-bit hex RGB integer (0xRRGGBB).
     * The threshold determines the cutoff for binarization: pixels with distances less than
     * the threshold are marked white, and others are marked black.
     *
     * @param distanceFinder an object that computes the distance between two colors
     * @param targetColor the reference color as a 24-bit hex RGB integer (0xRRGGBB)
     * @param threshold the distance threshold used to decide whether a pixel is white or black
     */
    public DistanceImageBinarizer(ColorDistanceFinder distanceFinder, int targetColor, int threshold) {
        this.distanceFinder = distanceFinder;
        this.targetColor = targetColor;
        this.threshold = threshold;
    }

    /**
     * Converts the given BufferedImage into a binary 2D array using color distance and a threshold.
     * Each entry in the returned array is either 0 or 1, representing a black or white pixel.
     * A pixel is white (1) if its Euclidean distance to the target color is less than the threshold.
     *
     * @param image the input RGB BufferedImage
     * @return a 2D binary array where 1 represents white and 0 represents black
     */
    @Override
    public int[][] toBinaryArray(BufferedImage image) {

        //Buffered Image (x = columns, y = rows)

        int height = image.getHeight();
        int width = image.getWidth();

        int[][] result = new int[height][width];

        for(int row = 0; row < height; row++){
            for(int col = 0; col < width; col++){
                //get rgb of current pixel
                int rgb = image.getRGB(col, row);

                double distance = distanceFinder.distance(rgb, targetColor);

                // pixel is white if distance < threshold
                if(distance < threshold){
                    result[row][col] = 1;
                } else{
                    result[row][col] = 0;
                }
            }
        }

        return result;

    }

    /**
     * Converts a binary 2D array into a BufferedImage.
     * Each value should be 0 (black) or 1 (white).
     * Black pixels are encoded as 0x000000 and white pixels as 0xFFFFFF.
     *
     * @param image a 2D array of 0s and 1s representing the binary image
     * @return a BufferedImage where black and white pixels are represented with standard RGB hex values
     */
    @Override
    public BufferedImage toBufferedImage(int[][] image) {
        //set white and black color codes
        int black = 0x000000;
        int white = 0xFFFFFF;

        // get input height and width
        int height = image.length;
        int width = image[0].length;

        // create new BufferImage
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);


        // loop through full 2d array
        for(int row = 0; row < height; row++){
            for(int col = 0; col < width; col++){
                // if binary image is 1, set to white
                if(image[row][col] == 1){
                    resultImage.setRGB(col, row, white);
                    //else set to black
                } else {
                    resultImage.setRGB(col, row, black);
                }
            }
        }

        //return the BufferedImage
        return resultImage;
    }
}
