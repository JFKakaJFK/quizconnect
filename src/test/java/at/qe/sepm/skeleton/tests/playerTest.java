package at.qe.sepm.skeleton.tests;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class playerTest {

    private WebDriver driver;
    private String loginpage = "localhost:8080/login.xhtml";
    private String playerUsername = "user3";
    private String playerPassword = "pw1";
    private String playerOverview = "http://localhost:8080/players/all.xhtml";
    //private String driverPath = "src\\test\\java\\at\\qe\\sepm\\skeleton\\tests\\selenium\\chromedriver.exe";//chromedriver for 64-bit Version of Chrome 74
    private String driverPath = "src/test/java/at/qe/sepm/skeleton/tests/selenium/chromedriver";       //chromedriver for Linux 64-bit Version of Chrome 75

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", driverPath);
        driver = new ChromeDriver();
        driver.get(loginpage);
        driver.findElement(By.id("username")).sendKeys(playerUsername);
        driver.findElement(By.id("password")).sendKeys(playerPassword);
        driver.findElement(By.id("submit")).click();
    }

    @Test
    public void playerOverviewTest() {
        driver.get(playerOverview); //player overview button not clickable with selenium
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "http://localhost:8080/players/all.xhtml" );
    }

    @Test
    public void createGameTest() throws InterruptedException {
        driver.findElement(By.id("form:j_idt45")).click();
        Thread.sleep(1000);
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "http://localhost:8080/quizroom/createRoom.xhtml" );
    }

    //Game Logic not testable by selenium

    @After
    public void shutDown() {
        driver.close();
    }
}
