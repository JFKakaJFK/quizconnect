package at.qe.sepm.skeleton.tests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Some very basic tests for {@link at.qe.sepm.skeleton.ui.controllers.PlayerStatsController}.
 *
 * @author Simon Triendl & Johanens Koch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PlayerStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testNoUser() throws Exception {
        this.mockMvc.perform(post("/players/stats"))
                .andExpect(status().is3xxRedirection());
    }
}
