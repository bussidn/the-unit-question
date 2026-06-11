namespace Part1.StaticDeps.Domain;

public record ShippingConfirmation(string OrderId, string TrackingNumber, string EstimatedDelivery);
