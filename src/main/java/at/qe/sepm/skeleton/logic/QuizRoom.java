package at.qe.sepm.skeleton.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.Question;
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.model.QuestionSetDifficulty;

/**
 * Class representing a QuizRoom containing all major logic required.
 * 
 * @author Lorenz_Smidt
 *
 */
public class QuizRoom implements IPlayerAction
{
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	// all times in ms
	private final long frameTimeStep = 50; // time between FramUpdate calls
	private final long activityTimeStep = 500; // time between player activity checks
	private final long activityDuration = 30000; // time until an activity ping invalidates
	private final long timeoutDuration = 10000; // time until player gets kicked after no activity
	private final long aliveTimeStep = 500; // time between alive ping checks
	private final long aliveDuration = 1000; // maximum time between alive pings
	private final long timerSyncTimeStep = 1000; // time between timer sync calls to players
	private final long roomStartDelay = 5000; // time between all Players ready and game start
	
	private final int defaultNumberJokers = 1; // number of jokers to start with
	private final int playerAnswerSlots = 6;
	
	private int pin;
	private int maxPlayers;
	private QuizRoomManager manager;
	private RoomDifficulty difficulty;
	private GameMode gameMode;
	private List<QuestionSet> questionSets;
	private Timer timerFrameUpdate;
	
	private volatile long activityCheckTime; // ms since last activity check
	private volatile long aliveCheckTime; // ms since last alive check
	private volatile long timerSyncTime; // ms since last timer sync
	
	private volatile List<Question> questionsPoolEasy; // list of all unused easy questions
	private volatile List<Question> questionsPoolHard; // list of all unused hard questions
	private volatile int completedQuestions; // total number of answered questions
	private int maxQuestions; // maximum number of questions until game ends
	
	private volatile List<Player> players; // players in the room
	private IRoomAction playerInterface; // interface for all players
	private volatile List<DelayedAction> delayQueue; // queue of delayed actions
	
	private volatile int score; // current room score
	private volatile int numReshuffleJokers; // number of jokers available
	private volatile List<ActiveQuestion> activeQuestions; // list of currently active questions
	private volatile HashMap<Player, ActiveQuestion> playerQuestions; // map for storing assigned questions of players
	private volatile HashMap<Player, List<ActiveQuestion>> playerAnswers; // map for storing assigned answers of players, both right and wrong
	private volatile HashMap<Player, Long> playerActivityTimestamps; // map for storing activity time stamps of players
	private volatile HashMap<Player, Long> playerAlivePingTimestamps; // map for storing alive ping time stamps of players
	
	private volatile List<Player> readyPlayers; // list of players who declared themselves ready
	private volatile boolean wfpMode; // true if the room is in 'waiting for players' mode
	
	
	/**
	 * Initializes a new QR.
	 * 
	 * @param scheduler
	 *            ThreadPoolTaskScheduler for scheduling Timers.
	 * @param manager
	 *            Reference to QuizRoomManager for notifying on room close.
	 * @param pin
	 *            Pin of the QuizRoom.
	 * @param maxPlayers
	 *            Maximum number of players.
	 * @param difficulty
	 *            Difficulty of the room.
	 * @param gameMode
	 *            Game mode of the room.
	 * @param qSets
	 *            QuestionSets to be used by the room.
	 */
	public QuizRoom(ThreadPoolTaskScheduler scheduler, QuizRoomManager manager, int pin, int maxPlayers, RoomDifficulty difficulty, GameMode gameMode,
			List<QuestionSet> qSets, IRoomAction roomAction)
	{
		LOGGER.debug("QuizRoom [" + pin + "] started.");
		this.pin = pin;
		this.manager = manager;
		this.maxPlayers = maxPlayers;
		this.difficulty = difficulty;
		this.gameMode = gameMode;
		this.questionSets = qSets;
		
		players = new LinkedList<>();
		playerInterface = roomAction;
		delayQueue = new LinkedList<>();
		
		activityCheckTime = 0;
		aliveCheckTime = 0;
		timerSyncTime = 0;
		
		numReshuffleJokers = defaultNumberJokers;
		completedQuestions = 0;
		score = 0;
		
		activeQuestions = new LinkedList<>();
		playerQuestions = new HashMap<>(maxPlayers);
		playerAnswers = new HashMap<>(maxPlayers);
		playerActivityTimestamps = new HashMap<>(maxPlayers);
		playerAlivePingTimestamps = new HashMap<>(maxPlayers);
		
		readyPlayers = new LinkedList<>();
		wfpMode = true;
		
		loadQuestions();
		
		// create and start frame timer
		timerFrameUpdate = new Timer(scheduler, new ITimedAction()
		{
			@Override
			public void onTimeUpdate(long delta)
			{
				onFrameUpdate(delta);
			}
		}, frameTimeStep);
	}
	
