package at.qe.sepm.skeleton.ui.beans;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

/**
 * Basic test for AnswerPictureBean
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AnswerPictureBeanTest {

    @Autowired
    private AnswerPictureBean answerPictureBean;

    @Test
    public void getAnswers() {
        String testPath = null;
        Assert.assertTrue(answerPictureBean.getAnswers(testPath).contains("default"));
        testPath = "default/default/default.png";
        Assert.assertTrue(answerPictureBean.getAnswers(testPath).contains(testPath));
    }
}