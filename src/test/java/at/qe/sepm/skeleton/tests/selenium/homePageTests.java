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

public class homePageTests {

    private WebDriver driver;
    private String homepage = "localhost:8080";
    private String testUsername = "user1";
    private String testPassword = "pw1";

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Test\\chromedriver.exe"); //
        driver = new ChromeDriver();
    }

    @Test
    public void homePageButtonsTest() {

        driver.get(homepage);
        WebElement button = driver.findElement(By.xpath("//a[@class='btn btn-primary btn-lg']"));
        button.click();
        String currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "http://localhost:8080/login.xhtml" );

        driver.get(homepage);
        WebElement loginButton = driver.findElement(By.xpath("//a[@href='/login.xhtml']"));
        Actions actions = new Actions(driver);
        actions.moveToElement(loginButton);
        actions.perform();
        loginButton.click();
        currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "http://localhost:8080/login.xhtml" );

        driver.get(homepage);
        WebElement signupButton = driver.findElement(By.xpath("//a[@href='/signup.xhtml']"));
        actions = new Actions(driver);
        actions.moveToElement(signupButton);
        actions.perform();
        signupButton.click();
        currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "http://localhost:8080/signup.xhtml" );

        driver.get(homepage);
        WebElement coffeeButton = driver.findElement(By.xpath("//a[@href='https://www.buymeacoffee.com/']"));
        actions = new Actions(driver);
        actions.moveToElement(coffeeButton);
        actions.perform();
        coffeeButton.click();
        currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "https://www.buymeacoffee.com/" );

        driver.get(homepage);
        WebElement donateButton = driver.findElement(By.xpath("//a[@href='https://support.worldwildlife.org/site/SPageNavigator/donate_to_charity']"));
        actions = new Actions(driver);
        actions.moveToElement(donateButton);
        actions.perform();
        donateButton.click();
        currentURL = driver.getCurrentUrl();
        Assert.assertEquals(currentURL, "https://support.worldwildlife.org/site/SPageNavigator/donate_to_charity" );
    }

    @Test
    public void loginTest() {
        driver.get("http://localhost:8080/login.xhtml");
        driver.findElement(By.id("username")).sendKeys(testUsername);
        driver.findElement(By.id("password")).sendKeys(testPassword);
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
