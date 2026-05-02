package service;

import domain.Order;
import domain.ShippingConfirmation;

public interface ShippingService {
    ShippingConfirmation createShipment(Order order);

    boolean cancelShipment(String trackingNumber);
}