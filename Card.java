class Card {
    public enum Couleur { COEUR, CARREAU, TREFLE, PIQUE }
    public enum Valeur {
        DEUX(2), TROIS(3), QUATRE(4), CINQ(5), SIX(6), SEPT(7), HUIT(8), NEUF(9), DIX(10),
        VALET(10), DAME(10), ROI(10), AS(11);
        final int points;
        Valeur(int p) { this.points = p; }
    }

    public final Couleur couleur;
    public final Valeur valeur;

    public Card(Couleur c, Valeur v) { this.couleur = c; this.valeur = v; }

    @Override public String toString() {
        return valeur.name() + " de " + couleur.name();
    }
}
