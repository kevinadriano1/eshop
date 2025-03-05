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
public class PaymentServiceImpl implements PaymentService{
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    OrderRepository orderRepository;

    Map<String,String> paymentMapping = new HashMap<>();

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        String status;
        Order order1 = orderRepository.findById(order.getId());
        if (order1 == null) {
            throw new NoSuchElementException();
        }
        if (method.equals("voucherCode")) {
            status = payWithVoucher(paymentData);
        } else if (method.equals("bankTransfer")) {
            status = payWithBankTransfer(paymentData);
        } else {
            throw new IllegalArgumentException();
        }
        Payment payment = new Payment(UUID.randomUUID().toString(), method, paymentData, status);
        if (status == PaymentStatus.SUCCESS.getValue()) {
            order1.setStatus(OrderStatus.SUCCESS.getValue());
        } else {
            order1.setStatus(OrderStatus.FAILED.getValue());
        }
        orderRepository.save(order1);
        paymentMapping.put(payment.getId(), order1.getId());
        paymentRepository.save(payment);
        return payment;
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        Payment payment1 = paymentRepository.getPayment(payment.getId());
        if (payment1 == null) {
            throw new NoSuchElementException();
        }
        payment.setStatus(status);
        paymentRepository.save(payment);
        Order order = orderRepository.findById(paymentMapping.get(payment.getId()));
        if (status == PaymentStatus.SUCCESS.getValue()) {
            order.setStatus(OrderStatus.SUCCESS.getValue());
        } else {
            order.setStatus(OrderStatus.FAILED.getValue());
        }
        orderRepository.save(order);
        return payment;
    }

    @Override
    public Payment getPayment(String paymentId) {return paymentRepository.getPayment(paymentId);}

    @Override
    public List<Payment> getAllPayments() {return paymentRepository.getAllPayments();}

    public Map<String,String> getPaymentMapping() {
        return paymentMapping;
    }

    private String payWithVoucher(Map<String, String> paymentData) {
        String status;
        if (!paymentData.containsKey("voucherCode")) {
            throw new IllegalArgumentException();
        }
        String voucherCode = paymentData.get("voucherCode");
        if (voucherCode == null) {
            status = PaymentStatus.REJECTED.getValue();
            return status;
        }

        if (voucherCode.length() != 16 || !voucherCode.startsWith("ESHOP") || getDigitCount(voucherCode) != 8) {
            status = PaymentStatus.REJECTED.getValue();
        } else {
            status = PaymentStatus.SUCCESS.getValue();
        }
        return status;
    }

    private static int getDigitCount(String voucherCode) {
        int digitCount = 0;
        for (int i = 0; i < voucherCode.length(); i++) {
            char ch = voucherCode.charAt(i);
            if (Character.isDigit(ch)) {
                digitCount++;
            }
        }
        return digitCount;
    }

    private String payWithBankTransfer(Map<String, String> paymentData) {
        if (!paymentData.containsKey("bankName") || !paymentData.containsKey("referenceCode")) {
            throw new IllegalArgumentException();
        }
        String bank = paymentData.get("bankName");
        String referenceCode = paymentData.get("referenceCode");
        if (bank == null|| bank.isEmpty() || referenceCode == null || referenceCode.isEmpty()){
            return PaymentStatus.REJECTED.getValue();
        } else {
            return PaymentStatus.SUCCESS.getValue();
        }
    }
}