package io.github.jameson789.app;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.util.List;
import java.util.ArrayList;

public class BinarizingImageGroupFinderTest {

    class StubImageBinarizer implements ImageBinarizer {
        private final int[][] binary;

        public StubImageBinarizer(int[][] binary) {
            this.binary = binary;
        }

        @Override
        public int[][] toBinaryArray(BufferedImage image) {
            return binary;
        }

        @Override
        public BufferedImage toBufferedImage(int[][] image) {
            return null;
        }
    }

    class StubGroupFinder implements BinaryGroupFinder {
        private final List<Group> groups;

        public StubGroupFinder(List<Group> groups) {
            this.groups = groups;
        }

        @Override
        public List<Group> findConnectedGroups(int[][] image) {
            return groups;
        }
    }

    @Test
    public void testFindConnectedGroups_basic() {
        int[][] binary = {
            {1, 0},
            {0, 1}
        };

        List<Group> expectedGroups = new ArrayList<>();
        expectedGroups.add(new Group(1, new Coordinate(0, 0)));
        expectedGroups.add(new Group(1, new Coordinate(1, 1)));

        ImageBinarizer binarizer = new StubImageBinarizer(binary);
        BinaryGroupFinder groupFinder = new StubGroupFinder(expectedGroups);
        BinarizingImageGroupFinder finder = new BinarizingImageGroupFinder(binarizer, groupFinder);

        List<Group> result = finder.findConnectedGroups(dummyImage(2, 2));

        assertEquals(2, result.size());
        assertEquals(expectedGroups, result);
    }

    @Test
    public void testFindConnectedGroups_empty() {
        int[][] binary = new int[0][0];
        List<Group> expectedGroups = new ArrayList<>();

        ImageBinarizer binarizer = new StubImageBinarizer(binary);
        BinaryGroupFinder groupFinder = new StubGroupFinder(expectedGroups);
        BinarizingImageGroupFinder finder = new BinarizingImageGroupFinder(binarizer, groupFinder);

        List<Group> result = finder.findConnectedGroups(dummyImage(1, 1)); // dummy image must be > 0x0

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testFindConnectedGroups_nullBinarizerThrows() {
        ImageBinarizer binarizer = new ImageBinarizer() {
            public int[][] toBinaryArray(BufferedImage image) { return null; }
            public BufferedImage toBufferedImage(int[][] image) { return null; }
        };

        BinaryGroupFinder groupFinder = new StubGroupFinder(new ArrayList<>());
        BinarizingImageGroupFinder finder = new BinarizingImageGroupFinder(binarizer, groupFinder);

        assertThrows(NullPointerException.class, () -> {
            finder.findConnectedGroups(dummyImage(1, 1));
        });
    }

    @Test
    public void testFindConnectedGroups_nullGroupFinderThrows() {
        int[][] binary = {{1}};
        ImageBinarizer binarizer = new StubImageBinarizer(binary);

        BinaryGroupFinder groupFinder = new BinaryGroupFinder() {
            public List<Group> findConnectedGroups(int[][] image) { return null; }
        };

        BinarizingImageGroupFinder finder = new BinarizingImageGroupFinder(binarizer, groupFinder);

        assertThrows(NullPointerException.class, () -> {
            finder.findConnectedGroups(dummyImage(1, 1));
        });
    }

    @Test
    public void testLargeBinaryImage() {
        int[][] binary = new int[50][50];
        binary[0][0] = 1;
        binary[49][49] = 1;

        List<Group> expected = List.of(
            new Group(1, new Coordinate(0, 0)),
            new Group(1, new Coordinate(49, 49))
        );

        ImageBinarizer binarizer = new StubImageBinarizer(binary);
        BinaryGroupFinder groupFinder = new StubGroupFinder(expected);
        BinarizingImageGroupFinder finder = new BinarizingImageGroupFinder(binarizer, groupFinder);

        List<Group> result = finder.findConnectedGroups(dummyImage(50, 50));
        assertEquals(2, result.size());
    }

    @Test
    public void testHugeImage1920x1080_withSparseWhitePixels() {
        int width = 1920;
        int height = 1080;
        int[][] binary = new int[height][width]; // [y][x]

        // Add some sparse white pixels
        binary[100][100] = 1;
        binary[500][1500] = 1;
        binary[1079][1919] = 1;

        List<Group> expected = List.of(
            new Group(1, new Coordinate(100, 100)),
            new Group(1, new Coordinate(1500, 500)),
            new Group(1, new Coordinate(1919, 1079))
        );

        ImageBinarizer binarizer = new StubImageBinarizer(binary);
        BinaryGroupFinder groupFinder = new DfsBinaryGroupFinder(); // Real logic

        BinarizingImageGroupFinder finder = new BinarizingImageGroupFinder(binarizer, groupFinder);
        List<Group> result = finder.findConnectedGroups(dummyImage(width, height));

        assertEquals(3, result.size());

        // Optional: validate coordinates of centroids
        for (Group g : result) {
            assertEquals(1, g.size());
        }

        // These specific centroids might be in any order depending on DFS traversal,
        // so we just make sure they all exist in the results
        for (Group expectedGroup : expected) {
            assertTrue(result.contains(expectedGroup), "Expected group not found: " + expectedGroup);
        }
    }


    @Test
    public void testAllBlackPixelsReturnsEmptyList() {
        int[][] binary = {
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}
        };

        ImageBinarizer binarizer = new StubImageBinarizer(binary);
        BinaryGroupFinder groupFinder = new DfsBinaryGroupFinder(); // uses real logic

        BinarizingImageGroupFinder finder = new BinarizingImageGroupFinder(binarizer, groupFinder);
        List<Group> result = finder.findConnectedGroups(dummyImage(3, 3));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testOddDimensionImage() {
        int[][] binary = {
            {1, 0, 1, 0, 1, 0, 1},
            {0, 1, 0, 1, 0, 1, 0},
            {1, 0, 1, 0, 1, 0, 1},
            {0, 1, 0, 1, 0, 1, 0},
            {1, 0, 1, 0, 1, 0, 1}
        };

        ImageBinarizer binarizer = new StubImageBinarizer(binary);
        BinaryGroupFinder groupFinder = new DfsBinaryGroupFinder();

        BinarizingImageGroupFinder finder = new BinarizingImageGroupFinder(binarizer, groupFinder);
        List<Group> result = finder.findConnectedGroups(dummyImage(7, 5));

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testSingleMassiveConnectedGroup() {
        int[][] binary = new int[20][20]; // make larger for stress
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                binary[i][j] = 1;
            }
        }

        ImageBinarizer binarizer = new StubImageBinarizer(binary);
        BinaryGroupFinder groupFinder = new DfsBinaryGroupFinder(); // real logic

        BinarizingImageGroupFinder finder = new BinarizingImageGroupFinder(binarizer, groupFinder);
        List<Group> result = finder.findConnectedGroups(dummyImage(20, 20));

        assertEquals(1, result.size());
        assertEquals(400, result.get(0).size()); // 20 x 20
    }

