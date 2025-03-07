package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)  // Use Mockito for dependency injection
public class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;  // Mock PaymentService

    @InjectMocks
    private PaymentController paymentController; // Inject mocks into PaymentController

    private Payment samplePayment;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();  // Initialize MockMvc

        // Create a sample payment
        samplePayment = new Payment(UUID.randomUUID().toString(), "CREDIT_CARD",
                Map.of("cardNumber", "123456789"), "SUCCESS");
    }

    // ✅ Test GET /payment/detail (Form Display)
    @Test
    void testShowPaymentDetailForm() throws Exception {
        mockMvc.perform(get("/payment/detail"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentdetail"));
    }

    // ✅ Test GET /payment/detail/{paymentId} (Display Payment Details)
    @Test
    void testShowPaymentDetail() throws Exception {
        when(paymentService.getPayment(samplePayment.getId())).thenReturn(samplePayment);

        mockMvc.perform(get("/payment/detail/" + samplePayment.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentdetail"))
                .andExpect(model().attributeExists("payment"));

        verify(paymentService, times(1)).getPayment(samplePayment.getId());
    }

    // ✅ Test GET /payment/admin/list (List All Payments)
    @Test
    void testShowAllPayments() throws Exception {
        List<Payment> payments = List.of(samplePayment);
        when(paymentService.getAllPayments()).thenReturn(payments);

        mockMvc.perform(get("/payment/admin/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentadmin_list"))
                .andExpect(model().attributeExists("payments"));

        verify(paymentService, times(1)).getAllPayments();
    }

    // ✅ Test GET /payment/admin/detail/{paymentId} (Admin View Payment Details)
    @Test
    void testShowAdminPaymentDetail() throws Exception {
        when(paymentService.getPayment(samplePayment.getId())).thenReturn(samplePayment);

        mockMvc.perform(get("/payment/admin/detail/" + samplePayment.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentadmin_detail"))
                .andExpect(model().attributeExists("payment"));

        verify(paymentService, times(1)).getPayment(samplePayment.getId());
    }

    // ✅ Test POST /payment/admin/set-status/{paymentId} (Successful Status Update)
    @Test
    void testSetPaymentStatus_Success() throws Exception {
        when(paymentService.getPayment(samplePayment.getId())).thenReturn(samplePayment);
        when(paymentService.setStatus(any(Payment.class), anyString())).thenReturn(samplePayment);

        mockMvc.perform(post("/payment/admin/set-status/" + samplePayment.getId())
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentadmin_detail"))
                .andExpect(model().attributeExists("message"));

        verify(paymentService, times(1)).setStatus(any(Payment.class), eq("COMPLETED"));
    }

    // ✅ Test POST /payment/admin/set-status/{paymentId} (Failure: Payment Not Found)
    @Test
    void testSetPaymentStatus_Failure_PaymentNotFound() throws Exception {
        when(paymentService.getPayment(samplePayment.getId())).thenReturn(null);

        mockMvc.perform(post("/payment/admin/set-status/" + samplePayment.getId())
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentadmin_detail"))
                .andExpect(model().attributeExists("error"));

        verify(paymentService, never()).setStatus(any(Payment.class), anyString());
    }
}
