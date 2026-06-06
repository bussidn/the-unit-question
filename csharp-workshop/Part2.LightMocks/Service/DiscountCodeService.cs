using Part2.LightMocks.Domain;
using Part2.LightMocks.Repository;

namespace Part2.LightMocks.Service;

/// <summary>
/// Discount code validation service.
/// </summary>
public class DiscountCodeService
{
    private readonly IDiscountCodeRepository _discountCodeRepository;

    public DiscountCodeService(IDiscountCodeRepository discountCodeRepository)
    {
        _discountCodeRepository = discountCodeRepository;
    }

    /// <summary>
    /// Checks a discount code for a given customer.
    /// </summary>
    /// <returns>true if the code has already been used by this customer</returns>
    public bool CheckDiscountCode(string customerId, DiscountCode discountCode)
    {
        return _discountCodeRepository.CheckDiscountCode(customerId, discountCode);
    }

    public void MarkAsUsed(string customerId, DiscountCode discountCode)
    {
        _discountCodeRepository.MarkAsUsed(customerId, discountCode);
    }
}
