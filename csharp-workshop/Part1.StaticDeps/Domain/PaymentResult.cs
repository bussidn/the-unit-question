namespace Part1.StaticDeps.Domain;

public record PaymentResult(string OrderId, PaymentStatus Status, string? TransactionId);
