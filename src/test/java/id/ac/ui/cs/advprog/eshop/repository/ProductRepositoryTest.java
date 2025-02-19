package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryTest {

    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ProductRepository();
    }

    // Helper method to create a Product instance.
    private Product createProduct(String id, String name, int quantity) {
        Product product = new Product();
        product.setProductId(id);
        product.setProductName(name);
        product.setProductQuantity(quantity);
        return product;
    }

    @Test
    void testCreateAndFindAll() {
        Product product = createProduct("1", "Test Product", 10);
        Product created = repository.create(product);
        assertEquals(product, created, "Created product should be the same as the passed product");

        Iterator<Product> iterator = repository.findAll();
        assertTrue(iterator.hasNext(), "Iterator should have at least one product");
        Product found = iterator.next();
        assertEquals("1", found.getProductId());
        assertEquals("Test Product", found.getProductName());
        assertEquals(10, found.getProductQuantity());
        assertFalse(iterator.hasNext(), "Iterator should not have any more products");
    }

    @Test
    void testUpdateProductFound() {
        // Create and add an original product.
        Product original = createProduct("1", "Original Product", 5);
        repository.create(original);

        // Create an updated product with the same productId.
        Product updated = createProduct("1", "Updated Product", 15);
        Product result = repository.update(updated);
        assertNotNull(result, "Update should return the updated product");
        assertEquals("Updated Product", result.getProductName());
        assertEquals(15, result.getProductQuantity());
    }

    @Test
    void testUpdateProductNotFound() {
        // Add a product with id "1"
        Product product = createProduct("1", "Test Product", 10);
        repository.create(product);

        // Attempt to update a product with a non-existing id "2"
        Product nonExistent = createProduct("2", "Non Existent", 20);
        Product result = repository.update(nonExistent);
        assertNull(result, "Update should return null if the product is not found");
    }

    @Test
    void testDeleteProductFound() {
        // Create and add a product.
        Product product = createProduct("1", "Test Product", 10);
        repository.create(product);

        // Delete the product.
        boolean deleted = repository.delete("1");
        assertTrue(deleted, "Delete should return true when the product is found and removed");

        // Ensure the product is no longer in the repository.
        Iterator<Product> iterator = repository.findAll();
        assertFalse(iterator.hasNext(), "Repository should be empty after deletion");
    }

    @Test
    void testDeleteProductNotFound() {
        // Add a product with id "1"
        Product product = createProduct("1", "Test Product", 10);
        repository.create(product);

        // Attempt to delete a product with a non-existing id "2"
        boolean deleted = repository.delete("2");
        assertFalse(deleted, "Delete should return false if the product is not found");

        // Ensure the existing product is still in the repository.
        Iterator<Product> iterator = repository.findAll();
        assertTrue(iterator.hasNext(), "Product should still exist in the repository");
        Product found = iterator.next();
        assertEquals("1", found.getProductId());
    }
}
