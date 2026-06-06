namespace Part3.MocksClean.Domain;

public record PaymentResult(string OrderId, PaymentStatus Status, string? TransactionId);
