import java.util.*;

class Hand {
    private final List<Card> cartes = new ArrayList<>();

    public void ajouter(Card c) { cartes.add(c); }
    public List<Card> getCartes() { return Collections.unmodifiableList(cartes); }
    public void clear() { cartes.clear(); }

    public int meilleureValeur() {
        int total = 0; int nbAs = 0;
        for (Card c : cartes) {
            total += c.valeur.points;
            if (c.valeur == Card.Valeur.AS) nbAs++;
        }
        while (total > 21 && nbAs > 0) {
            total -= 10;
            nbAs--;
        }
        return total;
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cartes.size(); i++) {
            sb.append(cartes.get(i));
            if (i < cartes.size() - 1) sb.append(", ");
        }
        sb.append(" (" + meilleureValeur() + ")");
        return sb.toString();
    }
}
