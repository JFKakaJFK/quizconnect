package at.qe.sepm.skeleton.services;

import at.qe.sepm.skeleton.ui.controllers.ImageAPIController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implementation of {@link ImageService} using the built in java AWT library
 *
 * @author Johannes Koch
 */
@Service
public class AWTImageService implements ImageService {

    private static final Logger log = LoggerFactory.getLogger(ImageAPIController.class);
    // Supported img types: https://docs.oracle.com/javase/7/docs/api/javax/imageio/package-summary.html
    // sadly jpg & jpeg have intolerable artifacts and gif file sizes are enormous (and gif -> mp4 probably is a pain with java)
    // private static final String JPEG = "jpeg";
    private static final String JPG = "jpg";
    private static final String PNG = "png";


    /**
     * Takes the path of an image as input and stores the resized image(aspect ratio is kept) to
     * the desired path
     *
     * @param input path to image file
     * @param output path where result will be stored
     * @param width of the resulting image
     * @param height of the resulting image
     * @param extension of the result, either png or jpg
     * @return path to resized image
     */
    @Override
    public Path resizeImage(Path input, Path output, int width, int height, String extension) {
        if(width <= 0 || height <= 0){
            throw new IllegalArgumentException("Dimensions must be positive and greater 0");
        }
        if(!Files.exists(output)){
            throw new IllegalArgumentException("Output path is must be valid");
        }
        String ext;
        if(extension.toLowerCase().equals(JPG)){
            ext = JPG;
        } else if(extension.toLowerCase().equals(PNG)){
            ext = PNG;
        } else {
            throw new IllegalArgumentException("Image extension must be 'jpg' or 'png'");
        }
        try {
            BufferedImage original = ImageIO.read(input.toFile());
            BufferedImage resized = scaleImage(original, width, height);

            Path result = Files.createTempFile(output, "", "_" + resized.getWidth() + "x" + resized.getHeight() + "." + ext);
            ImageIO.write(resized, ext, result.toFile());

            return result;
        } catch (IOException e){
            log.warn("Image resizing failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Apparently if one uses the faster bilinear interpolation the best quality is achieved by scaling down by at most 50%
     * iteratively to the desired size
     *
     * Source: https://stackoverflow.com/a/32266733/6244663
     *
     * @param input The image to resize.
     * @param width The desired with.
     * @param height The desired with.
     * @return The resized BufferedImage.
     */
    private BufferedImage scaleImage(BufferedImage input, int width, int height){
        int type = (input.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage result = input;
        BufferedImage temp = null;
        Graphics2D g = null;
        int currentWidth = input.getWidth();
        int currentHeight = input.getHeight();
        double aspectRatio = ((double) currentWidth) / currentHeight;
        int lastWidth = currentWidth;
        int lastHeight = currentHeight;

        do {
            currentWidth /= 2;
            currentHeight /= 2;
            // scale the image to a size that has at least the required dimensions while keeping the aspect ratio
            if(currentWidth < width || currentHeight < height){
                currentWidth = aspectRatio >= 1 ? (int) (aspectRatio * width) : width;
                currentHeight = aspectRatio <= 1 ? (int) (((double) height) / aspectRatio) : height;
            }

            if(temp == null){
                temp = new BufferedImage(currentWidth, currentHeight, type);
                g = temp.createGraphics();
            }
            g.setComposite(AlphaComposite.Src);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(result, 0, 0, currentWidth, currentHeight, 0, 0, lastWidth, lastHeight, null);
            lastWidth = currentWidth;
            lastHeight = currentHeight;
            result = temp;
        } while (currentWidth != width && currentHeight != height);

        if(g != null) { g.dispose(); }

        if(width != result.getWidth() || height != result.getHeight()){
            int x = 0;
            int y = 0;
            if(aspectRatio > 1){
                x = (currentWidth - width) / 2;
            } else {
                y = (currentHeight - height) / 2;
            }
            // crop the image to the wanted size
            temp = result.getSubimage(x, y , width, height);
            BufferedImage copy = new BufferedImage(temp.getWidth(), temp.getHeight(), type);
            g = copy.createGraphics();
            g.setComposite(AlphaComposite.Src);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(temp, 0, 0, null);
            g.dispose();
            result = copy;
        }
        return result;
    }
}
