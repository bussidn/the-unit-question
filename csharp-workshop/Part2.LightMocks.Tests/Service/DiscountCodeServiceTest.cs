using NSubstitute;
using Part2.LightMocks.Domain;
using Part2.LightMocks.Repository;
using Part2.LightMocks.Service;
using Xunit;

namespace Part2.LightMocks.Tests.Service;

public class DiscountCodeServiceTest
{
    [Fact]
    public void P2_CheckDiscountCode_ReturnsTrue_WhenCodeHasAlreadyBeenUsed()
    {
        var repository = Substitute.For<IDiscountCodeRepository>();
        var discountCodeService = new DiscountCodeService(repository);

        repository.CheckDiscountCode("CUST-001", DiscountCode.SUMMER20).Returns(true);

        Assert.True(discountCodeService.CheckDiscountCode("CUST-001", DiscountCode.SUMMER20));
    }

    [Fact]
    public void P2_CheckDiscountCode_ReturnsFalse_WhenCodeHasNotBeenUsed()
    {
        var repository = Substitute.For<IDiscountCodeRepository>();
        var discountCodeService = new DiscountCodeService(repository);

        repository.CheckDiscountCode("CUST-001", DiscountCode.SUMMER20).Returns(false);

        Assert.False(discountCodeService.CheckDiscountCode("CUST-001", DiscountCode.SUMMER20));
    }

    [Fact]
    public void P2_MarkAsUsed_DelegatesToRepository()
    {
        var repository = Substitute.For<IDiscountCodeRepository>();
        var discountCodeService = new DiscountCodeService(repository);

        discountCodeService.MarkAsUsed("CUST-001", DiscountCode.SUMMER20);

        repository.Received(1).MarkAsUsed("CUST-001", DiscountCode.SUMMER20);
    }
}
