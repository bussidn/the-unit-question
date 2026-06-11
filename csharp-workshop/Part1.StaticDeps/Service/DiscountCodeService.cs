using Part1.StaticDeps.Domain;
using Part1.StaticDeps.Infrastructure;
using Part1.StaticDeps.Repository;

namespace Part1.StaticDeps.Service;

/// <summary>
/// Discount code validation service.
/// Instantiates its own repository directly — consistent with Part 1 architecture.
/// </summary>
public class DiscountCodeService
{
    private readonly IDiscountCodeRepository _repository;

    public DiscountCodeService()
    {
        // Instantiates repository directly - requires real database connection
        _repository = new DatabaseDiscountCodeRepository();
    }

    /// <summary>
    /// Checks a discount code for a given customer.
    /// </summary>
    /// <returns>true if the code has already been used by this customer</returns>
    public bool CheckDiscountCode(string customerId, DiscountCode discountCode) =>
        _repository.CheckDiscountCode(customerId, discountCode);

    public void MarkAsUsed(string customerId, DiscountCode discountCode) =>
        _repository.MarkAsUsed(customerId, discountCode);
}
