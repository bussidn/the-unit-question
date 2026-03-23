package infrastructure;

public class EmailGateway {
    public static void send(String to, String subject, String body) {
        // External email service - fails if not properly configured!
        throw new RuntimeException("Email server not configured! Cannot send to: " + to);
    }
}

