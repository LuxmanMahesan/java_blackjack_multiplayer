import java.io.*;
import java.net.*;

class ClientHandler extends Thread {
    private final Socket socket;
    private final GameTable table;
    private BufferedReader in;
    private PrintWriter out;
    private String pseudo = "?";
    public final Hand main = new Hand();
    private boolean actif = true;

    public ClientHandler(Socket socket, GameTable table) {
        this.socket = socket; this.table = table;
    }

    public String getPseudo() { return pseudo; }
    public boolean estActif() { return actif; }

    public void envoyer(String msg) {
        try { out.println(msg); } catch (Exception ignored) {}
    }

    @Override public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            pseudo = in.readLine();
            if (pseudo == null || pseudo.isBlank()) pseudo = "Joueur" + new java.util.Random().nextInt(1000);
            table.broadcast("[INFO] " + pseudo + " a rejoint la partie.");

            String line;
            while ((line = in.readLine()) != null) {
                table.receptionMessage(this, line.trim());
            }
        } catch (IOException e) {
        } finally {
            actif = false;
            table.retirerClient(this);
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}