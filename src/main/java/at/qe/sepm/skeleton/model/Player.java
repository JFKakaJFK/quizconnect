package at.qe.sepm.skeleton.model;

import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.data.domain.Persistable;

/**
 * Entity representing a Player.
 * 
 * @author Lorenz_Smidt
 *
 */
@Entity
public class Player implements Persistable<Integer>
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Number of Players to be saved in the "played with last" list.
	 */
	private static final int maxPlayedWithLast = 10;
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@OneToOne(optional = false, fetch = FetchType.EAGER)
	private User user;
	
	private String avatarPath;
	
	@ManyToOne(optional = false)
	private Manager creator;
	
	/**
	 * List of Players the Player played with recently. Number of players limited by maxPlayedWithLast. Sorted from least recently (index 0) to most recently played with.
	 */
	@ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	private List<Player> playedWithLast;
	
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "player", fetch = FetchType.LAZY)
	private List<QuestionSetPerformance> qSetPerformances;
	
	public User getUser()
	{
		return user;
	}
	
	public void setUser(User user)
	{
		this.user = user;
		user.setRole(UserRole.PLAYER);
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
	
	// TODO rewrite to add / update performances of individual QuestionSets
	public void setqSetPerformances(List<QuestionSetPerformance> qSetPerformances)
	{
		this.qSetPerformances = qSetPerformances;
	}
	
	public void setId(Integer id)
	{
		this.id = id;
	}
	
	@Override
	public Integer getId()
	{
		return id;
	}
	
	@Override
	public boolean isNew()
	{
		return id == null;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}