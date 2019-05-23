package at.qe.sepm.skeleton.logic;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.Question;
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.model.QuestionSetDifficulty;

/**
 * System for any Question management of a {@link QuizRoom}. Each QR has one of these systems during runtime. Handles Question loading / generation, distribution, and removal.
 * 
 * @author Lorenz_Smidt
 *
 */
public class QR_QuestionSystem
{
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	protected final int playerAnswerSlots = 6; // number of answers a Player can have at most
	private final int maxQuestions = 30; // maximum number of questions until game ends
	
	private QuizRoom quizRoom;
	
	private volatile List<QR_Question> questionsPoolEasy; // list of all unused easy questions
	private volatile List<QR_Question> questionsPoolHard; // list of all unused hard questions
	private volatile int completedQuestions; // total number of answered questions
	private volatile int missingQuestions; // number of players without questions (used for hot-joining the room)

	private volatile List<ActiveQuestion> activeQuestions; // list of currently active questions
	private volatile HashMap<Integer, ActiveQuestion> activeByQuestionId; // map for finding active questions by the qid;
	private volatile HashMap<Player, ActiveQuestion> playerQuestions; // map for storing assigned questions of players
	private volatile HashMap<Player, List<ActiveQuestion>> playerAnswers; // map for storing assigned answers of players, both right and wrong
	private volatile boolean isJokerTimeout; // true while joker timeout is going on (= time between joker call and distribution of new questions)
	
	protected QR_QuestionSystem(QuizRoom quizRoom, GameMode gameMode, List<QuestionSet> questionSets)
	{
		this.quizRoom = quizRoom;
		
		completedQuestions = 0;
		missingQuestions = 0;
		activeQuestions = new LinkedList<>();
		activeByQuestionId = new HashMap<>();
		playerQuestions = new HashMap<>();
		playerAnswers = new HashMap<>();
		isJokerTimeout = false;

		loadQuestions(gameMode, questionSets);
	}
	
	/**
	 * Called on QuizRoom creation; Loads / generates all Questions to be used into the respective pools.
	 */
	private void loadQuestions(GameMode gameMode, List<QuestionSet> questionSets)
	{
		questionsPoolEasy = new LinkedList<>();
		questionsPoolHard = new LinkedList<>();
		
		// TODO generate runtime Question objects depending on game mode
		
		if (gameMode == GameMode.normal)
		{
			/**
			 * Normal game mode, load all Questions from the QuestionSets, remove duplicate questions / answers, put in appropriate pools depending on difficulty.
			 */
			Set<String> questionStrings = new HashSet<>();
			Set<String> rightAnswers = new HashSet<>();
			int nextID = 1;
			for (QuestionSet qSet : questionSets)
			{
				for (Question q : qSet.getQuestions())
				{
					String questionString = q.getQuestionString().toLowerCase().trim();
					String rightAnswer = q.getRightAnswerString().toLowerCase().trim();
					if (questionStrings.contains(questionString) || rightAnswers.contains(rightAnswer))
					{
						LOGGER.debug("### INFO ### skipped Question on load with duplicate question / answer!");
						continue;
					}
					questionStrings.add(questionString);
					rightAnswers.add(rightAnswer);
					
					QR_Question nQ = new QR_Question(nextID++, q.getType(), q.getQuestionString(), q.getRightAnswerString(), q.getWrongAnswerString_1(),
							q.getWrongAnswerString_2(), q.getWrongAnswerString_3(), q.getWrongAnswerString_4(), q.getWrongAnswerString_5());
					
					if (qSet.getDifficulty() == QuestionSetDifficulty.easy)
						questionsPoolEasy.add(nQ);
					else if (qSet.getDifficulty() == QuestionSetDifficulty.hard)
						questionsPoolHard.add(nQ);
				}
			}
		}
		LOGGER.debug("### INFO ### QuizRoom loaded " + questionsPoolEasy.size() + " easy Questions and " + questionsPoolHard.size() + " hard Questions.");
	}
	
	/**
	 * Called from QuizRoom when a new Player joins; Creates all needed data structures.
	 * 
	 * @param player
	 *            Player to be added.
	 */
	protected synchronized void addPlayer(Player player)
	{
		playerQuestions.put(player, null);
		playerAnswers.put(player, new LinkedList<>());
	}
	
