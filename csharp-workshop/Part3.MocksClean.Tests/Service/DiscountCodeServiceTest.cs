using NSubstitute;
using Part3.MocksClean.Domain;
using Part3.MocksClean.Repository;
using Part3.MocksClean.Service;
using Xunit;

namespace Part3.MocksClean.Tests.Service;

public class DiscountCodeServiceTest
{
    private readonly IDiscountCodeRepository _discountCodeRepository = Substitute.For<IDiscountCodeRepository>();
    private readonly DiscountCodeService _discountCodeService;

    public DiscountCodeServiceTest()
    {
        _discountCodeService = new DiscountCodeService(_discountCodeRepository);
    }

    [Fact]
    public void P3_CheckDiscountCode_ReturnsTrue_WhenCodeHasAlreadyBeenUsed()
    {
        _discountCodeRepository.CheckDiscountCode("CUST-001", DiscountCode.SUMMER20).Returns(true);

        Assert.True(_discountCodeService.CheckDiscountCode("CUST-001", DiscountCode.SUMMER20));
    }

    [Fact]
    public void P3_CheckDiscountCode_ReturnsFalse_WhenCodeHasNotBeenUsed()
    {
        _discountCodeRepository.CheckDiscountCode("CUST-001", DiscountCode.SUMMER20).Returns(false);

        Assert.False(_discountCodeService.CheckDiscountCode("CUST-001", DiscountCode.SUMMER20));
    }

    [Fact]
    public void P3_MarkAsUsed_DelegatesToRepository()
    {
        _discountCodeService.MarkAsUsed("CUST-001", DiscountCode.SUMMER20);

        _discountCodeRepository.Received(1).MarkAsUsed("CUST-001", DiscountCode.SUMMER20);
    }
}
