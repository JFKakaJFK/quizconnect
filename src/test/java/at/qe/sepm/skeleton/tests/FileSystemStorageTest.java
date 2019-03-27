package at.qe.sepm.skeleton.tests;


import at.qe.sepm.skeleton.services.FileSystemStorageService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testStoreAvatar() throws IOException {
        File testFile = folder.newFile("testPic.jpg");

        //String stored = fileSystemStorageService.storeAvatar(testFile, "manager");
    }
}
