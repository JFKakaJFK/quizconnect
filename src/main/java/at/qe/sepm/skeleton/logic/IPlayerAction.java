package at.qe.sepm.skeleton.logic;

import java.util.List;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.QuestionSet;

/**
 * Interface for {@link Player} to {@link QuizRoom} communication. Implemented by QuizRoom and provided to each player UI when joining the QuizRoom.
 * 
 * @author Lorenz_Smidt
 *
 */
public interface IPlayerAction
{
	
	/**
	 * @return The pin of the QuizRoom.
	 */
	public int getRoomPin();
	
	/**
	 * @return A list of all {@link Player}s in the QuizRoom. Creates a copy each time. Use getRoomPlayerCount if you only need the number of players! Synchronized across Players.
	 */
	public List<Player> getRoomPlayers();
	
	/**
	 * @return The number of {@link Player}s in the QuizRoom.
	 */
	public int getRoomPlayerCount();
	
	/**
	 * @return The Difficulty of the QuizRoom.
	 */
	public RoomDifficulty getRoomDifficulty();

	/**
	 * @return The GameMode of the QuizRoom.
	 */
	public GameMode getRoomMode();

	/**
	 * @return A list of names of all {@link QuestionSet}s used in the QuizRoom. Creates a copy each time. Synchronized across Players.
	 */
	public List<String> getRoomQuestionSets();
	
	/**
	 * @return The current score of the QuizRoom. Avoid using, use the IRoomAction.onScoreChange() events instead to update score!
	 */
	public int getRoomScore();
	
	/**
	 * @return A list of all ready {@link Players} in the QuizRoom. Only works during 'waiting for players' stage. Creates a copy each time. Synchronized across Players.
	 */
	public List<Player> getRoomReadyPlayers();
	
	/**
	 * @return The expected time between alive pings in ms.
	 */
	public long getAlivePingTimeStep();
	
	/**
	 * @return The number of Jokers remaining in the QR.
	 */
	public int getNumberOfJokers();
	
	/**
	 * @return True if the {@link QuizRoom} is in 'waiting for players' mode, false if game is currently ongoing.
	 */
	public boolean isRoomInWaitingMode();

	/*
	 * ### ALL FUNCTIONS BENEATH THIS POINT MUST PROVIDE A REFERENCE TO THE PLAYER WHO MAKES THE CALL! NECESSARY TO DIFFERENTIATE PLAYERS IN THE QUIZROOM. ###
	 */
	
	/**
	 * Marks the {@link Player} as ready. Ready status can't be revoked after calling this. Only works during 'waiting for players' stage. Synchronized across Players.
	 * 
	 * @param p
	 *            The Player making the call.
	 */
	public void readyUp(Player p);
	
	/**
	 * Called when the {@link Player} chooses the index'th answer of {@link Question} with id questionId. Player has to have the Question currently assigned. Counts as activity. Synchronized across
	 * Players.
	 * 
	 * @param p
	 *            The Player making the call.
	 * @param questionId
	 *            The id of the Question to be answered.
	 * @param index
	 *            Index of the Answer to be used. (e.g. 0 = right answer, 1 = wrong answer 1, 2 = wrong answer 2, ...); see {@link IRoomAction}.assignAnswer for more info.
	 */
	public void answerQuestion(Player p, int questionId, int index);
	
	/**
	 * Called when the {@link Player} chooses to use a Joker. Only works if a Joker is still available. Counts as activity. Synchronized across Players.
	 * 
	 * @param p
	 *            The Player making the call.
	 */
	public void useJoker(Player p);
	
	/**
	 * Called if the {@link Player} terminates the AFK timeout. If this is not called before timeoutTime (provided in the event call) runs out the Player will get kicked (see {@link IRoomAction}.onKick
	 * for more info). Only works if the Player is currently in AFK timeout. Does not work as an activity ping!
	 * 
	 * @param p
	 *            The Player making the call.
	 */
	public void cancelTimeout(Player p);
	
	/**
	 * Called if the {@link Player} leaves the QuizRoom naturally (i.e. not kicked). No more calls from this Player to the QuizRoom are permitted after calling this. Synchronized across Players.
	 * 
	 * @param p
	 *            The Player making the call.
	 */
	public void leaveRoom(Player p);

	/**
	 * Called by the {@link Player} client to signal the QuizRoom that the Player is still connected. If no pings are received for some time the Player will be registered as disconnected. Does not count
	 * as activity!
	 * 
	 * @param p
	 *            The Player making the call.
	 */
	public void sendAlivePing(Player p);

}