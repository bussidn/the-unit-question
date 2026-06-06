using Part3.MocksClean.Domain;

namespace Part3.MocksClean.Repository;

public interface IStockRepository
{
    Stock? FindByProductId(string productId);
    void UpdateQuantity(string productId, int newQuantity);
}
