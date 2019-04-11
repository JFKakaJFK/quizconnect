package at.qe.sepm.skeleton.logic;

import java.util.List;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.Question;

public class ActiveQuestion
{
	public Question question;
	public Player playerQuestion;
	public Player playerAnswer;
	public List<Player> playersWrongAnswers;
	public long timeRemaining;
	
	public ActiveQuestion(Question question, Player playerQuestion, Player playerAnswer, List<Player> playersWrongAnswers, long timeRemaining)
	{
		super();
		this.question = question;
		this.playerQuestion = playerQuestion;
		this.playerAnswer = playerAnswer;
		this.playersWrongAnswers = playersWrongAnswers;
		this.timeRemaining = timeRemaining;
	}
	
}