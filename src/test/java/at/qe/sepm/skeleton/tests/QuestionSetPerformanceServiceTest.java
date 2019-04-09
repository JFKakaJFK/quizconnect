package at.qe.sepm.skeleton.tests;

import at.qe.sepm.skeleton.model.*;
import at.qe.sepm.skeleton.repositories.QuestionRepository;
import at.qe.sepm.skeleton.services.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Tests for the {@link QuestionSetPerfService}
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class QuestionSetPerformanceServiceTest {
    @Autowired
    QuestionSetPerformanceService questionSetPerformanceService;
    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionSetService questionSetService;

    @Autowired
    PlayerService playerService;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    private ManagerService managerService;

    private Manager manager;
    private QuestionSet questionSet;
    private Player player;
    private QuestionSetPerformance questionSetPerformance;

    @Before
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void setUp(){
        manager = managerService.getManagerById(101);

        player = playerService.getPlayerById(202);
        QuestionSet questionSet = new QuestionSet();

        questionSet.setAuthor(manager);
        questionSet.setDescription("DasIstEineBeschreibung");
        questionSet.setName("DasTestSet");
        questionSet.setDifficulty(QuestionSetDifficulty.easy);

        this.questionSetPerformance = getValidQuestionSetPerformance();

        this.questionSet = questionSetService.saveQuestionSet(questionSet);
    }

    public QuestionSetPerformance getValidQuestionSetPerformance(){
        QuestionSetPerformance testPerformance = new QuestionSetPerformance();

        testPerformance.setPlayCount(42);
        testPerformance.setPlayer(player);
        testPerformance.setRightAnswers(7);
        testPerformance.setQuestionSet(questionSet);
        testPerformance.setTotalQuestions(420);

        return testPerformance;
    }
    /*
    ID of QuestionSetPerformance is not Generated

     */

    @Test //(expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testUpdatePlayerStatsQuestionSetPerformanceNull() {
        //questionSetPerformanceService.updatePlayerStats(questionSet, player, 45, 436);

        Player nullPlayer = null;

        questionSetPerformanceService.updatePlayerStats(questionSet, nullPlayer, 45, 436);
    }





}
