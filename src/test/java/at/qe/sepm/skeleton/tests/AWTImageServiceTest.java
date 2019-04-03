package at.qe.sepm.skeleton.tests;

import at.qe.sepm.skeleton.services.AWTImageService;
import at.qe.sepm.skeleton.services.UserService;
import com.sun.javaws.exceptions.InvalidArgumentException;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Some very basic tests for {@link UserService}.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Softwaredevelopment and Project Management" offered by the University
 * of Innsbruck.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AWTImageServiceTest {

    @Autowired
    AWTImageService imageService;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File testFile1;
    private File testFile2;
    private Path testFolder;

    @Before
    public void init() throws IOException {
        testFile1 = new File("src/test/resources/testImage1.jpg");
        testFile2 = new File("src/test/resources/testImage2.jpg");
        testFolder = folder.newFile("hack").toPath().getParent();
    }


    @Test
    public void testResizeImagePortraitJPG() throws IOException {
        Path output = imageService.resizeImage(testFile1.toPath(), testFolder, 200, 300, "jpg");

        Assert.assertTrue("Stored file exists", Files.exists(output));
        Assert.assertTrue("Stored file has right path", Files.exists(testFolder.resolve(output.getFileName())));
        Assert.assertTrue("Stored file is non empty", output.toFile().length() > 0);
        Assert.assertEquals("File type matches", "jpg", FilenameUtils.getExtension(output.toString()));
    }

    @Test
    public void testResizeImagePortraitPNG() throws IOException {
        Path output = imageService.resizeImage(testFile1.toPath(), testFolder, 200, 300, "png");

        Assert.assertTrue("Stored file exists", Files.exists(output));
        Assert.assertTrue("Stored file has right path", Files.exists(testFolder.resolve(output.getFileName())));
        Assert.assertTrue("Stored file is non empty", output.toFile().length() > 0);
        Assert.assertEquals("File type matches", "png", FilenameUtils.getExtension(output.toString()));
    }

    @Test
    public void testResizeImageLandscapeJPG() throws IOException {
        Path output = imageService.resizeImage(testFile2.toPath(), testFolder, 200, 300, "jpg");

        Assert.assertTrue("Stored file exists", Files.exists(output));
        Assert.assertTrue("Stored file has right path", Files.exists(testFolder.resolve(output.getFileName())));
        Assert.assertTrue("Stored file is non empty", output.toFile().length() > 0);
        Assert.assertEquals("File type matches", "jpg", FilenameUtils.getExtension(output.toString()));
    }

    @Test
    public void testResizeImageLandscapePNG() throws IOException {
        Path output = imageService.resizeImage(testFile2.toPath(), testFolder, 200, 300, "png");

        Assert.assertTrue("Stored file exists", Files.exists(output));
        Assert.assertTrue("Stored file has right path", Files.exists(testFolder.resolve(output.getFileName())));
        Assert.assertTrue("Stored file is non empty", output.toFile().length() > 0);
        Assert.assertEquals("File type matches", "png", FilenameUtils.getExtension(output.toString()));
    }

    @Test
    public void testSize1() throws IOException {
        Path output = imageService.resizeImage(testFile1.toPath(), testFolder, 10, 50, "jpg");

        Assert.assertTrue("Stored file exists", Files.exists(output));
        Assert.assertTrue("Stored file has right path", Files.exists(testFolder.resolve(output.getFileName())));
        Assert.assertTrue("Stored file is non empty", output.toFile().length() > 0);
        Assert.assertEquals("File type matches", "jpg", FilenameUtils.getExtension(output.toString()));

        BufferedImage img = ImageIO.read(output.toFile());
        Assert.assertEquals("Width matches", 10, img.getWidth());
        Assert.assertEquals("Height matches", 50, img.getHeight());
    }

    @Test
    public void testSize2() throws IOException {
        Path output = imageService.resizeImage(testFile1.toPath(), testFolder, 5000, 1000, "jpg");

        BufferedImage bufferedOutput = ImageIO.read(output.toFile());

        Assert.assertTrue("Stored file exists", Files.exists(output));
        Assert.assertTrue("Stored file has right path", Files.exists(testFolder.resolve(output.getFileName())));
        Assert.assertTrue("Stored file is non empty", output.toFile().length() > 0);
        Assert.assertEquals("File type matches", "jpg", FilenameUtils.getExtension(output.toString()));
        Assert.assertEquals("Width is scaled probably", 200, bufferedOutput.getWidth());
        Assert.assertEquals("Height is scaled probably", 300, bufferedOutput.getHeight());
    }

    @Test
    public void testStoreAvatarWithScale100X205() throws IOException {
        File testFile = new File("src/test/resources/testImage.jpg");
        // is there a better way to get the path?
        Path testFolder = folder.newFile("hack").toPath().getParent();

        Path output = imageService.resizeImage(testFile.toPath(), testFolder, 100, 205, "png");

        BufferedImage bufferedOutput = ImageIO.read(output.toFile());

        Assert.assertTrue("Stored file exists", Files.exists(output));
        Assert.assertTrue("Stored file has right path", Files.exists(testFolder.resolve(output.getFileName())));
        Assert.assertTrue("Stored file is non empty", output.toFile().length() > 0);
        Assert.assertEquals("File type matches", "png", FilenameUtils.getExtension(output.toString()));
        Assert.assertEquals("Width is scaled probably", 100, bufferedOutput.getWidth());
        Assert.assertEquals("Height is scaled probably", 205, bufferedOutput.getHeight());
    }

    @Test
    public void testStoreAvatarInvalidType() throws IOException {
        File testFile = new File("src/test/resources/testImage.jpg");
        // is there a better way to get the path?
        Path testFolder = folder.newFile("hack").toPath().getParent();

        Path output = imageService.resizeImage(testFile.toPath(), testFolder, 100, 205, "gif");


        Assert.assertNull("Invalid Image Type", output);
    }

    @Test (expected = RasterFormatException.class)
    public void testStoreAvatarInvalidWidth() throws IOException {
        File testFile = new File("src/test/resources/testImage.jpg");
        // is there a better way to get the path?
        Path testFolder = folder.newFile("hack").toPath().getParent();

        Path output = imageService.resizeImage(testFile.toPath(), testFolder, -10, 205, "jpg");
    }

    @Test (expected = RasterFormatException.class)
    public void testStoreAvatarInvalidHeight() throws IOException {
        File testFile = new File("src/test/resources/testImage.jpg");
        // is there a better way to get the path?
        Path testFolder = folder.newFile("hack").toPath().getParent();

        Path output = imageService.resizeImage(testFile.toPath(), testFolder, 100, -205, "jpg");
    }

    /*
    @Test
    public void testStoreAvatarInvalidOutputPath() throws IOException {
        File testFile = new File("src/test/resources/testImage.jpg");
        // is there a better way to get the path?
        Path invalidPath = Paths.get("src/test/java");
        //Path testFolder = folder.newFile("hack").toPath().getParent();

        Path output = imageService.resizeImage(testFile.toPath(), invalidPath, 100, 205, "jpg");

        Assert.assertNull("Invalid Path as argument", output);
    }
     */


    @Test
    public void testStoreAvatarInvalidInputPath() throws IOException {
        File testFile = new File("src/test/resources/testImage.jpg");
        // is there a better way to get the path?
        Path invalidPath = Paths.get("src/test/java");
        Path testFolder = folder.newFile("hack").toPath().getParent();

        Path output = imageService.resizeImage(invalidPath, testFolder, 100, 205, "jpg");


        Assert.assertNull("Invalid Image Type", output);

        BufferedImage img = ImageIO.read(output.toFile());
        Assert.assertEquals("Width matches", 5000, img.getWidth());
        Assert.assertEquals("Height matches", 1000, img.getHeight());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZero() throws IOException {
        Path output = imageService.resizeImage(testFile1.toPath(), testFolder, 0, 0, "jpg");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeWidth() throws IOException {
        Path output = imageService.resizeImage(testFile1.toPath(), testFolder, -1, 1, "jpg");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeHeight() throws IOException {
    Path output = imageService.resizeImage(testFile1.toPath(), testFolder, 1, -1, "jpg");
  }
}
