package at.qe.sepm.skeleton.tests.selenium;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.concurrent.TimeUnit;

public class managerTests {

    private WebDriver driver;
    private String homepage = "localhost:8080";
    private String loginpage = "localhost:8080/login.xhtml";
    private String managerUsername = "user1";
    private String managerPassword = "pw1";
    private String questionSetOverview ="localhost:8080/secured/QSOverview.xhtml";

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

    }



    @Test
    public void createEasyQuestionSetTest() throws InterruptedException {
        driver.get(questionSetOverview);
        driver.findElement(By.id("j_idt28:CreateNew")).click();
        driver.findElement(By.id("questionSetForm:setName")).sendKeys("testQuestionSet");
        driver.findElement(By.id("questionSetForm:setDesc")).sendKeys("testQuestionSetDescription");
        driver.findElement(By.id("questionSetForm:saveQuestion")).click();
        driver.findElement(By.id("questionForm:question")).sendKeys("Question?");
        driver.findElement(By.id("questionForm:corrAnswer")).sendKeys("CorrectAnswer");
        driver.findElement(By.id("questionForm:wrongAnswer1")).sendKeys("WrongAnswer1");
        driver.findElement(By.id("questionForm:wrongAnswer2")).sendKeys("WrongAnswer2");
        driver.findElement(By.id("questionForm:wrongAnswer3")).sendKeys("WrongAnswer3");
        driver.findElement(By.id("submitNew")).click();
        WebElement yesButton = driver.findElement(By.id("questionForm:j_idt49:j_idt57"));
        Thread.sleep(1000);
        yesButton.click();
        Thread.sleep(1000);
        driver.findElement(By.id("questionForm:question")).sendKeys("Question?");
        driver.findElement(By.id("questionForm:corrAnswer")).sendKeys("CorrectAnswer");
        driver.findElement(By.id("questionForm:wrongAnswer1")).sendKeys("WrongAnswer1");
        driver.findElement(By.id("questionForm:wrongAnswer2")).sendKeys("WrongAnswer2");
        driver.findElement(By.id("questionForm:wrongAnswer3")).sendKeys("WrongAnswer3");
        driver.findElement(By.id("submitNew")).click();
        WebElement noButton = driver.findElement(By.id("questionForm:j_idt49:j_idt56"));
        Thread.sleep(1000);
        noButton.click();
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "http://localhost:8080/secured/QSOverview.xhtml" );


    }



    @After
    public void shutDown() {
        driver.close();
    }
}
