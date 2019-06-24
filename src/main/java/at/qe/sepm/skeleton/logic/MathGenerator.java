package at.qe.sepm.skeleton.logic;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import at.qe.sepm.skeleton.model.QuestionSetDifficulty;
import at.qe.sepm.skeleton.model.QuestionType;

/**
 * Class for generating math questions as QR_Questions. Used from QR_QuestionSystem if needed (= if gamemode = mathgod). All answers are guaranteed to be integers. Generates questions based on
 * templates and random numbers of different average sizes. Please call the help hotline at +43148817-242 for further questions.
 * 
 * @author Lorenz_Smidt
 *
 */
public class MathGenerator
{
	/**
	 * Enum for selection of number size for generation.
	 */
	private enum numSize
	{
		low, mid, high
	}
	
	private Random random;
	
	public MathGenerator()
	{
		random = new Random();
	}
	
	/**
	 * Generates count questions of difficulty and returns them in form of a List of QR_Questions.
	 * 
	 * @param difficulty
	 *            Difficulty of the Questions to generate. Determines which templates are to be used.
	 * @param count
	 *            Number of Questions to generate.
	 * @param startId
	 *            Runtime id to start at (inclusive). Will generate ids up to startId + count.
	 * @return The List of QR_Questions.
	 */
	public List<QR_Question> generateQuestions(QuestionSetDifficulty difficulty, int count, int startId)
	{
		List<QR_Question> questions = new LinkedList<>();
		HashSet<String> qs = new HashSet<>();
		HashSet<String> as = new HashSet<>();
		int id = startId;
		
		for (int i = 0; i < count; i++)
		{
			QR_Question newQ = null;
			while (newQ == null)
			{
				if (difficulty == QuestionSetDifficulty.easy)
					newQ = getEasyQuestion(id);
				else
					newQ = getHardQuestion(id);
				
				// safety checks
				// no wrong answers match right answer
				String ra = newQ.getRightAnswerString();
				if (ra.equals(newQ.getWrongAnswerString_1()) || ra.equals(newQ.getWrongAnswerString_2()) || ra.equals(newQ.getWrongAnswerString_3())
						|| ra.equals(newQ.getWrongAnswerString_4()) || ra.equals(newQ.getWrongAnswerString_5()))
				{
					newQ = null;
					continue;
				}
				
				// unique to other questions
				if (qs.contains(newQ.getQuestionString()) || as.contains(newQ.getRightAnswerString()) || as.contains(newQ.getWrongAnswerString_1())
						|| as.contains(newQ.getWrongAnswerString_2()) || as.contains(newQ.getWrongAnswerString_3())
						|| as.contains(newQ.getWrongAnswerString_4()) || as.contains(newQ.getWrongAnswerString_5()))
				{
					newQ = null;
				}
				
			}
			questions.add(newQ);
			qs.add(newQ.getQuestionString());
			as.add(newQ.getRightAnswerString());
			id++;
		}
		
		return questions;
	}
	
	/**
	 * Randomly selects a template of easy difficulty and generates a new question using it.
	 * 
	 * @param id
	 *            Runtime id for the new QR_Question to use.
	 * @return The newly generated QR_Question.
	 */
	private QR_Question getEasyQuestion(int id)
	{
		// randomly select template
		switch (random.nextInt(5))
		{
		case 0:
			return genEasy_T1(id);
		case 1:
			return genEasy_T2(id);
		case 2:
			return genEasy_T3(id);
		case 3:
			return genEasy_T4(id);
		case 4:
			return genEasy_T5(id);
		default:
			return genEasy_T1(id);
		}
	}
	
	/**
	 * Randomly selects a template of hard difficulty and generates a new question using it.
	 * 
	 * @param id
	 *            Runtime id for the new QR_Question to use.
	 * @return The newly generated QR_Question.
	 */
	private QR_Question getHardQuestion(int id)
	{
		// randomly select template
		switch (random.nextInt(5))
		{
		case 0:
			return genHard_T1(id);
		case 1:
			return genHard_T2(id);
		case 2:
			return genHard_T3(id);
		case 3:
			return genHard_T4(id);
		case 4:
			return genHard_T5(id);
		default:
			return genHard_T1(id);
		}
	}
	
