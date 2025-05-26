public class Karta {
    public Figura figura;
    public Kolor kolor;
    public boolean joker;

    public Karta(Figura figura, Kolor kolor, boolean joker) {
        this.figura = figura;
        this.kolor = kolor;
        this.joker = joker;
    }

    @Override
    public String toString() {
        String symbolFigury = switch (figura) {
            case DWA -> "2";
            case TRZY -> "3";
            case CZTERY -> "4";
            case PIEC -> "5";
            case SZESC -> "6";
            case SIEDEM -> "7";
            case OSIEM -> "8";
            case DZIEWIEC -> "9";
            case DZIESIEC -> "10";
            case WALET -> "W";
            case DAMA -> "D";
            case KROL -> "K";
            case AS -> "A";
            case JOKER -> "J";
        };

        String symbolKoloru = switch (kolor) {
            case KARO -> "♦";
            case KIER -> "♥";
            case TREFL -> "♣";
            case PIK -> "♠";
            case JOKER -> "J";
        };

        String symbolJokera = joker ? "J" : "";

        if ("J".equals(symbolFigury) && "J".equals(symbolKoloru) && "J".equals(symbolJokera)) {
            return "J";
        }

        return symbolFigury + symbolKoloru + symbolJokera;
    }
}
