package id.ac.ui.cs.advprog.eshop.model;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;

import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Getter
public class Payment {
    private static final Set<String> ALLOWED_STATUSES = Set.of("SUCCESS", "REJECTED");

    String id;
    String method;
    String status;
    Map<String, String> paymentData;

    public Payment(String id, String method, Map<String,String> paymentData) {
        this.id = id;
        this.method = method;
        this.paymentData = paymentData;
    }

    public Payment(String id, String method, Map<String,String> paymentData, String status) {
        this(id,method, paymentData);
        this.setStatus(status);
    }

    public void setStatus(String status) {
        if (ALLOWED_STATUSES.contains(status)) {
            this.status = status;
        } else {
            throw new IllegalArgumentException();
        }
    }
}