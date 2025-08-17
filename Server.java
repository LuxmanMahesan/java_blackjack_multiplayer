import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java Server <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("[SERVEUR] Écoute sur le port " + port + " ...");

        GameTable table = new GameTable();

        Thread accepter = new Thread(() -> {
            while (true) {
                try {
                    Socket s = serverSocket.accept();
                    ClientHandler ch = new ClientHandler(s, table);
                    ch.start();
                    table.ajouterClient(ch);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        accepter.setDaemon(true);
        accepter.start();

        while (true) {
            try {
                while (table.nombreJoueursActifs() == 0) {
                    Thread.sleep(500);
                }
                table.demarrerNouvelleManche();
                for (ClientHandler ch : table.clientsSnapshot()) {
                    if (!table.estActif(ch)) continue;
                    table.tourDuJoueur(ch);
                }
                table.tourDuCroupier();
                table.afficherResultats();
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
