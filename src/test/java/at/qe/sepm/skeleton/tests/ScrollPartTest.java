package at.qe.sepm.skeleton.tests;

import at.qe.sepm.skeleton.utils.ScrollPart;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class ScrollPartTest {

    @Mock
    public ScrollPart scrollPart;

    @Test
    public void testGetterAndSetter(){
        scrollPart.setInitialized(false);
        Assert.assertFalse(scrollPart.isInitialized());
        List itemList = new ArrayList();
        scrollPart.setItems(itemList);
        Assert.assertEquals(scrollPart.getItems(), itemList);
    }
}
