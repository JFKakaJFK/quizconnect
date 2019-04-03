package at.qe.sepm.skeleton.tests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import at.qe.sepm.skeleton.services.StorageService;
import at.qe.sepm.skeleton.ui.controllers.ImageAPIController;
import org.apache.commons.io.FilenameUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileInputStream;

/**
 * Some very basic tests for {@link ImageAPIController}.
 *
 * @author Simon Triendl & Johanens Koch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ImageAPIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StorageService storageService;

    private File testFile;
    private String manager;

    @Value("${storage.api.avatars}")
    private String avatars;
    @Value("${storage.api.answers}")
    private String answers;
    @Value("${storage.avatars.default}")
    private String defaultAvatar;
    @Value("${storage.answers.default}")
    private String defaultAnswer;
    @Value("${storage.avatars.imageType}")
    private String avatarType;
    @Value("${storage.answers.imageType}")
    private String answerType;

    @Autowired
    public void init(
            @Value("${storage.api.avatars}") String avatarEndpoint,
            @Value("${storage.api.answers}") String answerEndpoint){
        this.avatars = "/" + avatarEndpoint;
        this.answers = "/" + answerEndpoint;
        this.testFile = new File("src/test/resources/testImage1.jpg");
        this.manager = "manager";
    }

    @Test
    public void badAvatarRequestShouldReturnDefaultTest() throws Exception {
        String expected = FilenameUtils.getExtension(defaultAvatar);

        this.mockMvc.perform(get(avatars + "bad/request.png"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/" + expected))
                .andExpect(header().string("Cache-Control", "max-age=31104000"));
    }

    @Test
    public void badAnswerRequestShouldReturnDefaultTest() throws Exception {
        String expected = FilenameUtils.getExtension(defaultAnswer);

        this.mockMvc.perform(get(answers + "bad/request.png"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/" + expected))
                .andExpect(header().string("Cache-Control", "max-age=31104000"));
    }

    @Test
    public void badAvatarRequestTest() throws Exception {
        this.mockMvc.perform(get(avatars + "bad/request.quizConnect"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void badAnswerRequestTest() throws Exception {
        this.mockMvc.perform(get(answers + "bad/request.quizConnect"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void storeAndRequestAvatarTest() throws Exception {
        String testAvatar = storageService.storeAvatar(new FileInputStream(testFile), testFile.getName(), manager);

        this.mockMvc.perform(get(avatars + testAvatar))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/" + avatarType))
                .andExpect(header().string("Cache-Control", "max-age=31104000"));

        // TODO: files are not deleted, because the request is tested async and this method is called too early
        storageService.deleteAvatar(testAvatar);
    }

    @Test
    public void storeAndRequestAnswerTest() throws Exception {
        String testAnswer = storageService.storeAnswer(new FileInputStream(testFile), testFile.getName(), manager);

        this.mockMvc.perform(get(answers + testAnswer))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/" + answerType))
                .andExpect(header().string("Cache-Control", "max-age=31104000"));


        storageService.deleteAnswer(testAnswer);
    }
}
