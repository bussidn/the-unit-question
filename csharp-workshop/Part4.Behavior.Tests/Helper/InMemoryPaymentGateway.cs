using Part4.Behavior.Domain;
using Part4.Behavior.Gateway;

namespace Part4.Behavior.Tests.Helper;

public class InMemoryPaymentGateway : IPaymentGateway
{
    private bool _willDecline = false;
    private PaymentResult? _lastResult;
    private readonly List<string> _refunds = new();

    public void WillDecline()
    {
        _willDecline = true;
    }

    public PaymentResult Process(PaymentRequest request)
    {
        if (_willDecline)
        {
            _lastResult = new PaymentResult(request.OrderId, PaymentStatus.Failed, null);
        }
        else
        {
            _lastResult = new PaymentResult(request.OrderId, PaymentStatus.Success, "TXN-" + request.OrderId);
        }
        return _lastResult;
    }

    public bool Refund(string transactionId)
    {
        _refunds.Add(transactionId);
        return true;
    }

    public bool WasCharged() => _lastResult != null && _lastResult.Status == PaymentStatus.Success;

    public string? LastTransactionId() => _lastResult?.TransactionId;

    public bool WasRefunded(string transactionId) => _refunds.Contains(transactionId);
}
