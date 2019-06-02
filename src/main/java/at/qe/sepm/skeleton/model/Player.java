package at.qe.sepm.skeleton.model;

import java.util.*;

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
	
	private static final int maxSavedRecentGameScores = 10;
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@OneToOne(optional = false, fetch = FetchType.EAGER)
	private User user;
	
	@Column(length = 200)
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
	private Set<String> playedWithLast;
	
	/**
	 * Map storing the final scores of the last 10 games played by this Player. Mapping from time stamp of game end to the score.
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	private Map<Long, Integer> lastScores;
	
	/**
	 * @return The List of usernames of the Players in the last game this Player played.
	 */
	public List<String> getPlayedWithLast()
	{
		return new ArrayList<>(playedWithLast);
	}
	
	/**
	 * Stores the Players as last played with. Don't forget to save the Player after updating any stats!
	 * 
	 * @param players Players to be stored.
	 */
	public void _setPlayedWithLast(List<Player> players)
	{
		playedWithLast = new HashSet<>();
		if (players == null)
			return;
		for (Player player : players)
		{
			if (player.getId().equals(this.id))
				continue;
			
			playedWithLast.add(player.getUser().getUsername());
		}
	}
	
	public void setPlayedWithLast(Set<String> players)
	{
		this.playedWithLast = players;
	}
	
	/**
	 * @return The rank of the Player. Rank is calculated from total score and accuracy.
	 */
	public String getPlayerRank()
	{
		long actualScore = stat_totalScore; // (long) (stat_totalScore * getPlayerAccuracy());
		if (actualScore < -100000)
			return "Just stop playing (please)";
		else if (actualScore < -80000)
			return "Falling off the earth";
		else if (actualScore < -60000)
			return "Australian Astronaut";
		else if (actualScore < -40000)
			return "Australian";
		else if (actualScore < -20000)
			return "Squid";
		else if (actualScore < -10000)
			return "Atlantis";
		else if (actualScore < -5000)
			return "Submarine";
		else if (actualScore < 0)
			return "Scuba Diver";
		else if (actualScore < 5000)
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
	 * @param additionalScore Score to add to total.
	 */
	public void addToTotalScore(long additionalScore)
	{
		this.stat_totalScore += additionalScore;
	}
	
	/**
	 * @return The highscore of the Player.
	 */
	public int getHighScore()
	{
		return stat_highScore;
	}
	
	/**
	 * Sets the new highscore of the Player. Don't forget to save the Player after updating any stats!
	 * 
	 * @param stat_highScore The new highscore.
	 */
	public void setHighScore(int stat_highScore)
	{
		if (stat_highScore > this.stat_highScore)
			this.stat_highScore = stat_highScore;
	}
	
	/**
	 * @return The number of correct answers of the Player.
	 */
	public int getCorrectAnswersCount()
	{
		return stat_correctAnswers;
	}
	
	/**
	 * Adds additionalRightAnswers to the number of correct answers of the Player. Don't forget to save the Player after updating any stats!
	 * 
	 * @param additionalRightAnswers Number of right answers to add.
	 */
	public void AddCorrectAnswers(int additionalRightAnswers)
	{
		this.stat_correctAnswers += additionalRightAnswers;
	}
	
	/**
	 * @return The total number of answers of the Player, right and wrong.
	 */
	public int getTotalAnswers()
	{
		return stat_totalAnswers;
	}
	
	/**
	 * Adds additionalTotalAnswers to the total number of answers of the Player. Don't forget to save the Player after updating any stats!
	 * 
	 * @param additionalTotalAnswers Number of answers to add.
	 */
	public void addTotalAnswers(int additionalTotalAnswers)
	{
		this.stat_totalAnswers += additionalTotalAnswers;
	}
	
	/**
	 * @return The total play time of the Player in ms.
	 */
	public long getPlayTime()
	{
		return stat_playTime;
	}
	
	/**
	 * Adds additionalPlayTime ms to the total play time of the Player. Don't forget to save the Player after updating any stats!
	 * 
	 * @param additionalPlayTime Time to add in ms.
	 */
	public void addPlayTime(long additionalPlayTime)
	{
		this.stat_playTime += additionalPlayTime;
	}
	
	/**
	 * @return A Map from QuestionSetIds to number of times played of all QuestionSets the Player has played. If no entry for a QS exists the Player hasn't played it yet.
	 */
	public Map<Integer, Integer> getqSetPlayCounts()
	{
		return qSetPlayCounts;
	}
	
	/**
	 * Adds one play count to each QuestionSet in the list. Don't forget to save the Player after updating any stats!
	 * 
	 * @param qSets QuestionSets to be marked as played one more time.
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
	 * @param qSetId Id of QuestionSet to remove.
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
	
	/**
	 * Adds a game score to the last played games score list. Automatically limits the list size to 10. Don't forget to save the Player after updating any stats!
	 * 
	 * @param timestamp
	 *            Time stamp of game end.
	 * @param score
	 *            Final score of the game.
	 */
	public void addGameScore(long timestamp, int score)
	{
		if (lastScores == null)
			throw new IllegalStateException("lastScores of Player is null, please save the Player before use!");
		
		lastScores.put(timestamp, score);
		
		if (lastScores.size() > maxSavedRecentGameScores)
		{
			long min = Long.MAX_VALUE;
			for (long time : lastScores.keySet())
			{
				if (time < min)
					min = time;
			}
			
			lastScores.remove(min);
		}
	}
	
	/**
	 * Computes and returns an array of scores of the last 10 games played. The scores are sorted by time from least recently (index 0) to most recently (index 9). If less than 10 games were saved the
	 * missing values are set to 0 (values are aligned to index 9). e.g. At 4 games played (44 being the score of the most recent one) the returned array would be [0, 0, 0, 0, 0, 0, 11, 22, 33, 44].
	 * 
	 * @return The sorted score array.
	 */
	public int[] getLastGameScores()
	{
		if (lastScores == null)
			throw new IllegalStateException("lastScores of Player is null, please save the Player before use!");
		
		List<Long> times = new LinkedList<>(lastScores.keySet());
		times.sort(Comparator.naturalOrder());
		
		int[] values = new int[maxSavedRecentGameScores];
		int missing = maxSavedRecentGameScores - times.size();
		for (int i = 0; i < maxSavedRecentGameScores; i++)
		{
			if (i < missing)
				values[i] = 0;
			else
				values[i] = lastScores.get(times.get(i - missing));
		}
		
		return values;
	}
	
	/**
	 * Gets called automatically upon Player creation - DO NOT USE OTHERWISE!
	 */
	public void setLastScores(Map<Long, Integer> map)
	{
		this.lastScores = map;
	}
	
	public User getUser()
	{
		return user;
	}
	
	/**
	 * Gets called automatically upon creation of the Player. Should not be called manually!
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