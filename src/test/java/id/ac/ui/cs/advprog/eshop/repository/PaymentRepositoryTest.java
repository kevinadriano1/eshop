package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentRepositoryTest {
    private PaymentRepository paymentRepository;
    private List<Payment> payments;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();

        payments = List.of(
                new Payment("13652556-012a-4c07-b546-54eb1396dcc9", "voucherCode",
                        Map.of("voucherCode", "SSHOPABC12345678"), PaymentStatus.SUCCESS.getValue()),

                new Payment("ed2a3070-1c4c-4cb9-81c2-44ca6cfb1abc", "voucherCode",
                        Map.of("voucherCode", "SSHOP87654321"), PaymentStatus.REJECTED.getValue())
        );

        // Save all test payments before running tests
        for (Payment payment : payments) {
            paymentRepository.save(payment);
        }
    }

    @Test
    void testSaveCreate() {
        Payment payment = payments.get(1);
        Payment retrieved = paymentRepository.getPayment(payment.getId());

        assertEquals(payment, retrieved, "Saved and retrieved payment should be the same.");
    }

    @Test
    void testSaveUpdate() {
        Payment existingPayment = payments.get(0);
        Payment updatedPayment = new Payment(existingPayment.getId(),
                existingPayment.getMethod(), existingPayment.getPaymentData());
        updatedPayment.setStatus(PaymentStatus.REJECTED.getValue());

        paymentRepository.save(updatedPayment);
        Payment retrieved = paymentRepository.getPayment(existingPayment.getId());

        assertEquals(updatedPayment, retrieved, "Updated payment should be stored correctly.");
    }

    @Test
    void testGetPaymentFound() {
        Payment retrieved = paymentRepository.getPayment(payments.get(1).getId());

        assertEquals(payments.get(1), retrieved, "Payment should be retrievable by ID.");
    }

    @Test
    void testGetPaymentNotFound() {
        assertNull(paymentRepository.getPayment("nonexistent-id"),
                "Should return null for non-existent payment.");
    }

    @Test
    void testGetAllPayments() {
        List<Payment> allPayments = paymentRepository.getAllPayments();

        assertEquals(payments.size(), allPayments.size(), "All saved payments should be retrieved.");
        assertEquals(payments, allPayments, "Retrieved payments should match expected list.");
    }
}
