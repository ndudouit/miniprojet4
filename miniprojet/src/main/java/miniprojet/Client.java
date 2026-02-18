package miniprojet;

import miniprojet.Serveurs.ClientTCP;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            
            // Connexion Middleware
            ClientTCP mw = new ClientTCP("127.0.0.1", 5100);
            mw.send("fr");
            String reponseMw = (String) mw.receive();
            mw.close();
            
            if (reponseMw.startsWith("ERROR")) {
                System.out.println("Aucun serveur disponible.");
                return;
            }
            
            String[] parts = reponseMw.split(":");
            String ipJeu = parts[0];
            int portJeu = Integer.parseInt(parts[1]);
            
            System.out.println("Connexion au serveur : " + ipJeu + ":" + portJeu);
            
            // Connexion Jeu
            ClientTCP jeu = new ClientTCP(ipJeu, portJeu);
            jeu.send("START");
            
            String indice = (String) jeu.receive();
            System.out.println("\nMOT A DEVINER : " + indice);
            
            boolean fini = false;
            int essai = 1;
            
            while (!fini && essai <= 6) {
                System.out.print("Essai " + essai + " > ");
                String proposition = sc.nextLine();
                
                jeu.send(proposition);
                String retour = (String) jeu.receive();
                
                if (retour.startsWith("TAILLE_INCORRECTE")) {
                    System.out.println(retour.split(":")[1]);
                } else if (retour.startsWith("WIN")) {
                    System.out.println("BRAVO ! Score : " + retour.split(":")[1]);
                    fini = true;
                } else if (retour.startsWith("LOSE")) {
                    String[] data = retour.split(":");
                    System.out.println("PERDU... Mot : " + data[1] + ", Score consolation : " + data[2]);
                    fini = true;
                } else if (retour.startsWith("CONTINUE")) {
                    System.out.println("Résultat : " + retour.split(":")[1]);
                    essai++;
                }
            }
            jeu.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
