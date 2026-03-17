package service;

import domain.CancellationResult;
import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.OrderRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderCancellationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private StockService stockService;

    @Mock
    private ShippingService shippingService;

    @InjectMocks
    private OrderCancellationService service;

    @Test
    void cancelOrder_fails_whenRefundFails() {
        var order = new Order(
            "ORDER-001",
            "CUST-001",
            List.of(new OrderItem("PROD-001", 1, 20.0)),
            OrderStatus.CONFIRMED,
            0.0,
            "TXN-001",
            "TRACK-001"
        );

        when(orderRepository.findById("ORDER-001")).thenReturn(order);
        when(paymentService.refundPayment(anyString())).thenReturn(false);

        var result = service.cancelOrder("ORDER-001");

        assertEquals(new CancellationResult.Failure("Refund failed"), result);
        verify(paymentService).refundPayment("TXN-001");
        verify(orderRepository, never()).updateStatus(anyString(), any());
        verifyNoInteractions(shippingService, stockService);
    }
}