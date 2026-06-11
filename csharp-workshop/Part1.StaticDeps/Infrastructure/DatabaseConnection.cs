namespace Part1.StaticDeps.Infrastructure;

/// <summary>
/// Database connection that requires a running database server.
/// This class simulates a connection (e.g., a DbContext or ADO.NET connection).
/// It will fail to connect in test environments.
/// </summary>
public class DatabaseConnection
{
    private DatabaseConnection()
    {
        // Private constructor - use Connect()
    }

    public static DatabaseConnection Connect()
    {
        // Simulates connection to a database that isn't available in tests
        throw new InvalidOperationException(
            "Cannot connect to database: Server=localhost;Port=5432;Database=orders - " +
            "Connection refused. Did you forget to mock DiscountCodeService?");
    }

    public bool ExecuteQuery(string sql, params object[] parameters)
    {
        // Would execute SQL query
        return false;
    }

    public void ExecuteUpdate(string sql, params object[] parameters)
    {
        // Would execute SQL update
    }
}
