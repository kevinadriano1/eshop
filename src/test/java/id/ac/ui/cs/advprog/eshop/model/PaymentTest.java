package id.ac.ui.cs.advprog.eshop.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {
    Order order;

    @Test
    void testCreatePaymentSuccessfully() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "SHOP4567XYZ1234");
        Payment payment = new Payment("a1b2c3d4-5678-9101-1121-314151617181", "voucherCode", paymentData,
                PaymentStatus.SUCCESS.getValue());
        assertEquals("a1b2c3d4-5678-9101-1121-314151617181", payment.getId());
        assertEquals("voucherCode", payment.getMethod());
        assertEquals("SHOP4567XYZ1234", payment.getPaymentData().get("voucherCode"));
        assertEquals(PaymentStatus.SUCCESS.getValue(), payment.getStatus());
    }

    @Test
    void testCreatePaymentForInvalidStatus() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "SHOP4567XYZ1234");
        assertThrows(IllegalArgumentException.class,
                () ->new Payment("a1b2c3d4-5678-9101-1121-314151617181", "voucherCode", paymentData,
                        "MEOW"));
    }

    @Test
    void testSetStatusForSuccessStatus() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "SHOP9999ABC111");
        Payment payment = new Payment("a1b2c3d4-5678-9101-1121-314151617181", "voucherCode", paymentData,
                PaymentStatus.REJECTED.getValue());
        assertEquals("REJECTED", payment.getStatus());
        payment.setStatus("SUCCESS");
        assertEquals("SUCCESS", payment.getStatus());
    }

    @Test
    void testSetStatusForInvalidStatus() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "SHOP9999ABC111");
        Payment payment = new Payment("a1b2c3d4-5678-9101-1121-314151617181", "voucherCode", paymentData,
                PaymentStatus.REJECTED.getValue());
        assertThrows(IllegalArgumentException.class,
                () -> payment.setStatus("MEOW"));
    }
}