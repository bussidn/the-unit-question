package service;

import domain.Order;
import domain.PaymentResult;
import domain.PaymentStatus;
import domain.ShippingConfirmation;
import domain.StockReservation;

import java.util.List;

public class OrderService {
    private final StockService stockService;
    private final PricingService pricingService;
    private final PaymentService paymentService;
    private final ShippingService shippingService;

    public OrderService(
        StockService stockService,
        PricingService pricingService,
        PaymentService paymentService,
        ShippingService shippingService
    ) {
        this.stockService = stockService;
        this.pricingService = pricingService;
        this.paymentService = paymentService;
        this.shippingService = shippingService;
    }

    public OrderResult createOrder(Order order) {
        List<StockReservation> reservations = stockService.reserveStock(order.items());
        if (reservations.stream().anyMatch(reservation -> !reservation.reserved())) {
            return new OrderResult.Failure(order.reject(), "Insufficient stock");
        }

        double totalPrice = pricingService.calculateTotal(order.items());
        PaymentResult paymentResult = paymentService.processPayment(
            order.id(),
            order.customerId(),
            totalPrice
        );

        if (paymentResult.status() != PaymentStatus.SUCCESS) {
            stockService.releaseStock(reservations);
            return new OrderResult.Failure(order.reject(), "Payment failed");
        }

        ShippingConfirmation shippingConfirmation = shippingService.createShipment(order);
        Order confirmedOrder = order.confirm(totalPrice);
        return new OrderResult.Success(confirmedOrder, shippingConfirmation);
    }
}

