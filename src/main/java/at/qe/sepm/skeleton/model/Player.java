package at.qe.sepm.skeleton.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
	
	@Column(nullable = true, length = 200)
	private String avatarPath;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Manager creator;
	
	/**
	 * List of player usernames the Player played with recently. Number of players limited by maxPlayedWithLast. Sorted from least recently (index 0) to most recently played with.
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> playedWithLast;

	@Deprecated // TODO remove deprecated
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "player", fetch = FetchType.LAZY)
	private List<QuestionSetPerformance> qSetPerformances;
	
	public User getUser()
	{
		return user;
	}
	
	/**
	 * Gets called automatically upon creation of the Player. Should not be called manually!
	 * 
	 * @param user
	 */
	public void setUser(User user)
	{
		this.user = user;
	}
	
	public String getAvatarPath()
	{
		return avatarPath;
	}
	
	public void setAvatarPath(String avatarPath)
	{
		this.avatarPath = avatarPath;
	}

	@Deprecated // TODO remove deprecated
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
	public List<String> getPlayedWithLast()
	{
		return playedWithLast;
	}
	
	/**
	 * Adds the players' usernames to the list of players the player played with recently, removing duplicates and limits the number to maxPlayedWithLast.
	 * 
	 * @param player
	 */
	public void addToPlayedWithLast(Player player)
	{
		String username = player.getUser().getUsername();
		if (playedWithLast.contains(username))
		{
			playedWithLast.remove(username); // remove from last position
		}
		else if (playedWithLast.size() > maxPlayedWithLast)
		{
			playedWithLast.remove(0); // remove least recently played with
		}
		
		playedWithLast.add(username); // add new player at end of list (= most recently played with)
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