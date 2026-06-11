namespace Part4.Behavior.Domain;

public record ShippingConfirmation(string OrderId, string TrackingNumber, string EstimatedDelivery);
