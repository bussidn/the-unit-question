using Part1.StaticDeps.Domain;
using Part1.StaticDeps.Service;
using Xunit;

namespace Part1.StaticDeps.Tests.Service;

/// <summary>
/// All tests require constructor interception of
/// <see cref="Part1.StaticDeps.Infrastructure.DatabaseDiscountCodeRepository"/>.
/// In Java, Mockito.mockConstruction() intercepts every <c>new DatabaseDiscountCodeRepository()</c>
/// call inside the service constructor. In C#, NSubstitute cannot intercept constructors.
/// Use JustMock or Typemock for the equivalent capability.
/// </summary>
public class DiscountCodeServiceTest
{
    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_CheckDiscountCode_ReturnsTrue_WhenCodeHasAlreadyBeenUsed()
    {
        // Given
        // mockConstruction(DatabaseDiscountCodeRepository) stubs
        //   CheckDiscountCode("CUST-001", DiscountCode.SUMMER20) → true

        // When
        // discountCodeService.CheckDiscountCode("CUST-001", DiscountCode.SUMMER20)

        // Then result == true
    }

    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_CheckDiscountCode_ReturnsFalse_WhenCodeHasNotBeenUsed()
    {
        // Given
        // mockConstruction(DatabaseDiscountCodeRepository) stubs
        //   CheckDiscountCode("CUST-001", DiscountCode.SUMMER20) → false

        // Then result == false
    }

    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_MarkAsUsed_DelegatesToRepository()
    {
        // Given mockConstruction(DatabaseDiscountCodeRepository) with no stubs

        // When discountCodeService.MarkAsUsed("CUST-001", DiscountCode.SUMMER20)

        // Then
        // constructedRepo.Received(1).MarkAsUsed("CUST-001", DiscountCode.SUMMER20)
    }
}
