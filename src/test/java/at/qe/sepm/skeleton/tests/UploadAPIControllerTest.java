package at.qe.sepm.skeleton.tests;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Some very basic tests for {@link at.qe.sepm.skeleton.ui.controllers.UploadAPIController}.
 *
 * @author Simon Triendl & Johanens Koch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UploadAPIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testNoUser() throws Exception {
        this.mockMvc.perform(post("/uploads"))
                .andExpect(status().is3xxRedirection());
        // this would be a 401 but since in the web security config /uploads has
        // restricted access a user is redirected
    }
}
