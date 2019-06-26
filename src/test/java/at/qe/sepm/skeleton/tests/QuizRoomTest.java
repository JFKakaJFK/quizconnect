package at.qe.sepm.skeleton.tests;

import at.qe.sepm.skeleton.logic.*;
import at.qe.sepm.skeleton.model.Player;
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

/**
 * Few tests for the QuizRoom
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class QuizRoomTest
{
	@Autowired
	@Qualifier("threadPoolTaskScheduler")
	ThreadPoolTaskScheduler taskScheduler;
	
	@Autowired
	QuestionSetService questionSetService;
	
	@Autowired
	QuizRoomManager quizRoomManager;
	
	@Autowired
	PlayerService playerService;
	
	
	private class TestInterface implements IRoomAction
	{
		
		@Override
		public void onReadyUp(int pin, Player p, int totalReady)
		{
		
		}
		
		@Override
		public void onPlayerJoin(int pin, Player p)
		{
		
		}
		
		@Override
		public void onGameStart(int pin)
		{
		
		}
		
		@Override
		public void onGameEnd(int pin)
		{
		
		}
		
		@Override
		public void onJokerUse(int pin, int remaining)
		{
		
		}
		
		@Override
		public void onPlayerLeave(int pin, Player p, String reason)
		{
		
		}
		
		@Override
		public void onScoreChange(int pin, int newScore)
		{
		
		}
		
		@Override
		public void onTimerSync(int pin, Player p, ActiveQuestion q, long currentTime)
		{
		
		}
		
		@Override
		public void onTimeoutStart(int pin, Player p, long timeoutTime)
		{
		
		}
		
		@Override
		public void onKick(int pin, Player p)
		{
		
		}
		
		@Override
		public void assignQuestion(int pin, ActiveQuestion q)
		{
		
		}
		
		@Override
		public void removeQuestion(int pin, ActiveQuestion q)
		{
		
		}

		@Override
		public void onAllReady(int pin) {

		}
	}
	
	
	@Test
	public void testCreateQuizRoomEasy()
	{
		List<QuestionSet> questionSets = questionSetService.getAllQuestionSets();
		IRoomAction roomAction = new TestInterface();
		QuizRoom qr = new QuizRoom(taskScheduler, null, 111111, 5, RoomDifficulty.easy, GameMode.normal, questionSets, roomAction);
		
	}
	
	@Test
	public void testCreateQuizRoomHard()
	{
		List<QuestionSet> questionSets = questionSetService.getAllQuestionSets();
		IRoomAction roomAction = new TestInterface();
		QuizRoom qr = new QuizRoom(taskScheduler, null, 111111, 5, RoomDifficulty.hard, GameMode.normal, questionSets, roomAction);
		
	}
	
	@Test
	public void testCreateQuizRoomMath()
	{
		List<QuestionSet> questionSets = questionSetService.getAllQuestionSets();
		IRoomAction roomAction = new TestInterface();
		QuizRoom qr = new QuizRoom(taskScheduler, null, 111111, 5, RoomDifficulty.hard, GameMode.mathgod, questionSets, roomAction);
		
	}
	

	@Test
	public void testQuizRoomAddPlayer()
	{
		List<QuestionSet> questionSets = questionSetService.getAllQuestionSets();
		IRoomAction roomAction = new TestInterface();
		Player p = playerService.getPlayerById(201);
		int pin = quizRoomManager.createRoom(5, RoomDifficulty.easy, GameMode.normal, questionSets, roomAction);
		IPlayerAction playerAction = quizRoomManager.joinRoom(pin, p);
		playerAction.leaveRoom(p);
		assertFalse("QuizRoom still exists.", quizRoomManager.doesRoomExist(pin));
	}
}
