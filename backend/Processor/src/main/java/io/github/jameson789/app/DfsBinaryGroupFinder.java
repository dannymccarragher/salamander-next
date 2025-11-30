package io.github.jameson789.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DfsBinaryGroupFinder implements BinaryGroupFinder {
   /**
    * Finds connected pixel groups of 1s in an integer array representing a binary image.
    *
    * The input is a non-empty rectangular 2D array containing only 1s and 0s.
    * If the array or any of its subarrays are null, a NullPointerException
    * is thrown. If the array is otherwise invalid, an IllegalArgumentException
    * is thrown.
    *
    * Pixels are considered connected vertically and horizontally, NOT diagonally.
    * The top-left cell of the array (row:0, column:0) is considered to be coordinate
    * (x:0, y:0). Y increases downward and X increases to the right. For example,
    * (row:4, column:7) corresponds to (x:7, y:4).
    *
    * The method returns a list of sorted groups. The group's size is the number
    * of pixels in the group. The centroid of the group
    * is computed as the average of each of the pixel locations across each dimension.
    * For example, the x coordinate of the centroid is the sum of all the x
    * coordinates of the pixels in the group divided by the number of pixels in that group.
    * Similarly, the y coordinate of the centroid is the sum of all the y
    * coordinates of the pixels in the group divided by the number of pixels in that group.
    * The division should be done as INTEGER DIVISION.
    *
    * The groups are sorted in DESCENDING order according to Group's compareTo method
    * (size first, then x, then y). That is, the largest group will be first, the
    * smallest group will be last, and ties will be broken first by descending
    * y value, then descending x value.
    *
    * @param image a rectangular 2D array containing only 1s and 0s
    * @return the found groups of connected pixels in descending order
    */
    @Override
    public List<Group> findConnectedGroups(int[][] image) {
        if (image == null || image.length == 0 || image[0] == null)
        throw new IllegalArgumentException("Invalid image input");

        int expectedWidth = image[0].length;
        List<Group> coordinates = new ArrayList<>();

        // search entire image looking for connections of 1's
        for(int row = 0; row < image.length; row++){
            if (image[row] == null || image[row].length != expectedWidth) {
                throw new IllegalArgumentException("Input binary array is not rectangular.");
            }

            for(int col = 0; col < image[0].length; col++){
                if(image[row][col] == 1){
                    List<int[]> coordinate  = new ArrayList<>();
                    findConnectedGroupsDFS(image, row, col, coordinate);

                    // get size of group and x / y sum for calculating centroid
                    int size = coordinate.size();
                    int xSum = 0;
                    int ySum = 0;

                    for(int[] cord : coordinate){
                        int y = cord[0];
                        int x = cord[1];

                        xSum += x;
                        ySum += y;
                    }

                    //x = sum of all x coords in group / size
                    //y = sum of all y coords in group / size
                    Coordinate centroid = new Coordinate(xSum / size, ySum / size);
                    coordinates.add(new Group(size, centroid));
                }
            }
        }

        //sort coordinates in descending order (reverses compareTo method)
        coordinates.sort(Collections.reverseOrder());
        return coordinates;
    }

    public void findConnectedGroupsDFS(int[][] image, int row, int col, List<int[]> coordinates) {

        // base case for edge of image and if pixel is not a 1
        if (row < 0 || row >= image.length || col < 0 || image[row] == null || col >= image[row].length || image[row][col] != 1) {
            return;
        }

        coordinates.add(new int[]{row, col});

        // set the current location as a 0 to mark as visited
        image[row][col] = 0;

        int[][] directions = {
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1}
        };

        for (int[] direction : directions) {
            findConnectedGroupsDFS(image, row + direction[0], col + direction[1], coordinates);
        }

    }

}
