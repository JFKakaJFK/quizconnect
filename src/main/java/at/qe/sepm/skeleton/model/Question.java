package at.qe.sepm.skeleton.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.data.domain.Persistable;

/**
 * Entity representing a Question. All question / rightAnswer / wrongAnswer Strings have different usage depending on QuestionType (e.g. type picture: all Strings are file paths).
 * 
 * @author Lorenz_Smidt
 *
 */
@Entity
public class Question implements Persistable<Integer>
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private QuestionSet questionSet;
	
	@Enumerated(EnumType.STRING)
	private QuestionType type;
	
	@Column(nullable = false, length = 200)
	private String questionString;
	
	@Column(nullable = false, length = 200)
	private String rightAnswerString;
	
	@Column(nullable = false, length = 200)
	private String wrongAnswerString_1;
	@Column(length = 200)
	private String wrongAnswerString_2;
	@Column(length = 200)
	private String wrongAnswerString_3;
	@Column(length = 200)
	private String wrongAnswerString_4;
	@Column(length = 200)
	private String wrongAnswerString_5;

	public QuestionSet getQuestionSet()
	{
		return questionSet;
	}
	
	public void setQuestionSet(QuestionSet questionSet)
	{
		this.questionSet = questionSet;
	}
	
	public QuestionType getType()
	{
		return type;
	}
	
	public void setType(QuestionType type)
	{
		this.type = type;
	}
	
	public String getQuestionString()
	{
		return questionString;
	}
	
	public void setQuestionString(String questionString)
	{
		this.questionString = questionString;
	}
	
	public String getRightAnswerString()
	{
		return rightAnswerString;
	}
	
	public void setRightAnswerString(String rightAnswerString)
	{
		this.rightAnswerString = rightAnswerString;
	}
	
	public String getWrongAnswerString_1()
	{
		return wrongAnswerString_1;
	}
	
	public void setWrongAnswerString_1(String wrongAnswerString_1)
	{
		this.wrongAnswerString_1 = wrongAnswerString_1;
	}
	
	public String getWrongAnswerString_2()
	{
		return wrongAnswerString_2;
	}
	
	public void setWrongAnswerString_2(String wrongAnswerString_2)
	{
		this.wrongAnswerString_2 = wrongAnswerString_2;
	}

	public String getWrongAnswerString_3()
	{
		return wrongAnswerString_3;
	}
	
	public void setWrongAnswerString_3(String wrongAnswerString_3)
	{
		this.wrongAnswerString_3 = wrongAnswerString_3;
	}
	
	public String getWrongAnswerString_4()
	{
		return wrongAnswerString_4;
	}
	
	public void setWrongAnswerString_4(String wrongAnswerString_4)
	{
		this.wrongAnswerString_4 = wrongAnswerString_4;
	}
	
	public String getWrongAnswerString_5()
	{
		return wrongAnswerString_5;
	}
	
	public void setWrongAnswerString_5(String wrongAnswerString_5)
	{
		this.wrongAnswerString_5 = wrongAnswerString_5;
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
		Question other = (Question) obj;
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