using Part2.LightMocks.Domain;

namespace Part2.LightMocks.Repository;

public interface IDiscountCodeRepository
{
    bool CheckDiscountCode(string customerId, DiscountCode discountCode);
    void MarkAsUsed(string customerId, DiscountCode discountCode);
}