	/**
	 * Called on QuizRoom creation; Loads all Questions contained in the QuestionSets to be used into the respective pools.
	 */
	private void loadQuestions()
	{
		questionsPoolEasy = new LinkedList<>();
		questionsPoolHard = new LinkedList<>();
		
		for (QuestionSet qSet : questionSets)
		{
			for (Question q : qSet.getQuestions())
			{
				if (qSet.getDifficulty() == QuestionSetDifficulty.easy)
					questionsPoolEasy.add(q);
				else if (qSet.getDifficulty() == QuestionSetDifficulty.hard)
					questionsPoolHard.add(q);
			}
		}
	}
	
	/**
	 * Gets called once every frameTimeStep ms by timerFrameUpdate.
	 * 
	 * @param deltaTime
	 */
	public void onFrameUpdate(long deltaTime)
	{
		// LOGGER.debug("frame call after " + timerFrameUpdate.getElapsedTime() + " ms.");
		if (deltaTime > 2 * frameTimeStep)
		{
			LOGGER.debug("large delay in frameUpdate call of QR [" + pin + "] (" + deltaTime + "ms)");
		}
		checkDelayQueue();
		
		if (!wfpMode) // disable during wfp mode
		{
			aliveCheckTime += deltaTime;
			if (aliveCheckTime >= aliveTimeStep)
			{
				checkPlayerAlivePings();
				aliveCheckTime = 0;
			}
			
			activityCheckTime += deltaTime;
			if (activityCheckTime >= activityTimeStep)
			{
				checkPlayerActivity();
				activityCheckTime = 0;
			}
			
			timerSyncTime += deltaTime;
			if (timerSyncTime >= timerSyncTimeStep)
			{
				synchonizeTimers();
				timerSyncTime = 0;
			}
			
			checkQuestionTimes(deltaTime);
		}
		
	}
	
	/**
	 * Executes function f on the Player interface for the QuizRoom.
	 * 
	 * @param f
	 *            Function to be executed on all Players.
	 */
	private synchronized void eventCall(Consumer<IRoomAction> f)
	{
		f.accept(playerInterface);
		/*
		 * for (IRoomAction action : playerInterfaces.values()) { f.accept(action); }
		 */
	}
	
	/**
	 * Adds the action to the queue to be executed at execTime.
	 * 
	 * @param action
	 */
	private synchronized void addDelayedAction(DelayedAction action) throws IllegalArgumentException
	{
		if (action == null || action.action == null)
			throw new IllegalArgumentException("DelayAction is invalid!");
		
		for (int i = 0; i < delayQueue.size(); i++)
		{
			if (delayQueue.get(i).execTime > action.execTime)
			{
				delayQueue.add(i, action);
				return;
			}
		}
		delayQueue.add(action);
	}
	
	/**
	 * Called every frameUpdate, executes delayed action at around the right time (max. execTime + frameTimeStep).
	 */
	private synchronized void checkDelayQueue()
	{
		long now = new Date().getTime() + 10; // offset by 10ms to make up for 'just missed' calls
		while (delayQueue.size() > 0 && delayQueue.get(0).execTime <= now)
		{
			delayQueue.get(0).action.run();
			delayQueue.remove(0);
		}
	}
	
