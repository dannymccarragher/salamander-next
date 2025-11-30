package io.github.jameson789.app;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EuclideanColorDistanceTest {

    // === Real Implementation Tests ===

    @Test
    public void testSameColorReturnsZero() {
        EuclideanColorDistance finder = new EuclideanColorDistance();
        int color = 0x112233;
        assertEquals(0.0, finder.distance(color, color), 0.0001);
    }

    @Test
    public void testBlackAndWhiteDistance() {
        EuclideanColorDistance finder = new EuclideanColorDistance();
        int black = 0x000000;
        int white = 0xFFFFFF;
        double expected = Math.sqrt(255 * 255 * 3);
        assertEquals(expected, finder.distance(black, white), 0.0001);
    }

    @Test
    public void testPrimaryColorDistance() {
        EuclideanColorDistance finder = new EuclideanColorDistance();
        int red = 0xFF0000;
        int green = 0x00FF00;
        double expected = Math.sqrt(255 * 255 + 255 * 255);
        assertEquals(expected, finder.distance(red, green), 0.0001);
    }

    @Test
    public void testCustomColorDistance() {
        EuclideanColorDistance finder = new EuclideanColorDistance();
        int colorA = 0x123456;
        int colorB = 0x654321;
        double expected = Math.sqrt(Math.pow(0x12 - 0x65, 2)
                                  + Math.pow(0x34 - 0x43, 2)
                                  + Math.pow(0x56 - 0x21, 2));
        assertEquals(expected, finder.distance(colorA, colorB), 0.0001);
    }

    // === Fake Implementation ===

    static class FakeColorDistanceFinder implements ColorDistanceFinder {
        private int lastColorA;
        private int lastColorB;
        private int callCount = 0;

        @Override
        public double distance(int colorA, int colorB) {
            this.lastColorA = colorA;
            this.lastColorB = colorB;
            this.callCount++;
            return 42.0; // fake return value
        }

        public int getLastColorA() {
            return lastColorA;
        }

        public int getLastColorB() {
            return lastColorB;
        }

        public int getCallCount() {
            return callCount;
        }
    }

    // === Consumer class using dependency injection ===

    static class ColorAnalyzer {
        private final ColorDistanceFinder distanceFinder;

        public ColorAnalyzer(ColorDistanceFinder distanceFinder) {
            this.distanceFinder = distanceFinder;
        }

        public boolean areVisuallyDifferent(int colorA, int colorB, double threshold) {
            return distanceFinder.distance(colorA, colorB) > threshold;
        }
    }

    // === Tests using Fake instead of Mockito ===

    @Test
    public void testAnalyzerUsesFakeFinder() {
        FakeColorDistanceFinder fakeFinder = new FakeColorDistanceFinder();
        ColorAnalyzer analyzer = new ColorAnalyzer(fakeFinder);

        boolean result1 = analyzer.areVisuallyDifferent(0xABCDEF, 0x123456, 10);
        boolean result2 = analyzer.areVisuallyDifferent(0xABCDEF, 0x123456, 100);

        assertTrue(result1);
        assertFalse(result2);
        assertEquals(2, fakeFinder.getCallCount());
        assertEquals(0xABCDEF, fakeFinder.getLastColorA());
        assertEquals(0x123456, fakeFinder.getLastColorB());
    }
}
