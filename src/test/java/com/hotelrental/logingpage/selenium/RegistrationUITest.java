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
import org.openqa.selenium.support.ui.Select;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegistrationUITest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static String baseUrl;
    private static String uniqueUsername;
    private static String uniqueEmail;
    private static Process frontendProcess;

    @BeforeAll
    public static void setUp() throws Exception {
        // Start the frontend application
        Properties npmConfig = new Properties();
        npmConfig.load(RegistrationUITest.class.getResourceAsStream("/application-test.properties"));
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
        
        // Setup Chrome Driver
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--start-maximized");
        
        driver = new ChromeDriver(options);
        
        // Get configuration from test properties
        Properties props = new Properties();
        props.load(RegistrationUITest.class.getResourceAsStream("/application-test.properties"));
        baseUrl = props.getProperty("frontend.url");
        int waitTimeout = Integer.parseInt(props.getProperty("test.selenium.wait.timeout", "20"));
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(waitTimeout));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));

        // Generate unique test data
        Random random = new Random();
        uniqueUsername = "testuser" + random.nextInt(10000);
        uniqueEmail = "test" + random.nextInt(10000) + "@example.com";
    }

    @Test
    @Order(1)
    @DisplayName("Test 1: Successful User Registration")
    public void testSuccessfulRegistration() throws InterruptedException {
        // Navigate to the application
        driver.get(baseUrl);

        // Click on Register link
        WebElement registerLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Register"))
        );
        registerLink.click();

        // Wait for registration form to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));

        // Fill in the registration form
        driver.findElement(By.id("username")).sendKeys(uniqueUsername);
        driver.findElement(By.id("email")).sendKeys(uniqueEmail);
        driver.findElement(By.id("password")).sendKeys("Test@1234");

        // Select country from dropdown
        Select countryDropdown = new Select(driver.findElement(By.id("country")));
        countryDropdown.selectByValue("Sri Lanka");

        driver.findElement(By.id("city")).sendKeys("Colombo");
        driver.findElement(By.id("phone")).sendKeys("0771234567");

        // Submit the form
        WebElement submitButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
        Thread.sleep(500); // Wait for scroll
        submitButton.click();

        // Verify success message
        WebElement successMessage = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.className("success-message"))
        );

        assertTrue(successMessage.isDisplayed());
        assertTrue(successMessage.getText().contains("Registration successful"));

        // Verify redirect to login page
        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Registration with Duplicate Username")
    public void testDuplicateUsernameRegistration() throws InterruptedException {
        // Navigate to registration page
        driver.get(baseUrl + "/register");

        // Wait for form to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));

        // Try to register with same username
        driver.findElement(By.id("username")).sendKeys(uniqueUsername);
        driver.findElement(By.id("email")).sendKeys("another@example.com");
        driver.findElement(By.id("password")).sendKeys("Test@1234");

        Select countryDropdown = new Select(driver.findElement(By.id("country")));
        countryDropdown.selectByValue("India");

        driver.findElement(By.id("city")).sendKeys("Mumbai");
        driver.findElement(By.id("phone")).sendKeys("9876543210");

        // Submit the form
        WebElement submitButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
        Thread.sleep(500); // Wait for scroll
        submitButton.click();

        // Verify error message
        WebElement errorMessage = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.className("error-message"))
        );

        assertTrue(errorMessage.isDisplayed());
        assertTrue(errorMessage.getText().contains("Registration failed") ||
                errorMessage.getText().contains("Invalid registration data"));
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        if (frontendProcess != null) {
            frontendProcess.destroy();
        }
    }
}