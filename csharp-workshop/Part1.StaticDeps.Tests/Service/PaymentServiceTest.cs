using Part1.StaticDeps.Domain;
using Part1.StaticDeps.Service;
using Xunit;

namespace Part1.StaticDeps.Tests.Service;

/// <summary>
/// All tests in this class require static mocking of <see cref="Part1.StaticDeps.Infrastructure.PaymentGateway"/>.
/// In Java, Mockito.mockStatic() handles this. In C#, NSubstitute cannot mock static methods.
/// Use JustMock or Typemock for the equivalent capability.
/// </summary>
public class PaymentServiceTest
{
    [Fact(Skip = "Static mocking requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockStatic().")]
    public void P1_ProcessPayment_ReturnsSuccess_WhenGatewaySucceeds()
    {
        // Given
        // PaymentGateway.ProcessPayment("CUST-456", 100.0) stubbed to return Success / "TXN-789"

        // When
        // paymentService.ProcessPayment("ORDER-123", "CUST-456", 100.0)

        // Then
        // result.Status == PaymentStatus.Success
        // result.OrderId == "ORDER-123"
        // result.TransactionId == "TXN-789"
        // PaymentGateway.ProcessPayment called once
    }

    [Fact(Skip = "Static mocking requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockStatic().")]
    public void P1_ProcessPayment_ReturnsFailure_ForZeroAmount_WithoutCallingGateway()
    {
        // Given amount = 0.0

        // When
        // paymentService.ProcessPayment("ORDER-123", "CUST-456", 0.0)

        // Then
        // result.Status == PaymentStatus.Failed
        // PaymentGateway.ProcessPayment never called
    }

    [Fact(Skip = "Static mocking requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockStatic().")]
    public void P1_ProcessPayment_ReturnsFailure_WhenGatewayFails()
    {
        // Given
        // PaymentGateway.ProcessPayment stubbed to return Failed

        // Then result.Status == PaymentStatus.Failed
    }

    [Fact(Skip = "Static mocking requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockStatic().")]
    public void P1_RefundPayment_ReturnsTrue_WhenGatewayRefundSucceeds()
    {
        // Given PaymentGateway.Refund("TXN-789") returns true

        // Then result == true, Refund called once
    }

    [Fact(Skip = "Static mocking requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockStatic().")]
    public void P1_RefundPayment_ReturnsFalse_WhenPaymentWasAlreadyRefunded()
    {
        // Given PaymentGateway.Refund("TXN-789") returns false

        // Then result == false
    }
}
