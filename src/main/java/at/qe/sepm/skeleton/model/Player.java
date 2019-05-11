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
	 * List of Players that were in the same game that this Player last played. May be null or empty.
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> playedWithLast;
	
	/**
	 * Returns the List of usernames of the Players in the last game this Player played.
	 * 
	 * @return
	 */
	public List<String> getPlayedWithLast()
	{
		return playedWithLast;
	}
	
	/**
	 * Stores the Players as last played with. Don't forget to save the Player after updating any stats!
	 * 
	 * @param players
	 */
	public void setPlayedWithLast(List<Player> players)
	{
		playedWithLast = new ArrayList<String>();
		if (players == null)
			return;
		for (int i = 0; i < players.size(); i++)
		{
			Player player = players.get(i);
			if (player.getId() == this.id)
				continue;
			
			playedWithLast.add(player.getUser().getUsername());
		}
	}
	
	/**
	 * Returns the rank of the Player. Rank is calculated from total score and accuracy.
	 */
	public String getPlayerRank()
	{
		long actualScore = (long) (stat_totalScore * getPlayerAccuracy());
		if (actualScore < 5000)
			return "Noob";
		else if (actualScore < 10000)
			return "Beginner";
		else if (actualScore < 20000)
			return "Learner";
		else if (actualScore < 40000)
			return "Getting There";
		else if (actualScore < 60000)
			return "Almost Good";
		else if (actualScore < 80000)
			return "Well Established";
		else if (actualScore < 100000)
			return "Proficient";
		else if (actualScore < 150000)
			return "Master";
		else if (actualScore < 250000)
			return "Grand Master";
		else if (actualScore < 500000)
			return "Demi-God";
		else
			return "200 IQ";
	}
	
	/**
	 * Returns the accuracy of the Player (= correct / total answers).
	 */
	public float getPlayerAccuracy()
	{
		return (float) stat_correctAnswers / (float) stat_totalAnswers;
	}
	
	/**
	 * Returns the total score of the Player.
	 */
	public long getTotalScore()
	{
		return stat_totalScore;
	}
	
	/**
	 * Adds the additionalScore to the total score of the Player. Don't forget to save the Player after updating any stats!
	 * 
	 * @param additionalScore
	 */
	public void addToTotalScore(long additionalScore)
	{
		this.stat_totalScore += additionalScore;
	}
	
	/**
	 * Returns the highscore of the Player.
	 */
	public int getHighScore()
	{
		return stat_highScore;
	}
	
	/**
	 * Sets the new highscore of the Player. Don't forget to save the Player after updating any stats!
	 * 
	 * @param stat_highScore
	 */
	public void setHighScore(int stat_highScore)
	{
		this.stat_highScore = stat_highScore;
	}
	
	/**
	 * Returns the number of correct answers of the Player.
	 */
	public int getCorrectAnswersCount()
	{
		return stat_correctAnswers;
	}
	
	/**
	 * Adds additionalRightAnswers to the number of correct answers of the Player. Don't forget to save the Player after updating any stats!
	 * 
	 * @param additionalRightAnswers
	 */
	public void AddCorrectAnswers(int additionalRightAnswers)
	{
		this.stat_correctAnswers += additionalRightAnswers;
	}
	
	/**
	 * Returns the total number of answers of the Player, right or wrong.
	 */
	public int getTotalAnswers()
	{
		return stat_totalAnswers;
	}
	
	/**
	 * Adds additionalTotalAnswers to the total number of answers of the Player. Don't forget to save the Player after updating any stats!
	 * 
	 * @param additionalTotalAnswers
	 */
	public void addTotalAnswers(int additionalTotalAnswers)
	{
		this.stat_totalAnswers += additionalTotalAnswers;
	}
	
	/**
	 * Returns the total play time of the Player in ms.
	 */
	public long getPlayTime()
	{
		return stat_playTime;
	}
	
	/**
	 * Adds additionalPlayTime ms to the total play time of the Player. Don't forget to save the Player after updating any stats!
	 * 
	 * @param additionalPlayTime
	 */
	public void addPlayTime(long additionalPlayTime)
	{
		this.stat_playTime += additionalPlayTime;
	}
	
	/**
	 * Returns a Map from QuestionSetIds to number of times played of all QuestionSets the Player has played. If no entry for a QS exists the Player hasn't played it yet.
	 */
	public Map<Integer, Integer> getqSetPlayCounts()
	{
		return qSetPlayCounts;
	}
	
	/**
	 * Adds one play count to each QuestionSet in the list. Don't forget to save the Player after updating any stats!
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
	 * Removes a QuestionSet id from the map of played ones. To be used if a QuestionSet no longer exists. Don't forget to save the Player after updating any stats!
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