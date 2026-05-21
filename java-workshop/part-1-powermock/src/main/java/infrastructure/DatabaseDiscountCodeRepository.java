package infrastructure;

import domain.DiscountCode;
import repository.DiscountCodeRepository;

/**
 * Repository implementation that requires a real database connection.
 *
 * This class connects to the database at construction time.
 * It will fail in test environments where no database is available.
 */
public class DatabaseDiscountCodeRepository implements DiscountCodeRepository {

    private final DatabaseConnection connection;

    public DatabaseDiscountCodeRepository() {
        // Requires real database infrastructure - will fail in tests without mocking
        this.connection = DatabaseConnection.connect();
    }

    @Override
    public boolean checkDiscountCode(String customerId, DiscountCode discountCode) {
        return connection.executeQuery(
            "SELECT used FROM discount_codes WHERE customer_id = ? AND code = ?",
            customerId, discountCode.name()
        );
    }

    @Override
    public void markAsUsed(String customerId, DiscountCode discountCode) {
        connection.executeUpdate(
            "INSERT INTO discount_codes (customer_id, code, used) VALUES (?, ?, true)",
            customerId, discountCode.name()
        );
    }
}

