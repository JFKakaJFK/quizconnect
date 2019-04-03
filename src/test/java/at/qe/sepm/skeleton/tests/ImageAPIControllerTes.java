package at.qe.sepm.skeleton.tests;

import at.qe.sepm.skeleton.ui.controllers.ImageAPIController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Some very basic tests for {@link ImageAPIController}.
 *
 * @author Simon Triendl & Johanens Koch
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = ImageAPIController.class)
//@WebAppConfiguration
//@WebMvcTest(ImageController.class)
public class ImageAPIControllerTes {


    private String avatarsEndpoint;
    private String answersEndpoint;

    @Autowired
    WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @Autowired
    private void init(
            @Value("${storage.api.avatars}") String avatarsEndpoint,
            @Value("${storage.api.answers}") String answersEndpoint) {
        this.avatarsEndpoint = "/" + avatarsEndpoint;
        this.answersEndpoint = "/" + answersEndpoint;
    }

    @Before
    public void setUp(){
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @MockBean
    private ImageAPIController imageAPIController;

    /*
    TODO
    - test actual received content
        - is image
        - size > 0
    = test valid api calls
        - testpic?
     */

    @Test
    public void testDefaultAvatar() throws Exception {
        String uri = avatarsEndpoint + "test/bad.png";
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get(uri)
                .accept(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG)).andReturn();

        int status = result.getResponse().getStatus();
        Assert.assertEquals("Status is OK", 200, status);
        System.out.println(result.getResponse().getContentType());
    }

    @Test
    public void testDefaultAnswer() throws Exception {

        String uri = answersEndpoint + "test/bad.png";
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get(uri)
                .accept(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG)).andReturn();

        int status = result.getResponse().getStatus();
        Assert.assertEquals("Status is OK", 200, status);
    }
}
