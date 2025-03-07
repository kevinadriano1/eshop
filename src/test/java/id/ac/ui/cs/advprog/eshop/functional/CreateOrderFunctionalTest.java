package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class CreateOrderFunctionalTest {

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    private String baseUrl;

    @BeforeEach
    void setupTest() {
        WebDriverManager.chromedriver().driverVersion("134.0.0.0").setup();
        baseUrl = String.format("%s:%d", testBaseUrl, serverPort);
    }

    @Test
    void user_CreatesOrder_AndCompletesPayment(ChromeDriver driver) throws Exception {
        // Step 1: Navigate to order creation page
        driver.get(baseUrl + "/order/create");

        // Step 2: Verify we are on the correct page
        assertTrue(driver.getTitle().contains("Create Order"));

        // Step 3: Fill in order details
        WebElement authorInput = driver.findElement(By.id("authorInput"));
        authorInput.sendKeys("John Doe");

        WebElement productsInput = driver.findElement(By.id("productsInput"));
        productsInput.sendKeys("Laptop, Mouse");

        // Step 4: Submit the order creation form
        WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
        submitButton.click();

        // Step 5: Verify success message appears
        WebElement successMessage = driver.findElement(By.id("successMessage"));
        assertTrue(successMessage.getText().contains("Order created successfully!"));

        // Step 6: Navigate to order history
        driver.get(baseUrl + "/order/history");

        // Step 7: Search for the created order
        WebElement authorSearch = driver.findElement(By.id("authorSearch"));
        authorSearch.sendKeys("John Doe");

        WebElement searchButton = driver.findElement(By.xpath("//button[@type='submit']"));
        searchButton.click();

        // Step 8: Verify order appears in history
        WebElement orderAuthor = driver.findElement(By.xpath("//td[contains(text(), 'John Doe')]"));
        WebElement orderProduct = driver.findElement(By.xpath("//td[contains(text(), 'Laptop')]"));

        assertEquals("John Doe", orderAuthor.getText());
        assertTrue(orderProduct.getText().contains("Laptop"));

        // Step 9: Navigate to payment page for the order
        WebElement payButton = driver.findElement(By.xpath("//a[contains(@href, '/order/pay/')]"));
        String paymentUrl = payButton.getAttribute("href");
        driver.get(paymentUrl);

        // Step 10: Select payment method and enter payment details
        WebElement methodDropdown = driver.findElement(By.id("paymentMethod"));
        methodDropdown.sendKeys("Credit Card");

        WebElement cardNumberInput = driver.findElement(By.id("cardNumber"));
        cardNumberInput.sendKeys("1234567812345678");

        WebElement paySubmitButton = driver.findElement(By.xpath("//button[@type='submit']"));
        paySubmitButton.click();

        // Step 11: Verify payment success message
        WebElement paymentSuccessMessage = driver.findElement(By.id("paymentSuccessMessage"));
        assertTrue(paymentSuccessMessage.getText().contains("Payment successful!"));
    }
}
