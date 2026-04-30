package domain;

public enum DiscountCode {
    SUMMER20(20),
    ONCE50(50),
    WELCOME(10);

    private final int percentage;

    DiscountCode(int percentage) {
        this.percentage = percentage;
    }

    public int getPercentage() {
        return percentage;
    }
}
