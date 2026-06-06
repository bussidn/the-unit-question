using Part4.Behavior.Domain;

namespace Part4.Behavior.Repository;

public interface IDiscountCodeRepository
{
    bool CheckDiscountCode(string customerId, DiscountCode discountCode);
    void MarkAsUsed(string customerId, DiscountCode discountCode);
}
