using Part4.Behavior.Domain;
using Part4.Behavior.Repository;

namespace Part4.Behavior.Tests.Helper;

public class InMemoryDiscountCodeRepository : IDiscountCodeRepository
{
    private readonly HashSet<string> _usedCodes = new();

    public InMemoryDiscountCodeRepository WithUsedCode(string customerId, DiscountCode discountCode)
    {
        _usedCodes.Add(Key(customerId, discountCode));
        return this;
    }

    public bool IsMarkedAsUsed(string customerId, DiscountCode discountCode)
    {
        return _usedCodes.Contains(Key(customerId, discountCode));
    }

    public bool CheckDiscountCode(string customerId, DiscountCode discountCode)
    {
        return _usedCodes.Contains(Key(customerId, discountCode));
    }

    public void MarkAsUsed(string customerId, DiscountCode discountCode)
    {
        _usedCodes.Add(Key(customerId, discountCode));
    }

    private static string Key(string customerId, DiscountCode discountCode) =>
        $"{customerId}:{discountCode}";
}
