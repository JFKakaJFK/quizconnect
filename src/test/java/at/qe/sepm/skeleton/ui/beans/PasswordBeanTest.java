package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.ui.beans.PasswordBean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class PasswordBeanTest {

    @Autowired
    PasswordBean passwordBean;

    @Test
    public void passwordBeanTest(){
        String testString = "1234passwort";
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password1 = passwordBean.encodePassword(testString);

        Assert.assertTrue("Password Encoder is not correctly functioning", passwordEncoder.matches(testString, password1));
        Assert.assertNotEquals("PasswordEncoder does not work as expected", passwordEncoder.encode(testString), passwordBean.encodePassword(testString));
    }
}
