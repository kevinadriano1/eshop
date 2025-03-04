package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PaymentRepository {
    private List<Payment> paymentList = new ArrayList<>();

    public Payment save(Payment payment) {
        int i = 0;
        for (Payment savedPayment : paymentList) {
            if (savedPayment.getId().equals(payment.getId())) {
                paymentList.remove(i);
                paymentList.add(i, payment);
                return payment;
            }
            i += 1;
        }

        paymentList.add(payment);
        return payment;
    }

    public Payment getPayment(String id) {
        for (Payment savedPayment: paymentList) {
            if (savedPayment.getId().equals(id)) {
                return savedPayment;
            }
        }
        return null;
    }

    public List<Payment> getAllPayments() {
        List<Payment> result = new ArrayList<>();
        for (Payment savedPayment : paymentList) {
            result.add(savedPayment);
        }
        return result;
    }
}