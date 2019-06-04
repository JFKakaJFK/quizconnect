package at.qe.sepm.skeleton.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.data.domain.Persistable;

/**
 * Entity representing a QuestionSet. Each one contains multiple Questions, a name, description, and difficulty.
 * 
 * @author Lorenz_Smidt
 *
 */
@Entity
public class QuestionSet implements Persistable<Integer>
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(length = 100)
	private String name;
	
	@Column(length = 300)
	private String description;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Manager author;
	
	@Enumerated(EnumType.STRING)
	private QuestionSetDifficulty difficulty;
	
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "questionSet", fetch = FetchType.EAGER)
	private Set<Question> questions;

	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}

	@Deprecated // TODO remove deprecated
	public Manager getAuthor()
	{
		return author;
	}
	
	public void setAuthor(Manager author)
	{
		this.author = author;
	}
	
	public QuestionSetDifficulty getDifficulty()
	{
		return difficulty;
	}
	
	public void setDifficulty(QuestionSetDifficulty difficulty)
	{
		this.difficulty = difficulty;
	}
	
	public Set<Question> getQuestions()
	{
		return questions;
	}
	
	public void setQuestions(Set<Question> questions)
	{
		this.questions = questions;
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
		QuestionSet other = (QuestionSet) obj;
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