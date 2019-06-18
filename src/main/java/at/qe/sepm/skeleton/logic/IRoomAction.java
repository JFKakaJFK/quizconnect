package at.qe.sepm.skeleton.logic;

import at.qe.sepm.skeleton.model.Player;

/**
 * Interface for {@link QuizRoom} to {@link Player} communication. Must be implemented by the Player UI and provided to the QuizRoom when joining (done through the {@link QuizRoomManager}).
 * 
 * @author Lorenz_Smidt
 *
 */
public interface IRoomAction
{
	/**
	 * Called when a Player in the QR readies up.
	 * 
	 * @param pin
	 *            Pin of the QuizRoom making the call.
	 * @param p
	 *            The Player who declared themselves ready.
	 * @param totalReady
	 *            Number of Players currently ready.
	 */
	public void onReadyUp(int pin, Player p, int totalReady);
	
	/**
	 * Called when a Player joins the QR.
	 * 
	 * @param pin
	 *            Pin of the QuizRoom making the call.
	 * @param p
	 *            The Player who joined.
	 */
	public void onPlayerJoin(int pin, Player p);
	
	/**
	 * Called when the QR starts (= all players are ready)
	 * 
	 * @param pin
	 *            Pin of the QuizRoom making the call.
	 */
	public void onGameStart(int pin);
	
	/**
	 * Called when the QR ends. No more calls to the {@link IPlayerAction} interface are allowed after this event.
	 * 
	 * @param pin
	 *            Pin of the QuizRoom making the call.
	 */
	public void onGameEnd(int pin);
	
	/**
	 * Called when a joker is used in the QR.
	 * 
	 * @param pin
	 *            Pin of the QuizRoom making the call.
	 * @param remaining
	 *            Number of Jokers remaining.
	 */
	public void onJokerUse(int pin, int remaining);
	
	/**
	 * Called when a Player leaves the QR either actively or from being kicked.
	 * 
	 * @param pin
	 *            Pin of the QuizRoom making the call.
	 * @param p
	 *            The Player who left.
	 * @param reason
	 *            Reason for the Player leaving (e.g. 'disconnected', 'kicked').
	 */
	public void onPlayerLeave(int pin, Player p, String reason);
	
	/**
	 * Called when the current score of the QR changes.
	 * 
	 * @param pin
	 *            Pin of the QuizRoom making the call.
	 * @param newScore
	 *            The new score.
	 */
	public void onScoreChange(int pin, int newScore);
	
	/**
	 * Called every timerSyncTimeStep ms to synchronize the remaining time on the current Player question.
	 * 
	 * @param pin
	 *            Pin of the QuizRoom making the call.
	 * @param p
	 *            Player the timer synchronized.
	 * @param q
	 *            Question of the Player to be synchronized.
	 * @param currentTime
	 *            The time remaining on the current ActiveQuestion in ms.
	 */
	public void onTimerSync(int pin, Player p, ActiveQuestion q, long currentTime);
	
	/**
	 * Called when the AFK timeout for the Player starts due to missing activity pings.
	 * 
	 * @param pin
	 *            Pin of the QuizRoom making the call.
	 * @param p
	 *            Player to have the afk timeout start.
	 * @param timeoutTime
	 *            Time until inactivity kick occurs in ms.
	 */
	public void onTimeoutStart(int pin, Player p, long timeoutTime);
	
	/**
	 * Called when the Player gets kicked from the QR. No more calls to the {@link IPlayerAction} interface are allowed after this event from the Player.
	 * 
	 * @param pin
	 *            Pin of the QuizRoom making the call.
	 * @param p
	 *            Player to be kicked.
	 */
	public void onKick(int pin, Player p);
	
	/**
	 * Called when all Players in the QuizRoom are marked as ready, starting the 5 second countdown until game start.
	 *
	 * @param pin
	 * 		PIn of the QuizRoom making the call.
	 */
	public void onAllReady(int pin);
	
	/**
	 * Called when the QR assigns a new Question.
	 * 
	 * @param pin
	 *            Pin of the QuizRoom making the call.
	 * @param q
	 *            The ActiveQuestion assigned.
	 */
	public void assignQuestion(int pin, ActiveQuestion q);
	
	/**
	 * Called when the QR unassigns the Question. One Player has no active question after this call.
	 * 
	 * @param pin
	 *            Pin of the QuizRoom making the call.
	 * @param q
	 *            The ActiveQuestion removed from play.
	 */
	public void removeQuestion(int pin, ActiveQuestion q);
	
}