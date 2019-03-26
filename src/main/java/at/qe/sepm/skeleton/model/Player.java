package at.qe.sepm.skeleton.model;

import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.data.domain.Persistable;

/**
 * Entity representing a Player.
 * 
 * @author Lorenz_Smidt
 *
 */
@Entity
public class Player implements Persistable<String>
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Number of Players to be saved in the "played with last" list.
	 */
	private static final int maxPlayedWithLast = 10;
	
	@Id
	@Column(length = 100)
	private String username;
	
	private String password;
	
	private String avatarPath;
	
	@ManyToOne(optional = false)
	private Manager creator;
	
	/**
	 * List of Players the Player played with recently. Number of players limited by maxPlayedWithLast. Sorted from least recently (index 0) to most recently played with.
	 */
	private List<Player> playedWithLast;
	
	@OneToMany
	private List<QuestionSetPerformance> qSetPerformances;
	
	public String getUsername()
	{
		return username;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public String getAvatarPath()
	{
		return avatarPath;
	}
	
	public void setAvatarPath(String avatarPath)
	{
		this.avatarPath = avatarPath;
	}
	
	public Manager getCreator()
	{
		return creator;
	}
	
	public void setCreator(Manager creator)
	{
		this.creator = creator;
	}
	
	/**
	 * Returns an iterator over the list of players the player recently played with. Iterator to prevent manipulation of reference.
	 * 
	 * @return
	 */
	public Iterator<Player> getPlayedWithLast()
	{
		return playedWithLast.iterator();
	}
	
	/**
	 * Adds the players to the list of players the player played with recently, removing duplicates and limits the number to maxPlayedWithLast.
	 * 
	 * @param players
	 */
	public void addToPlayedWithLast(List<Player> players)
	{
		for (Player player : players)
		{
			if (playedWithLast.contains(player))
			{
				playedWithLast.remove(player); // remove from last position
			}
			else if (playedWithLast.size() > maxPlayedWithLast)
			{
				playedWithLast.remove(0); // remove least recently played with
			}
			
			playedWithLast.add(player); // add new player at end of list (= most recently played with)
		}
	}
	
	public List<QuestionSetPerformance> getqSetPerformances()
	{
		return qSetPerformances;
	}
	
	public void setqSetPerformances(List<QuestionSetPerformance> qSetPerformances)
	{
		this.qSetPerformances = qSetPerformances;
	}
	
	@Override
	public String getId()
	{
		return username;
	}
	
	@Override
	public boolean isNew()
	{
		return false;
	}
	
}