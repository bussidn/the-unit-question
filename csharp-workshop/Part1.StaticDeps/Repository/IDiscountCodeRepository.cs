using Part1.StaticDeps.Domain;

namespace Part1.StaticDeps.Repository;

public interface IDiscountCodeRepository
{
    bool CheckDiscountCode(string customerId, DiscountCode discountCode);
    void MarkAsUsed(string customerId, DiscountCode discountCode);
}