    @Test
    public void testCheckerboardPattern_noDiagonalConnections() {
        int size = 6;
        int[][] binary = new int[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                binary[y][x] = (x + y) % 2;
            }
        }

        ImageBinarizer binarizer = new StubImageBinarizer(binary);
        BinaryGroupFinder groupFinder = new DfsBinaryGroupFinder(); // real logic

        BinarizingImageGroupFinder finder = new BinarizingImageGroupFinder(binarizer, groupFinder);
        List<Group> result = finder.findConnectedGroups(dummyImage(size, size));

        assertEquals(18, result.size()); // 18 ones that are NOT connected diagonally
        for (Group g : result) {
            assertEquals(1, g.size());
        }
    }

    @Test
    public void testNonRectangularBinaryArray_throwsException() {
        int[][] binary = new int[3][];
        binary[0] = new int[]{1, 0, 1};
        binary[1] = new int[]{0};
        binary[2] = new int[]{1, 0, 1};

        ImageBinarizer binarizer = new StubImageBinarizer(binary);
        BinaryGroupFinder groupFinder = new DfsBinaryGroupFinder();

        BinarizingImageGroupFinder finder = new BinarizingImageGroupFinder(binarizer, groupFinder);

        assertThrows(IllegalArgumentException.class, () -> {
            finder.findConnectedGroups(dummyImage(3, 3));
        });
    }

    private BufferedImage dummyImage(int width, int height) {
        int[] pixels = new int[width * height];
        DataBufferInt dataBuffer = new DataBufferInt(pixels, pixels.length);
        WritableRaster raster = WritableRaster.createPackedRaster(dataBuffer, width, height, width, new int[]{0xFF0000, 0xFF00, 0xFF}, null);
        ColorModel colorModel = new DirectColorModel(32, 0xFF0000, 0xFF00, 0xFF);
        return new BufferedImage(colorModel, raster, false, null);
    }
}
