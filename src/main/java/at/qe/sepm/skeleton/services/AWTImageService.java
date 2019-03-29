package at.qe.sepm.skeleton.services;

import at.qe.sepm.skeleton.ui.controllers.ImageController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class AWTImageService implements ImageService {

    private static final Logger log = LoggerFactory.getLogger(ImageController.class);
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
        String ext;
        if(extension.toLowerCase().equals(JPG)){
            ext = JPG;
        } else if(extension.toLowerCase().equals(PNG)){
            ext = PNG;
        } else {
            log.error("Invalid image type");
            return null;
        }
        try {
            BufferedImage original = ImageIO.read(input.toFile());
            BufferedImage resized = scaleImage(original, width, height);

            Path result = Files.createTempFile(output, "", "_" + resized.getWidth() + "x" + resized.getHeight() + "." + ext);
            ImageIO.write(resized, ext, result.toFile());

            return result;
        } catch (IOException e){
            log.error("");
            return null;
        }
    }

    /**
     * Apparently if one uses the faster bilinear interpolation the best quality is achieved by scaling down by at most 50%
     * iteratively to the desired size
     *
     * Source: https://stackoverflow.com/a/32266733/6244663
     *
     * @param input
     * @param width
     * @param height
     * @return
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

        if(g == null) { g.dispose(); }

        if(width != result.getWidth() || height != result.getHeight()){
            int x = 0;
            int y = 0;
            if(aspectRatio > 1){
                x = (currentWidth - width) / 2;
            } else {
                y = (currentHeight - height) / 2;
            }
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
