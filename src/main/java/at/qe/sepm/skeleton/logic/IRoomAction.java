package at.qe.sepm.skeleton.logic;

import at.qe.sepm.skeleton.model.Player;

/**
 * Interface for {@link QuizRoom} to {@link Player} communication. Must be implemented by the Player UI and provided to the QuizRoom when joining.
 * 
 * @author Lorenz_Smidt
 *
 */
public interface IRoomAction
{
	/**
	 * Called when a Player in the QR readies up.
	 * 
	 * @param p
	 *            The Player who declared themselves ready.
	 * @param totalReady
	 *            Number of Players currently ready.
	 */
	public void onReadyUp(Player p, int totalReady);
	
	/**
	 * Called when a Player joins the QR.
	 * 
	 * @param p
	 *            The Player who joined.
	 */
	public void onPlayerJoin(Player p);
	
	/**
	 * Called when the QR starts (= all players are ready)
	 */
	public void onGameStart();
	
	/**
	 * Called when the QR ends. No more calls to the {@link IPlayerAction} interface are allowed after this event.
	 */
	public void onGameEnd();
	
	/**
	 * Called when a joker is used in the QR.
	 */
	public void onJokerUse();
	
	/**
	 * Called when a Player leaves the QR either actively or from being kicked.
	 * 
	 * @param p
	 *            The Player who left.
	 * @param reason
	 *            Reason for the Player leaving (e.g. 'disconnected', 'kicked').
	 */
	public void onPlayerLeave(Player p, String reason);
	
	/**
	 * Called when the current score of the QR changes.
	 * 
	 * @param newScore
	 *            The new score.
	 */
	public void onScoreChange(int newScore);
	
	/**
	 * Called every timerSyncTimeStep ms to synchronize the remaining time on the current Player question.
	 * 
	 * @param p
	 *            Player the timer synchronized.
	 * @param q
	 *            Question of the Player to be synchronized.
	 * @param currentTime
	 *            The time remaining on the current ActiveQuestion in ms.
	 */
	public void onTimerSync(Player p, ActiveQuestion q, long currentTime);
	
	/**
	 * Called when the AFK timeout for the Player starts due to missing activity pings.
	 * 
	 * @param timeoutTime
	 *            Time until inactivity kick occurs in ms.
	 */
	public void onTimeoutStart(long timeoutTime);
	
	/**
	 * Called when the Player gets kicked from the QR. No more calls to the {@link IPlayerAction} interface are allowed after this event from the Player.
	 * 
	 * @param p
	 *            Player to be kicked.
	 */
	public void onKick(Player p);
	
	/**
	 * Called when the QR assigns the Player a new Question. MIGHT NEED TO BE ADJUSTED TO ACCOMODATE REVERSE GAMEMODE!
	 * 
	 * @param p
	 *            Player to have the Question assigned to.
	 * @param q
	 *            The ActiveQuestion assigned to the Player.
	 */
	public void assignQuestion(Player p, ActiveQuestion q);
	
	/**
	 * Called when the QR assigns the Player a new Question answer. MIGHT NEED TO BE ADJUSTED TO ACCOMODATE REVERSE GAMEMODE!
	 * 
	 * @param p
	 *            Player to have the answer assigned to.
	 * @param q
	 *            The ActiveQuestion assigned to the Player. To be used when making answer calls in IPlayerAction.
	 * @param index
	 *            Index of the Answer to be used. (e.g. 0 = right answer, 1 = wrong answer 1, 2 = wrong answer 2, ...)
	 */
	public void assignAnswer(Player p, ActiveQuestion q, int index);
	
	/**
	 * Called when the QR unassigns the Question from the Player. Player has no active question after this call.
	 * 
	 * @param p
	 *            Player to have the Question removed.
	 * @param q
	 *            The ActiveQuesiton unassigned from the Player.
	 */
	public void removeQuestion(Player p, ActiveQuestion q);
	
	/**
	 * Called when the QR unassigns ALL Answers associated with the ActiveQuestion from the Player.
	 * 
	 * @param p
	 *            Player to have all answers of the Question removed from.
	 * @param q
	 *            The ActiveQuestion from which all Answers are unassigned from the Player.
	 */
	public void removeAnswer(Player p, ActiveQuestion q);
}