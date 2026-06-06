namespace Part2.LightMocks.Domain;

public record ShippingConfirmation(string OrderId, string TrackingNumber, string EstimatedDelivery);
