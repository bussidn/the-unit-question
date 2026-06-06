namespace Part2.LightMocks.Domain;

public record PaymentRequest(string OrderId, string CustomerId, double Amount);
