package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
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
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentControllerTest paymentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    /*** ✅ Test GET /payment/detail ***/
    @Test
    void testShowPaymentDetailForm() throws Exception {
        mockMvc.perform(get("/payment/detail"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentdetail"));
    }

    /*** ✅ Test GET /payment/detail/{paymentId} ***/
    @Test
    void testShowPaymentDetail() throws Exception {
        String paymentId = UUID.randomUUID().toString();
        Payment payment = new Payment();
        when(paymentService.getPayment(paymentId)).thenReturn(payment);

        mockMvc.perform(get("/payment/detail/{paymentId}", paymentId))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentdetail"))
                .andExpect(model().attributeExists("payment"));

        verify(paymentService, times(1)).getPayment(paymentId);
    }

    /*** ✅ Test GET /payment/admin/list ***/
    @Test
    void testShowAllPayments() throws Exception {
        List<Payment> payments = new ArrayList<>();
        when(paymentService.getAllPayments()).thenReturn(payments);

        mockMvc.perform(get("/payment/admin/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentadmin_list"))
                .andExpect(model().attributeExists("payments"));

        verify(paymentService, times(1)).getAllPayments();
    }

    /*** ✅ Test GET /payment/admin/detail/{paymentId} ***/
    @Test
    void testShowAdminPaymentDetail() throws Exception {
        String paymentId = UUID.randomUUID().toString();
        Payment payment = new Payment();
        when(paymentService.getPayment(paymentId)).thenReturn(payment);

        mockMvc.perform(get("/payment/admin/detail/{paymentId}", paymentId))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentadmin_detail"))
                .andExpect(model().attributeExists("payment"));

        verify(paymentService, times(1)).getPayment(paymentId);
    }

    /*** ✅ Test POST /payment/admin/set-status/{paymentId} ***/
    @Test
    void testSetPaymentStatus() throws Exception {
        String paymentId = UUID.randomUUID().toString();
        Payment payment = new Payment();
        when(paymentService.getPayment(paymentId)).thenReturn(payment);

        mockMvc.perform(post("/payment/admin/set-status/{paymentId}", paymentId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentadmin_detail"))
                .andExpect(model().attributeExists("message"));

        verify(paymentService, times(1)).getPayment(paymentId);
        verify(paymentService, times(1)).setStatus(payment, "COMPLETED");
    }
}