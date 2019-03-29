package at.qe.sepm.skeleton.tests;

import at.qe.sepm.skeleton.services.AWTImageService;
import at.qe.sepm.skeleton.services.UserService;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    @Test
    public void testStoreAvatar() throws IOException {
        File testFile = new File("src/test/resources/testImage.jpg");
        // is there a better way to get the path?
        Path testFolder = folder.newFile("hack").toPath().getParent();

        Path output = imageService.resizeImage(testFile.toPath(), testFolder, 200, 300, "jpg");

        Assert.assertTrue("Stored file exists", Files.exists(output));
        Assert.assertTrue("Stored file has right path", Files.exists(testFolder.resolve(output.getFileName())));
        Assert.assertTrue("Stored file is non empty", output.toFile().length() > 0);
        Assert.assertEquals("File type matches", "jpg", FilenameUtils.getExtension(output.toString()));
    }

}