	/**
	 * Called from QuizRoom when a Player leaves; Removes all player data structures and re-distributes questions if necessary.
	 * 
	 * @param player
	 *            Player to be removed.
	 */
	protected synchronized void removePlayer(Player player)
	{
		// remove ActiveQuestion with question at player
		if (playerQuestions.containsKey(player))
		{
			removeQuestion(playerQuestions.get(player));
		}
		
		// redistribute questions with right answer at player, modify activeQuestions with wrong answers at player
		int redistCount = 0;
		for (ActiveQuestion activeQuestion : playerAnswers.get(player))
		{
			if (activeQuestion.playerAnswer == player)
			{
				// return question to appropriate pool and remove from current play
				if (activeQuestion.questionDifficulty == QuestionSetDifficulty.easy)
				{
					questionsPoolEasy.add(activeQuestion.question);
				}
				else
				{
					questionsPoolHard.add(activeQuestion.question);
				}
				removeQuestion(activeQuestion);
				redistCount++;
			}
			else if (activeQuestion.playersWrongAnswers.contains(player))
			{
				// remove all instances of player (player may have multiple wrong answers of AQ)
				while (activeQuestion.playersWrongAnswers.contains(player))
					activeQuestion.playersWrongAnswers.remove(player);
			}
			else
			{
				LOGGER.error(
						"Illegal state for ActiveQuestion detected - playerAnswers has AQ registered but AQ does not player as playerAnswer or playersWrongAnswers!");
			}
		}
		
		// redistribute questions after 0.5sec delay
		final int rC = redistCount;
		quizRoom.addDelayedAction(new DelayedAction((new Date().getTime()) + 500, () -> {
			for (int i = 0; i < rC; i++)
			{
				distributeQuestion();
			}
		}));
		
		playerQuestions.remove(player);
		playerAnswers.remove(player);
	}
	
	/**
	 * Adds count number of Questions as missing (= to be distributed on next call).
	 * 
	 * @param count
	 */
	protected void addMissingQuestions(int count)
	{
		missingQuestions += count;
	}
	
	/**
	 * Sets the total number of missing Questions.
	 * 
	 * @param count
	 */
	protected void setMissingQuestions(int count)
	{
		missingQuestions = count;
	}
	
	/**
	 * Adds count number of completed Questions to be counted.
	 * 
	 * @param count
	 */
	protected void addCompletedQuestions(int count)
	{
		completedQuestions += count;
	}
	
	/**
	 * Called every frameUpdate from QuizRoom, reduces remaining time and checks for all ActiveQuestions if time has run out. Removes a Question from all Players associated if time has run out.
	 */
	protected synchronized void checkQuestionTimes(long deltaTime)
	{
		int missing = 0;
		for (int i = activeQuestions.size() - 1; i >= 0; i--)
		{
			activeQuestions.get(i).timeRemaining -= deltaTime;
			if (activeQuestions.get(i).timeRemaining <= 0)
			{ // question time elapsed, remove
				quizRoom.changeScore(activeQuestions.get(i).questionDifficulty == QuestionSetDifficulty.easy ? 4 : 5, 0);
				
				removeQuestion(activeQuestions.get(i));
				missing++;
			}
		}
		
		if (missing > 0)
		{
			missingQuestions += missing - 1;
			distributeQuestion();
		}
	}
	
	/**
	 * Called every timerSyncTimeStep, calls the onTimerSync event on all Players, sending the current remaining time on the Question timer.
	 */
	protected synchronized void synchronizeTimers(IRoomAction playerInterface, int pin)
	{
		for (ActiveQuestion activeQuestion : activeQuestions)
		{
			playerInterface.onTimerSync(pin, activeQuestion.playerQuestion, activeQuestion, activeQuestion.timeRemaining);
		}
	}
	
	/**
	 * Takes a random unused question and assigns it to players with open slots. Question selection is dependent on room difficulty and available questions. Processes missing Questions on call.
	 */
	protected synchronized void distributeQuestion()
	{
		if (isJokerTimeout)
		{
			LOGGER.debug("### INFO ### distributeQuestion call while in joker timeout, ignoring.");
			return;
		}
		
		_distributeQuestion();
		while (missingQuestions > 0)
		{
			_distributeQuestion();
			missingQuestions--;
		}
		
		missingQuestions = 0; // fix any negative number
	}
	
