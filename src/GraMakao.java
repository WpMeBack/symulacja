import java.util.*;
import java.util.stream.Collectors;

public class GraMakao extends Gra {
    private Stack<Karta> stos;
    private Stack<Karta> dobor;
    private int ileDobrac = 0;
    private int ilePauzowac = 0;
    private int[] ilePauza;
    private Kolor jakiKolor = null;
    private Figura jakaFigura = null;
    private Gracz graczZadajacyKolor = null;
    private Gracz graczZadajacyFigure = null;
    private Random rand = new Random();
    private boolean dobieranieError = false;

    public GraMakao(int liczbaGraczy) {
        gracze = new ArrayList<>();
        for (int i = 0; i < liczbaGraczy; i++) {
            Gracz g = new Gracz();
            g.id = i;
            gracze.add(g);
        }
        aktualnyGracz = 0;
        stos = new Stack<>();
        dobor = new Stack<>();
        ilePauza = new int[liczbaGraczy];
    }

    @Override
    public void rozpocznijGre() {
        zbudujTalie();
        dobor.addAll(talia);
        Collections.shuffle(dobor);
        rozdajKarty();

        Karta pierwsza;
        do {
            pierwsza = dobor.pop();
            if (pierwsza.figura == Figura.JOKER) {
                Stack<Karta> temp = new Stack<>();
                while (!dobor.isEmpty()) {
                    temp.push(dobor.pop());
                }
                dobor.push(pierwsza);
                while (!temp.isEmpty()) {
                    dobor.push(temp.pop());
                }
            }
        } while (pierwsza.figura == Figura.JOKER);

        stos.push(pierwsza);

        int tura = 0;
        while (true) {
            System.out.println("\nTURA " + (tura++) + ": Gracz " + aktualnyGracz + " ma: " + gracze.get(aktualnyGracz).posiadaneKarty);
            System.out.println("Na stosie: " + stos.peek());
            //System.out.println("Na stosie: " + stos.peek() + ", ileDobrac=" + ileDobrac + ", ilePauzowac=" + ilePauzowac + ", jakiKolor=" + jakiKolor + ", jakaFigura=" + jakaFigura);

            ruch(gracze.get(aktualnyGracz));

            if(dobieranieError) {
                break;
            }

            if (gracze.get(aktualnyGracz).posiadaneKarty.isEmpty()) {
                System.out.println("Gracz " + aktualnyGracz + " wygrał!");
                break;
            }

            NastepnyGracz();

            if (tura > 1000) {
                System.out.println("Gra przerwana (limit tur).");
                break;
            }
        }
    }

    @Override
    protected void rozdajKarty() {
        for (int i = 0; i < 5; i++) {
            for (Gracz g : gracze) {
                g.posiadaneKarty.add(dobor.pop());
            }
        }
    }

    @Override
    protected void ruch(Gracz gracz) {
        if (gracz == graczZadajacyKolor) {
            jakiKolor = null;
            graczZadajacyKolor = null;
        }
        if (gracz == graczZadajacyFigure) {
            jakaFigura = null;
            graczZadajacyFigure = null;
        }
        if (ilePauza[gracz.id] > 0) {
            System.out.println("Gracz " + gracz.id + " pauzuje turę.");
            ilePauza[gracz.id]--;
            return;
        }

        List<Karta> doZagrania = new ArrayList<>();
        for (Karta k : gracz.posiadaneKarty) {
            if (czyMoznaZagrac(k)) {
                doZagrania.add(k);
            }
        }

        if (!doZagrania.isEmpty()) {
            Karta karta = doZagrania.get(rand.nextInt(doZagrania.size()));
            zagrajKarte(gracz, karta);
        } else {
            if (dobor.isEmpty()) {
                przetasuj();
                if(dobieranieError) return;
            }
            if(ileDobrac > 0) {
                if(ileDobrac > dobor.size()) {
                    przetasuj();
                    if(dobieranieError) return;
                }
                Karta pierwsza = dobor.pop();
                System.out.println("Gracz " + gracz.id + " dobiera kartę: " + pierwsza);
                if(czyMoznaZagrac(pierwsza)) {
                    gracz.posiadaneKarty.add(pierwsza);
                    zagrajKarte(gracz, pierwsza);
                    System.out.println("Gracz unika dobrania kart.");
                } else {
                    ileDobrac--;
                    for (int i = 0; i < ileDobrac && !dobor.isEmpty(); i++) {
                        gracz.posiadaneKarty.add(dobor.pop());
                    }
                    System.out.println("Gracz " + gracz.id + " dobiera " + ileDobrac + " kart.");
                    ileDobrac = 0;
                }
            } else if (ilePauzowac > 0) {
                System.out.println("Gracz " + gracz.id + " pauzuje turę.");
                ilePauza[aktualnyGracz] += ilePauzowac - 1;
                ilePauzowac = 0;
            } else {
                Karta dobrana = dobor.pop();
                System.out.println("Gracz " + gracz.id + " dobiera kartę: " + dobrana);
                gracz.posiadaneKarty.add(dobrana);
                if (czyMoznaZagrac(dobrana)) {
                    zagrajKarte(gracz, dobrana);
                }
            }
        }
    }

