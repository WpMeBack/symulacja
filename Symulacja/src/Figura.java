public enum Figura {
    DWA("2"),
    TRZY("3"),
    CZTERY("4"),
    PIEC("5"),
    SZESC("6"),
    SIEDEM("7"),
    OSIEM("8"),
    DZIEWIEC("9"),
    DZIESIEC("10"),
    WALET("Walet"),
    DAMA("Dama"),
    KROL("Król"),
    AS("As"),
    JOKER("Joker");

    private final String displayName;

    Figura(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
