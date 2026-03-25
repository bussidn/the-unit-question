package infrastructure;

/**
 * SMTP connection for sending emails.
 * 
 * This class requires a real mail server to be configured.
 * It will fail to connect in test environments.
 */
public class SmtpConnection {
    
    private SmtpConnection() {
        // Private constructor - use connect()
    }

    public static SmtpConnection connect() {
        // Simulates connection to mail server that isn't available in tests
        throw new IllegalStateException(
            "Cannot connect to SMTP server: mail.example.com:587 - " +
            "Connection refused. Did you forget to mock EmailService?"
        );
    }

    public void send(String to, String subject, String body) {
        // Would send email via SMTP
        System.out.println("Sending email to " + to + ": " + subject);
    }
}

