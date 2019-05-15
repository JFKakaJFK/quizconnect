package at.qe.sepm.skeleton.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Controller;

import at.qe.sepm.skeleton.model.Player;
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
	@Qualifier("threadPoolTaskScheduler")
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
	 * Creates a new {@link QuizRoom} with the specified settings. Returns the pin
	 * of the QR.
	 * 
	 * @param maxPlayers
	 *            Maximum number of {@link Player}s in the QuizRoom.
	 * @param difficulty
	 *            Difficulty of the QuizRoom.
	 * @param gameMode
	 *            Game mode of the QuizRoom.
	 * @param qSets
	 *            List of QuestionSets to be used by the QuizRoom.
	 * @return Pin of the new QuizRoom.
	 */
	public int createRoom(int maxPlayers, RoomDifficulty difficulty, GameMode gameMode, List<QuestionSet> qSets, IRoomAction roomAction)
			throws IllegalArgumentException
	{
		if (maxPlayers < minimumPlayers)
			throw new IllegalArgumentException("QuizRoom max players cannot less than " + minimumPlayers + "!");
		else if (roomAction == null)
			throw new IllegalArgumentException("roomAction interface provided cannot be null!");
		// else if (qSets == null || qSets.size() == 0)
		// throw new IllegalArgumentException("QuizRoom question sets must contain at least one set!");
		
		int newPin = generatePin();
		QuizRoom newRoom = new QuizRoom(taskScheduler, this, newPin, maxPlayers, difficulty, gameMode, qSets, roomAction);
		rooms.put(newPin, newRoom);
		
		return newPin;
	}
	
	/**
	 * Used by a {@link Player} to join a {@link QuizRoom}.
	 * 
	 * @param roomPin
	 *            The pin of the QuizRoom to join.
	 * @param player
	 *            The Player to join the room.
	 * @param roomAction
	 *            Interface provided by the Player to the QuizRoom for QuizRoom to
	 *            Player communication.
	 * @return The IPlayerAction interface used for Player to QuizRoom
	 *         communication.
	 * @throws IllegalArgumentException
	 *             Thrown if the QuizRoom doesn't exist, if the roomAction interface
	 *             is invalid, or if the QuizRoom is already full.
	 */
	public IPlayerAction joinRoom(int roomPin, Player player) throws IllegalArgumentException
	{
		if (!rooms.containsKey(roomPin))
			throw new IllegalArgumentException("QuizRoom (pin " + roomPin + ") does not exist!");
		
		QuizRoom quizRoom = rooms.get(roomPin);
		
		boolean full = quizRoom.addPlayer(player);
		if (full)
			throw new IllegalArgumentException("QuizRoom already full!");

		return quizRoom;
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
	 * Returns true if a {@link QuizRoom} with pin currently exists.
	 * 
	 * @param pin
	 *            Pin of the QuizRoom.
	 * @return
	 */
	public boolean doesRoomExist(int pin)
	{
		return rooms.containsKey(pin);
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
		// int pin = createRoom(10, RoomDifficulty.easy, GameMode.normal, null);
	}
}