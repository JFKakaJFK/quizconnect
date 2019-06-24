package at.qe.sepm.skeleton.tests.selenium;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class managerTests {

    private WebDriver driver;
    private String loginpage = "localhost:8080/login.xhtml";
    private String managerUsername = "user1";
    private String managerPassword = "pw1";
    private String questionSetOverview ="http://localhost:8080/secured/overview.xhtml";
    private String playerOverview = "http://localhost:8080/players/all.xhtml";
    private String driverPath = "src\\test\\java\\at\\qe\\sepm\\skeleton\\tests\\selenium\\chromedriver.exe";//chromedriver for 64-bit Version of Chrome 74

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", driverPath);
        driver = new ChromeDriver();
        driver.get(loginpage);
        driver.findElement(By.id("username")).sendKeys(managerUsername);
        driver.findElement(By.id("password")).sendKeys(managerPassword);
        driver.findElement(By.id("submit")).click();
    }
    @Test
    public void openQuestionSets() {
        WebElement questionSetButton = driver.findElement(By.xpath("//img[@src='questions.svg']"));
        questionSetButton.click();
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "http://localhost:8080/secured/overview.xhtml" );
    }



    @Test
    public void createEasyQuestionSetTest() throws InterruptedException {
        driver.get(questionSetOverview);
        driver.findElement(By.id("form:CreateNew")).click();
        driver.findElement(By.id("questionSetForm:setName")).sendKeys("testQuestionSet");
        driver.findElement(By.id("questionSetForm:setDesc")).sendKeys("testQuestionSetDescription");
        driver.findElement(By.id("questionSetForm:addQuestionSet")).click();
        Thread.sleep(1000);
        driver.findElement(By.id("questionForForm:questionModal:question")).sendKeys("Question?");
        driver.findElement(By.id("questionForForm:questionModal:corrAnswer")).sendKeys("CorrectAnswer");
        driver.findElement(By.id("questionForForm:questionModal:wrongAnswer1")).sendKeys("WrongAnswer1");
        driver.findElement(By.id("questionForForm:questionModal:wrongAnswer2")).sendKeys("WrongAnswer2");
        driver.findElement(By.id("questionForForm:questionModal:wrongAnswer3")).sendKeys("WrongAnswer3");
        driver.findElement(By.name("questionForForm:questionModal:submitButton")).click();
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "http://localhost:8080/secured/questionset.xhtml" );
    }

    @Test
    public void createNewPlayer() throws InterruptedException {
        driver.get(playerOverview);
        driver.findElement(By.id("form:j_idt14")).click();
        Thread.sleep(1000);
        driver.findElement(By.id("new_username")).sendKeys("testUser");
        driver.findElement(By.id("new_password")).sendKeys("testPassword");
        driver.findElement(By.id("repeat_new_password")).sendKeys("testPassword");
        Thread.sleep(1000);
        driver.findElement(By.id("addPlayerForm:addPlayer:j_idt76")).click();
    }


    @After
    public void shutDown() {
        driver.close();
    }
}
