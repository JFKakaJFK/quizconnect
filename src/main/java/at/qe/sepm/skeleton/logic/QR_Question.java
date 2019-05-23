package at.qe.sepm.skeleton.logic;

import at.qe.sepm.skeleton.model.QuestionType;

/**
 * Class representing a runtime only version of a {@link Question}, decoupling it from the database. Allows for question generation and modification at runtime.
 * 
 * @author Lorenz_Smidt
 *
 */
public class QR_Question
{
	/**
	 * Runtime only id, does not have to coincide with a {@link Question} database id! Only unique within a {@link QuizRoom}!
	 */
	private int id;
	
	private QuestionType type;
	
	private String questionString;
	private String rightAnswerString;
	private String wrongAnswerString_1;
	private String wrongAnswerString_2;
	private String wrongAnswerString_3;
	private String wrongAnswerString_4;
	private String wrongAnswerString_5;
	
	public QR_Question(int id, QuestionType type, String questionString, String rightAnswerString, String wrongAnswerString_1, String wrongAnswerString_2,
			String wrongAnswerString_3, String wrongAnswerString_4, String wrongAnswerString_5)
	{
		this.id = id;
		this.type = type;
		this.questionString = questionString;
		this.rightAnswerString = rightAnswerString;
		this.wrongAnswerString_1 = wrongAnswerString_1;
		this.wrongAnswerString_2 = wrongAnswerString_2;
		this.wrongAnswerString_3 = wrongAnswerString_3;
		this.wrongAnswerString_4 = wrongAnswerString_4;
		this.wrongAnswerString_5 = wrongAnswerString_5;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
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
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		QR_Question other = (QR_Question) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}