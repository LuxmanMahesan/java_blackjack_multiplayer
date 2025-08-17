import java.io.*;
import java.net.*;

class Client {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java Client <hôte> <port>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(host, port)) {
            System.out.println("[CLIENT] Connecté à " + host + ":" + port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Entrez votre pseudo: ");
            String pseudo = clavier.readLine();
            if (pseudo == null || pseudo.isBlank()) pseudo = "Joueur" + new java.util.Random().nextInt(1000);
            out.println(pseudo);

            Thread lecteur = new Thread(() -> {
                String line;
                try {
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("[CLIENT] Déconnecté.");
                }
            });
            lecteur.setDaemon(true);
            lecteur.start();

            while (true) {
                String saisie = clavier.readLine();
                if (saisie == null) break;
                out.println(saisie);
            }
        }
    }
}