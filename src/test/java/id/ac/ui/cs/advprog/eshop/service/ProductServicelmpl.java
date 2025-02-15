package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName("Test Product");
        product.setProductQuantity(10);
    }

    /** ✅ Test create() **/
    @Test
    void testCreateProduct() {
        when(productRepository.create(product)).thenReturn(product);

        Product createdProduct = productService.create(product);

        assertNotNull(createdProduct);
        assertEquals(product.getProductId(), createdProduct.getProductId());
        assertEquals(product.getProductName(), createdProduct.getProductName());
        assertEquals(product.getProductQuantity(), createdProduct.getProductQuantity());

        verify(productRepository, times(1)).create(product);
    }

    /** ✅ Test findAll() with products **/
    @Test
    void testFindAllProducts() {
        List<Product> productList = new ArrayList<>();
        productList.add(product);

        Iterator<Product> productIterator = productList.iterator();
        when(productRepository.findAll()).thenReturn(productIterator);

        List<Product> result = productService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product.getProductId(), result.get(0).getProductId());

        verify(productRepository, times(1)).findAll();
    }

    /** ✅ Test findAll() with empty list **/
    @Test
    void testFindAllEmpty() {
        when(productRepository.findAll()).thenReturn(new ArrayList<Product>().iterator());

        List<Product> result = productService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productRepository, times(1)).findAll();
    }

    /** ✅ Test update() for existing product **/
    @Test
    void testUpdateProduct() {
        when(productRepository.update(product)).thenReturn(product);

        Product updatedProduct = productService.update(product);

        assertNotNull(updatedProduct);
        assertEquals(product.getProductId(), updatedProduct.getProductId());

        verify(productRepository, times(1)).update(product);
    }

    /** ✅ Test update() for non-existent product **/
    @Test
    void testUpdateNonExistentProduct() {
        when(productRepository.update(product)).thenReturn(null);

        Product updatedProduct = productService.update(product);

        assertNull(updatedProduct, "Expected null when updating a non-existent product");

        verify(productRepository, times(1)).update(product);
    }

    /** ✅ Test delete() for existing product **/
    @Test
    void testDeleteProduct() {
        when(productRepository.delete(product.getProductId())).thenReturn(true);

        boolean result = productService.delete(product.getProductId());

        assertTrue(result);
        verify(productRepository, times(1)).delete(product.getProductId());
    }

    /** ✅ Test delete() for non-existent product **/
    @Test
    void testDeleteNonExistentProduct() {
        when(productRepository.delete(product.getProductId())).thenReturn(false);

        boolean result = productService.delete(product.getProductId());

        assertFalse(result);
        verify(productRepository, times(1)).delete(product.getProductId());
    }
}
