package at.qe.sepm.skeleton.tests;

import at.qe.sepm.skeleton.logic.ActiveQuestion;
import at.qe.sepm.skeleton.logic.QR_Question;
import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.QuestionSetDifficulty;
import at.qe.sepm.skeleton.model.QuestionType;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.socket.events.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class QRWebSocketTest
{
	
	@Autowired
	PlayerService playerService;
	
	@Test
	public void TestAnswerJSON()
	{
		AnswerJSON j = new AnswerJSON();
		AnswerJSON n = new AnswerJSON(1, "answer", 2);
		assertEquals("wrong a id.", 1, n.getAnswerId());
		assertEquals("wrong answer", "answer", n.getAnswer());
		assertEquals("wrong player id", 2, n.getPlayerId());
		
		n.setAnswerId(2);
		n.setAnswer("ans");
		n.setPlayerId(1);
		assertEquals("wrong a id.", 2, n.getAnswerId());
		assertEquals("wrong answer", "ans", n.getAnswer());
		assertEquals("wrong player id", 1, n.getPlayerId());
	}
	
	@Test
	public void TestAssignQuestionEvent()
	{
		AssignQuestionEvent aqe = new AssignQuestionEvent();
		QR_Question qrq = new QR_Question(1, QuestionType.text, "question", "answer", "wrong1", "wrong2", "wrong3", "wrong4", "wrong5");
		Player p1 = playerService.getPlayerById(201);
		Player p2 = playerService.getPlayerById(202);
		ActiveQuestion aq = new ActiveQuestion(qrq, QuestionSetDifficulty.easy, p1, p2, new ArrayList<>(), 1000);
		AssignQuestionEvent n = new AssignQuestionEvent(aq);
		
		assertEquals("wrong type", QuestionType.text, n.getType());
		assertEquals("wrong question", "question", n.getQuestion());
		assertEquals("wrong player id", 201, n.getPlayerId());
		assertEquals("wrong time remaining", 1000, n.getTimeRemaining());
		assertEquals("answers are wrong size", 1, n.getAnswers().size());
		assertEquals("wrong question id", 1, n.getQuestionId());
		
		n.setQuestionId(2);
		n.setQuestion("ques");
		n.setAnswers(null);
		n.setPlayerId(202);
		n.setTimeRemaining(500);
		n.setType(QuestionType.picture);
		
		assertEquals("wrong type", QuestionType.picture, n.getType());
		assertEquals("wrong question", "ques", n.getQuestion());
		assertEquals("wrong player id", 202, n.getPlayerId());
		assertEquals("wrong time remaining", 500, n.getTimeRemaining());
		assertNull("answers are not null", n.getAnswers());
		assertEquals("wrong question id", 2, n.getQuestionId());
	}
	
	@Test
	public void TestChatMessageJSON()
	{
		ChatMessageJSON cmj = new ChatMessageJSON();
		ChatMessageJSON n = new ChatMessageJSON("message", "p1", 1, 20);
		
		assertEquals("message is wrong", "message", n.getMessage());
		assertEquals("from is wrong", "p1", n.getFrom());
		assertEquals("player id is wrong", 1, n.getPlayerId());
		assertEquals("id is wrong", 20, n.getId());
		
		n.setMessage("mes");
		n.setFrom("p2");
		n.setPlayerId(2);
		n.setId(21);
		n.setTimestamp(2000);
		
		assertEquals("message is wrong", "mes", n.getMessage());
		assertEquals("from is wrong", "p2", n.getFrom());
		assertEquals("player id is wrong", 2, n.getPlayerId());
		assertEquals("id is wrong", 21, n.getId());
		assertEquals("timestamp is wrong", 2000, n.getTimestamp());
	}
	
	@Test
	public void TestChatMessageEvent()
	{
		ChatMessageEvent cme = new ChatMessageEvent();
		ChatMessageEvent n = new ChatMessageEvent("message", "p1", 1, 20);
		assertEquals("wrong event", "chatMessage", n.getEvent());
		
		ChatMessageJSON cj = n.getMessage();
		assertEquals("message is wrong", "message", cj.getMessage());
		assertEquals("from is wrong", "p1", cj.getFrom());
		assertEquals("player id is wrong", 1, cj.getPlayerId());
		assertEquals("id is wrong", 20, cj.getId());
		
		ChatMessageJSON ncj = new ChatMessageJSON("mes", "p2", 2, 21);
		n.setMessage(ncj);
		cj = n.getMessage();
		
		assertEquals("message is wrong", "mes", cj.getMessage());
		assertEquals("from is wrong", "p2", cj.getFrom());
		assertEquals("player id is wrong", 2, cj.getPlayerId());
		assertEquals("id is wrong", 21, cj.getId());
	}
	
	@Test
	public void TestChatHistoryEvent()
	{
		ChatHistoryEvent che = new ChatHistoryEvent();
		ChatHistoryEvent n = new ChatHistoryEvent(null);
		assertNotNull("messages are null", n.getMessages());
		assertEquals("message count wrong", 0, n.getMessages().size());
		
		n.setMessages(null);
		assertNull("messages are null", n.getMessages());
	}
	
	@Test
	public void TestClientEvent()
	{
		ClientEvent n = new ClientEvent();
		
		n.setEvent("event");
		n.setPlayerId(1);
		n.setMessage("message");
		n.setAnswerId(2);
		n.setQuestionId(3);
		
		assertEquals("wrong event", "event", n.getEvent());
		assertEquals("wrong player id", 1, n.getPlayerId());
		assertEquals("wrong message", "message", n.getMessage());
		assertEquals("wrong answer id", 2, n.getAnswerId());
		assertEquals("wrong question id", 3, n.getQuestionId());
	}
	
	@Test
	public void TestJokerUseEvent()
	{
		JokerUseEvent jue = new JokerUseEvent();
		JokerUseEvent n = new JokerUseEvent(2);
		assertEquals("wrong number", 2, n.getRemaining());
		
		n.setRemaining(1);
		
		assertEquals("wrong number", 1, n.getRemaining());
	}
	
	@Test
	public void TestPlayerJSON()
	{
		PlayerJSON pj = new PlayerJSON();
		Player p1 = playerService.getPlayerById(201);
		PlayerJSON n = new PlayerJSON(p1, "p");
		
		assertEquals("wrong id", 201, n.getId());
		assertEquals("wrong username", p1.getUser().getUsername(), n.getUsername());
		assertNotNull("avatar is null", n.getAvatar());
		assertFalse("wrong ready", n.isReady());
		
		n.setReady(true);
		n.setAvatar("asdf");
		n.setId(3);
		n.setUsername("user");
		
		assertEquals("wrong avatar", "asdf", n.getAvatar());
		assertEquals("wrong id", 3, n.getId());
		assertEquals("wrong username", "user", n.getUsername());
		assertTrue("wrong ready", n.isReady());
	}
	
	@Test
	public void TestPlayerJoinEvent()
	{
		PlayerJoinEvent pje = new PlayerJoinEvent();
		Player p1 = playerService.getPlayerById(201);
		PlayerJoinEvent n = new PlayerJoinEvent(p1, "p");
		
		PlayerJSON pj = n.getPlayer();
		assertEquals("wrong id", 201, pj.getId());
		assertEquals("wrong username", p1.getUser().getUsername(), pj.getUsername());
		assertNotNull("avatar is null", pj.getAvatar());
		assertFalse("wrong ready", pj.isReady());
		
		PlayerJSON n1 = new PlayerJSON(p1, "p");
		n1.setReady(true);
		n1.setAvatar("asdf");
		n1.setId(3);
		n1.setUsername("user");
		n.setPlayer(n1);
		
		pj = n.getPlayer();
		assertEquals("wrong avatar", "asdf", pj.getAvatar());
		assertEquals("wrong id", 3, pj.getId());
		assertEquals("wrong username", "user", pj.getUsername());
		assertTrue("wrong ready", pj.isReady());
	}
	
	@Test
	public void TestPlayerKickEvent()
	{
		PlayerKickEvent pke = new PlayerKickEvent();
		Player p1 = playerService.getPlayerById(201);
		PlayerKickEvent n = new PlayerKickEvent(p1);
		
		assertEquals("wrong player id", 201, n.getPlayerId());
		
		n.setPlayerId(2);
		assertEquals("wrong player id", 2, n.getPlayerId());
	}
	
	@Test
	public void TestPlayerLeaveEvent()
	{
		PlayerLeaveEvent ple = new PlayerLeaveEvent();
		Player p1 = playerService.getPlayerById(201);
		PlayerLeaveEvent n = new PlayerLeaveEvent(p1, "reason");
		
		assertEquals("wrong player id", 201, n.getPlayerId());
		assertEquals("wrong reason", "reason", n.getReason());
		
		n.setPlayerId(2);
		n.setReason("reas");
		assertEquals("wrong player id", 2, n.getPlayerId());
		assertEquals("wrong reason", "reas", n.getReason());
	}
	
	@Test
	public void TestPlayerTimeoutEvent()
	{
		PlayerTimeoutEvent pte = new PlayerTimeoutEvent();
		Player p1 = playerService.getPlayerById(201);
		PlayerTimeoutEvent n = new PlayerTimeoutEvent(p1, 1000);
		
		assertEquals("wrong player id", 201, n.getPlayerId());
		assertEquals("wrong reason", 1000, n.getRemaining());
		
		n.setPlayerId(2);
		n.setRemaining(500);
		assertEquals("wrong player id", 2, n.getPlayerId());
		assertEquals("wrong reason", 500, n.getRemaining());
	}
	
	@Test
	public void TestReadyUpEvent()
	{
		ReadyUpEvent rue = new ReadyUpEvent();
		Player p1 = playerService.getPlayerById(201);
		ReadyUpEvent n = new ReadyUpEvent(p1, 1000);
		
		assertEquals("wrong player id", 201, n.getPlayerId());
		assertEquals("wrong reason", 1000, n.getTotalReady());
		
		n.setPlayerId(2);
		n.setTotalReady(500);
		assertEquals("wrong player id", 2, n.getPlayerId());
		assertEquals("wrong reason", 500, n.getTotalReady());
	}
	
	@Test
	public void TestRemoveQuestionEvent()
	{
		RemoveQuestionEvent rqe = new RemoveQuestionEvent();
		QR_Question qrq = new QR_Question(1, QuestionType.text, "question", "answer", "wrong1", "wrong2", "wrong3", "wrong4", "wrong5");
		Player p1 = playerService.getPlayerById(201);
		Player p2 = playerService.getPlayerById(202);
		ActiveQuestion aq = new ActiveQuestion(qrq, QuestionSetDifficulty.easy, p1, p2, new ArrayList<>(), 1000);
		RemoveQuestionEvent n = new RemoveQuestionEvent(aq);
		
		assertEquals("wrong question id", 1, n.getQuestionId());
		n.setQuestionId(2);
		assertEquals("wrong question id", 2, n.getQuestionId());
	}
	
	@Test
	public void TestRoomInfoEvent()
	{
		RoomInfoEvent rie = new RoomInfoEvent();
		RoomInfoEvent n = new RoomInfoEvent(1, "easy", "normal", null, 2, 3, 4, true, new ArrayList<>());
		
		assertEquals("wrong pin", 1, n.getPin());
		assertEquals("wrong difficulty", "easy", n.getDifficulty());
		assertEquals("wrong mode", "normal", n.getMode());
		assertNull("wrong question sets", n.getQuestionSets());
		assertEquals("wrong score", 2, n.getScore());
		assertEquals("wrong alive ping interval", 3, n.getAlivePingInterval());
		assertEquals("wrong number jokers", 4, n.getNumJokers());
		assertEquals("wrong state", 0, n.getState());
		assertNotNull("players null", n.getPlayers());
		assertEquals("wrong players", 0, n.getPlayers().size());
		assertEquals("wrong num", 0, n.getNum());
		
		n.setPin(2);
		n.setDifficulty("hard");
		n.setMode("math");
		n.setQuestionSets(new ArrayList<>());
		n.setScore(3);
		n.setAlivePingInterval(4);
		n.setNumJokers(5);
		n.setNum(0);
		n.setState(1);
		n.setPlayers(null);
		
		assertEquals("wrong pin", 2, n.getPin());
		assertEquals("wrong difficulty", "hard", n.getDifficulty());
		assertEquals("wrong mode", "math", n.getMode());
		assertNotNull("wrong question sets", n.getQuestionSets());
		assertEquals("wrong question sets", 0, n.getQuestionSets().size());
		assertEquals("wrong score", 3, n.getScore());
		assertEquals("wrong alive ping interval", 4, n.getAlivePingInterval());
		assertEquals("wrong number jokers", 5, n.getNumJokers());
		assertEquals("wrong state", 1, n.getState());
		assertNull("players null", n.getPlayers());
		assertEquals("wrong num", 0, n.getNum());
	}
	
	@Test
	public void TestScoreChangeEvent()
	{
		ScoreChangeEvent sce = new ScoreChangeEvent();
		ScoreChangeEvent n = new ScoreChangeEvent(1);
		
		assertEquals("wrong new score", 1, n.getNewScore());
		n.setNewScore(2);
		assertEquals("wrong new score", 2, n.getNewScore());
	}
	
	@Test
	public void TestTimerSyncEvent()
	{
		TimerSyncEvent tse = new TimerSyncEvent();
		QR_Question qrq = new QR_Question(1, QuestionType.text, "question", "answer", "wrong1", "wrong2", "wrong3", "wrong4", "wrong5");
		Player p1 = playerService.getPlayerById(201);
		Player p2 = playerService.getPlayerById(202);
		ActiveQuestion aq = new ActiveQuestion(qrq, QuestionSetDifficulty.easy, p1, p2, new ArrayList<>(), 1000);
		TimerSyncEvent n = new TimerSyncEvent(aq, 1000);
		
		assertEquals("wrong question id", 1, n.getQuestionId());
		assertEquals("wrong remaining", 1000, n.getRemaining());
		
		n.setQuestionId(2);
		n.setRemaining(500);
		
		assertEquals("wrong question id", 2, n.getQuestionId());
		assertEquals("wrong remaining", 500, n.getRemaining());
	}
	
	@Test
	public void TestGenericSocketEvent()
	{
		GenericSocketEvent gse = new GenericSocketEvent();
		GenericSocketEvent n = new GenericSocketEvent("event");
	}
}