	/**
	 * Generates an easy question based on the template a+b*c=?
	 * 
	 * @return The generated question.
	 */
	private QR_Question genEasy_T1(int id)
	{
		int[] nums = getNumbers(new numSize[] { numSize.low, numSize.mid, numSize.low });
		String question = nums[0] + " + " + nums[1] + " \\cdot " + nums[2] + " = ?";
		String answer = "= " + (nums[0] + (nums[1] * nums[2]));
		String wrong1 = "= " + ((nums[0] + nums[1]) * nums[2]);
		String wrong2 = "= " + ((nums[0] * nums[2]) + nums[1]);
		String wrong3 = "= " + (nums[0] + ((nums[1] - 1) * nums[2]));
		String wrong4 = "= " + (nums[0] + (nums[1] * (nums[2] - 1)));
		String wrong5 = "= " + (nums[1] * nums[2]);
		
		return new QR_Question(id, QuestionType.math, question, answer, wrong1, wrong2, wrong3, wrong4, wrong5);
	}
	
	/**
	 * Generates an easy question based on the template a*b-c*d=?
	 * 
	 * @return The generated question.
	 */
	private QR_Question genEasy_T2(int id)
	{
		int[] nums = getNumbers(new numSize[] { numSize.low, numSize.mid, numSize.low, numSize.mid });
		String question = nums[0] + " \\cdot " + nums[1] + " - " + nums[2] + " \\cdot " + nums[3] + " = ?";
		String answer = "= " + ((nums[0] * nums[1]) - (nums[2] * nums[3]));
		String wrong1 = "= " + (nums[0] * (nums[1] - nums[2]) * nums[3]);
		String wrong2 = "= " + ((nums[0] * nums[1]) + (nums[2] * nums[3]));
		String wrong3 = "= " + (((nums[0] - 1) * nums[1]) - (nums[2] * nums[3]));
		String wrong4 = "= " + ((nums[0] * nums[1]) - ((nums[2] - 1) * nums[3]));
		String wrong5 = "= " + ((nums[0] * nums[1]) - (nums[2] * (nums[3] - 1)));
		
		return new QR_Question(id, QuestionType.math, question, answer, wrong1, wrong2, wrong3, wrong4, wrong5);
	}
	
	/**
	 * Generates an easy question based on the template a-b*c=?
	 * 
	 * @return The generated question.
	 */
	private QR_Question genEasy_T3(int id)
	{
		int[] nums = getNumbers(new numSize[] { numSize.low, numSize.mid, numSize.low });
		String question = nums[0] + " - " + nums[1] + " \\cdot " + nums[2] + " = ?";
		String answer = "= " + (nums[0] - (nums[1] * nums[2]));
		String wrong1 = "= " + ((nums[0] - nums[1]) * nums[2]);
		String wrong2 = "= " + ((nums[0] * nums[2]) - nums[1]);
		String wrong3 = "= " + (nums[0] - ((nums[1] - 1) * nums[2]));
		String wrong4 = "= " + (nums[0] - (nums[1] * (nums[2] - 1)));
		String wrong5 = "= " + (nums[1] * (nums[2] + 1));
		
		return new QR_Question(id, QuestionType.math, question, answer, wrong1, wrong2, wrong3, wrong4, wrong5);
	}
	
	/**
	 * Generates an easy question based on the template a+b-(c-d)=?
	 * 
	 * @return The generated question.
	 */
	private QR_Question genEasy_T4(int id)
	{
		int[] nums = getNumbers(new numSize[] { numSize.mid, numSize.low, numSize.mid, numSize.mid });
		String question = nums[0] + " + " + nums[1] + " - ( " + nums[2] + " - " + nums[3] + " ) = ?";
		String answer = "= " + (nums[0] + nums[1] - nums[2] + nums[3]);
		String wrong1 = "= " + (nums[0] + nums[1] - nums[2] - nums[3]);
		String wrong2 = "= " + (nums[0] + nums[1] + nums[2] + nums[3]);
		String wrong3 = "= " + (nums[0] + nums[1] + nums[2] - nums[3]);
		String wrong4 = "= " + (nums[0] + nums[1] - (nums[2] + 1) + nums[3]);
		String wrong5 = "= " + (nums[0] - nums[2] + nums[3]);
		
		return new QR_Question(id, QuestionType.math, question, answer, wrong1, wrong2, wrong3, wrong4, wrong5);
	}
	
