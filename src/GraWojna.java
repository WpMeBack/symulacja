import java.util.*;

public class GraWojna extends Gra {
    private List<List<Karta>> kartyPrzedSoba;
    private List<Integer> kolejnoscOdpadania = new ArrayList<>();

    public GraWojna(int liczbaGraczy) {
        gracze = new ArrayList<>();
        for (int i = 0; i < liczbaGraczy; i++) {
            gracze.add(new Gracz());
        }
        kartyPrzedSoba = new ArrayList<>();
        for (int i = 0; i < liczbaGraczy; i++) {
            kartyPrzedSoba.add(new ArrayList<>());
        }
        kolejnoscOdpadania = new ArrayList<>();
        zbudujTalie();
        Collections.shuffle(talia);
        rozdajKarty();
    }

    @Override
    public void rozpocznijGre() {
        while (gracze.stream().filter(g -> g.ileMaKart() > 0).count() > 1) {
            // Każdy gracz wykonuje ruch (zagrywa kartę)
            for (int i = 0; i < gracze.size(); i++) {
                Gracz g = gracze.get(aktualnyGracz);
                ruch(g);
                NastepnyGracz();
            }

            for (int i = 0; i < gracze.size(); i++) {
                if (gracze.get(i).ileMaKart() == 0 && !kolejnoscOdpadania.contains(i)) {
                    kolejnoscOdpadania.add(i);
                }
            }

            rozstrzygnijBitwe();
        }

        for (int i = 0; i < gracze.size(); i++) {
            if (gracze.get(i).ileMaKart() > 0 && !kolejnoscOdpadania.contains(i)) {
                kolejnoscOdpadania.add(i); // dodaj zwycięzcę jako ostatniego
                System.out.println("Gracz " + i + " wygrywa grę!");
            }
        }

// Odwróć listę, żeby zwycięzca był na pierwszym miejscu
        Collections.reverse(kolejnoscOdpadania);

        System.out.println("\n=== Miejsca graczy ===");
        for (int miejsce = 0; miejsce < kolejnoscOdpadania.size(); miejsce++) {
            int graczId = kolejnoscOdpadania.get(miejsce);
            System.out.println((miejsce + 1) + ". Gracz " + graczId);
        }
    }

    @Override
    protected void rozdajKarty() {
        int i = 0;
        for (Karta karta : talia) {
            gracze.get(i % gracze.size()).posiadaneKarty.add(karta);
            i++;
        }
    }

    @Override
    protected void ruch(Gracz gracz) {
        if (gracz.posiadaneKarty.isEmpty()) return;
        Karta zagrana = gracz.posiadaneKarty.remove(0);
        kartyPrzedSoba.get(gracze.indexOf(gracz)).add(zagrana);
        System.out.println("Gracz " + gracze.indexOf(gracz) + " zagrywa: " + zagrana);
    }

    @Override
    protected void NastepnyGracz() {
        aktualnyGracz = (aktualnyGracz + 1) % gracze.size();
    }

    @Override
    protected void zbudujTalie() {
        talia = new ArrayList<>();
        for (Kolor kolor : Kolor.values()) {
            if (kolor == Kolor.JOKER) continue;
            for (Figura figura : Figura.values()) {
                if (figura == Figura.JOKER) continue;
                talia.add(new Karta(figura, kolor, false));
            }
        }
    }

    private void rozstrzygnijBitwe() {
        int max = -1;
        List<Integer> zwyciezcy = new ArrayList<>();

        for (int i = 0; i < gracze.size(); i++) {
            List<Karta> karty = kartyPrzedSoba.get(i);
            if (!karty.isEmpty()) {
                int wartosc = karty.get(karty.size() - 1).figura.ordinal();
                if (wartosc > max) {
                    max = wartosc;
                    zwyciezcy.clear();
                    zwyciezcy.add(i);
                } else if (wartosc == max) {
                    zwyciezcy.add(i);
                }
            }
        }

        if (zwyciezcy.size() == 1) {
            // Normalna wygrana
            int zwyciezca = zwyciezcy.get(0);
            List<Karta> wygraneKarty = new ArrayList<>();

            for (List<Karta> lista : kartyPrzedSoba) {
                wygraneKarty.addAll(lista);
                lista.clear();
            }

            Collections.shuffle(wygraneKarty);

            gracze.get(zwyciezca).posiadaneKarty.addAll(wygraneKarty);
            System.out.println("Gracz " + zwyciezca + " wygrywa bitwę.");
        } else {
            System.out.println("WOJNA między graczami: " + zwyciezcy);

            // Uczestnicy wojny – próbują wyłożyć 3 zakryte + 1 odkrytą kartę
            boolean wojnaMozliwa = false;

            for (int idx : zwyciezcy) {
                Gracz gracz = gracze.get(idx);
                int ileMa = gracz.posiadaneKarty.size();
                if (ileMa > 0) wojnaMozliwa = true;

                if (gracz.posiadaneKarty.isEmpty()) return;
                Karta zakryta = gracz.posiadaneKarty.remove(0);
                kartyPrzedSoba.get(idx).add(zakryta);

                // Odkryta karta bitwy (jeśli jest)
                if (!gracz.posiadaneKarty.isEmpty()) {
                    Karta odkryta = gracz.posiadaneKarty.remove(0);
                    kartyPrzedSoba.get(idx).add(odkryta);
                    System.out.println("Gracz " + idx + " dokłada do wojny: " + odkryta);
                } else {
                    System.out.println("Gracz " + idx + " nie ma już kart do wojny.");
                }
            }

            if (wojnaMozliwa) {
                // ZACHOWAJ TYLKO karty uczestników wojny, inni gracze mają puste kartyPrzedSoba
                for (int i = 0; i < gracze.size(); i++) {
                    if (!zwyciezcy.contains(i)) {
                        kartyPrzedSoba.get(i).clear();
                    }
                }
                rozstrzygnijBitwe();
            } else {
                // Nikt nie ma kart – wszystkie karty przepadają
                System.out.println("Nikt nie miał kart na wojnę — karty przepadają.");
                for (List<Karta> lista : kartyPrzedSoba) {
                    lista.clear();
                }
            }
        }
    }

}
