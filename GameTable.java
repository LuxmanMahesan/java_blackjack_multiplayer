import java.util.*;

class GameTable {
    private final Set<ClientHandler> clients = new LinkedHashSet<>();
    private final Deck deck = new Deck(6);
    private final Hand mainCroupier = new Hand();
    private final Map<ClientHandler, String> dernierMessage = new HashMap<>();

    public synchronized void ajouterClient(ClientHandler ch) { clients.add(ch); }
    public synchronized void retirerClient(ClientHandler ch) { clients.remove(ch); }
    public synchronized boolean estActif(ClientHandler ch) { return ch.estActif() && clients.contains(ch); }
    public synchronized int nombreJoueursActifs() { return (int) clients.stream().filter(ClientHandler::estActif).count(); }
    public synchronized List<ClientHandler> clientsSnapshot() { return new ArrayList<>(clients); }
    public synchronized void broadcast(String msg) { for (ClientHandler ch : clients) ch.envoyer(msg); }

    public synchronized void demarrerNouvelleManche() {
        mainCroupier.clear();
        for (ClientHandler ch : clients) ch.main.clear();

        for (int i = 0; i < 2; i++) {
            for (ClientHandler ch : clients) if (ch.estActif()) ch.main.ajouter(deck.tirer());
            mainCroupier.ajouter(deck.tirer());
        }
        afficherEtatGeneral();
    }

    public void tourDuJoueur(ClientHandler ch) {
        while (true) {
            ch.envoyer("\nVotre tour, " + ch.getPseudo());
            afficherEtatGeneral();
            ch.envoyer("T pour tirer, R pour rester : ");
            String rep = attendreReponse(ch, Set.of("T", "R"));
            if (rep == null) return;
            if (rep.equals("T")) {
                Card c = deck.tirer();
                ch.main.ajouter(c);
                broadcast(ch.getPseudo() + " tire " + c + " → " + ch.main.meilleureValeur());
                if (ch.main.meilleureValeur() > 21) {
                    broadcast(ch.getPseudo() + " est cramé !");
                    break;
                }
            } else {
                broadcast(ch.getPseudo() + " reste à " + ch.main.meilleureValeur());
                break;
            }
        }
    }

    public void tourDuCroupier() {
        broadcast("\nTour du robot");
        while (mainCroupier.meilleureValeur() < 17) {
            Card c = deck.tirer();
            mainCroupier.ajouter(c);
            broadcast("Robot tire " + c + " → " + mainCroupier.meilleureValeur());
        }
        broadcast("Robot reste à " + mainCroupier.meilleureValeur());
    }

    public void afficherResultats() {
        afficherEtatGeneral();
        broadcast("=== Manche terminée ===\n");
    }

    private void afficherEtatGeneral() {
        broadcast("\n-----------------");
        broadcast("Robot: " + mainCroupier);
        for (ClientHandler ch : clients) {
            if (ch.estActif()) broadcast(ch.getPseudo() + ": " + ch.main);
        }
        broadcast("-----------------");
    }

    public synchronized void receptionMessage(ClientHandler ch, String msg) {
        dernierMessage.put(ch, msg.toUpperCase());
        notifyAll();
    }

    private synchronized String attendreReponse(ClientHandler ch, Set<String> options) {
        try {
            while (true) {
                String msg = dernierMessage.remove(ch);
                if (msg != null && options.contains(msg)) return msg;
                wait(1000);
                if (!ch.estActif()) return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
