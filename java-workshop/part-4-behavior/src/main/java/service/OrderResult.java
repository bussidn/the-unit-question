package service;

import domain.Order;
import domain.ShippingConfirmation;

public sealed interface OrderResult permits OrderResult.Success, OrderResult.Failure {
    record Success(Order order, ShippingConfirmation shippingConfirmation) implements OrderResult {
    }

    record Failure(Order order, String reason) implements OrderResult {
    }
}

