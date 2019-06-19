package at.qe.sepm.skeleton.tests;

import at.qe.sepm.skeleton.logic.MathGenerator;
import at.qe.sepm.skeleton.logic.QR_Question;
import at.qe.sepm.skeleton.model.QuestionSetDifficulty;
import at.qe.sepm.skeleton.model.QuestionType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

import java.awt.geom.GeneralPath;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MathGeneratorTest
{
	private MathGenerator generator;
	
	@Before
	public void init()
	{
		generator = new MathGenerator();
	}
	
	@Test
	@WithMockUser(username = "user1", authorities = {"MANAGER"})
	public void testGetEasyQuestions()
	{
		List<QR_Question> Qs = generator.generateQuestions(QuestionSetDifficulty.easy, 30, 1);
		assertEquals("Wrong number of Questions generated.", 30, Qs.size());
		
		for (int i = 0; i < 30; i++)
		{
			QR_Question q = Qs.get(i);
			assertEquals("Question has wrong id.", i + 1, q.getId());
			assertEquals("Question has wrong type.", QuestionType.math, q.getType());
			assertTrue("Question has invalid question string.", q.getQuestionString() != null && !q.getQuestionString().equals(""));
			assertTrue("Question has invalid right answer string.", q.getRightAnswerString() != null && !q.getRightAnswerString().equals(""));
			assertTrue("Question has invalid wrong answer string.", q.getWrongAnswerString_1() != null && !q.getWrongAnswerString_1().equals(""));
			assertTrue("Question has invalid wrong answer string.", q.getWrongAnswerString_2() != null && !q.getWrongAnswerString_2().equals(""));
			assertTrue("Question has invalid wrong answer string.", q.getWrongAnswerString_3() != null && !q.getWrongAnswerString_3().equals(""));
			assertTrue("Question has invalid wrong answer string.", q.getWrongAnswerString_4() != null && !q.getWrongAnswerString_4().equals(""));
			assertTrue("Question has invalid wrong answer string.", q.getWrongAnswerString_5() != null && !q.getWrongAnswerString_5().equals(""));
		}
	}
	
	@Test
	@WithMockUser(username = "user1", authorities = {"MANAGER"})
	public void testGetHardQuestions()
	{
		List<QR_Question> Qs = generator.generateQuestions(QuestionSetDifficulty.hard, 30, 1);
		assertEquals("Wrong number of Questions generated.", 30, Qs.size());
		
		for (int i = 0; i < 30; i++)
		{
			QR_Question q = Qs.get(i);
			assertEquals("Question has wrong id.", i + 1, q.getId());
			assertTrue("Question has invalid question string.", q.getQuestionString() != null && !q.getQuestionString().equals(""));
			assertTrue("Question has invalid right answer string.", q.getRightAnswerString() != null && !q.getRightAnswerString().equals(""));
			assertTrue("Question has invalid wrong answer string.", q.getWrongAnswerString_1() != null && !q.getWrongAnswerString_1().equals(""));
			assertTrue("Question has invalid wrong answer string.", q.getWrongAnswerString_2() != null && !q.getWrongAnswerString_2().equals(""));
			assertTrue("Question has invalid wrong answer string.", q.getWrongAnswerString_3() != null && !q.getWrongAnswerString_3().equals(""));
			assertTrue("Question has invalid wrong answer string.", q.getWrongAnswerString_4() != null && !q.getWrongAnswerString_4().equals(""));
			assertTrue("Question has invalid wrong answer string.", q.getWrongAnswerString_5() != null && !q.getWrongAnswerString_5().equals(""));
		}
	}
}
