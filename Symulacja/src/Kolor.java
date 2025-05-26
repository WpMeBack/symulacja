public enum Kolor {
    KIER("♥"),
    KARO("♦"),
    TREFL("♣"),
    PIK("♠"),
    JOKER("J");

    private final String symbol;

    Kolor(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}