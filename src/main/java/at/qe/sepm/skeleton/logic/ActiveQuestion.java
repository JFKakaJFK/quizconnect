package at.qe.sepm.skeleton.logic;

import java.util.List;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.QuestionSetDifficulty;

/**
 * Class representing a Question currently in play (= distributed to at least 2 players). The remaining time to answer this question is also saved within this data structure.
 * 
 * @author Lorenz_Smidt
 *
 */
public class ActiveQuestion
{
	public QR_Question question;
	public QuestionSetDifficulty questionDifficulty;
	public Player playerQuestion;
	public Player playerAnswer;
	public List<Player> playersWrongAnswers;
	public long timeRemaining;
	
	public ActiveQuestion(QR_Question question, QuestionSetDifficulty difficulty, Player playerQuestion, Player playerAnswer, List<Player> playersWrongAnswers,
			long timeRemaining)
	{
		this.question = question;
		this.questionDifficulty = difficulty;
		this.playerQuestion = playerQuestion;
		this.playerAnswer = playerAnswer;
		this.playersWrongAnswers = playersWrongAnswers;
		this.timeRemaining = timeRemaining;
	}
	
}