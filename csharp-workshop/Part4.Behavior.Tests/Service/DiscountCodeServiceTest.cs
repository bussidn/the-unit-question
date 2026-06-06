using Part4.Behavior.Domain;
using Part4.Behavior.Service;
using Part4.Behavior.Tests.Helper;
using Xunit;

namespace Part4.Behavior.Tests.Service;

public class DiscountCodeServiceTest
{
    private readonly InMemoryDiscountCodeRepository _discountCodeRepository;
    private readonly DiscountCodeService _discountCodeService;

    public DiscountCodeServiceTest()
    {
        _discountCodeRepository = new InMemoryDiscountCodeRepository();
        _discountCodeService = new DiscountCodeService(_discountCodeRepository);
    }

    [Fact]
    public void P4_CheckDiscountCode_ReturnsTrue_WhenCodeHasAlreadyBeenUsed()
    {
        _discountCodeRepository.WithUsedCode("CUST-001", DiscountCode.SUMMER20);

        Assert.True(_discountCodeService.CheckDiscountCode("CUST-001", DiscountCode.SUMMER20));
    }

    [Fact]
    public void P4_CheckDiscountCode_ReturnsFalse_WhenCodeHasNotBeenUsed()
    {
        Assert.False(_discountCodeService.CheckDiscountCode("CUST-001", DiscountCode.SUMMER20));
    }

    [Fact]
    public void P4_MarkAsUsed_MakesCodeCheckedAsUsed()
    {
        _discountCodeService.MarkAsUsed("CUST-001", DiscountCode.SUMMER20);

        Assert.True(_discountCodeRepository.IsMarkedAsUsed("CUST-001", DiscountCode.SUMMER20));
    }
}
