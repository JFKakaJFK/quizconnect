package at.qe.sepm.skeleton.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Controller;

import at.qe.sepm.skeleton.model.QuestionSet;

/**
 * Manager for creating and joining {@link QuizRoom}s.
 * 
 * @author Lorenz_Smidt
 *
 */
@Scope("application")
@Controller
public class QuizRoomManager implements ApplicationListener<ContextRefreshedEvent>
{
	public static boolean DEBUG = true;
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private final int minimumPlayers = 3;
	
	@Autowired
	ThreadPoolTaskScheduler taskScheduler;
	
	private HashMap<Integer, QuizRoom> rooms;
	
	/**
	 * Initializes the QRmanager, called on startup.
	 */
	@PostConstruct
	public void init()
	{
		rooms = new HashMap<>();
	}
	
	/**
	 * Creates a new {@link QuizRoom} with the specified settings. Returns the pin of the QR.
	 * 
	 * @param maxPlayers
	 * @param difficulty
	 * @param gameMode
	 * @param qSets
	 * @return pin of the new QR.
	 */
	public int createRoom(int maxPlayers, RoomDifficulty difficulty, GameMode gameMode, List<QuestionSet> qSets) throws IllegalArgumentException
	{
		if (maxPlayers < minimumPlayers)
			throw new IllegalArgumentException("QuizRoom max players cannot less than " + minimumPlayers + "!");
		// else if (qSets == null || qSets.size() == 0)
		// throw new IllegalArgumentException("QuizRoom question sets must contain at least one set!");
		
		int newPin = generatePin();
		QuizRoom newRoom = new QuizRoom(taskScheduler, newPin, maxPlayers, difficulty, gameMode, qSets);
		rooms.put(newPin, newRoom);
		
		return newPin;
	}
	
	/**
	 * Removes the QuizRoom. DO NOT USE FROM FRONTEND! Gets called automatically by a QuizRoom once a game ends.
	 * 
	 * @param pin
	 */
	public void removeRoom(int pin) throws IllegalArgumentException
	{
		if (rooms.containsKey(pin))
		{
			rooms.remove(pin);
		}
		else
		{
			throw new IllegalArgumentException("QuizRoom to remove in QRmanager does not exist!");
		}
	}
	
	/**
	 * Returns a new unique pin for a QR.
	 * 
	 * @return
	 */
	private int generatePin()
	{
		int pin;
		do
		{
			pin = (int) ((new Date().getTime()) % 1000000);
		} while (rooms.containsKey(pin));
		
		return pin;
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0)
	{
		if (!DEBUG)
			return;
		LOGGER.debug("start");
		int pin = createRoom(10, RoomDifficulty.easy, GameMode.normal, null);
	}
}