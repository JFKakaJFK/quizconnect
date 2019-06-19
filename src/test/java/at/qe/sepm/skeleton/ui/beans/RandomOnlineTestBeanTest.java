package at.qe.sepm.skeleton.ui.beans;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class RandomOnlineTestBeanTest {

    @Autowired
    private RandomOnlineTestBean randomOnlineTestBean;

    @Test
    public void getOnline() {
        Assert.assertNotNull(randomOnlineTestBean.getOnline());
    }

    @Test
    public void setOnline() {

    }
}