namespace Part3.MocksClean.Domain;

public record ShippingConfirmation(string OrderId, string TrackingNumber, string EstimatedDelivery);
