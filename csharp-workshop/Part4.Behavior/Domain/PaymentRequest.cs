namespace Part4.Behavior.Domain;

public record PaymentRequest(string OrderId, string CustomerId, double Amount);
