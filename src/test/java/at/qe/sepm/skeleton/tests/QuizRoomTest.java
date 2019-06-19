package at.qe.sepm.skeleton.tests;

import at.qe.sepm.skeleton.logic.GameMode;
import at.qe.sepm.skeleton.logic.QuizRoom;
import at.qe.sepm.skeleton.logic.RoomDifficulty;
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.services.QuestionSetService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class QuizRoomTest
{
	@Autowired
	@Qualifier("threadPoolTaskScheduler")
	ThreadPoolTaskScheduler taskScheduler;

	@Autowired
	QuestionSetService questionSetService;
	
	@Test
	public void testCreateQuizRoomEasy()
	{
		List<QuestionSet> questionSets = questionSetService.getAllQuestionSets();
		QuizRoom qr = new QuizRoom(taskScheduler, null, 111111, 5, RoomDifficulty.easy, GameMode.normal, questionSets, null);
		
	}
	
	@Test
	public void testCreateQuizRoomHard()
	{
		List<QuestionSet> questionSets = questionSetService.getAllQuestionSets();
		QuizRoom qr = new QuizRoom(taskScheduler, null, 111111, 5, RoomDifficulty.hard, GameMode.normal, questionSets, null);
		
	}
	
	@Test
	public void testCreateQuizRoomMath()
	{
		List<QuestionSet> questionSets = questionSetService.getAllQuestionSets();
		QuizRoom qr = new QuizRoom(taskScheduler, null, 111111, 5, RoomDifficulty.hard, GameMode.mathgod, questionSets, null);
		
	}
}