    private boolean czyMoznaZagrac(Karta karta) {
        Karta wierzch = stos.peek();

        if (ileDobrac > 0 && karta.figura != Figura.JOKER) {
            if (wierzch.figura == Figura.DWA) {
                return (karta.figura == Figura.DWA)
                        || (karta.figura == Figura.TRZY && karta.kolor == wierzch.kolor)
                        || (karta.figura == Figura.KROL &&
                        (karta.kolor == Kolor.PIK || karta.kolor == Kolor.KIER) &&
                        karta.kolor == wierzch.kolor);
            }

            if (wierzch.figura == Figura.TRZY) {
                return (karta.figura == Figura.TRZY)
                        || (karta.figura == Figura.DWA && karta.kolor == wierzch.kolor)
                        || (karta.figura == Figura.KROL &&
                        (karta.kolor == Kolor.PIK || karta.kolor == Kolor.KIER) &&
                        karta.kolor == wierzch.kolor);
            }

            if ((wierzch.figura == Figura.KROL && wierzch.kolor == Kolor.PIK) || (wierzch.figura == Figura.KROL && wierzch.kolor == Kolor.KIER)) {
                return (karta.figura == Figura.DWA && karta.kolor == wierzch.kolor)
                        || (karta.figura == Figura.TRZY && karta.kolor == wierzch.kolor);
            }

            return false;
        }

        if(ilePauzowac > 0) {
            return karta.figura == Figura.CZTERY;
        }

        if (jakiKolor != null) {
            if(wierzch.figura == Figura.AS) {
                return karta.kolor == jakiKolor || karta.figura == Figura.AS;
            } else {
                return karta.kolor == jakiKolor;
            }
        }

        if (jakaFigura != null) {
            if(wierzch.figura == Figura.WALET) {
                return karta.figura == jakaFigura || karta.figura == Figura.WALET;
            } else {
                return karta.figura == jakaFigura;
            }
        }

        return karta.kolor == wierzch.kolor || karta.figura == wierzch.figura || karta.figura == Figura.JOKER;
    }


