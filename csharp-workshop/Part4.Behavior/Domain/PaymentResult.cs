namespace Part4.Behavior.Domain;

public record PaymentResult(string OrderId, PaymentStatus Status, string? TransactionId);
