package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/create")
    public String showCreateOrderForm(Model model) {
        List<Product> sampleProducts = new ArrayList<>();
        Product sampleProduct = new Product();
        sampleProduct.setProductId("sample-id");
        sampleProduct.setProductName("Sample Product");
        sampleProduct.setProductQuantity(1);
        sampleProducts.add(sampleProduct);

        model.addAttribute("order", new Order(null, sampleProducts, System.currentTimeMillis(), "Sample Author"));
        return "createorder";
    }

    @PostMapping("/create")
    public String createOrder(@RequestParam("author") String author,
                              @RequestParam("products") String productsInput,
                              Model model) {
        try {
            List<Product> products = new ArrayList<>();
            for (String productName : productsInput.split(",")) {
                productName = productName.trim();
                if (!productName.isEmpty()) {
                    Product product = new Product();
                    product.setProductId(java.util.UUID.randomUUID().toString()); // Assign a unique ID
                    product.setProductName(productName);
                    product.setProductQuantity(1); // Default quantity
                    products.add(product);
                }
            }

            if (products.isEmpty()) {
                throw new IllegalArgumentException("Order must contain at least one product.");
            }

            Order order = new Order(java.util.UUID.randomUUID().toString(), products, System.currentTimeMillis(), author);

            orderService.createOrder(order);
            model.addAttribute("message", "Order created successfully!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Failed to create order: " + e.getMessage());
        }

        return "createorder";
    }

    @GetMapping("/history")
    public String showHistoryForm() {
        return "orderhistory";
    }

    @PostMapping("/history")
    public String showOrderHistory(@RequestParam("author") String author, Model model) {
        List<Order> orders = orderService.findAllByAuthor(author);
        model.addAttribute("orders", orders);
        return "orderhistory";
    }

    @GetMapping("/pay/{orderId}")
    public String showOrderPaymentPage(@PathVariable String orderId, Model model) {
        Order order = orderService.findById(orderId);
        model.addAttribute("order", order);
        return "orderpay";
    }

    @PostMapping("/pay/{orderId}")
    public String processOrderPayment(@PathVariable String orderId,
                                      @RequestParam("method") String method,
                                      @RequestParam Map<String, String> paymentData,
                                      Model model) {
        Order order = orderService.findById(orderId);
        if (order != null) {
            Payment payment = paymentService.addPayment(order, method, paymentData);
            model.addAttribute("paymentId", payment.getId());
            return "payment_success";
        } else {
            model.addAttribute("error", "Order not found.");
            return "orderpay";
        }
    }
}