	/**
	 * Called every frameUpdate, reduces remaining time and checks for all ActiveQuestions if time has run out. Removes a Question from all Players associated if time has run out.
	 */
	private synchronized void checkQuestionTimes(long deltaTime)
	{
		for (int i = activeQuestions.size() - 1; i >= 0; i--)
		{
			activeQuestions.get(i).timeRemaining -= deltaTime;
			if (activeQuestions.get(i).timeRemaining <= 0)
			{
				removeQuestion(activeQuestions.get(i));
			}
		}
	}
	
	/**
	 * Called every activityTimeStep ms, checks for each Player if the last activity time stamp is more than activityDuration ms ago.
	 */
	private synchronized void checkPlayerActivity()
	{
		long tooLong = (new Date().getTime()) - activityDuration;
		for (Player player : players)
		{
			if (playerActivityTimestamps.get(player) < tooLong)
			{
				playerInterface.onTimeoutStart(pin, player, timeoutDuration);
				addDelayedAction(new DelayedAction(timeoutDuration, () -> {
					kickPlayerIfNoActivity(player);
				}));
			}
		}
	}
	
	/**
	 * Called delayed if an activity check fails on a player. If still no activity / timeout cancel detected kick player
	 */
	private synchronized void kickPlayerIfNoActivity(Player player)
	{
		long tooLong = (new Date().getTime()) - activityDuration;
		if (playerActivityTimestamps.get(player) < tooLong)
		{
			playerInterface.onKick(pin, player);
			removePlayer(player, "kicked for AFK");
		}
	}
	
	/**
	 * Called every aliveTimeStep ms, checks for each Player if the last alive ping is more than aliveDuration ms ago. Removes the player if no activity is detected.
	 */
	private synchronized void checkPlayerAlivePings()
	{
		long tooLong = (new Date().getTime()) - aliveDuration;
		for (int i = players.size() - 1; i >= 0; i--)
		{
			Player player = players.get(i);
			if (playerAlivePingTimestamps.get(player) < tooLong)
			{
				removePlayer(player, "disconnected");
			}
		}
	}
	
	/**
	 * Called every timerSyncTimeStep, calls the onTimerSync event on all Players, sending the current remaining time on the Question timer.
	 */
	private synchronized void synchonizeTimers()
	{
		for (ActiveQuestion activeQuestion : activeQuestions)
		{
			playerInterface.onTimerSync(pin, activeQuestion.playerQuestion, activeQuestion, activeQuestion.timeRemaining);
		}
	}
	
	/**
	 * Called by the {@link QuizRoomManager} if a Player tries to join the QuizRoom.
	 * 
	 * @param player
	 *            Player to join the room.
	 * @return True if join successful, false if room is full.
	 */
	public synchronized boolean addPlayer(Player player)
	{
		if (players.size() == maxPlayers)
		{
			return false;
		}
		
		players.add(player);
		playerQuestions.put(player, null);
		playerAnswers.put(player, new LinkedList<>());
		
		eventCall(x -> {
			x.onPlayerJoin(pin, player);
		});
		
		return true;
	}
	
	/**
	 * Removes a Player from the QuizRoom. May be called due to Player leaving, or afk kick. Makes the onPlayerLeave event call.
	 * 
	 * @param player
	 *            Player to be removed.
	 * @param reason
	 *            Reason for the removal (e.g. 'left', 'kicked', 'disconnected')
	 */
	private synchronized void removePlayer(Player player, String reason)
	{
		eventCall(x -> {
			x.onPlayerLeave(pin, player, reason);
		});
		
		removeQuestion(playerQuestions.get(player));
		
		players.remove(player);
		playerQuestions.remove(player);
		playerAnswers.remove(player);
		
		playerActivityTimestamps.remove(player);
		playerAlivePingTimestamps.remove(player);
	}
	
