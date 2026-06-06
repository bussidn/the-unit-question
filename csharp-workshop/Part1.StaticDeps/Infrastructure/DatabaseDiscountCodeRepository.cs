using Part1.StaticDeps.Domain;
using Part1.StaticDeps.Repository;

namespace Part1.StaticDeps.Infrastructure;

/// <summary>
/// Repository implementation that requires a real database connection.
/// This class connects to the database at construction time.
/// It will fail in test environments where no database is available.
/// </summary>
public class DatabaseDiscountCodeRepository : IDiscountCodeRepository
{
    private readonly DatabaseConnection _connection;

    public DatabaseDiscountCodeRepository()
    {
        // Requires real database infrastructure - will fail in tests without mocking
        _connection = DatabaseConnection.Connect();
    }

    public bool CheckDiscountCode(string customerId, DiscountCode discountCode) =>
        _connection.ExecuteQuery(
            "SELECT used FROM discount_codes WHERE customer_id = @customerId AND code = @code",
            customerId, discountCode.ToString());

    public void MarkAsUsed(string customerId, DiscountCode discountCode) =>
        _connection.ExecuteUpdate(
            "INSERT INTO discount_codes (customer_id, code, used) VALUES (@customerId, @code, true)",
            customerId, discountCode.ToString());
}