	/**
	 * INTERNAL ONLY! Called by distributeQuestion to distribute an individual Question.
	 */
	private synchronized void _distributeQuestion()
	{
		if (quizRoom.wfpMode)
		{ // game has ended (or somehow in call in wfp mode), ignore call
			return;
		}
		
		// check if too many questions in play
		if (activeByQuestionId.keySet().size() >= quizRoom.players.size())
		{
			LOGGER.error("### WARNING ### [QR " + quizRoom.pin + "] distributing more questions than Players in game, skipping call.");
			return;
		}
		
		AbstractMap.SimpleEntry<QR_Question, QuestionSetDifficulty> pair = selectQuestion();
		if (pair == null)
		{ // close when no more Questions available or if maximum number of Questions answered.
			quizRoom.onRoomClose();
			return;
		}
		
		QR_Question question = pair.getKey();
		
		List<Player> questionFreePlayers = new ArrayList<>(); // players with no questions
		List<Player> answerFreePlayers = new ArrayList<>(); // players with open answer slots (multiple slots open = multiple times in list)
		for (Player player : quizRoom.players)
		{
			if (playerQuestions.get(player) == null)
			{
				questionFreePlayers.add(player);
			}
			if (playerAnswers.get(player).size() < playerAnswerSlots)
			{
				for (int i = 0; i < (playerAnswerSlots - playerAnswers.get(player).size()); i++)
					answerFreePlayers.add(player);
			}
		}
		
		if (questionFreePlayers.size() == 0 || answerFreePlayers.size() == 0)
		{
			LOGGER.error("### ERROR ### [QR " + quizRoom.pin + "] no question / answer free players available in distributeQuestion! ("
					+ questionFreePlayers.size()
					+ "|" + answerFreePlayers.size() + ")");
			return;
		}
		
		Random random = new Random();
		// question assignment
		int qIndex = random.nextInt(questionFreePlayers.size());
		Player qPlayer = questionFreePlayers.get(qIndex);
		
		// answers assignment (depending on number of right + wrong answers already assigned to players)
		int raIndex = random.nextInt(answerFreePlayers.size());
		Player raPlayer = answerFreePlayers.get(raIndex);
		answerFreePlayers.remove(raIndex);
		
		List<Player> waPlayers = new ArrayList<>();
		Player p;
		for (int i = 0; i < 5; i++)
		{
			String qString = null;
			if (i == 0)
				qString = question.getWrongAnswerString_1();
			else if (i == 1)
				qString = question.getWrongAnswerString_2();
			else if (i == 2)
				qString = question.getWrongAnswerString_3();
			else if (i == 3)
				qString = question.getWrongAnswerString_4();
			else if (i == 4)
				qString = question.getWrongAnswerString_5();
			
			if (answerFreePlayers.size() > 0 && qString != null && qString != "")
			{
				int index = random.nextInt(answerFreePlayers.size());
				p = answerFreePlayers.get(index);
				answerFreePlayers.remove(index);
				waPlayers.add(p);
			}
			else
				break;
		}
		
		long qTime = computeQuestionTime(pair.getValue());
		
		ActiveQuestion newActive = new ActiveQuestion(question, pair.getValue(), qPlayer, raPlayer, waPlayers, qTime);
		
		activeQuestions.add(newActive);
		activeByQuestionId.put(question.getId(), newActive);
		
		playerQuestions.put(qPlayer, newActive);
		
		playerAnswers.get(raPlayer).add(newActive);
		
		for (int i = 0; i < waPlayers.size(); i++)
		{
			playerAnswers.get(waPlayers.get(i)).add(newActive);
		}
		
		// event call
		quizRoom.playerInterface.assignQuestion(quizRoom.pin, newActive);
	}
	
	/**
	 * Returns a Question (+ its difficulty) at random from either the easy or the hard pool (depending on emptiness / difficulty) or null if game end reached.
	 */
	private AbstractMap.SimpleEntry<QR_Question, QuestionSetDifficulty> selectQuestion()
	{
		Random random = new Random();
		int bound = quizRoom.difficulty == RoomDifficulty.easy ? 66 : 33;
		
		boolean easy;
		if ((completedQuestions >= maxQuestions) || (questionsPoolEasy.size() == 0 && questionsPoolHard.size() == 0))
		{ // game complete state reached
			return null;
		}
		else if (questionsPoolEasy.size() == 0)
		{ // have to select hard one
			easy = false;
		}
		else if (questionsPoolHard.size() == 0)
		{ // have to select easy one
			easy = true;
		}
		else if (random.nextInt(100) < bound)
		{ // select easy question
			easy = true;
		}
		else
		{ // select hard question
			easy = false;
		}
		
		QR_Question question;
		if (easy)
		{
			int index = random.nextInt(questionsPoolEasy.size());
			question = questionsPoolEasy.get(index);
			questionsPoolEasy.remove(index);
		}
		else
		{
			int index = random.nextInt(questionsPoolHard.size());
			question = questionsPoolHard.get(index);
			questionsPoolHard.remove(index);
		}
		
		return new AbstractMap.SimpleEntry<QR_Question, QuestionSetDifficulty>(question, easy ? QuestionSetDifficulty.easy : QuestionSetDifficulty.hard);
	}
	
