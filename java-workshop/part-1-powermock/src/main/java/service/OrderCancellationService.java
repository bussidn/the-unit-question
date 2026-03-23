package service;

import domain.CancellationResult;
import domain.Order;
import domain.OrderStatus;
import domain.StockReservation;
import repository.OrderRepository;

import java.util.List;

public class OrderCancellationService {

    public CancellationResult cancelOrder(String orderId) {
        PaymentService paymentService = new PaymentService();
        StockService stockService = new StockService();
        ShippingService shippingService = new ShippingService();

        Order order = OrderRepository.findById(orderId);
        if (order == null) {
            return new CancellationResult.Failure("Order not found");
        }

        if (order.transactionId() != null && !paymentService.refundPayment(order.transactionId())) {
            return new CancellationResult.Failure("Refund failed");
        }

        List<StockReservation> reservations = order.items().stream()
            .map(item -> new StockReservation(item.productId(), item.quantity(), true))
            .toList();
        stockService.releaseStock(reservations);

        if (order.trackingNumber() != null) {
            shippingService.cancelShipment(order.trackingNumber());
        }

        OrderRepository.updateStatus(orderId, OrderStatus.CANCELLED);
        return new CancellationResult.Success();
    }
}