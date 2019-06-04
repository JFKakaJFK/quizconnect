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
    private String questionSetOverview ="http://localhost:8080/secured/questionSet.xhtml";
    private String playerOverview = "http://localhost:8080/players/all.xhtml";

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Test\\chromedriver.exe"); //
        driver = new ChromeDriver();
        driver.get(loginpage);
        driver.findElement(By.id("username")).sendKeys(managerUsername);
        driver.findElement(By.id("password")).sendKeys(managerPassword);
        driver.findElement(By.id("submit")).click();
    }
    @Test
    public void openQuestionSets() {
        WebElement questionSetButton = driver.findElement(By.xpath("//a[@href='/secured/questionSet.xhtml']"));
        questionSetButton.click();
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "http://localhost:8080/secured/questionSet.xhtml" );
    }



    @Test
    public void createEasyQuestionSetTest() throws InterruptedException {
        driver.get(questionSetOverview);
        driver.findElement(By.id("questionSetForm:saveQuestion")).click();
        driver.findElement(By.id("questionSetForm:setName")).sendKeys("testQuestionSet");
        driver.findElement(By.id("questionSetForm:setDesc")).sendKeys("testQuestionSetDescription");
        driver.findElement(By.id("questionSetForm:saveQuestion")).click();
        driver.findElement(By.id("questionForm:question")).sendKeys("Question?");
        driver.findElement(By.id("questionForm:corrAnswer")).sendKeys("CorrectAnswer");
        driver.findElement(By.id("questionForm:wrongAnswer1")).sendKeys("WrongAnswer1");
        driver.findElement(By.id("questionForm:wrongAnswer2")).sendKeys("WrongAnswer2");
        driver.findElement(By.id("questionForm:wrongAnswer3")).sendKeys("WrongAnswer3");
        driver.findElement(By.name("questionForm:j_idt94")).click();
        WebElement yesButton = driver.findElement(By.id("questionForm:j_idt97:j_idt107"));
        Thread.sleep(1000);
        yesButton.click();
        Thread.sleep(1000);
        driver.findElement(By.id("questionForm:question")).sendKeys("Question?");
        driver.findElement(By.id("questionForm:corrAnswer")).sendKeys("CorrectAnswer");
        driver.findElement(By.id("questionForm:wrongAnswer1")).sendKeys("WrongAnswer1");
        driver.findElement(By.id("questionForm:wrongAnswer2")).sendKeys("WrongAnswer2");
        driver.findElement(By.id("questionForm:wrongAnswer3")).sendKeys("WrongAnswer3");
        driver.findElement(By.name("questionForm:j_idt94")).click();
        WebElement noButton = driver.findElement(By.id("questionForm:j_idt97:j_idt107"));
        Thread.sleep(1000);
        noButton.click();
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "http://localhost:8080/secured/questionSet.xhtml" );


    }

    @Test
    public void createNewPlayer() throws InterruptedException {
        driver.get(playerOverview);
        driver.findElement(By.id("form:j_idt32")).click();
        Thread.sleep(1000);
        driver.findElement(By.id("form:modal:j_idt61")).sendKeys("testUser");
        driver.findElement(By.id("form:modal:j_idt63")).sendKeys("testPassword");
        Thread.sleep(1000);
        driver.findElement(By.id("form:addPlayer:j_idt73")).click();
    }


    @After
    public void shutDown() {
        driver.close();
    }
}
