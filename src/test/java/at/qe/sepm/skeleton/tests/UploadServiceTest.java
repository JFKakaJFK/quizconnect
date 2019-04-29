package at.qe.sepm.skeleton.tests;

import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.services.ManagerService;
import at.qe.sepm.skeleton.services.UploadService;
import at.qe.sepm.skeleton.services.UserService;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Some very basic tests for {@link UploadService}.
 *
 * @author Simon Triendl & Johanens Koch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class UploadServiceTest {


    private UploadService uploadService;
    private UserService userService;
    private ManagerService managerService;
    private Path temp;
    private File testFile;

    @Autowired
    public void init(UserService userService,
                     ManagerService managerService,
                     UploadService uploadService,
                     @Value("${storage.uploads.temporary}") String temp){
        this.uploadService = uploadService;
        this.temp = Paths.get(temp);
        this.userService = userService;
        this.managerService = managerService;
    }

    @Before
    public void setUp(){
        testFile = new File("src/test/resources/testImage1.jpg");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUploadNullUser(){
        uploadService.getUpload(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClearUploadNullUser(){
        uploadService.clearUploads(null);
    }

    @Test
    @WithMockUser(username = "user3", authorities = { "PLAYER" })
    public void testGetUploadPlayer() throws IOException {
        User u = userService.loadUser("user3");
        Path p = storeFile(u);
        File f = uploadService.getUpload(u);

        Assert.assertNotNull("No file found", f);
        Assert.assertEquals("Retrieved wrong file", p.toFile(), f);

        Files.deleteIfExists(p);
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testGetUploadManager() throws IOException {
        User u = userService.loadUser("user1");
        Path p = storeFile(u);
        File f = uploadService.getUpload(u);

        Assert.assertNotNull("No file found", f);
        Assert.assertEquals("Retrieved wrong file", p.toFile(), f);

        Files.deleteIfExists(p);
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testClearUploads() throws IOException {
        User u = userService.loadUser("user1");
        Path p = storeFile(u);

        Assert.assertTrue("File wasn't stored", p.toFile().exists());

        uploadService.clearUploads(u);

        Assert.assertFalse("File wasn't deleted", p.toFile().exists());
    }

    private Path storeFile(User user) throws IOException {
        int managerId = user.getManager() == null ? managerService.getManagerOfPlayer(user.getPlayer()).getId() : user.getManager().getId();

        Path p;
        try(InputStream is = new FileInputStream(testFile)) {
            p = temp.resolve(String.valueOf(managerId)).resolve(user.getUsername() + "." + FilenameUtils.getExtension(testFile.getName()));
            Files.createDirectories(p);
            Files.copy(is, p, StandardCopyOption.REPLACE_EXISTING);
        }

        return p;
    }
}
