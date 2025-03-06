package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.OrderRepository;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    private final Map<String, String> paymentMapping = new HashMap<>();

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        Order existingOrder = orderRepository.findById(order.getId());
        if (existingOrder == null) {
            throw new NoSuchElementException("Order not found");
        }

        String status = processPayment(method, paymentData);
        Payment payment = new Payment(UUID.randomUUID().toString(), method, paymentData, status);

        updateOrderStatus(existingOrder, status);
        paymentMapping.put(payment.getId(), existingOrder.getId());
        paymentRepository.save(payment);

        return payment;
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        Payment existingPayment = paymentRepository.getPayment(payment.getId());
        if (existingPayment == null) {
            throw new NoSuchElementException("Payment not found");
        }

        existingPayment.setStatus(status);
        paymentRepository.save(existingPayment);

        Order order = orderRepository.findById(paymentMapping.get(payment.getId()));
        if (order == null) {
            throw new NoSuchElementException("Associated order not found");
        }

        updateOrderStatus(order, status);
        return existingPayment;
    }

    @Override
    public Payment getPayment(String paymentId) {
        return paymentRepository.getPayment(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.getAllPayments();
    }

    public Map<String, String> getPaymentMapping() {
        return paymentMapping;
    }

    private String processPayment(String method, Map<String, String> paymentData) {
        if ("voucherCode".equals(method)) {
            return payWithVoucher(paymentData);
        } else if ("bankTransfer".equals(method)) {
            return payWithBankTransfer(paymentData);
        } else {
            throw new IllegalArgumentException("Invalid payment method: " + method);
        }
    }

    private void updateOrderStatus(Order order, String status) {
        order.setStatus(PaymentStatus.SUCCESS.getValue().equals(status)
                ? OrderStatus.SUCCESS.getValue()
                : OrderStatus.FAILED.getValue());
        orderRepository.save(order);
    }

    private String payWithVoucher(Map<String, String> paymentData) {
        if (!paymentData.containsKey("voucherCode")) {
            throw new IllegalArgumentException("Missing required voucher code");
        }

        String voucherCode = paymentData.get("voucherCode");
        if (voucherCode == null || voucherCode.length() != 16 || !voucherCode.startsWith("ESHOP") || getDigitCount(voucherCode) != 8) {
            return PaymentStatus.REJECTED.getValue();
        }
        return PaymentStatus.SUCCESS.getValue();
    }


    private static int getDigitCount(String input) {
        int count = 0;
        for (char ch : input.toCharArray()) {
            if (Character.isDigit(ch)) count++;
        }
        return count;
    }

    private String payWithBankTransfer(Map<String, String> paymentData) {
        if (!paymentData.containsKey("bankName") || !paymentData.containsKey("referenceCode")) {
            throw new IllegalArgumentException("Missing required bank transfer details");
        }
        String bank = paymentData.get("bankName");
        String referenceCode = paymentData.get("referenceCode");

        if (bank == null || bank.isEmpty() || referenceCode == null || referenceCode.isEmpty()) {
            return PaymentStatus.REJECTED.getValue();
        }
        return PaymentStatus.SUCCESS.getValue();
    }

}
