package at.qe.sepm.skeleton.logic;

/**
 * Class for executing actions at a certain time. Used by {@link QuizRoom}.
 * 
 * @author Lorenz_Smidt
 *
 */
public class DelayedAction
{
	public long execTime;
	public Runnable action;
	
	public DelayedAction(long execTime, Runnable action)
	{
		this.execTime = execTime;
		this.action = action;
	}
}