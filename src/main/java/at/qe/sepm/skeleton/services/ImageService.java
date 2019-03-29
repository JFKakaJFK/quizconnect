package at.qe.sepm.skeleton.services;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Service for manipulating images
 *
 * @author Johannes Koch
 */
public interface ImageService {

    /**
     * Resizes an image and stores result to output as either .jpg or .png
     *
     * @throws IOException
     */
    Path resizeImage(Path input, Path output, final int width, final int height, String extension);
}
