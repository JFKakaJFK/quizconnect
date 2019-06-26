package at.qe.sepm.skeleton.ui.converter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

/**
 * Tests CapitalizeConverter
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class CapitalizeConverterTest {

    @Autowired
    private CapitalizeConverter capitalizeConverter;

    @Test
    public void testCapitalize() {
        Assert.assertEquals(capitalizeConverter.capitalize("hello"), "Hello");
    }
}