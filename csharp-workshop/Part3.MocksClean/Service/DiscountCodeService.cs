using Part3.MocksClean.Domain;
using Part3.MocksClean.Repository;

namespace Part3.MocksClean.Service;

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
