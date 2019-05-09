package at.qe.sepm.skeleton.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
	
	private long stat_totalScore;
	
	private int stat_highScore;
	
	private int stat_correctAnswers;
	
	private int stat_totalAnswers;
	
	private long stat_playTime; // time in ms
	
	/**
	 * Map of {@link QuestionSet}s (represented by their ids) to the number of times the Player has played the QS. If no entry exists the QS has not been played yet. If a QS no longer exists, please use
	 * the 'removeQSetFromPlayed()' function to remove it from the map.
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	private Map<Integer, Integer> qSetPlayCounts;
	
	/**
	 * List of Players that were in the same game that this Player last played. Size is limited to maxPlayedWithLast but may be less. May be null.
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> playedWithLast;
	
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
	 * Stores the Players as last played with. Limits the number of stored players to maxPlayedWithLast.
	 * 
	 * @param players
	 */
	public void setPlayedWithLast(List<Player> players)
	{
		playedWithLast = new ArrayList<String>();
		for (int i = 0; i < (players.size() > maxPlayedWithLast ? maxPlayedWithLast : players.size()); i++)
		{
			Player player = players.get(i);
			if (player.getId() == this.id)
				continue;
			
			playedWithLast.add(player.getUser().getUsername());
		}
	}
	
	public long getTotalScore()
	{
		return stat_totalScore;
	}
	
	public void addToTotalScore(long additionalScore)
	{
		this.stat_totalScore += additionalScore;
	}
	
	public int getHighScore()
	{
		return stat_highScore;
	}
	
	public void setHighScore(int stat_highScore)
	{
		this.stat_highScore = stat_highScore;
	}
	
	public int getCorrectAnswersCount()
	{
		return stat_correctAnswers;
	}
	
	public void AddCorrectAnswers(int additionalRightAnswers)
	{
		this.stat_correctAnswers += additionalRightAnswers;
	}
	
	public int getTotalAnswers()
	{
		return stat_totalAnswers;
	}
	
	public void addTotalAnswers(int additionalTotalAnswers)
	{
		this.stat_totalAnswers += additionalTotalAnswers;
	}
	
	public long getPlayTime()
	{
		return stat_playTime;
	}
	
	public void addPlayTime(long additionalPlayTime)
	{
		this.stat_playTime += additionalPlayTime;
	}
	
	public Map<Integer, Integer> getqSetPlayCounts()
	{
		return qSetPlayCounts;
	}
	
	/**
	 * Adds one play count to each QuestionSet in the list.
	 * 
	 * @param qSets
	 */
	public void addPlayToQSets(List<QuestionSet> qSets)
	{
		for (QuestionSet questionSet : qSets)
		{
			if (qSetPlayCounts.containsKey(questionSet.getId()))
			{
				qSetPlayCounts.put(questionSet.getId(), qSetPlayCounts.get(questionSet.getId()) + 1);
			}
			else
			{
				qSetPlayCounts.put(questionSet.getId(), 1);
			}
		}
	}
	
	/**
	 * Removes a QuestionSet id from the map of played ones. To be used if a QuestionSet no longer exists.
	 * 
	 * @param qSetId
	 */
	public void removeQSetFromPlayed(Integer qSetId)
	{
		if (qSetPlayCounts.containsKey(qSetId))
		{
			qSetPlayCounts.remove(qSetId);
		}
	}
	
	/**
	 * Gets called automatically upon Player creation - DO NOT USE OTHERWISE!
	 */
	public void setqSetPlayCounts(Map<Integer, Integer> qSetPlayCounts)
	{
		this.qSetPlayCounts = qSetPlayCounts;
	}
	
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