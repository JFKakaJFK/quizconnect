package at.qe.sepm.skeleton.tests;

import at.qe.sepm.skeleton.utils.ScrollPaginator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the pagination.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class ScrollPaginatorTest {

    private List<Integer> generateList(int size){
        if(size < 0) return null;

        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < size; i++){
            list.add(i);
        }

        return list;
    }

    @Test
    public void testUpdateList(){
        ScrollPaginator<Integer> paginator = new ScrollPaginator<>(null);

        Assert.assertNotNull("Parts are null", paginator.getParts());
        Assert.assertEquals("List of parts not empty", 0, paginator.getParts().size());

        paginator.updateList(generateList(5));

        Assert.assertNotNull("Parts are null", paginator.getParts());
        Assert.assertNotEquals("List of parts empty", 0, paginator.getParts().size());
    }

    @Test
    public void testInitNext(){
        ScrollPaginator<Integer> paginator = new ScrollPaginator<>(generateList(100));

        Assert.assertNotNull("Parts are null", paginator.getParts());
        Assert.assertNotEquals("List of parts not empty", 0, paginator.getParts().size());

        Assert.assertTrue("Has no second part", paginator.getParts().size() >= 2);
        Assert.assertFalse("Second part is initialized", paginator.getParts().get(1).isInitialized());
        Assert.assertEquals("Second part size is not 0", 0, paginator.getParts().get(1).getItems().size());

        paginator.initNext();

        Assert.assertTrue("Has no second part", paginator.getParts().size() >= 2);
        Assert.assertTrue("Second part is not initialized", paginator.getParts().get(1).isInitialized());
        Assert.assertNotEquals("Second part size is 0", 0, paginator.getParts().get(1).getItems().size());
    }

    @Test
    public void testIsInitialized(){
        ScrollPaginator<Integer> paginator = new ScrollPaginator<>(generateList(10), 5);

        Assert.assertNotNull("Parts are null", paginator.getParts());
        Assert.assertNotEquals("List of parts not empty", 0, paginator.getParts().size());

        // since the first part SHOULD be bigger and initialized, this makes little sense but hey %COVERAGE%
        paginator.initNext();

        Assert.assertTrue("Has no first part", paginator.getParts().size() > 0);
        Assert.assertTrue("First part is not initialized", paginator.getParts().get(0).isInitialized());
        Assert.assertNotEquals("First part size is 0", 0, paginator.getParts().get(0).getItems().size());

        Assert.assertTrue("Paginator is not fully initialized", paginator.isInitialized());
    }
}
