package service;

import domain.Order;
import domain.OrderStatus;
import domain.PaymentResult;
import domain.PaymentStatus;
import domain.ShippingConfirmation;
import domain.StockReservation;
import repository.OrderRepository;

import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository;
    private final StockService stockService;
    private final PricingService pricingService;
    private final PaymentService paymentService;
    private final ShippingService shippingService;
    private final DiscountCodeService discountCodeService;

    public OrderService(
        OrderRepository orderRepository,
        StockService stockService,
        PricingService pricingService,
        PaymentService paymentService,
        ShippingService shippingService,
        DiscountCodeService discountCodeService
    ) {
        this.orderRepository = orderRepository;
        this.stockService = stockService;
        this.pricingService = pricingService;
        this.paymentService = paymentService;
        this.shippingService = shippingService;
        this.discountCodeService = discountCodeService;
    }

    public OrderResult placeOrder(Order order) {
        if (!isValid(order)) {
            return new OrderResult.Failure(order.reject(), "Invalid order");
        }

        if (order.discountCode() != null
            && !discountCodeService.checkDiscountCode(order.customerId(), order.discountCode())) {
            return new OrderResult.Failure(order.reject(), "Discount code already used");
        }

        List<StockReservation> reservations = stockService.reserveStock(order.items());
        if (reservations.stream().anyMatch(reservation -> !reservation.reserved())) {
            return new OrderResult.Failure(order.reject(), "Insufficient stock");
        }

        double totalPrice = order.discountCode() != null
            ? pricingService.calculateTotal(order.items(), order.discountCode())
            : pricingService.calculateTotal(order.items());
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
        orderRepository.save(confirmedOrder);

        if (order.discountCode() != null) {
            discountCodeService.markAsUsed(order.customerId(), order.discountCode());
        }

        return new OrderResult.Success(confirmedOrder, shippingConfirmation);
    }

    private boolean isValid(Order order) {
        return order.status() == OrderStatus.PENDING
            && order.items() != null
            && !order.items().isEmpty();
    }
}