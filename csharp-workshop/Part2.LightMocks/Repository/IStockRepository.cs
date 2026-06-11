using Part2.LightMocks.Domain;

namespace Part2.LightMocks.Repository;

public interface IStockRepository
{
    Stock? FindByProductId(string productId);
    void UpdateQuantity(string productId, int newQuantity);
}
