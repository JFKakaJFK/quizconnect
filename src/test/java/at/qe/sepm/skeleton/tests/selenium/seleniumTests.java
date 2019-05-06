package at.qe.sepm.skeleton.tests.selenium;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class seleniumTests {

    private WebDriver driver;

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Test\\chromedriver.exe"); //
        driver = new ChromeDriver();
    }

    @Test
    public void homePageTest() {

        driver.get("localhost:8080");
        WebElement button = driver.findElement(By.xpath("//a[@class='btn btn-primary btn-lg']"));
        button.click();
        String URL = driver.getCurrentUrl();
        Assert.assertEquals(URL, "http://localhost:8080/login.xhtml" );
    }

    @Test
    public void loginTest() {
        driver.get("http://localhost:8080/login.xhtml");
        driver.findElement(By.id("username")).sendKeys("user1");
        driver.findElement(By.id("password")).sendKeys("pw1");
        WebElement button = driver.findElement(By.id("submit"));
        button.click();
        String URL = driver.getCurrentUrl();
        Assert.assertEquals(URL, "http://localhost:8080/secured/home.xhtml" );
    }

    @After
    public void shutDown() {
        driver.close();
    }
}
