public class Symulacja {
    public static void main(String[] args) {
        Gra gram = new GraMakao(3);
        gram.rozpocznijGre();

        Gra graw = new GraWojna(3);
        graw.rozpocznijGre();
    }
}