	/**
	 * Called roomStartDelay ms after all Players have readied up.
	 */
	private void onGameStart()
	{
		readyPlayers = null;
		
		long now = new Date().getTime();
		for (Player player : players)
		{
			// initialize time stamps
			playerActivityTimestamps.put(player, now);
			playerAlivePingTimestamps.put(player, now);
		}
		
		wfpMode = false;
		
		// distribute questions to all players
		for (int i = 0; i < players.size(); i++)
		{
			distributeQuestion();
		}
	}
	
	/**
	 * Called just before the room is closed. Cleans up the class.
	 */
	private void onRoomClose()
	{
		timerFrameUpdate.stop();
		manager.removeRoom(pin);

		LOGGER.debug("QuizRoom [" + pin + "] closed after " + timerFrameUpdate.getElapsedTime() + " ms.");
	}
	
	/**
	 * Takes a random unused question and assigns it to players with open slots. Question selection is dependent on room difficulty and available questions.
	 */
	private synchronized void distributeQuestion()
	{
		Question question = selectQuestion();
		if (question == null)
		{
			eventCall(x -> {
				x.onGameEnd(pin);
			});
			onRoomClose();
		}
		
		List<Player> questionFreePlayers = new ArrayList<>(); // players with no questions
		List<Player> answerFreePlayers = new ArrayList<>(); // players with open answer slots (multiple slots open = multiple times in list)
		for (Player player : players)
		{
			if (playerQuestions.get(player) == null)
			{
				questionFreePlayers.add(player);
			}
			if (playerAnswers.get(player).size() < playerAnswerSlots)
			{
				for (int i = 0; i < playerAnswers.get(player).size(); i++)
					answerFreePlayers.add(player);
			}
		}
		
		Random random = new Random();
		// question assignment
		int qIndex = random.nextInt(questionFreePlayers.size());
		Player qPlayer = questionFreePlayers.get(qIndex);
		
		// answers assignment (depending on number of right + wrong answers already assigned to players)
		int raIndex = random.nextInt(answerFreePlayers.size());
		Player raPlayer = answerFreePlayers.get(raIndex);
		answerFreePlayers.remove(raIndex);
		
		List<Player> waPlayers = new ArrayList<>();
		Player p;
		
		int f1Index = random.nextInt(answerFreePlayers.size());
		p = answerFreePlayers.get(f1Index);
		answerFreePlayers.remove(f1Index);
		waPlayers.add(p);
		
		for (int i = 0; i < 5; i++)
		{
			String qString = null;
			if (i == 0)
				qString = question.getWrongAnswerString_1();
			else if (i == 1)
				qString = question.getWrongAnswerString_2();
			else if (i == 2)
				qString = question.getWrongAnswerString_3();
			else if (i == 3)
				qString = question.getWrongAnswerString_4();
			else if (i == 4)
				qString = question.getWrongAnswerString_5();
			
			if (answerFreePlayers.size() > 0 && qString != null && qString != "")
			{
				int index = random.nextInt(answerFreePlayers.size());
				p = answerFreePlayers.get(index);
				answerFreePlayers.remove(index);
				waPlayers.add(p);
			}
			else
				break;
		}
		
		// TODO compute question time depending on already answered questions + difficulty of Q + Room
		long qTime = 60;
		
		ActiveQuestion newActive = new ActiveQuestion(question, qPlayer, raPlayer, waPlayers, qTime);
		
		activeQuestions.add(newActive);
		
		playerQuestions.put(qPlayer, newActive);
		
		playerAnswers.get(raPlayer).add(newActive);
		
		for (int i = 0; i < waPlayers.size(); i++)
		{
			playerAnswers.get(waPlayers.get(i)).add(newActive);
		}

		playerInterface.assignQuestion(pin, newActive);
	}
	
