package io.github.jameson789.app;

import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

public class ImageProcessor {
    private final ImageBinarizer binarizer;
    private final ImageGroupFinder groupFinder;

    public ImageProcessor(int targetColor, int threshold) {
        ColorDistanceFinder distanceFinder = new EuclideanColorDistance();
        this.binarizer = new DistanceImageBinarizer(distanceFinder, targetColor, threshold);
        this.groupFinder = new BinarizingImageGroupFinder(binarizer, new DfsBinaryGroupFinder());
    }

    public CentroidResult processImage(BufferedImage image) {
        if (image == null) {
            return null;
        }
        List<Group> groups = groupFinder.findConnectedGroups(image);

        Group largestGroup = null;
        for (Group group : groups) {
            if (largestGroup == null || group.size() > largestGroup.size()) {
                largestGroup = group;
            }
        }

        if (largestGroup == null) {
            return null;
        }

        int x = largestGroup.centroid().x();
        int y = largestGroup.centroid().y();
        return new CentroidResult(x, y);
    }

    public BufferedImage getBinarizedImage(BufferedImage image) {
        return binarizer.toBufferedImage(binarizer.toBinaryArray(image));
    }

}
