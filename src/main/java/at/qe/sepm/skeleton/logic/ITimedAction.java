package at.qe.sepm.skeleton.logic;

/**
 * Interface for {@link Timer} to make calls to a private function independent of implementation.
 */
public interface ITimedAction
{
	public void onTimeUpdate(long delta);
}