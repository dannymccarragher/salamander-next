package io.github.jameson789.app;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class ImageSummaryAppTest {

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setErr(new PrintStream(errContent));

        // Clean up any existing output files
        new File("frame_centroids.csv").delete();
        deleteDirectory(new File("binarized_frames"));
        new File("dummyTask.csv").delete();
    }

    @AfterEach
    void tearDown() {
        // Clean up output files after tests
        new File("frame_centroids.csv").delete();
        deleteDirectory(new File("binarized_frames"));
        new File("dummyTask.csv").delete();
    }

    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    @Test
    void testInvalidArguments() {
        String[] args = { "ensantina.mp4", "FF0000" }; // Missing threshold and task_id
        assertThrows(IllegalArgumentException.class, () -> ImageSummaryApp.main(args));
    }

    @Test
    void testInvalidHexColor() {
        assertDoesNotThrow(() -> {
            ImageSummaryApp.main(new String[] { "video.mp4", "ZZZZZZ", "180", "dummyTask" });
        });
    }

    @Test
    void testInvalidColorFormat() {
        assertDoesNotThrow(() -> {
            ImageSummaryApp.main(new String[] { "video.mp4", "#FF0000", "180", "dummyTask" });
        });
    }
}
