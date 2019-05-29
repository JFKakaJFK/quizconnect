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
    private WebDriver secondDriver;
    private String homepage = "localhost:8080";
    private String loginpage = "localhost:8080/login.xhtml";
    private String playerUsername = "user3";
    private String playerPassword = "pw1";
    private String playerTwoUsername = "user4";
    private String playerTwoPassword = "pw1";
    private String playerOverview = "http://localhost:8080/players/all.xhtml";

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Test\\chromedriver.exe"); //
        driver = new ChromeDriver();
        driver.get(loginpage);
        driver.findElement(By.id("username")).sendKeys(playerUsername);
        driver.findElement(By.id("password")).sendKeys(playerPassword);
        driver.findElement(By.id("submit")).click();
        secondDriver = new ChromeDriver();
        secondDriver.get(loginpage);
        secondDriver.findElement(By.id("username")).sendKeys(playerTwoUsername);
        secondDriver.findElement(By.id("password")).sendKeys(playerTwoPassword);
        secondDriver.findElement(By.id("submit")).click();
    }

    @Test
    public void createGameTest(){
        driver.findElement(By.id("formId:j_idt58")).click(); //not working button for selenium
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "http://localhost:8080/quizroom/createRoom.xhtml" );
    }

    @Test
    public void joinGameTest(){
        secondDriver.get("http://localhost:8080/quizroom/createRoom.xhtml");
        //TODO Create Game to join
        driver.findElement(By.xpath("//a[@class='btn btn-primary btn-lg']"));
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "http://localhost:8080/quizroom/join.html" );
    }

    @Test
    public void openPlayerStats(){
        //TODO
        //driver.findElement()
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "http://localhost:8080/players/profile.xhtml" );
    }

    @Test
    public void openPlayerOverview(){
        //TODO
        //driver.findElement()
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "http://localhost:8080/players/all.xhtml" );
    }

    @Test
    public void logoutTest(){
        //TODO
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "http://localhost:8080/" );
    }

    @After
    public void shutDown() {
        driver.close();
        secondDriver.close();
    }
}
