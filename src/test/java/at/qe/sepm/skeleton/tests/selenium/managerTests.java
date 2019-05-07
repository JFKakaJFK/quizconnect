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

public class managerTests {

    private WebDriver driver;
    private String homepage = "localhost:8080";
    private String loginpage = "localhost:8080/login.xhtml";
    private String managerUsername = "user1";
    private String managerPassword = "pw1";

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Test\\chromedriver.exe"); //
        driver = new ChromeDriver();
        driver.get(loginpage);
        driver.findElement(By.id("username")).sendKeys(managerUsername );
        driver.findElement(By.id("password")).sendKeys(managerPassword);
        WebElement button = driver.findElement(By.id("submit"));
        button.click();
    }
    @Test
    public void openQuestionSets() {

    }



    @Test
    public void createQuestionSetTest() {

    }


    @After
    public void shutDown() {
        driver.close();
    }
}