	/**
	 * Returns a Question at random from either the easy or the hard pool (depending on emptiness / difficulty) or null if game end reached.
	 */
	private Question selectQuestion()
	{
		Random random = new Random();
		int bound = difficulty == RoomDifficulty.easy ? 66 : 33;
		
		boolean easy;
		if ((completedQuestions >= maxQuestions) || (questionsPoolEasy.size() == 0 && questionsPoolHard.size() == 0))
		{ // game complete state reached
			return null;
		}
		else if (questionsPoolEasy.size() == 0)
		{ // have to select hard one
			easy = false;
		}
		else if (questionsPoolHard.size() == 0)
		{ // have to select easy one
			easy = true;
		}
		else if (random.nextInt(100) < bound)
		{ // select easy question
			easy = true;
		}
		else
		{ // select hard question
			easy = false;
		}
		
		Question question;
		if (easy)
		{
			int index = random.nextInt(questionsPoolEasy.size());
			question = questionsPoolEasy.get(index);
			questionsPoolEasy.remove(index);
		}
		else
		{
			int index = random.nextInt(questionsPoolHard.size());
			question = questionsPoolHard.get(index);
			questionsPoolHard.remove(index);
		}
		
		return question;
	}
	
	/**
	 * Removes the ActiveQuestion from all Players involved (question + answers).
	 * 
	 * @param q
	 */
	private synchronized void removeQuestion(ActiveQuestion q)
	{
		// TODO question removal
		completedQuestions++;
	}
	
	@Override
	public int getRoomPin()
	{
		return pin;
	}
	
	@Override
	public synchronized List<Player> getRoomPlayers()
	{
		List<Player> ps = new ArrayList<>(players.size());
		for (Player player : players)
		{
			ps.add(player);
		}
		return ps;
	}
	
	@Override
	public int getRoomPlayerCount()
	{
		return players.size();
	}
	
	@Override
	public RoomDifficulty getRoomDifficulty()
	{
		return difficulty;
	}
	
	@Override
	public GameMode getRoomMode()
	{
		return gameMode;
	}
	
	@Override
	public synchronized List<String> getRoomQuestionSets()
	{
		List<String> QSstrings = new ArrayList<>(questionSets.size());
		for (QuestionSet questionSet : questionSets)
		{
			QSstrings.add(questionSet.getName());
		}
		return QSstrings;
	}
	
	@Override
	public int getRoomScore()
	{
		return score;
	}
	
	@Override
	public synchronized List<Player> getRoomReadyPlayers()
	{
		List<Player> ps = new ArrayList<>(readyPlayers.size());
		for (Player player : readyPlayers)
		{
			ps.add(player);
		}
		return ps;
	}
	
	@Override
	public long getAlivePingTimeStep()
	{
		return aliveTimeStep;
	}
	
	@Override
	public synchronized void readyUp(Player p)
	{
		// player already ready?
		if (readyPlayers.contains(p))
			return;
		
		readyPlayers.add(p);
		
		// are all players ready?
		if (readyPlayers.size() == players.size())
		{
			addDelayedAction(new DelayedAction(new Date().getTime() + roomStartDelay, () -> {
				onGameStart();
			}));
		}
		
		eventCall(x -> {
			x.onReadyUp(pin, p, readyPlayers.size());
		});
	}
	
	@Override
	public void answerQuestion(Player p, ActiveQuestion q, int index)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public synchronized void useJoker(Player p)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public synchronized void cancelTimeout(Player p)
	{
		long tooLong = (new Date().getTime()) - activityDuration;
		if (playerActivityTimestamps.get(p) < tooLong)
			playerActivityTimestamps.put(p, new Date().getTime());
	}
	
	@Override
	public void leaveRoom(Player p)
	{
		removePlayer(p, "left");
	}
	
	@Override
	public synchronized void sendAlivePing(Player p)
	{
		playerAlivePingTimestamps.put(p, new Date().getTime());
	}
	
	@Override
	public int getNumberOfJokers()
	{
		return numReshuffleJokers;
	}
	
}