package at.qe.sepm.skeleton.tests;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.services.QuestionSetService;
import at.qe.sepm.skeleton.services.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QuestionSetServiceTest {

    @Autowired
    QuestionSetService questionSetService;

    @Autowired
    UserService userService;

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testGetQuestionSetsOfManager()
    {
        User m = userService.loadUser("user1");

        List<QuestionSet> questionSets = questionSetService.getQuestionSetsOfManager(m.getManager());
        // TODO check if test fails because of no qsets in db or bug
        assertNotNull("QuestionSets not loaded!", questionSets);
        //assertEquals("Wrong number of Players loaded", 3, questionSets.size());
    }
}
