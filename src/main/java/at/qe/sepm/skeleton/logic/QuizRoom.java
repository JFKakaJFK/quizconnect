package at.qe.sepm.skeleton.logic;

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
	
	private int pin;
	private QuizRoomManager manager;
	private List<Player> players;
	private HashMap<Player, IRoomAction> playerInterfaces;
	private int maxPlayers;
	private RoomDifficulty difficulty;
	private GameMode gameMode;
	private Timer timerFrameUpdate;
	private volatile List<DelayedAction> delayQueue;
	
	private List<QuestionSet> questionSets;
	
	int temp = 0;
	
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
			List<QuestionSet> qSets)
	{
		LOGGER.debug("QuizRoom [" + pin + "] started.");
		this.pin = pin;
		this.manager = manager;
		this.maxPlayers = maxPlayers;
		this.difficulty = difficulty;
		this.gameMode = gameMode;
		this.questionSets = qSets;
		
		players = new LinkedList<>();
		playerInterfaces = new HashMap<>(maxPlayers);
		delayQueue = new LinkedList<>();
		
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
		
		// temporary automatic close of QR after 10 sec.
		temp++;
		if (temp > 200)
			onRoomClose();
		
	}
	
	/**
	 * Executes f for each Player in the QuizRoom.
	 * 
	 * @param f
	 *            Function to be executed on all Player interfaces.
	 */
	private synchronized void eventCall(Consumer<IRoomAction> f)
	{
		for (IRoomAction action : playerInterfaces.values())
		{
			f.accept(action);
		}
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
	 * Called by the {@link QuizRoomManager} if a Player tries to join the QuizRoom.
	 * 
	 * @param player
	 *            Player to join the room.
	 * @param roomAction
	 *            Interface for communication to the Player.
	 * @return True if join successful, false if room is full.
	 */
	public synchronized boolean addPlayer(Player player, IRoomAction roomAction)
	{
		if (players.size() == maxPlayers)
		{
			return false;
		}
		
		players.add(player);
		playerInterfaces.put(player, roomAction);
		
		eventCall(x -> {
			x.onPlayerJoin(player);
		});
		
		return true;
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
	
	@Override
	public int getRoomPin()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public List<Player> getRoomPlayers()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int getRoomPlayerCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public RoomDifficulty getRoomDifficulty()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public GameMode getRoomMode()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<String> getRoomQuestionSets()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int getRoomScore()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public List<Player> getRoomReadyPlayers()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public long getAlivePingTimeStep()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void readyUp(Player p)
	{
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void sendAlivePing(Player p)
	{
		// TODO Auto-generated method stub
		
	}
	
}