	/**
	 * Generates an easy question based on the template a*(b+c)-d=?
	 * 
	 * @return The generated question.
	 */
	private QR_Question genEasy_T5(int id)
	{
		int[] nums = getNumbers(new numSize[] { numSize.low, numSize.mid, numSize.low, numSize.mid });
		String question = nums[0] + " \\cdot ( " + nums[1] + " + " + nums[2] + " ) - " + nums[3] + " = ?";
		String answer = "= " + (nums[0] * (nums[1] + nums[2]) - nums[3]);
		String wrong1 = "= " + (nums[0] * (nums[1] - nums[2]) - nums[3]);
		String wrong2 = "= " + ((nums[0] + 1) * (nums[1] + nums[2]) - nums[3]);
		String wrong3 = "= " + (nums[0] * (nums[1] + nums[2]) + nums[3]);
		String wrong4 = "= " + (nums[0] * (nums[1] + nums[2] - nums[3]));
		String wrong5 = "= " + ((nums[0] - 1) * (nums[1] + nums[2]) - nums[3]);
		
		return new QR_Question(id, QuestionType.math, question, answer, wrong1, wrong2, wrong3, wrong4, wrong5);
	}
	
	/**
	 * Generates a hard question based on the template sqrt(a+b)*c=?
	 * 
	 * @return The generated question.
	 */
	private QR_Question genHard_T1(int id)
	{
		int[] nums = getNumbers(new numSize[] { numSize.low, numSize.low, numSize.mid });
		int a = nums[2] * nums[2] - nums[0];
		String question = "\\sqrt{" + a + " + " + nums[0] + "} \\cdot " + nums[1] + " = ?";
		String answer = "= " + (nums[2] * nums[1]);
		String wrong1 = "= " + (nums[2] * (nums[1] - 1));
		String wrong2 = "= " + (nums[2] * (nums[1] + 1));
		String wrong3 = "= " + ((nums[2] - 1) * nums[1]);
		String wrong4 = "= " + ((nums[2] + 1) * nums[1]);
		String wrong5 = "= " + (nums[2] * nums[1] + 1);
		
		return new QR_Question(id, QuestionType.math, question, answer, wrong1, wrong2, wrong3, wrong4, wrong5);
	}
	
	/**
	 * Generates a hard question based on the template (a*b)/(c-d)=?
	 * 
	 * @return The generated question.
	 */
	private QR_Question genHard_T2(int id)
	{
		int[] nums = getNumbers(new numSize[] { numSize.low, numSize.low, numSize.low, numSize.low });
		int a = nums[2] * nums[3];
		int c = nums[0] * nums[2] + nums[1];
		String question = "\\frac{" + a + " \\cdot " + nums[0] + "} {" + c + " - " + nums[1] + "} = ?";
		String answer = "= " + (nums[3]);
		String wrong1 = "= " + (nums[3] + nums[1]);
		String wrong2 = "= " + (nums[3] * nums[0]);
		String wrong3 = "= " + (nums[3] - 1);
		String wrong4 = "= " + (nums[3] * 2);
		String wrong5 = "= " + (nums[2] * nums[3]);
		
		return new QR_Question(id, QuestionType.math, question, answer, wrong1, wrong2, wrong3, wrong4, wrong5);
	}
	
