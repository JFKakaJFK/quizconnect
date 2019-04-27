package at.qe.sepm.skeleton.tests;


import at.qe.sepm.skeleton.services.FileSystemStorageService;
import at.qe.sepm.skeleton.services.ImageService;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.jni.Directory;
import org.junit.Assert;
import org.junit.Before;
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
 * Some very basic tests for {@link FileSystemStorageService}.
 *
 * @author Simon Triendl & Johanens Koch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class FileSystemStorageTest {

    @Autowired
    private FileSystemStorageService fileSystemStorageService;

    @Value("${storage.avatars.imageType}")
    private String avatarType;
    @Value("${storage.answers.imageType}")
    private String answerType;
    private Path temp;
    private Path avatars;
    private Path answers;
    @Value("${storage.avatars.default}")
    private String defaultAvatar;
    @Value("${storage.answers.default}")
    private String defaultAnswer;

    private File testFile;
    private String manager;

    @Autowired
    public void init(
            @Value("${storage.uploads.location}") String root,
            @Value("${storage.uploads.temporary}") String temp,
            @Value("${storage.api.avatars}") String avatarEndpoint,
            @Value("${storage.api.answers}") String answerEndpoint){
        this.temp = Paths.get(temp);
        this.avatars = Paths.get(root).resolve(avatarEndpoint);
        this.answers = Paths.get(root).resolve(answerEndpoint);
    }

    @Before
    public void setUp(){
        testFile = new File("src/test/resources/testImage1.jpg");
        manager = "manager";
    }

    @Test
    public void testInit() throws IOException {
        fileSystemStorageService.init();

        Assert.assertTrue("Directory at locations exists", Files.exists(temp));
        Assert.assertTrue("Directory at temp exists", Files.exists(answers));
        Assert.assertTrue("Directory at thumbs exists", Files.exists(answers));
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testStoreAvatar() throws IOException {
        String rightPath = avatars.toString();
        InputStream is = new FileInputStream(testFile);

        String stored = fileSystemStorageService.storeAvatar(is, testFile.getName(), manager);

        Assert.assertTrue("Stored file exists", Files.exists(avatars.resolve(stored)));
        Assert.assertTrue("Stored file has right path", avatars.resolve(stored).toString().contains(rightPath));
        Assert.assertTrue("Stored file is non empty", avatars.resolve(stored).toFile().length() > 0);
        Assert.assertEquals("Stored file is of right type", avatarType, FilenameUtils.getExtension(stored));

        Files.deleteIfExists(avatars.resolve(stored));
    }

    @Test
    public void testStoreAnswer() throws IOException {
        String rightPath = answers.toString();
        InputStream inputStream = new FileInputStream(testFile);

        String stored = fileSystemStorageService.storeAnswer(inputStream, testFile.getName(), manager);

        Assert.assertTrue("Stored answer exists", Files.exists(answers.resolve(stored)));
        Assert.assertTrue("Stored answer has right path", answers.resolve(stored).toString().contains(rightPath));
        Assert.assertTrue("Stored file is non empty", answers.resolve(stored).toFile().length() > 0);
        Assert.assertEquals("Stored file is of right type", answerType, FilenameUtils.getExtension(stored));

        Files.deleteIfExists(answers.resolve(stored));
    }

    @Test(expected = NullPointerException.class)
    public void testEmptyAvatar() throws IOException {
        File testFile = folder.newFile("testPic.jpg");
        InputStream is = new FileInputStream(testFile);

        fileSystemStorageService.storeAvatar(is, testFile.getName(), manager);
    }

    @Test(expected = NullPointerException.class)
    public void testEmptyAnswer() throws IOException {
        File testFile = folder.newFile("testPic.jpg");
        InputStream is = new FileInputStream(testFile);

        fileSystemStorageService.storeAnswer(is, testFile.getName(), manager);
    }

    @Test
    public void testLoadAvatar() throws IOException {
        InputStream inputStream = new FileInputStream(testFile);

        String stored = fileSystemStorageService.storeAvatar(inputStream,  testFile.getName(), manager);

        Path loaded = fileSystemStorageService.loadAvatar(stored);

        Assert.assertTrue("Retrieved avatar exists", Files.exists(loaded));
        Assert.assertTrue("Retrieved file is non empty", loaded.toFile().length() > 0);
        Assert.assertEquals("Retrieved file is of right type", avatarType, FilenameUtils.getExtension(loaded.toString()));

        Files.deleteIfExists(avatars.resolve(stored));
    }

    @Test
    public void testLoadDefaultAvatar() {
        Path loaded = fileSystemStorageService.loadAvatar("QuizConnect");

        Assert.assertTrue("Default exists", Files.exists(loaded));
        Assert.assertEquals("Default filename matches", defaultAvatar, FilenameUtils.getName(loaded.toString()));
        Assert.assertTrue("Default is non empty", loaded.toFile().length() > 0);
    }

    @Test
    public void testLoadDefaultAnswer() {
        Path loaded = fileSystemStorageService.loadAnswer("QuizConnect");

        Assert.assertTrue("Default exists", Files.exists(loaded));
        Assert.assertEquals("Default filename matches", defaultAnswer, FilenameUtils.getName(loaded.toString()));
        Assert.assertTrue("Default is non empty", loaded.toFile().length() > 0);
    }

    @Test
    public void testLoadAnswer() throws IOException {
        InputStream inputStream = new FileInputStream(testFile);

        String stored = fileSystemStorageService.storeAnswer(inputStream,  testFile.getName(), manager);

        Path loaded = fileSystemStorageService.loadAnswer(stored);

        Assert.assertTrue("Retrieved aanswer exists", Files.exists(loaded));
        Assert.assertTrue("Retrieved file is non empty", loaded.toFile().length() > 0);
        Assert.assertEquals("Retrieved file is of right type", answerType, FilenameUtils.getExtension(loaded.toString()));

        Files.deleteIfExists(answers.resolve(stored));
    }

    @Test
    public void testDeleteAvatar() throws IOException {
        InputStream inputStream = new FileInputStream(testFile);

        String stored = fileSystemStorageService.storeAvatar(inputStream,  testFile.getName(), manager);

        fileSystemStorageService.deleteAvatar(stored);

        Assert.assertFalse("File was deleted", Files.exists(avatars.resolve(stored)));
    }

    @Test
    public void testDeleteAnswer() throws IOException {
        InputStream inputStream = new FileInputStream(testFile);

        String stored = fileSystemStorageService.storeAnswer(inputStream,  testFile.getName(), manager);

        fileSystemStorageService.deleteAnswer(stored);

        Assert.assertFalse("File was deleted", Files.exists(answers.resolve(stored)));
    }
}
