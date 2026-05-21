package infrastructure;

/**
 * Database connection that requires a running database server.
 *
 * This class simulates a JDBC/DSL context connection.
 * It will fail to connect in test environments.
 */
public class DatabaseConnection {

    private DatabaseConnection() {
        // Private constructor - use connect()
    }

    public static DatabaseConnection connect() {
        // Simulates connection to database that isn't available in tests
        throw new IllegalStateException(
            "Cannot connect to database: jdbc:postgresql://localhost:5432/orders - " +
            "Connection refused. Did you forget to mock DiscountCodeService?"
        );
    }

    public boolean executeQuery(String sql, Object... params) {
        // Would execute SQL query
        return false;
    }

    public void executeUpdate(String sql, Object... params) {
        // Would execute SQL update
    }
}

