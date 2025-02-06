package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
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
class CreateProductFunctionalTest {

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    private String baseUrl;

    @BeforeEach
    void setupTest() {
        baseUrl = String.format("%s:%d", testBaseUrl, serverPort);
    }

    @Test
    void user_CreateProduct_andSee(ChromeDriver driver) throws Exception {
        // Step 1: Navigate to product creation page
        driver.get(baseUrl + "/product/create");

        // Step 2: Verify we are on the correct page
        assertEquals("Create New Product", driver.getTitle());

        // Step 3: Fill in product details
        WebElement nameInput = driver.findElement(By.id("nameInput"));
        nameInput.sendKeys("Test Product");

        WebElement quantityInput = driver.findElement(By.id("quantityInput"));
        quantityInput.sendKeys("10");

        // Step 4: Submit the form
        WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
        submitButton.click();

        // Step 5: Verify redirection to product list page
        assertTrue(driver.getCurrentUrl().contains("/product/list"));

        // Step 6: Check if the new product appears in the product list
        WebElement addedProductName = driver.findElement(By.xpath("//td[contains(text(), 'Test Product')]"));
        WebElement addedProductQuantity = driver.findElement(By.xpath("//td[contains(text(), '10')]"));

        assertEquals("Test Product", addedProductName.getText());
        assertEquals("10", addedProductQuantity.getText());
    }
}
