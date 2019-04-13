package at.qe.sepm.skeleton.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
	
	private volatile List<Player> players; // players in the room
	private IRoomAction playerInterface; // interface for all players
	private volatile List<DelayedAction> delayQueue; // queue of delayed actions
	
	private volatile int score; // current room score
	private volatile int numReshuffleJokers; // number of jokers available
	private volatile List<ActiveQuestion> activeQuestions; // list of currently active questions
	private volatile HashMap<Player, ActiveQuestion> playerQuestions; // map for storing assigned questions of players
	private volatile HashMap<Player, List<ActiveQuestion>> playerAnswers; // map for storing assigned answers of players
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

		}
		
		checkQuestionTimes(deltaTime);
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
		long now = new Date().getTime();
		while (delayQueue.size() > 0 && delayQueue.get(0).execTime <= now)
		{
			delayQueue.get(0).action.run();
			delayQueue.remove(0);
		}
	}
	
	/**
	 * Called every frameUpdate, reduces remaining time and checks for all ActiveQuestions if time has run out.
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
		// TODO implement activity check
	}
	
	/**
	 * Called every aliveTimeStep ms, checks for each Player if the last alive ping is more than aliveDuration ms ago.
	 */
	private synchronized void checkPlayerAlivePings()
	{
		// TODO implement alive check
	}
	
	/**
	 * Called every timerSyncTimeStep, calls the onTimerSync event on all Players, sending the current remaining time on the Question timer.
	 */
	private synchronized void synchonizeTimers()
	{
		// TODO implement timer sync
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
		
		eventCall(x -> {
			x.onPlayerJoin(player);
		});
		
		return true;
	}
	
	/**
	 * Removes a Player from the QuizRoom. May be called due to Player leaving, or afk kick.
	 * 
	 * @param player
	 *            Player to be removed.
	 * @param reason
	 *            Reason for the removal (e.g. 'left', 'kicked', 'disconnected')
	 */
	public synchronized void removePlayer(Player player, String reason)
	{
		eventCall(x -> {
			x.onPlayerLeave(player, reason);
		});
		
		players.remove(player);
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
	 * Takes a random unused question and assigns it to players with open slots. Question selection is dependent on room difficulty and passed time.
	 */
	private synchronized void distributeQuestion()
	{
		// TODO question distribution algorithm
	}
	
	/**
	 * Removes the ActiveQuestion from all Players involved (question + answers).
	 * 
	 * @param q
	 */
	private synchronized void removeQuestion(ActiveQuestion q)
	{
		// TODO question removal
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
			x.onReadyUp(p, readyPlayers.size());
		});
	}
	
	@Override
	public void answerQuestion(Player p, ActiveQuestion q, int index)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void useJoker(Player p)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void cancelTimeout(Player p)
	{
		// TODO Auto-generated method stub
		
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
	
}