	/**
	 * Computes the answer time for a question based on the room difficulty, question difficulty, player count, completed questions count, and some constants.
	 * 
	 * @param questionDifficulty
	 * @return
	 */
	private long computeQuestionTime(QuestionSetDifficulty questionDifficulty)
	{
		long playerBonus = 5000; // ms added for each player in the room
		double scaleFactor = 0.3; // factor affecting time reduction towards end of game
		
		// TODO change times depending on gamemode
		long base = 0;
		if (quizRoom.difficulty == RoomDifficulty.easy)
		{
			if (questionDifficulty == QuestionSetDifficulty.easy)
				base = 30000;
			else
				base = 45000;
		}
		else
		{
			if (questionDifficulty == QuestionSetDifficulty.easy)
				base = 20000;
			else
				base = 30000;
		}
		
		return (long) (base - (((completedQuestions / (double) maxQuestions) * scaleFactor) * base) + (quizRoom.players.size() * playerBonus));
	}
	
	/**
	 * Removes the ActiveQuestion from all Players involved (question + answers).
	 * 
	 * @param q
	 */
	protected synchronized void removeQuestion(ActiveQuestion q)
	{
		if (q == null)
			return;
		
		// remove question
		playerQuestions.put(q.playerQuestion, null);
		
		// remove right answer
		List<ActiveQuestion> qs = playerAnswers.get(q.playerAnswer);
		qs.remove(q);
		playerAnswers.put(q.playerAnswer, qs);
		
		// remove wrong answers
		for (Player player : q.playersWrongAnswers)
		{
			qs = playerAnswers.get(player);
			qs.remove(q);
			playerAnswers.put(player, qs);
		}
		
		activeQuestions.remove(q);
		activeByQuestionId.remove(q.question.getId());
		
		// event call
		quizRoom.playerInterface.removeQuestion(quizRoom.pin, q);
	}
	
	/**
	 * Returns an ActiveQuestion by the runtime Question id. Returns null if none was found.
	 * 
	 * @param questionId
	 * @return
	 */
	protected ActiveQuestion getActiveQuestionById(int questionId)
	{
		if (!activeByQuestionId.containsKey(questionId))
			return null;
		return activeByQuestionId.get(questionId);
	}
	
	/**
	 * Called when a player answers a question. Called from QuizRoom.
	 * 
	 * @param player
	 *            Player making the answer call.
	 * @param questionId
	 *            Runtime id of the question to be answered.
	 * @param index
	 *            Index of the answer option chosen.
	 */
	protected synchronized void answerQuestion(Player player, int questionId, int index) throws IllegalStateException
	{
		if (!activeByQuestionId.containsKey(questionId))
		{
			LOGGER.debug(
					"### WARNING ### [QR " + quizRoom.pin + "] Answer Question call from Player " + player.getId() + " on already removed ActiveQuestion (qid: "
					+ questionId + ")");
			throw new IllegalStateException();
		}
		
		ActiveQuestion q = activeByQuestionId.get(questionId);
		if (!playerAnswers.get(player).contains(q))
		{
			LOGGER.debug("### WARNING ### [QR " + quizRoom.pin + "] Answer Question call from Player " + player.getId()
					+ " Question not assigned to Player! (qid: " + questionId + ")");
			throw new IllegalStateException();
		}
		
		removeQuestion(q);
		
		distributeQuestion();
	}
	
	/**
	 * Gets called from QuizRoom when a Player successfully uses a joker. Removes all current Questions and (after a short delay) distributes new ones.
	 */
	protected synchronized void useJoker()
	{
		for (int i = activeQuestions.size() - 1; i >= 0; i--)
		{
			ActiveQuestion aq = activeQuestions.get(i);
			removeQuestion(aq);
		}
		
		// prevent delayed calls to distribute questions before joker timeout has finished
		isJokerTimeout = true;
		
		// wait 1 second, then assign new questions to all players
		quizRoom.addDelayedAction(new DelayedAction((new Date().getTime()) + 1000, () -> {
			isJokerTimeout = false;
			missingQuestions = quizRoom.players.size() - 1;
			distributeQuestion();
		}));
	}
	
}