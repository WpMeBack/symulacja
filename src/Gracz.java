import java.util.*;

public class Gracz {
    public List<Karta> posiadaneKarty;
    public int id;

    public Gracz() {
        this.posiadaneKarty = new ArrayList<>();
    }

    public int ileMaKart() {
        return posiadaneKarty.size();
    }
}
