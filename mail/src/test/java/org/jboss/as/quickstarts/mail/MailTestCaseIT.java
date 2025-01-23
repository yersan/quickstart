package org.jboss.as.quickstarts.mail;

import java.time.Duration;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MailTestCaseIT {

    private static final String DEFAULT_SERVER_HOST = "http://localhost:8080";

    private WebDriver driver;

    @Before
    public void testSetup() {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");

        driver = new FirefoxDriver(options);
        driver.manage().window().maximize();

        String serverHost = System.getenv("SERVER_HOST");
        if (serverHost == null) {
            serverHost = System.getProperty("server.host");
        }
        if (serverHost == null) {
            serverHost = DEFAULT_SERVER_HOST;
        }

        driver.get(serverHost+"/mail");
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
    }

    @After
    public void cleanUp() {
        if (driver != null) {
            driver.close();
        }
    }

    @Test
    public void a_testSMTP() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        WebElement from = driver.findElement(By.id("smtp_from"));
        WebElement to = driver.findElement(By.id("smtp_to"));
        WebElement subject = driver.findElement(By.id("smtp_subject"));
        WebElement body = driver.findElement(By.id("smtp_body"));

        from.clear();
        from.sendKeys("user01@james.local");

        to.clear();
        to.sendKeys("user02@james.local");

        subject.clear();
        subject.sendKeys("This is a test");

        body.clear();
        body.sendKeys("Hello user02, I've sent an email.");

        WebElement submitButton = driver.findElement(By.id("smtp_send_btn"));
        submitButton.click();

        WebElement message = driver.findElement(By.xpath("//ul[@id='smtp_messages']/li"));
        wait.until(d -> message.isDisplayed());

        Assert.assertEquals("Unexpected result messages after sending an email via SMTP.", "Email sent to user02@james.local", message.getText());
    }

    @Test
    public void b_retrievePOP3() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        WebElement user = driver.findElement(By.id("pop3_user"));
        WebElement password = driver.findElement(By.id("pop3_password"));

        user.clear();
        user.sendKeys("user02@james.local");

        password.clear();
        password.sendKeys("1234");

        WebElement submitButton = driver.findElement(By.id("pop3_get_emails_btn"));
        submitButton.click();

        wait.until(d -> {
            try {
                WebElement emails = driver.findElement(By.id("pop3_emails"));
                return !emails.getText().isEmpty();
            } catch (StaleElementReferenceException sere) {
                return false;
            }
        });

        WebElement emails = driver.findElement(By.id("pop3_emails"));
        Assert.assertTrue("Expected From not found: " + emails.getText(), emails.getText().contains("From : user01@james.local"));
        Assert.assertTrue("Expected Subject not found: " + emails.getText(), emails.getText().contains("Subject : This is a test"));
        Assert.assertTrue("Expected Body not found : " + emails.getText(), emails.getText().contains("Body : Hello user02, I've sent an email."));
    }


    @Test
    public void c_retrieveIMAP() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        WebElement submitButton = driver.findElement(By.id("imap_get_emails_btn"));
        submitButton.click();

        wait.until(d -> {
            try {
                WebElement emails = driver.findElement(By.id("imap_emails"));
                return !emails.getText().isEmpty();
            } catch (StaleElementReferenceException sere) {
                return false;
            }
        });

        WebElement emails = driver.findElement(By.id("imap_emails"));
        Assert.assertNotNull("IMAP No messages found.", emails.getText());
        Assert.assertTrue("Expected email not found.", emails.getText().contains("From : user01@james.local"));
        Assert.assertTrue("Expected email not found.", emails.getText().contains("Subject : This is a test"));
        Assert.assertTrue("Expected email not found.", emails.getText().contains("Body : Hello user02, I've sent an email."));
    }
}
