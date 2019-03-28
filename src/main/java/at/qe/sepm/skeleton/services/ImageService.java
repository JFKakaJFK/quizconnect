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
     * Resizes an image and stores result to output as JPG
     *
     * @throws IOException
     */
    Path resizeToJPG(Path input, Path output, final int width, final int height) throws IOException;

    /**
     * Resizes an image and stores result to output as PNG
     *
     * @throws IOException
     */
    Path resizeToPNG(Path input, Path output, final int width, final int height) throws IOException;
}
