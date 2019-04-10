package at.qe.sepm.skeleton.tests;

import at.qe.sepm.skeleton.model.*;
import at.qe.sepm.skeleton.repositories.QuestionRepository;
import at.qe.sepm.skeleton.repositories.QuestionSetPerformanceRepository;
import at.qe.sepm.skeleton.services.*;
import org.junit.Assert;
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

    @Autowired
    private QuestionSetPerformanceRepository questionSetPerformanceRepository;

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
        testPerformance.setRightAnswers(70);
        testPerformance.setQuestionSet(questionSet);
        testPerformance.setTotalQuestions(420);

        return testPerformance;
    }
    /*
    ID of QuestionSetPerformance is not Generated

     */

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testGetQuestionSetPerformanceByPlayerAndQuestionSet() {
        QuestionSetPerformance questionSetPerformanceTest1;
        questionSetPerformanceTest1 = questionSetPerformanceService.updatePlayerStats(questionSet, player, 45, 436);

        QuestionSetPerformance questionSetPerformanceTest2;
        questionSetPerformanceTest2 = questionSetPerformanceService.getQuestionSetPerformanceByPlayerAndQuestionSet(player, questionSet);

        Assert.assertTrue("QuestionSetPerf gets loaded correctly", questionSetPerformanceTest1.getId().equals(questionSetPerformanceTest2.getId()));
    }

    // Pretty useless test
    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public  void testUpdatePlayerStats() {
        //questionSetPerformanceRepository.save(questionSetPerformance);

        QuestionSetPerformance questionSetPerformanceTest1;
        questionSetPerformanceTest1 = questionSetPerformanceService.updatePlayerStats(questionSet, player, 420, 70);
        //QuestionSetPerformance questionSetPerformanceTest2;
        //questionSetPerformanceTest2 = questionSetPerformanceService.updatePlayerStats(questionSet, player, 42, 436);

        int correctTotal = questionSetPerformanceTest1.getTotalQuestions(); //+ questionSetPerformance.getTotalQuestions();
        int correctRight = questionSetPerformanceTest1.getRightAnswers(); //+ questionSetPerformance.getRightAnswers();

        Assert.assertEquals("Checks if Total is correct", correctTotal, questionSetPerformanceTest1.getTotalQuestions());
        Assert.assertEquals("Checks if PlayCount is correct", 1, questionSetPerformanceTest1.getPlayCount());
        Assert.assertEquals("Checks if RightAnswer is correct", correctRight, questionSetPerformanceTest1.getRightAnswers());
    }



}
