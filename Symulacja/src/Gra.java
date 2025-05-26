import java.util.*;

public abstract class Gra {
    protected List<Gracz> gracze;
    protected List<Karta> talia;
    protected int aktualnyGracz;

    protected abstract void rozpocznijGre();
    protected abstract void rozdajKarty();
    protected abstract void ruch(Gracz gracz);
    protected abstract void NastepnyGracz();
    protected abstract void zbudujTalie();
}
