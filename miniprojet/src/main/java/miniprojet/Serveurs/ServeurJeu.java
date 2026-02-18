package miniprojet.Serveurs;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurJeu {
    
    public void demarrer(int portJeu) {
        try {
            ServerSocket serverSocket = new ServerSocket(portJeu);
            System.out.println("ServeurJeu prêt sur le port " + portJeu + ".");
            System.out.println("En attente de la connexion des 2 joueurs...");
            Socket s1 = serverSocket.accept();
            ObjectOutputStream out1 = new ObjectOutputStream(s1.getOutputStream());
            ObjectInputStream in1 = new ObjectInputStream(s1.getInputStream());
            out1.writeObject("X"); 
            System.out.println("Joueur 1 (X) connecté.");
            Socket s2 = serverSocket.accept();
            ObjectOutputStream out2 = new ObjectOutputStream(s2.getOutputStream());
            ObjectInputStream in2 = new ObjectInputStream(s2.getInputStream());
            out2.writeObject("O");
            System.out.println("Joueur 2 (O) connecté. La partie commence !");
            Morpion partie = new Morpion(); 
            while (true) { 
                out1.writeObject(partie); 
                out1.reset();
                out2.writeObject(partie); 
                out2.reset(); 
                if (partie.estTermine()) {
                    break;
                }
                int positionJouee;
                if (partie.getJoueurActuel().equals("X")) {
                    positionJouee = (Integer) in1.readObject();
                } else {
                    positionJouee = (Integer) in2.readObject();
                }
                partie.jouerCoup(positionJouee); 
            }
            s1.close(); 
            s2.close(); 
            serverSocket.close();
            System.out.println("Partie terminée, connexions fermées.");

        } catch (Exception e) {
            System.err.println("Erreur réseau sur le ServeurJeu : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ServeurJeu().demarrer(5200);
    }
}
