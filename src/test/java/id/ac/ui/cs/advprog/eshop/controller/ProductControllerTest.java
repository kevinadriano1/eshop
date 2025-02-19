package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    /*** ✅ Test GET /product/create ***/
    @Test
    void testCreateProductPage() throws Exception {
        mockMvc.perform(get("/product/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("createProduct"))
                .andExpect(model().attributeExists("product"));
    }

    /*** ✅ Test POST /product/create ***/
    @Test
    void testCreateProductPost() throws Exception {
        Product product = new Product();
        product.setProductName("Test Product");
        product.setProductQuantity(10);

        mockMvc.perform(post("/product/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("productName", product.getProductName())
                        .param("productQuantity", String.valueOf(product.getProductQuantity())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("list"));

        verify(productService, times(1)).create(any(Product.class));
    }

    /*** ✅ Test GET /product/list ***/
    @Test
    void testProductListPage() throws Exception {
        List<Product> productList = new ArrayList<>();
        Product product1 = new Product();
        product1.setProductId(UUID.randomUUID().toString());
        product1.setProductName("Product 1");
        product1.setProductQuantity(5);
        productList.add(product1);

        when(productService.findAll()).thenReturn(productList);

        mockMvc.perform(get("/product/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("productList"))
                .andExpect(model().attributeExists("products"));

        verify(productService, times(1)).findAll();
    }

    /*** ✅ Test GET /product/edit/{id} - Product Found ***/
    @Test
    void testEditProductPage_ProductFound() throws Exception {
        String productId = "12345";
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName("Test Product");
        product.setProductQuantity(10);

        List<Product> productList = new ArrayList<>();
        productList.add(product);
        when(productService.findAll()).thenReturn(productList);

        mockMvc.perform(get("/product/edit/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(view().name("editProduct"))
                .andExpect(model().attributeExists("product"));

        verify(productService, times(1)).findAll();
    }

    /*** ✅ Test GET /product/edit/{id} - Product Not Found ***/
    @Test
    void testEditProductPage_ProductNotFound() throws Exception {
        when(productService.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/product/edit/{id}", "non-existent-id"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/list"));

        verify(productService, times(1)).findAll();
    }

    /*** ✅ Test POST /product/edit ***/
    @Test
    void testEditProductPost() throws Exception {
        Product product = new Product();
        product.setProductId("12345");
        product.setProductName("Updated Product");
        product.setProductQuantity(20);

        mockMvc.perform(post("/product/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("productId", product.getProductId())
                        .param("productName", product.getProductName())
                        .param("productQuantity", String.valueOf(product.getProductQuantity())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/list"));

        verify(productService, times(1)).update(any(Product.class));
    }

    /*** ✅ Test GET /product/delete/{id} ***/
    @Test
    void testDeleteProduct() throws Exception {
        String productId = "12345";

        mockMvc.perform(get("/product/delete/{id}", productId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/list"));

        verify(productService, times(1)).delete(productId);

    }
    /*** ✅ Test GET /product/edit/{id} - Edge Case: Product has null id ***/
    @Test
    void testEditProductPage_ProductWithNullId() throws Exception {
        // Create a product with a null productId
        Product product = new Product();
        product.setProductId(null);
        product.setProductName("Product with null id");
        product.setProductQuantity(5);

        List<Product> productList = new ArrayList<>();
        productList.add(product);
        when(productService.findAll()).thenReturn(productList);

        // Attempt to access edit page with any id. Since the product's id is null, it should not match.
        mockMvc.perform(get("/product/edit/{id}", "anyId"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/list"));

        verify(productService, times(1)).findAll();
    }

}
