namespace Part2.LightMocks.Domain;

public record PaymentResult(string OrderId, PaymentStatus Status, string? TransactionId);