    private void zagrajKarte(Gracz gracz, Karta karta) {
        if (karta.figura == Figura.JOKER && karta.kolor == Kolor.JOKER) {
            zamienJokera(gracz, karta);
        }

        gracz.posiadaneKarty.remove(karta);
        stos.push(karta);
        System.out.println("Gracz " + gracz.id + " zagrywa: " + karta);

        switch (karta.figura) {
            case DWA -> ileDobrac += 2;
            case TRZY -> ileDobrac += 3;
            case KROL -> {
                if (karta.kolor == Kolor.PIK) ileDobrac += 5;
                if (karta.kolor == Kolor.KIER) ileDobrac += 5;
            }
            case CZTERY -> ilePauzowac += 1;
            case AS -> {
                // Lista dozwolonych kolorów (jeśli chcesz pominąć np. JOKER itp., tutaj możesz to kontrolować)
                List<Kolor> dozwoloneKolory = List.of(Kolor.TREFL, Kolor.KARO, Kolor.KIER, Kolor.PIK);

                // Zbierz kolory, które gracz faktycznie ma na ręce i które są dozwolone
                Set<Kolor> koloryGracza = gracz.posiadaneKarty.stream()
                        .map(k -> k.kolor)
                        .filter(dozwoloneKolory::contains)
                        .collect(Collectors.toSet());

                // Zamień na listę, aby losowo wybrać jeden
                List<Kolor> wspolneKolory = new ArrayList<>(koloryGracza);
                jakiKolor = wspolneKolory.isEmpty() ? null : wspolneKolory.get(rand.nextInt(wspolneKolory.size()));

                graczZadajacyKolor = gracz;

                System.out.println("Gracz " + gracz.id + " żąda " + (jakiKolor == null ? "niczego" : "koloru: " + jakiKolor));
            }
            case WALET -> {
                List<Figura> dozwoloneFigury = List.of(
                        Figura.DWA, Figura.TRZY, Figura.CZTERY, Figura.PIEC,
                        Figura.SZESC, Figura.SIEDEM, Figura.OSIEM,
                        Figura.DZIEWIEC, Figura.DZIESIEC, Figura.DAMA
                );
                Set<Figura> figuryGracza = gracz.posiadaneKarty.stream()
                        .map(k -> k.figura)
                        .filter(dozwoloneFigury::contains)
                        .collect(Collectors.toSet());

                List<Figura> wspolneFigury = new ArrayList<>(figuryGracza);
                jakaFigura = wspolneFigury.isEmpty() ? null : wspolneFigury.get(rand.nextInt(wspolneFigury.size()));
                graczZadajacyFigure = gracz;

                System.out.println("Gracz " + gracz.id + " żąda " + (jakaFigura == null ? "niczego" : "figury: " + jakaFigura));
            }
        }
    }

    private void zamienJokera(Gracz gracz, Karta joker) {
        Figura[] figury = Figura.values();
        Kolor[] kolory = Kolor.values();

        List<Karta> mozliweKarty = new ArrayList<>();

        for (Figura f : figury) {
            if (f == Figura.JOKER) continue;
            for (Kolor k : kolory) {
                if (k == Kolor.JOKER) continue;
                Karta testowa = new Karta(f, k, false);
                if (czyMoznaZagrac(testowa)) {
                    mozliweKarty.add(testowa);
                }
            }
        }

        Karta wybrana = mozliweKarty.get(rand.nextInt(mozliweKarty.size()));
        joker.figura = wybrana.figura;
        joker.kolor = wybrana.kolor;
        joker.joker = true;
        System.out.println("JOKER zamieniony na: " + joker);
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
        talia.add(new Karta(Figura.JOKER, Kolor.JOKER, true));
        talia.add(new Karta(Figura.JOKER, Kolor.JOKER, true));
    }

    private void przetasuj() {
        if (stos.size() <= 1) {
            System.out.println("Brak kart do przetasowania.");
            dobieranieError = true;
            return;
        }
        if (ileDobrac >= stos.size()) {
            System.out.println("Za malo kart do dobrania: " + ileDobrac + ", stos: " + stos.size());
            dobieranieError = true;
            return;
        }
        Karta wierzch = stos.pop();
        List<Karta> doPrzetasowania = new ArrayList<>();
        while (!stos.isEmpty()) {
            Karta karta = stos.pop();
            if (karta.joker) {
                karta.figura = Figura.JOKER;
                karta.kolor = Kolor.JOKER;
            }
            doPrzetasowania.add(karta);
        }
        Collections.shuffle(doPrzetasowania);
        dobor.addAll(doPrzetasowania);
        stos.push(wierzch);
        System.out.println("Przetasowano karty. Nowa talia zawiera " + dobor.size() + " kart.");
    }

}
