using Part3.MocksClean.Domain;

namespace Part3.MocksClean.Repository;

public interface IDiscountCodeRepository
{
    bool CheckDiscountCode(string customerId, DiscountCode discountCode);
    void MarkAsUsed(string customerId, DiscountCode discountCode);
}
