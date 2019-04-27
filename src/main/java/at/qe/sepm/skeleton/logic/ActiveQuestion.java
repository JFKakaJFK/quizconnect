package at.qe.sepm.skeleton.logic;

import java.util.List;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.Question;
import at.qe.sepm.skeleton.model.QuestionSetDifficulty;

public class ActiveQuestion
{
	public Question question;
	public QuestionSetDifficulty questionDifficulty;
	public Player playerQuestion;
	public Player playerAnswer;
	public List<Player> playersWrongAnswers;
	public long timeRemaining;
	
	public ActiveQuestion(Question question, QuestionSetDifficulty difficulty, Player playerQuestion, Player playerAnswer, List<Player> playersWrongAnswers,
			long timeRemaining)
	{
		super();
		this.question = question;
		this.questionDifficulty = difficulty;
		this.playerQuestion = playerQuestion;
		this.playerAnswer = playerAnswer;
		this.playersWrongAnswers = playersWrongAnswers;
		this.timeRemaining = timeRemaining;
	}
	
}