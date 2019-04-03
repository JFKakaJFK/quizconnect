package at.qe.sepm.skeleton.tests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.qe.sepm.skeleton.ui.controllers.ImageAPIController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    public void badAvatarRequestShouldReturnDefaultTest() throws Exception {
        this.mockMvc.perform(get("/avatars/bad/request.png")).andDo(print())
                .andExpect(status().isOk());
    }
}
