package service;

import domain.Order;
import domain.PaymentResult;
import domain.PaymentStatus;
import domain.ShippingConfirmation;
import domain.StockReservation;
import infrastructure.Database;

import java.util.List;

/**
 * Main orchestrator - creates all dependencies internally.
 * The PowerMock nightmare.
 */
public class OrderService {

    public OrderResult createOrder(Order order) {
        StockService stockService = new StockService();
        PricingService pricingService = new PricingService();
        PaymentService paymentService = new PaymentService();
        ShippingService shippingService = new ShippingService();

        if (!stockService.checkAvailability(order.items())) {
            return new OrderResult.Failure(order.reject(), "Insufficient stock");
        }

        double totalPrice = pricingService.calculateTotal(order.items());
        PaymentResult paymentResult = paymentService.processPayment(
            order.id(),
            order.customerId(),
            totalPrice
        );

        if (paymentResult.status() != PaymentStatus.SUCCESS) {
            return new OrderResult.Failure(order.reject(), "Payment failed");
        }

        List<StockReservation> reservations = stockService.reserveStock(order.items());
        if (reservations.stream().anyMatch(reservation -> !reservation.reserved())) {
            if (paymentResult.transactionId() != null) {
                paymentService.refundPayment(paymentResult.transactionId());
            }
            stockService.releaseStock(reservations);
            return new OrderResult.Failure(order.reject(), "Could not reserve stock");
        }

        ShippingConfirmation shippingConfirmation = shippingService.createShipment(order);
        Order confirmedOrder = order.confirm(
            totalPrice,
            paymentResult.transactionId(),
            shippingConfirmation.trackingNumber()
        );

        Database.saveOrder(confirmedOrder);

        return new OrderResult.Success(confirmedOrder, shippingConfirmation);
    }
}