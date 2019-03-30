package at.qe.sepm.skeleton.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.data.domain.Persistable;

/**
 * Entity representing a {@link Player}'s performance on a {@link QuestionSet}.
 * 
 * @author Lorenz_Smidt
 *
 */
@Entity
public class QuestionSetPerformance implements Persistable<Integer>
{
	private static final long serialVersionUID = 1L;
	
	@Id
	private Integer id;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private QuestionSet questionSet;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Player player;
	
	private int totalQuestions;
	
	private int rightAnswers;
	
	/**
	 * Number of total games played using the QuestionSet.
	 */
	private int playCount;
	
	@Override
	public Integer getId()
	{
		return id;
	}
	
	public void setId(Integer id)
	{
		this.id = id;
	}
	
	public QuestionSet getQuestionSet()
	{
		return questionSet;
	}
	
	public void setQuestionSet(QuestionSet questionSet)
	{
		this.questionSet = questionSet;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public int getTotalQuestions()
	{
		return totalQuestions;
	}
	
	public void setTotalQuestions(int totalQuestions)
	{
		this.totalQuestions = totalQuestions;
	}
	
	public int getRightAnswers()
	{
		return rightAnswers;
	}
	
	public void setRightAnswers(int rightAnswers)
	{
		this.rightAnswers = rightAnswers;
	}
	
	public int getPlayCount()
	{
		return playCount;
	}
	
	public void setPlayCount(int playCount)
	{
		this.playCount = playCount;
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
		QuestionSetPerformance other = (QuestionSetPerformance) obj;
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