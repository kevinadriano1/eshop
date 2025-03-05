package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.OrderRepository;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {
    @InjectMocks
    PaymentServiceImpl paymentService;
    @Mock
    PaymentRepository paymentRepository;
    @Mock
    OrderRepository orderRepository;
    List<Payment> payments;
    List<Order> orders;
    Map<String,String> paymentData1;
    Map<String,String> paymentData2;

    @BeforeAll
    static void mocking() {
        // Mocking UUID to return a fixed value for predictable test results
        UUID mockUUID = UUID.fromString("a1b2c3d4-5678-9abc-def0-1234567890ab");
        mockStatic(UUID.class);
        when(UUID.randomUUID()).thenReturn(mockUUID);
    }

    @BeforeEach
    void setUp() {
        // Initializing test data before each test case
        payments = new ArrayList<>();

        paymentData1 = new HashMap<>();
        paymentData1.put("voucherCode", "ESHOP5678XYZ1234");
        Payment successPayment1 = new Payment("a1b2c3d4-5678-9abc-def0-1234567890ab", "voucherCode", paymentData1,
                PaymentStatus.SUCCESS.getValue());
        payments.add(successPayment1);

        Map<String,String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP56781234");
        Payment rejectedPayment1 = new Payment("a1b2c3d4-5678-9abc-def0-1234567890ab", "voucherCode", paymentData,
                PaymentStatus.REJECTED.getValue());
        payments.add(rejectedPayment1);

        paymentData2 = new HashMap<>();
        paymentData2.put("bankName", "Mandiri");
        paymentData2.put("referenceCode", "abcd1234-efgh-5678-ijkl-9876543210mn");

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setProductId("1234abcd-5678-efgh-ijkl-9876543210mn");
        product1.setProductName("Laptop Pro X");
        product1.setProductQuantity(1);
        products.add(product1);

        orders = new ArrayList<>();
        Order order1 = new Order("a1b2c3d4-5678-9abc-def0-1234567890ab",
                products, 1710000000L, "John Doe");
        orders.add(order1);
        Order order2 = new Order("a1b2c3d4-5678-9abc-def0-1234567890ab",
                products, 1710000000L, "John Doe");
        orders.add(order2);
    }

    // Test for successfully adding a payment
    @Test
    void AddPaymentSuccessfully() {
        Payment payment = payments.get(0);
        doReturn(payment).when(paymentRepository).save(any(Payment.class));

        Order order = orders.get(0);
        doReturn(order).when(orderRepository).findById(order.getId());

        Payment result = paymentService.addPayment(orders.get(0), payment.getMethod(), payment.getPaymentData());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderRepository, times(1)).save(any(Order.class));
        assertEquals(payment.getId(), result.getId());
        assertEquals(PaymentStatus.SUCCESS.getValue(), result.getStatus());
        assertEquals(order.getId(), paymentService.getPaymentMapping().get(payment.getId()));
    }

    // Test for adding a rejected payment
    @Test
    void AddPaymentRejected() {
        Payment payment = payments.get(1);
        doReturn(payment).when(paymentRepository).save(any(Payment.class));

        Order order = orders.get(1);
        doReturn(order).when(orderRepository).findById(order.getId());

        Payment result = paymentService.addPayment(orders.get(1), payment.getMethod(), payment.getPaymentData());

        assertEquals(order.getId(), paymentService.getPaymentMapping().get(payment.getId()));
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderRepository, times(1)).save(any(Order.class));
        assertEquals(PaymentStatus.REJECTED.getValue(),result.getStatus());
        assertEquals(payment.getId(), result.getId());
    }

    // Test for creating a payment with a null voucher code value
    @Test
    void CreatePaymentByVoucherCodeNullVoucher() {
        Order order = orders.get(0);
        doReturn(order).when(orderRepository).findById(order.getId());

        paymentData1.put("voucherCode", null);
        Payment payment = paymentService.addPayment(orders.get(0), "voucherCode", paymentData1);

        assertEquals("a1b2c3d4-5678-9abc-def0-1234567890ab", payment.getId());
        assertEquals("voucherCode", payment.getMethod());
        assertNull(payment.getPaymentData().get("voucherCode"));
        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
    }

    // Test for creating a payment when voucher code key is not present in the payment data
    @Test
    void CreatePaymentByVoucherNotInData() {
        Order order = orders.get(0);
        doReturn(order).when(orderRepository).findById(order.getId());

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.addPayment(orders.get(0), "voucherCode", paymentData2));
    }

    //test correct bank transfer
    @Test
    void CreatePaymentByBankTransferCorrect() {
        Order order = orders.get(0);
        doReturn(order).when(orderRepository).findById(order.getId());

        Payment payment = paymentService.addPayment(orders.get(0), "bankTransfer", paymentData2);

        assertEquals("a1b2c3d4-5678-9abc-def0-1234567890ab", payment.getId());
        assertEquals("bankTransfer", payment.getMethod());
        assertEquals("Mandiri", payment.getPaymentData().get("bankName"));
        assertEquals("abcd1234-efgh-5678-ijkl-9876543210mn", payment.getPaymentData().get("referenceCode"));
        assertEquals(PaymentStatus.SUCCESS.getValue(), payment.getStatus());
    }

    //test empty bank name
    @Test
    void CreatePaymentByBankTransferEmptyBankName() {
        Order order = orders.get(0);
        doReturn(order).when(orderRepository).findById(order.getId());

        paymentData2.put("bankName", "");
        Payment payment = paymentService.addPayment(orders.get(0), "bankTransfer", paymentData2);

        assertEquals("a1b2c3d4-5678-9abc-def0-1234567890ab", payment.getId());
        assertEquals("bankTransfer", payment.getMethod());
        assertEquals("", payment.getPaymentData().get("bankName"));
        assertEquals("abcd1234-efgh-5678-ijkl-9876543210mn", payment.getPaymentData().get("referenceCode"));
        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
    }

    //test no bank
    @Test
    void CreatePaymentByBankTransferNoBank() {
        Order order = orders.get(0);
        doReturn(order).when(orderRepository).findById(order.getId());

        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("referenceCode", "9b420ba0-8a05-4fe6-9810-6fd6be40cbb2");

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.addPayment(orders.get(0), "bankTransfer", paymentData));
    }

    // test no referance code
    @Test
    void CreatePaymentByBankTransferNoReferenceCode() {
        Order order = orders.get(0);
        doReturn(order).when(orderRepository).findById(order.getId());

        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("bankName", "BCA");

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.addPayment(orders.get(0), "bankTransfer", paymentData));
    }

    // Test for updating payment status to success
    @Test
    void SetStatusToSuccess(){
        Payment payment = payments.get(1);
        Order order = orders.get(1);
        order.setStatus(OrderStatus.SUCCESS.getValue());

        doReturn(payment).when(paymentRepository).getPayment(payment.getId());
        doReturn(order).when(orderRepository).findById(order.getId());
        paymentService.addPayment(order, payment.getMethod(),payment.getPaymentData());

        payment.setStatus(PaymentStatus.SUCCESS.getValue());
        doReturn(payment).when(paymentRepository).save(any(Payment.class));
        doReturn(order).when(orderRepository).findById(order.getId());
        Payment result = paymentService.setStatus(payment, PaymentStatus.SUCCESS.getValue());

        assertEquals(payment.getId(), result.getId());
        assertEquals(PaymentStatus.SUCCESS.getValue(), result.getStatus());
        verify(paymentRepository, times(1)).getPayment(payment.getId());
        verify(paymentRepository, times(2)).save(any(Payment.class));
        verify(orderRepository, times(2)).save(order);
    }

    // test set to reject
    @Test
    void SetStatusToReject(){
        Payment payment = payments.get(0);
        Order order = orders.get(0);
        order.setStatus(OrderStatus.FAILED.getValue());

        doReturn(payment).when(paymentRepository).getPayment(payment.getId());
        doReturn(order).when(orderRepository).findById(order.getId());
        paymentService.addPayment(order, payment.getMethod(),payment.getPaymentData());

        payment.setStatus(PaymentStatus.REJECTED.getValue());
        doReturn(payment).when(paymentRepository).save(any(Payment.class));
        doReturn(order).when(orderRepository).save(order);
        Payment result = paymentService.setStatus(payment, PaymentStatus.REJECTED.getValue());

        assertEquals(payment.getId(), result.getId());
        assertEquals(PaymentStatus.REJECTED.getValue(), result.getStatus());
        verify(paymentRepository, times(1)).getPayment(payment.getId());
        verify(paymentRepository, times(2)).save(any(Payment.class));
        verify(orderRepository, times(2)).save(order);
    }

    @Test
    void GetPaymentIfPaymentExists() {
        Payment payment = payments.get(1);
        doReturn(payment).when(paymentRepository).getPayment(payment.getId());

        Payment result = paymentService.getPayment(payment.getId());
        assertEquals(payment.getId(), result.getId());
    }

    // Test for fetching all payments
    @Test
    void GetAllPayment() {
        doReturn(payments).when(paymentRepository).getAllPayments();

        List<Payment> results = paymentService.getAllPayments();
        for (int i = 0; i < payments.size(); i++) {
            assertEquals(payments.get(i).getId(), results.get(i).getId());
        }
        assertEquals(2,results.size());
    }
}