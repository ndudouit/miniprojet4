package miniprojet;

import miniprojet.Serveurs.ClientTCP;
import miniprojet.Serveurs.Middleware;
import miniprojet.Serveurs.ServeurJeu;
import miniprojet.Serveurs.ServeurDictionnaire;
import miniprojet.Serveurs.ServeurNoms;
import miniprojet.Serveurs.ServeurScores;

public class TestGlobal {

    public static void main(String[] args) {
        System.out.println("=== LANCEMENT DU TEST GLOBAL AUTOMATISÉ ===");

        
        lancerService("Dictionnaire", () -> ServeurDictionnaire.main(null));
        lancerService("Noms", () -> ServeurNoms.main(null));
        lancerService("Scores", () -> ServeurScores.main(null));
        
        pause(500); 

        
        lancerService("Middleware", () -> Middleware.main(null));

        pause(500);

        
        lancerService("ServeurJeu", () -> ServeurJeu.main(new String[]{"5400"}));

        
        System.out.println(">> Attente de l'initialisation des serveurs (2s)...");
        pause(2000);

        
        System.out.println("\n=== DÉMARRAGE DU CLIENT ROBOT ===");
        lancerClientRobot();
    }

    private static void lancerService(String nom, Runnable service) {
        new Thread(() -> {
            try {
                service.run();
            } catch (Exception e) {
                System.err.println("Erreur dans " + nom + ": " + e.getMessage());
            }
        }, "Thread-" + nom).start();
        System.out.println("[OK] " + nom + " lancé.");
    }

    private static void pause(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }

    
    private static void lancerClientRobot() {
        try {
            
            ClientTCP mw = new ClientTCP("127.0.0.1", 5100);
            mw.send("fr");
            String reponseMw = (String) mw.receive();
            mw.close();

            if (reponseMw.startsWith("ERROR")) {
                System.err.println("TEST ÉCHOUÉ : Pas de serveur disponible.");
                return;
            }

            String[] parts = reponseMw.split(":");
            System.out.println(">> Robot redirigé vers : " + parts[0] + ":" + parts[1]);

           
            ClientTCP jeu = new ClientTCP(parts[0], Integer.parseInt(parts[1]));
            jeu.send("START");
            String indice = (String) jeu.receive();
            System.out.println(">> Indice reçu : " + indice);
            int tailleMot = indice.length();

            
            String[] tentatives = {
                genererMot("A", tailleMot), // Essai 1
                genererMot("B", tailleMot), // Essai 2
                genererMot("C", tailleMot), // Essai 3
                genererMot("D", tailleMot), // Essai 4
                genererMot("E", tailleMot), // Essai 5
                genererMot("F", tailleMot)  // Essai 6
            };

            for (int i = 0; i < 6; i++) {
                String mot = tentatives[i];
                System.out.println(">> Robot essaie : " + mot);
                jeu.send(mot);

                String resultat = (String) jeu.receive();
                System.out.println("<< Réponse Serveur : " + resultat);

                if (resultat.startsWith("WIN")) {
                    System.out.println("=== VICTOIRE DU ROBOT ! ===");
                    break;
                } else if (resultat.startsWith("LOSE")) {
                    System.out.println("=== DÉFAITE DU ROBOT ===");
                    break;
                }
            }
            jeu.close();
            System.out.println("=== TEST TERMINÉ AVEC SUCCÈS ===");
            
            // Note : Le programme ne s'arrêtera pas tout seul car les serveurs tournent encore.
            // Il faut stopper manuellement l'exécution dans l'IDE.
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Génère un mot type "AAAAAA" de la bonne longueur
    private static String genererMot(String lettre, int longueur) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < longueur; i++) sb.append(lettre);
        return sb.toString();
    }
}
