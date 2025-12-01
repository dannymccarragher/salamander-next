package io.github.jameson789.app;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class DfsBinaryGroupFinderTest {

    @Test
    public void testSingleFullGroup() {
        int[][] image = {
                {1, 1},
                {1, 1}
        };
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        List<Group> groups = finder.findConnectedGroups(image);

        assertEquals(1, groups.size());
        Group group = groups.get(0);
        assertEquals(4, group.size());
        assertEquals(0, group.centroid().x()); // (0+1+0+1)/4 = 2/4 = 0
        assertEquals(0, group.centroid().y()); // (0+0+1+1)/4 = 2/4 = 0
    }

    @Test
    public void testDisconnectedGroups() {
        int[][] image = {
                {1, 0, 1},
                {0, 0, 0},
                {1, 1, 0}
        };
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        List<Group> groups = finder.findConnectedGroups(image);

        assertEquals(3, groups.size());

        // Largest group is (2,0) and (2,1)
        Group largest = groups.get(0);
        assertEquals(2, largest.size());
        assertEquals(0, largest.centroid().x()); // (0+1)/2 = 0
        assertEquals(2, largest.centroid().y()); // (2+2)/2 = 2

        // Next group is (0,2)
        Group middle = groups.get(1);
        assertEquals(1, middle.size());
        assertEquals(2, middle.centroid().x());
        assertEquals(0, middle.centroid().y());

        // Last group is (0,0)
        Group smallest = groups.get(2);
        assertEquals(1, smallest.size());
        assertEquals(0, smallest.centroid().x());
        assertEquals(0, smallest.centroid().y());
    }

    @Test
    public void testVerticalOnlyGroup() {
        int[][] image = {
                {0, 1},
                {0, 1},
                {0, 1}
        };
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        List<Group> groups = finder.findConnectedGroups(image);

        assertEquals(1, groups.size());
        Group group = groups.get(0);
        assertEquals(3, group.size());
        assertEquals(1, group.centroid().x()); // x = col = 1
        assertEquals(1, group.centroid().y()); // y = (0+1+2)/3 = 1
    }

    @Test
    public void testHorizontalOnlyGroup() {
        int[][] image = {
                {1, 1, 1}
        };
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        List<Group> groups = finder.findConnectedGroups(image);

        assertEquals(1, groups.size());
        Group group = groups.get(0);
        assertEquals(3, group.size());
        assertEquals(1, group.centroid().x()); // (0+1+2)/3 = 1
        assertEquals(0, group.centroid().y()); // only row 0
    }

    @Test
    public void testNoDiagonalConnection() {
        int[][] image = {
                {1, 0},
                {0, 1}
        };
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        List<Group> groups = finder.findConnectedGroups(image);

        assertEquals(2, groups.size());

        Group first = groups.get(0); // tie: both size 1, but lower y comes first
        assertEquals(1, first.size());
        assertEquals(1, first.centroid().x());
        assertEquals(1, first.centroid().y());

        Group second = groups.get(1);
        assertEquals(1, second.size());
        assertEquals(0, second.centroid().x());
        assertEquals(0, second.centroid().y());
    }

    @Test
    public void testImageWithNoGroups() {
        int[][] image = {
                {0, 0},
                {0, 0}
        };
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        List<Group> groups = finder.findConnectedGroups(image);
        assertTrue(groups.isEmpty());
    }

    @Test
    public void testNonRectangularInputThrowsException() {
        int[][] image = {
                {1, 0},
                {1}
        };
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        assertThrows(IllegalArgumentException.class, () -> {
            finder.findConnectedGroups(image);
        });
    }

    @Test
    public void testNullInputThrowsException() {
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        assertThrows(IllegalArgumentException.class, () -> {
            finder.findConnectedGroups(null);
        });
    }

    @Test
    public void testNullRowThrowsException() {
        int[][] image = {
                {1, 0},
                null
        };
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        assertThrows(IllegalArgumentException.class, () -> {
            finder.findConnectedGroups(image);
        });
    }
}
