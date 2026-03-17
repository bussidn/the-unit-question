package service;

import domain.CancellationResult;
import domain.Order;
import domain.OrderStatus;
import domain.StockReservation;
import repository.OrderRepository;

import java.util.List;

public class OrderCancellationService {
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final StockService stockService;
    private final ShippingService shippingService;

    public OrderCancellationService(
        OrderRepository orderRepository,
        PaymentService paymentService,
        StockService stockService,
        ShippingService shippingService
    ) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.stockService = stockService;
        this.shippingService = shippingService;
    }

    public CancellationResult cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            return new CancellationResult.Failure("Order not found");
        }

        if (order.status() != OrderStatus.CONFIRMED) {
            return new CancellationResult.Failure("Order cannot be cancelled");
        }

        if (order.transactionId() != null) {
            // THE TRAP: treats false as ERROR, but PaymentService means "nothing to refund"
            if (!paymentService.refundPayment(order.transactionId())) {
                return new CancellationResult.Failure("Refund failed");
            }
        }

        if (order.trackingNumber() != null && !shippingService.cancelShipment(order.trackingNumber())) {
            return new CancellationResult.Failure("Shipment cancellation failed");
        }

        List<StockReservation> reservations = order.items().stream()
            .map(item -> new StockReservation(item.productId(), item.quantity(), true))
            .toList();
        stockService.releaseStock(reservations);
        orderRepository.updateStatus(orderId, OrderStatus.CANCELLED);
        return new CancellationResult.Success();
    }
}