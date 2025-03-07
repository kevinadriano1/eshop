package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderControllerTest orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    void testShowCreateOrderForm() throws Exception {
        mockMvc.perform(get("/order/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("createorder"))
                .andExpect(model().attributeExists("order"));
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        mockMvc.perform(post("/order/create")
                        .param("author", "John Doe")
                        .param("products", "Product1, Product2"))
                .andExpect(status().isOk())
                .andExpect(view().name("createorder"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    void testCreateOrder_Failure_EmptyProducts() throws Exception {
        mockMvc.perform(post("/order/create")
                        .param("author", "John Doe")
                        .param("products", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("createorder"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void testShowOrderHistory() throws Exception {
        when(orderService.findAllByAuthor("John Doe")).thenReturn(new ArrayList<>());

        mockMvc.perform(post("/order/history")
                        .param("author", "John Doe"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderhistory"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    void testShowOrderPaymentPage() throws Exception {
        // Create a valid product list
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId("product-123");
        product.setProductName("Laptop");
        product.setProductQuantity(1);
        products.add(product);

        // Ensure the correct constructor is used
        Order order = new Order(
                "123",
                products,
                System.currentTimeMillis(),
                "John Doe"
        );

        when(orderService.findById("123")).thenReturn(order);

        mockMvc.perform(get("/order/pay/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderpay"))
                .andExpect(model().attributeExists("order"));
    }


    @Test
    void testProcessOrderPayment_Success() throws Exception {
        // Fix: Ensure order has at least one product
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId("product-123");
        product.setProductName("Laptop");
        product.setProductQuantity(1);
        products.add(product);

        Order order = new Order("123", products, System.currentTimeMillis(), "John Doe");

        // Fix: Correct Payment constructor usage
        Payment payment = new Payment("payment-123", "credit_card", new HashMap<>(), "SUCCESS");

        when(orderService.findById("123")).thenReturn(order);
        when(paymentService.addPayment(eq(order), anyString(), anyMap())).thenReturn(payment);

        mockMvc.perform(post("/order/pay/123")
                        .param("method", "credit_card")
                        .param("cardNumber", "1234567812345678"))
                .andExpect(status().isOk())
                .andExpect(view().name("payment_success"))
                .andExpect(model().attributeExists("paymentId"));
    }



    @Test
    void testProcessOrderPayment_Failure_OrderNotFound() throws Exception {
        when(orderService.findById("999")).thenReturn(null);

        mockMvc.perform(post("/order/pay/999")
                        .param("method", "credit_card")
                        .param("cardNumber", "1234567812345678"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderpay"))
                .andExpect(model().attributeExists("error"));
    }
}
