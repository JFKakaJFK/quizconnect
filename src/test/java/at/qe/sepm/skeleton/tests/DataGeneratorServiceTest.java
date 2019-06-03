package at.qe.sepm.skeleton.tests;

import at.qe.sepm.skeleton.services.DataGeneratorService;
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
public class DataGeneratorServiceTest {

    @Autowired
    private DataGeneratorService dataGeneratorService;

    }

