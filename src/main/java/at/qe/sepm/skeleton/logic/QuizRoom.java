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
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.model.QuestionSetDifficulty;
import at.qe.sepm.skeleton.services.PlayerService;

/**
 * Class representing a QuizRoom containing all major logic required. For illustrations on how this class works please see the 'Documentation' folder.
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
	private final long activityDuration = 60000; // time until an activity ping invalidates
	private final long timeoutDuration = 10000; // time until player gets kicked after no activity
	private final long aliveTimeStep = 500; // time between alive ping checks
	private final long aliveDuration = 5000; // maximum time between alive pings
	private final long timerSyncTimeStep = 1000; // time between timer sync calls to players
	private final long roomStartDelay = 5000; // time between all Players ready and game start
	
	private final int defaultNumberJokers = 1; // number of jokers to start with
	
	// constants while QR exists
	protected int pin;
	private int maxPlayers;
	private QuizRoomManager manager;
	protected RoomDifficulty difficulty;
	private GameMode gameMode;
	private Timer timerFrameUpdate;
	private QR_QuestionSystem questionSystem;
	
	private volatile long activityCheckTime; // ms since last activity check
	private volatile long aliveCheckTime; // ms since last alive check
	private volatile long timerSyncTime; // ms since last timer sync
	
	private List<QuestionSet> questionSets;
	private volatile HashMap<Player, Integer> correctlyAnsweredQuestions; // map for storing number of correctly answered Questions per player
	private volatile HashMap<Player, Integer> totalAnsweredQuestions; // map for storing total number of Questions answered per player
	private volatile long gameStartTime; // time stamp of game start, used for play time calculation
	
	protected volatile List<Player> players; // players in the room
	protected IRoomAction playerInterface; // interface for all players
	private volatile List<DelayedAction> delayQueue; // queue of delayed actions
	
	private volatile HashMap<Player, Long> playerActivityTimestamps; // map for storing activity time stamps of players
	private volatile int score; // current room score
	private volatile int numReshuffleJokers; // number of jokers available
	private volatile HashMap<Player, Long> playerAlivePingTimestamps; // map for storing alive ping time stamps of players
	private volatile List<Player> inactivePlayers; // list of players marked as inactive and to be kicked soon
	
	private volatile List<Player> readyPlayers; // list of players who declared themselves ready
	protected volatile boolean wfpMode; // true if the room is in 'waiting for players' mode
	private volatile boolean gameOver; // true if the game is finished
	
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
		LOGGER.debug("### INFO ### QuizRoom [" + pin + "] started. (room difficulty " + difficulty + "; maxPlayers: " + maxPlayers + ")");
		this.pin = pin;
		this.manager = manager;
		this.maxPlayers = maxPlayers;
		this.difficulty = difficulty;
		this.gameMode = gameMode;
		
		players = new LinkedList<>();
		playerInterface = roomAction;
		delayQueue = new LinkedList<>();
		this.questionSets = qSets;
		
		activityCheckTime = 0;
		aliveCheckTime = 0;
		timerSyncTime = 0;
		
		numReshuffleJokers = defaultNumberJokers;
		correctlyAnsweredQuestions = new HashMap<>();
		totalAnsweredQuestions = new HashMap<>();
		score = 0;
		
		playerActivityTimestamps = new HashMap<>();
		playerAlivePingTimestamps = new HashMap<>();
		inactivePlayers = new LinkedList<>();
		
		readyPlayers = new LinkedList<>();
		wfpMode = true;
		gameOver = false;
		
		// create and start frame timer
		timerFrameUpdate = new Timer(scheduler, this::onFrameUpdate, frameTimeStep);
		
		questionSystem = new QR_QuestionSystem(this, gameMode, qSets);
	}
	
	/**
	 * Gets called once every frameTimeStep ms by timerFrameUpdate.
	 *
	 * @param deltaTime Time since last call in ms.
	 */
	private void onFrameUpdate(long deltaTime)
	{
		if (gameOver)
			return;
		
		// LOGGER.debug("frame call after " + timerFrameUpdate.getElapsedTime() + " ms.");
		if (deltaTime > 2 * frameTimeStep)
		{
			LOGGER.debug("large delay in frameUpdate call of QR [" + pin + "] (" + deltaTime + "ms)");
		}
		checkDelayQueue();
		
		aliveCheckTime += deltaTime;
		if (aliveCheckTime >= aliveTimeStep)
		{
			checkPlayerAlivePings();
			aliveCheckTime = 0;
		}
		
		if (!wfpMode) // disable during wfp mode
		{
			activityCheckTime += deltaTime;
			if (activityCheckTime >= activityTimeStep)
			{
				checkPlayerActivity();
				activityCheckTime = 0;
			}
			
			timerSyncTime += deltaTime;
			if (timerSyncTime >= timerSyncTimeStep)
			{
				questionSystem.synchronizeTimers(playerInterface, pin);
				timerSyncTime = 0;
			}
			
			questionSystem.checkQuestionTimes(deltaTime);
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
	 * @param action Action to be executed after a delay.
	 */
	protected synchronized void addDelayedAction(DelayedAction action) throws IllegalArgumentException
	{
		if (gameOver)
			return;
		
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
		if (gameOver)
			return;
		
		long now = new Date().getTime() + 10; // offset by 10ms to make up for 'just missed' calls
		while (delayQueue.size() > 0 && delayQueue.get(0).execTime <= now)
		{
			DelayedAction a = delayQueue.get(0);
			delayQueue.remove(0);
			a.action.run();
		}
	}

	
	/**
	 * Called every activityTimeStep ms, checks for each Player if the last activity time stamp is more than activityDuration ms ago.
	 */
	private synchronized void checkPlayerActivity()
	{
		if (gameOver)
			return;
		
		long tooLong = (new Date().getTime()) - activityDuration;
		for (Player player : players)
		{
			if (playerActivityTimestamps.get(player) < tooLong && !inactivePlayers.contains(player))
			{
				inactivePlayers.add(player);
				playerInterface.onTimeoutStart(pin, player, timeoutDuration);
				addDelayedAction(new DelayedAction((new Date().getTime()) + timeoutDuration, () -> kickPlayerIfNoActivity(player)));
			}
		}
	}
	
	/**
	 * Called delayed if an activity check fails on a player. If still no activity / timeout cancel detected kick player
	 *
	 * @param player Player to be checked if still no activity registered.
	 */
	private synchronized void kickPlayerIfNoActivity(Player player)
	{
		long tooLong = (new Date().getTime()) - activityDuration;
		if (playerActivityTimestamps.containsKey(player) && playerActivityTimestamps.get(player) < tooLong)
		{
			playerInterface.onKick(pin, player);
			removePlayer(player, "kicked for AFK");
		}
		
		inactivePlayers.remove(player);
	}
	
	/**
	 * Called every aliveTimeStep ms, checks for each Player if the last alive ping is more than aliveDuration ms ago. Removes the player if no activity is detected.
	 */
	private synchronized void checkPlayerAlivePings()
	{
		if (gameOver)
			return;
		
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
	 * Called by the {@link QuizRoomManager} if a Player tries to join the QuizRoom.
	 *
	 * @param player
	 *            Player to join the room.
	 */
	protected synchronized void addPlayer(Player player)
	{
		if (players.contains(player))
		{
			throw new IllegalArgumentException("Player already in QuizRoom");
		}
		if (players.size() == maxPlayers)
		{
			throw new IllegalArgumentException("QuizRoom already full! (" + players.size() + "/" + maxPlayers + ")");
		}
		
		players.add(player);
		
		questionSystem.addPlayer(player);
		
		correctlyAnsweredQuestions.put(player, 0);
		totalAnsweredQuestions.put(player, 0);
		playerAlivePingTimestamps.put(player, new Date().getTime());
		
		eventCall(x -> x.onPlayerJoin(pin, player));
		
		// hot-join
		if (!wfpMode)
		{
			long now = new Date().getTime();
			playerActivityTimestamps.put(player, now);
			playerAlivePingTimestamps.put(player, now);

			// add additional question to distribute on next call
			questionSystem.addMissingQuestions(1);
		}
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
		eventCall(x -> x.onPlayerLeave(pin, player, reason));
		
		// remove QuizRoom if no more players in room
		if (players.size() == 1)
		{
			players.remove(player);
			onRoomClose();
			return;
		}
		else if (!wfpMode && players.size() == 2) // not lobby and only one player left
		{
			players.remove(player);
			players.remove(0);
			onRoomClose();
			return;
		}
		
		questionSystem.removePlayer(player);
		players.remove(player);
		
		//remove player data structures
		playerActivityTimestamps.remove(player);
		playerAlivePingTimestamps.remove(player);
		correctlyAnsweredQuestions.remove(player);
		totalAnsweredQuestions.remove(player);
	}
	
	/**
	 * Called roomStartDelay ms after all Players have readied up. Starts the onGameStart event call and distributes the initial questions to the players.
	 */
	private void onGameStart()
	{
		readyPlayers = null;
		
		long now = new Date().getTime() + roomStartDelay;
		for (Player player : players)
		{
			// initialize time stamps
			playerActivityTimestamps.put(player, now);
		}
		
		wfpMode = false;
		gameStartTime = new Date().getTime();
		
		eventCall(x -> x.onGameStart(pin));
		
		questionSystem.setMissingQuestions(players.size() - 1);
		questionSystem.distributeQuestion();
	}
	
	/**
	 * Called just before the room is closed. Cleans up the class.
	 */
	protected void onRoomClose()
	{
		eventCall(x -> x.onGameEnd(pin));
		
		wfpMode = true; // prevent processing of any frameUpdate calls on any runtime structures
		gameOver = true;
		
		timerFrameUpdate.stop();
		delayQueue.clear();
		
		updatePlayerStats();
		manager.removeRoom(pin); //de-register QuizRoom with QRManger
		
		LOGGER.debug("QuizRoom [" + pin + "] closed after " + timerFrameUpdate.getElapsedTime() + " ms.");
	}
	
	/**
	 * Updates the statistics of all Players currently in the Room with the tracked values. Called at the end of a game.
	 */
	private void updatePlayerStats()
	{
		PlayerService playerService = manager.getPlayerService();
		long endTime = new Date().getTime();
		long gameTime = endTime - gameStartTime;
		for (Player player : players)
		{
			player.addToTotalScore(score);
			player.addPlayTime(gameTime);
			player.setPlayedWithLast(players);
			player.addPlayToQSets(questionSets);
			player.addGameScore(endTime, score);
			
			if (!correctlyAnsweredQuestions.containsKey(player))
				LOGGER.error("### ERROR ### Missing correct answers entry for Player (id " + player.getId() + ")!");
			else
				player.AddCorrectAnswers(correctlyAnsweredQuestions.get(player));
			
			if (!totalAnsweredQuestions.containsKey(player))
				LOGGER.error("### ERROR ### Missing total answers entry for Player (id " + player.getId() + ")!");
			else
				player.addTotalAnswers(totalAnsweredQuestions.get(player));
			
			if (score > player.getHighScore())
				player.setHighScore(score);
			
			playerService.savePlayer(player);
		}
	}

    /**
     * Changes the score of the room according to the change code.
     *
     * @param code
     *            ChangeCode; Codes: 0 = wrong answer easy, 1 = wrong answer hard, 2 = right answer easy, 3 = right answer hard, 4 = timeout easy, 5 = timeout hard.
     * @param timeRemaining
     *            Time remaining on the Question
     */
    protected synchronized void changeScore(int code, long timeRemaining)
    {
        questionSystem.addCompletedQuestions(1);
        switch (code)
        {
            case 0:
                score -= (difficulty == RoomDifficulty.easy ? 50 : 75);
                break;
            case 1:
                score -= (difficulty == RoomDifficulty.easy ? 75 : 100);
                break;
            case 2:
                score += (difficulty == RoomDifficulty.easy ? 100 : 125) + (int) (timeRemaining / 1000);
                break;
            case 3:
                score += (difficulty == RoomDifficulty.easy ? 125 : 150) + (int) (timeRemaining / 1000);
                break;
            case 4:
                score -= (difficulty == RoomDifficulty.easy ? 50 : 75);
                break;
            case 5:
                score -= (difficulty == RoomDifficulty.easy ? 75 : 100);
                break;
        }
        
        eventCall(x -> x.onScoreChange(pin, score));
    }
	
	/*
	* ### IPlayerAction interface implementations. For documentation on the individual functions please see the interface definition. ###
	*/
	
	@Override
	public int getRoomPin()
	{
		return pin;
	}
	
	@Override
	public synchronized List<Player> getRoomPlayers()
	{
		List<Player> ps = new ArrayList<>(players.size());
		ps.addAll(players);
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
		List<String> QStrings = new ArrayList<>(questionSets.size());
		for (QuestionSet questionSet : questionSets)
		{
			QStrings.add(questionSet.getName());
		}
		return QStrings;
	}
	
	@Override
	public int getRoomScore()
	{
		return score;
	}
	
	@Override
	public synchronized List<Player> getRoomReadyPlayers()
	{
		if (!wfpMode)
			return null;
		
		List<Player> ps = new ArrayList<>(readyPlayers.size());
        ps.addAll(readyPlayers);
		return ps;
	}
	
	@Override
	public long getAlivePingTimeStep()
	{
		return aliveTimeStep;
	}
	
	@Override
	public boolean isRoomInWaitingMode()
	{
		return wfpMode;
	}
	
	@Override
	public synchronized void readyUp(Player p)
	{
		if (!wfpMode)
			return;
		
		// player already ready?
		if (readyPlayers.contains(p))
			return;
		
		readyPlayers.add(p);
		
		// are all players ready?
		if (readyPlayers.size() == players.size())
		{
			addDelayedAction(new DelayedAction((new Date().getTime()) + roomStartDelay, this::onGameStart));
		}
		
		eventCall(x -> x.onReadyUp(pin, p, readyPlayers.size()));
	}
	
	@Override
	public void answerQuestion(Player p, int questionId, int index)
	{
		if (wfpMode || gameOver)
		{
			return;
		}
		
		if (!playerActivityTimestamps.containsKey(p))
		{
			LOGGER.error("### ERROR ### [QR " + pin + "] Illegal call to answerQuestion! Player is not in QuizRoom! (id: " + p.getId() + ")");
			return;
		}
		
		ActiveQuestion q = questionSystem.getActiveQuestionById(questionId);
		try
		{
			questionSystem.answerQuestion(p, questionId);
		}
		catch (IllegalStateException e)
		{
			return;
		}
		
		// register player activity
		playerActivityTimestamps.put(p, new Date().getTime());
		
		// check if right answer
		if (index == 0)
		{
			changeScore(q.questionDifficulty == QuestionSetDifficulty.easy ? 2 : 3, q.timeRemaining);
			
			if (!correctlyAnsweredQuestions.containsKey(p))
				LOGGER.error("### ERROR ### Missing correctly answered entry for Player (id " + p.getId() + ")!");
			else
				correctlyAnsweredQuestions.put(p, correctlyAnsweredQuestions.get(p) + 1);
		}
		else
		{
			changeScore(q.questionDifficulty == QuestionSetDifficulty.easy ? 0 : 1, q.timeRemaining);
		}
		
		if (!totalAnsweredQuestions.containsKey(p))
			LOGGER.error("### ERROR ### Missing total answered entry for Player (id " + p.getId() + ")!");
		else
			totalAnsweredQuestions.put(p, totalAnsweredQuestions.get(p) + 1);
		
	}
	
	
	@Override
	public synchronized void useJoker(Player p)
	{
		if (numReshuffleJokers <= 0) // ignore if no jokers remaining
		{
			return;
		}
		
		if (!playerActivityTimestamps.containsKey(p))
		{
			LOGGER.error("Illegal call to useJoker! Player is not in QuizRoom! (id: " + p.getId() + ")");
			return;
		}
		
		// register player activity
		playerActivityTimestamps.put(p, new Date().getTime());
		
		numReshuffleJokers--;
		eventCall(x -> x.onJokerUse(pin, numReshuffleJokers));
		
		questionSystem.useJoker();
	}
	
	@Override
	public synchronized void cancelTimeout(Player p)
	{
		if (!playerActivityTimestamps.containsKey(p))
		{
			LOGGER.error("### ERROR ### [QR " + pin + "] Illegal call to cancelTimeout! Player is not in QuizRoom! (id: " + p.getId() + ")");
			return;
		}
		
		long tooLong = (new Date().getTime()) - activityDuration;
		if (playerActivityTimestamps.get(p) < tooLong)
			playerActivityTimestamps.put(p, new Date().getTime());
	}
	
	@Override
	public synchronized void leaveRoom(Player p)
	{
		if (!players.contains(p))
		{
			LOGGER.error("### ERROR ### [QR " + pin + "] Illegal call to leaveRoom! Player is not in QuizRoom! (id: " + p.getId() + ")");
			return;
		}
		
		removePlayer(p, "left");
	}
	
	@Override
	public synchronized void sendAlivePing(Player p)
	{
		if (gameOver)
			return;
		
		if (!playerAlivePingTimestamps.containsKey(p))
		{
			LOGGER.error("### ERROR ### [QR " + pin + "] Illegal call to sendAlivePing! Player is not in QuizRoom! (id: " + p.getId() + ")");
			return;
		}
		
		playerAlivePingTimestamps.put(p, new Date().getTime());
	}
	
	@Override
	public int getNumberOfJokers()
	{
		return numReshuffleJokers;
	}
	
}