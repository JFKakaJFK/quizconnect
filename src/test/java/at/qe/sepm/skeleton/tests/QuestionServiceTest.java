package at.qe.sepm.skeleton.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Tests for the {@link QuestionService}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class QuestionServiceTest {

    @DirtiesContext
    @Test(expected = NullPointerException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveManagerNoUser()
    {

    }
}