	/**
	 * Generates a hard question based on the template (a-b)*c+d=?
	 * 
	 * @return The generated question.
	 */
	private QR_Question genHard_T3(int id)
	{
		int[] nums = getNumbers(new numSize[] { numSize.high, numSize.mid, numSize.low, numSize.mid });
		String question = "( " + nums[0] + " - " + nums[1] + " ) \\cdot " + nums[2] + " + " + nums[3] + " = ?";
		String answer = "= " + ((nums[0] - nums[1]) * nums[2] + nums[3]);
		String wrong1 = "= " + ((nums[0] - nums[1]) * nums[2]);
		String wrong2 = "= " + ((nums[0] - nums[1] + 1) * nums[2] + nums[3]);
		String wrong3 = "= " + ((nums[0] - nums[1]) * (nums[2] - 1) + nums[3]);
		String wrong4 = "= " + ((nums[0] + nums[1]) * nums[2] + nums[3]);
		String wrong5 = "= " + ((nums[0]) * nums[2] + nums[3]);
		
		return new QR_Question(id, QuestionType.math, question, answer, wrong1, wrong2, wrong3, wrong4, wrong5);
	}
	
	/**
	 * Generates a hard question based on the template a*b*c-d=?
	 * 
	 * @return The generated question.
	 */
	private QR_Question genHard_T4(int id)
	{
		int[] nums = getNumbers(new numSize[] { numSize.low, numSize.mid, numSize.low, numSize.high });
		String question = nums[0] + " \\cdot " + nums[1] + " \\cdot " + nums[2] + " - " + nums[3] + " = ?";
		String answer = "= " + (nums[0] * nums[1] * nums[2] - nums[3]);
		String wrong1 = "= " + (nums[0] * nums[1] * nums[2]);
		String wrong2 = "= " + (nums[0] * nums[1] - nums[3]);
		String wrong3 = "= " + (nums[0] * (nums[1] + 1) * nums[2] - nums[3]);
		String wrong4 = "= " + (nums[0] * nums[1] * nums[2] - nums[3] - 1);
		String wrong5 = "= " + (nums[1] * nums[2] - nums[3]);
		
		return new QR_Question(id, QuestionType.math, question, answer, wrong1, wrong2, wrong3, wrong4, wrong5);
	}
	
	/**
	 * Generates a hard question based on the template (a+b)/c=?
	 * 
	 * @return The generated question.
	 */
	private QR_Question genHard_T5(int id)
	{
		int[] nums = getNumbers(new numSize[] { numSize.low, numSize.low, numSize.low });
		int a = nums[1] * nums[2] - nums[0];
		String question = "\\frac{" + a + " + " + nums[0] + "} {" + nums[1] + "} = ?";
		String answer = "= " + (nums[2]);
		String wrong1 = "= " + (nums[2] + nums[0]);
		String wrong2 = "= " + (a - nums[2]);
		String wrong3 = "= " + (nums[2] - nums[1]);
		String wrong4 = "= " + (nums[2] + 3);
		String wrong5 = "= " + (nums[2] * 2);
		
		return new QR_Question(id, QuestionType.math, question, answer, wrong1, wrong2, wrong3, wrong4, wrong5);
	}
	
	/**
	 * Generates mutually unique numbers with certain sizes as defined by the sizes array.
	 * 
	 * @param sizes
	 *            Definitions for the sizes (and count) of numbers to generate.
	 * @return An array of mutually unique numbers of the given sizes.
	 */
	private int[] getNumbers(numSize[] sizes)
	{
		// range definitions for number sizes
		int low_l = 3;
		int low_h = 8;
		int mid_l = 8;
		int mid_h = 25;
		int high_l = 25;
		int high_h = 99;
		
		HashSet<Integer> temp = new HashSet<>();
		int[] out = new int[sizes.length];
		for (int i = 0; i < sizes.length; i++)
		{
			int next = 0;
			while (next == 0 || temp.contains(next))
			{
				if (sizes[i] == numSize.low)
				{
					next = random.nextInt(low_h) + low_l;
				}
				else if (sizes[i] == numSize.mid)
				{
					next = random.nextInt(mid_h) + mid_l;
				}
				else
				{
					next = random.nextInt(high_h) + high_l;
				}
			}
			temp.add(next);
			out[i] = next;
		}
		return out;
	}
}