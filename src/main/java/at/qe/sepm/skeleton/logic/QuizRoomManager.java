package at.qe.sepm.skeleton.logic;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
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
public class QuizRoomManager
{
	private final int minimumPlayers = 3;
	
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
		else if (qSets == null || qSets.size() == 0)
			throw new IllegalArgumentException("QuizRoom question sets must contain at least one set!");
		
		int newPin = generatePin();
		QuizRoom newRoom = new QuizRoom(newPin, maxPlayers, difficulty, gameMode, qSets);
		rooms.put(newPin, newRoom);
		
		return newPin;
	}
	
	/**
	 * Returns a new unique pin for a QR.
	 * 
	 * @return
	 */
	private int generatePin()
	{
		return 0;
	}
}