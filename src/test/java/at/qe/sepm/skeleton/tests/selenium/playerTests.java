package at.qe.sepm.skeleton.tests.selenium;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class playerTests {

    private WebDriver driver;
    private String homepage = "localhost:8080";
    private String loginpage = "localhost:8080/login.xhtml";
    private String playerUsername = "user3";
    private String playerPassword = "pw1";
    private String playerOverview = "http://localhost:8080/players/all.xhtml";

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Test\\chromedriver.exe"); //
        driver = new ChromeDriver();
        driver.get(loginpage);
        driver.findElement(By.id("username")).sendKeys(playerUsername);
        driver.findElement(By.id("password")).sendKeys(playerPassword);
        driver.findElement(By.id("submit")).click();
    }

    @Test
    public void createGameTest(){
        driver.findElement(By.id("formId:j_idt58")).click(); //not working button for selenium
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "http://localhost:8080/quizroom/createRoom.xhtml" );
    }

    @Test
    public void joinGameTest(){
        driver.findElement(By.xpath("//a[@class='btn btn-primary btn-lg']"));
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "http://localhost:8080/quizroom/join.html" );
    }

    @Test
    public void openPlayerStats(){
        //TODO
    }

    @Test
    public void openPlayerOverview(){
        //TODO
    }

    @Test
    public void logoutTest(){
        //TODO
    }

    @After
    public void shutDown() {
        driver.close();
    }
}
