package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)  // Use Mockito Extension
public class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;  // Replaces @MockBean

    @Mock
    private PaymentService paymentService;  // Replaces @MockBean

    @InjectMocks
    private OrderController orderController; // Inject mocks into the controller

    private Order sampleOrder;
    private List<Product> sampleProducts;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();  // Initialize MockMvc

        // Create sample product list
        sampleProducts = new ArrayList<>();
        Product sampleProduct = new Product();
        sampleProduct.setProductId("sample-id");
        sampleProduct.setProductName("Sample Product");
        sampleProduct.setProductQuantity(1);
        sampleProducts.add(sampleProduct);

        // Create sample order
        sampleOrder = new Order(UUID.randomUUID().toString(), sampleProducts, System.currentTimeMillis(), "Sample Author");
    }

    // ✅ Test GET /order/create (Form Display)
    @Test
    void testShowCreateOrderForm() throws Exception {
        mockMvc.perform(get("/order/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("createorder"))
                .andExpect(model().attributeExists("order"));
    }

    // ✅ Test POST /order/create (Successful Order Creation)
    @Test
    void testCreateOrder_Success() throws Exception {
        when(orderService.createOrder(any(Order.class))).thenReturn(sampleOrder);

        mockMvc.perform(post("/order/create")
                        .param("author", "Sample Author")
                        .param("products", "Product1,Product2"))
                .andExpect(status().isOk())
                .andExpect(view().name("createorder"))
                .andExpect(model().attributeExists("message"));

        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    // ✅ Test POST /order/create (Failure: No Products)
    @Test
    void testCreateOrder_Failure_EmptyProducts() throws Exception {
        mockMvc.perform(post("/order/create")
                        .param("author", "Sample Author")
                        .param("products", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("createorder"))
                .andExpect(model().attributeExists("error"));

        verify(orderService, never()).createOrder(any(Order.class));
    }

    // ✅ Test GET /order/history (Display Order History Form)
    @Test
    void testShowHistoryForm() throws Exception {
        mockMvc.perform(get("/order/history"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderhistory"));
    }

    // ✅ Test POST /order/history (Retrieve Order History)
    @Test
    void testShowOrderHistory() throws Exception {
        List<Order> orders = List.of(sampleOrder);
        when(orderService.findAllByAuthor("Sample Author")).thenReturn(orders);

        mockMvc.perform(post("/order/history")
                        .param("author", "Sample Author"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderhistory"))
                .andExpect(model().attributeExists("orders"));

        verify(orderService, times(1)).findAllByAuthor("Sample Author");
    }

    // ✅ Test GET /order/pay/{orderId} (Display Payment Page)
    @Test
    void testShowOrderPaymentPage() throws Exception {
        when(orderService.findById(sampleOrder.getId())).thenReturn(sampleOrder);

        mockMvc.perform(get("/order/pay/" + sampleOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("orderpay"))
                .andExpect(model().attributeExists("order"));

        verify(orderService, times(1)).findById(sampleOrder.getId());
    }

    // ✅ Test POST /order/pay/{orderId} (Successful Payment)
    @Test
    void testProcessOrderPayment_Success() throws Exception {
        Payment samplePayment = new Payment("payment-id", "CREDIT_CARD", Map.of("cardNumber", "123456789"));
        when(orderService.findById(sampleOrder.getId())).thenReturn(sampleOrder);
        when(paymentService.addPayment(any(Order.class), anyString(), any(Map.class))).thenReturn(samplePayment);

        mockMvc.perform(post("/order/pay/" + sampleOrder.getId())
                        .param("method", "CREDIT_CARD")
                        .param("cardNumber", "123456789"))
                .andExpect(status().isOk())
                .andExpect(view().name("payment_success"))
                .andExpect(model().attributeExists("paymentId"));

        verify(paymentService, times(1)).addPayment(any(Order.class), anyString(), any(Map.class));
    }

    // ✅ Test POST /order/pay/{orderId} (Failure: Order Not Found)
    @Test
    void testProcessOrderPayment_Failure_OrderNotFound() throws Exception {
        when(orderService.findById(sampleOrder.getId())).thenReturn(null);

        mockMvc.perform(post("/order/pay/" + sampleOrder.getId())
                        .param("method", "CREDIT_CARD")
                        .param("cardNumber", "123456789"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderpay"))
                .andExpect(model().attributeExists("error"));

        verify(paymentService, never()).addPayment(any(Order.class), anyString(), any(Map.class));
    }
}
