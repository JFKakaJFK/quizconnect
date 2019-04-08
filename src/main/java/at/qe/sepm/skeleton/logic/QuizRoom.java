package at.qe.sepm.skeleton.logic;

import java.util.LinkedList;
import java.util.List;

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
public class QuizRoom
{
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	// all times in ms
	private final long frameTimeStep = 50; // time between FramUpdate calls
	private final long activityTimeStep = 500; // time between player activity checks
	private final long activityDuration = 30000; // time until an activity ping invalidates
	private final long timeoutDuration = 10000; // time until player gets kicked after no activity
	private final long aliveTimeStep = 500; // expected time between alive pings
	private final long aliveDuration = 1000; // maximum time between alive pings
	
	private int pin;
	private List<Player> players;
	private int maxPlayers;
	private RoomDifficulty difficulty;
	private GameMode gameMode;
	private Timer timerFrameUpdate;
	
	private List<QuestionSet> questionSets;
	
	/**
	 * Initializes a new QR.
	 * 
	 * @param scheduler
	 * @param pin
	 * @param maxPlayers
	 * @param difficulty
	 * @param gameMode
	 * @param qSets
	 */
	public QuizRoom(ThreadPoolTaskScheduler scheduler, int pin, int maxPlayers, RoomDifficulty difficulty, GameMode gameMode, List<QuestionSet> qSets)
	{
		LOGGER.debug("QR [" + pin + "] started.");
		this.pin = pin;
		this.maxPlayers = maxPlayers;
		this.difficulty = difficulty;
		this.gameMode = gameMode;
		this.questionSets = qSets;
		
		players = new LinkedList<>();
		
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
	 * Gets called one every frameTimeStep ms by timerFrameUpdate.
	 * 
	 * @param deltaTime
	 */
	public void onFrameUpdate(long deltaTime)
	{
		if (deltaTime > 2 * frameTimeStep)
		{
			LOGGER.debug("large delay in frameUpdate call of QR [" + pin + "] (" + deltaTime + "ms)");
		}
		
		// LOGGER.debug("frame call after " + timerFrameUpdate.getElapsedTime() + " ms.");
	}
	
}