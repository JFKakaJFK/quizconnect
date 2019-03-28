package at.qe.sepm.skeleton.tests;


import at.qe.sepm.skeleton.services.FileSystemStorageService;
import org.apache.tomcat.jni.Directory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Null;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Some very basic tests for {@link at.qe.sepm.skeleton.services.FileSystemStorageService}.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Softwaredevelopment and Project Management" offered by the University
 * of Innsbruck.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class FileSystemStorageTest {
    // TODO: find a way to get something of type uploadedfile and to load custom teststorage properties, before that
    // only tests which load/delete an existing file in storageproperties.uploaddir can be tested

    @Autowired
    FileSystemStorageService fileSystemStorageService;

    @Value("${storage.uploads.location}")
    private String location;
    @Value("${storage.uploads.temporary}")
    private String temp;
    @Value("${storage.thumbnails.location}")
    private String thumbs;
    @Value("${storage.prefixes.avatar}")
    private String avatarPrefix;
    @Value("${storage.prefixes.answer}")
    private String answerPrefix;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testStoreAvatar() throws IOException {
        File testFile = new File("src/test/resources/testImage.jpg");
        String manager = "manager";
        String rightPath = Paths.get(thumbs).resolve(manager + "/" + avatarPrefix).toString();
        String filename = testFile.getName();
        InputStream is = new FileInputStream(testFile);


        String stored = fileSystemStorageService.storeAvatar(is, filename, manager);

        Assert.assertTrue("Stored file exists", Files.exists(Paths.get(stored)));
        Assert.assertTrue("Stored file has right path", stored.contains(rightPath));
        Assert.assertTrue("Stored file is non empty", Paths.get(stored).toFile().length() > 0);
    }

    @Test
    public void testStoreAnswer() throws IOException {
        File testFile = new File("src/test/resources/testImage.jpg");
        String manager = "manager";
        // Not the right Path answers get stored in manage/answers
        String rightPath = Paths.get(location).resolve(manager + "/" + answerPrefix).toString();
        String filename = testFile.getName();
        InputStream inputStream = new FileInputStream(testFile);

        String stored = fileSystemStorageService.storeAnswer(inputStream, filename, manager);

        Assert.assertTrue("Stored answer exists", Files.exists(Paths.get(location).resolve(stored)));
        Assert.assertTrue("Stored answer has right path", Paths.get(location).resolve(stored).toString().contains(rightPath));
        Assert.assertTrue("Stored file is non empty", Paths.get(location).resolve(stored).toString().length() > 0);

    }

    @Test(expected = NullPointerException.class)
    public void testEmptyAvatar() throws IOException {
        File testFile = folder.newFile("testPic.jpg");
        String manager = "manager";
        String filename = testFile.getName();
        InputStream is = new FileInputStream(testFile);

        fileSystemStorageService.storeAvatar(is, filename, manager);
    }

    @Test
    public void testInit() throws IOException {
        Path locationPath = Paths.get(location);
        Path tempPath = Paths.get(temp);
        Path thumbsPath = Paths.get(thumbs);

        fileSystemStorageService.init();

        Assert.assertTrue("Directory at locations exists", Files.exists(locationPath));
        Assert.assertTrue("Directory at temp exists", Files.exists(tempPath));
        Assert.assertTrue("Directory at thumbs exists", Files.exists(thumbsPath));
    }
    @Test
    public void testDeleteFile() throws IOException {
        File testFile = folder.newFile("testFile.txt");
        String pathToFile = testFile.getPath();

        fileSystemStorageService.deleteFile(pathToFile);

        Assert.assertFalse("File was deleted", testFile.exists());
    }
}
