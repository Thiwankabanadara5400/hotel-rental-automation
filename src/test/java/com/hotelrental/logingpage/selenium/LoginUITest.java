package com.hotelrental.logingpage.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class LoginUITest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static String baseUrl;
    private static Process frontendProcess;

    @BeforeAll
    public static void setUp() throws Exception {
        // Start the frontend application
        Properties npmConfig = new Properties();
        npmConfig.load(LoginUITest.class.getResourceAsStream("/application-test.properties"));
        String npmPath = npmConfig.getProperty("npm.executable");
        if (npmPath == null) {
            // Try common locations
            if (new File("C:/Program Files/nodejs/npm.cmd").exists()) {
                npmPath = "C:/Program Files/nodejs/npm.cmd";
            } else if (new File(System.getenv("APPDATA") + "\\npm\\npm.cmd").exists()) {
                npmPath = System.getenv("APPDATA") + "\\npm\\npm.cmd";
            } else {
                throw new RuntimeException("Could not find npm executable. Please specify npm.executable in application-test.properties");
            }
        }
        ProcessBuilder frontendBuilder = new ProcessBuilder(npmPath, "run", "dev");
        frontendBuilder.directory(new File("../logfront"));
        frontendProcess = frontendBuilder.start();
        
        // Wait for frontend to start
        Thread.sleep(5000);
        
        // Setup WebDriver
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--start-maximized");
        
        driver = new ChromeDriver(options);
        
        // Get configuration from test properties
        Properties props = new Properties();
        props.load(LoginUITest.class.getResourceAsStream("/application-test.properties"));
        baseUrl = props.getProperty("frontend.url");
        int waitTimeout = Integer.parseInt(props.getProperty("test.selenium.wait.timeout", "20"));
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(waitTimeout));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
    }

    @Test
    @DisplayName("Test: Successful Login")
    public void testSuccessfulLogin() {
        try {
            driver.get(baseUrl);
            
            // Wait for page to load
            Thread.sleep(2000);

            // Click login link or navigate directly to login page
            try {
                WebElement loginLink = wait.until(
                    ExpectedConditions.elementToBeClickable(By.linkText("Login"))
                );
                loginLink.click();
            } catch (Exception e) {
                driver.get(baseUrl + "/login");
            }

            // Wait for form to load
            Thread.sleep(2000);

            // Fill login form
            WebElement usernameField = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("username"))
            );
            usernameField.clear();
            usernameField.sendKeys("testuser");

            WebElement passwordField = driver.findElement(By.id("password"));
            passwordField.clear();
            passwordField.sendKeys("password123");

            // Submit
            WebElement submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
            Thread.sleep(500); // Wait for scroll
            submitButton.click();

            // Verify login success
            WebElement welcomeMessage = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.className("user-welcome"))
            );

            assertTrue(welcomeMessage.getText().contains("Welcome"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Login test failed: " + e.getMessage());
        }
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}