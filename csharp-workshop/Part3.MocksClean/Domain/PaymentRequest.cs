namespace Part3.MocksClean.Domain;

public record PaymentRequest(string OrderId, string CustomerId, double Amount);
