package service;

import domain.Order;
import domain.PaymentResult;
import domain.PaymentStatus;
import domain.ShippingConfirmation;
import domain.StockReservation;
import repository.OrderRepository;

import java.util.List;

/**
 * Main orchestrator - creates all dependencies internally.
 * This is typical legacy code: no dependency injection, everything is instantiated inline.
 */
public class OrderService {

    public OrderResult placeOrder(Order order) {
        StockService stockService = new StockService();
        PaymentService paymentService = new PaymentService();
        ShippingService shippingService = new ShippingService();
        OrderRepository orderRepository = new OrderRepository();

        List<StockReservation> reservations = stockService.reserveStock(order.items());
        boolean hasUnavailableItems = reservations.stream().anyMatch(r -> !r.reserved());
        if (hasUnavailableItems) {
            return new OrderResult.Failure(order.reject(), "Insufficient stock");
        }

        double totalPrice = PricingService.calculateTotal(order.items());

        PaymentResult payment = paymentService.processPayment(order.id(), order.customerId(), totalPrice);
        if (payment.status() != PaymentStatus.SUCCESS) {
            stockService.releaseStock(reservations);
            return new OrderResult.Failure(order.reject(), "Payment failed");
        }

        ShippingConfirmation shipping = shippingService.createShipment(order);
        Order confirmedOrder = order.confirm(
            totalPrice,
            payment.transactionId(),
            shipping.trackingNumber()
        );

        orderRepository.save(confirmedOrder);
        return new OrderResult.Success(confirmedOrder, shipping);
    }
}