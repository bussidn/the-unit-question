using Part4.Behavior.Domain;

namespace Part4.Behavior.Repository;

public interface IStockRepository
{
    Stock? FindByProductId(string productId);
    void UpdateQuantity(string productId, int newQuantity);
}
