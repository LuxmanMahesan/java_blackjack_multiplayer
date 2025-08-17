import java.util.*;

class Deck {
    private final Deque<Card> cartes = new ArrayDeque<>();
    private final Random rnd = new Random();

    public Deck(int nbJeux) {
        List<Card> temp = new ArrayList<>();
        for (int n = 0; n < nbJeux; n++) {
            for (Card.Couleur c : Card.Couleur.values()) {
                for (Card.Valeur v : Card.Valeur.values()) {
                    temp.add(new Card(c, v));
                }
            }
        }
        Collections.shuffle(temp, rnd);
        cartes.addAll(temp);
    }

    public Card tirer() {
        if (cartes.isEmpty()) {
            Deck nouveau = new Deck(6);
            cartes.addAll(nouveau.cartes);
        }
        return cartes.removeFirst();
    }
}