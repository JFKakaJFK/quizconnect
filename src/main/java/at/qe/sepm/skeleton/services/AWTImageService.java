package at.qe.sepm.skeleton.services;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class AWTImageService implements ImageService {
    final String JPG = "jpg";
    final String PNG = "png";

    @Override
    public Path resizeToJPG(Path input, Path output, int width, int height) throws IOException {
        BufferedImage original = ImageIO.read(input.toFile());
        int type = original.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : original.getType();
        BufferedImage resized = new BufferedImage(width, height, type);
        Graphics2D g = resized.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();

        Path result = Files.createTempFile(output, "", "_" + width + "x" + height + "." + JPG);
        ImageIO.write(resized, JPG, result.toFile());
        return result;
    }

    @Override
    public Path resizeToPNG(Path input, Path output, int width, int height) throws IOException {
        BufferedImage original = ImageIO.read(input.toFile());
        int type = original.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : original.getType();
        BufferedImage resized = new BufferedImage(width, height, type);
        Graphics2D g = resized.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();

        Path result = Files.createTempFile(output, "", "_" + width + "x" + height + "." + PNG);
        ImageIO.write(resized, PNG, result.toFile());
        return result;
    }
}
