package miniprojet.Serveurs;

import java.util.*;
import java.io.Serializable;

public class ServeurScores {
    private static final int PORT = 5003;
    private List<ScoreEntry> scores;

    public static class ScoreEntry implements Serializable {
        String joueur;
        int points;
        Date date;

        public ScoreEntry(String joueur, int points) {
            this.joueur = joueur;
            this.points = points;
            this.date = new Date();
        }
        @Override
        public String toString() { return joueur + " : " + points + " pts (" + date + ")"; }
    }

    public ServeurScores() {
        this.scores = new ArrayList<>();
    }

    public void demarrer() {
        System.out.println("Serveur Scores démarré sur le port " + PORT);
        try {
            ServeurTCP serveur = new ServeurTCP(PORT);
            while (true) {
                serveur.attendreClient();
                Object req = serveur.recevoir();

                if (req instanceof String && req.equals("TOP10")) {
                    scores.sort((s1, s2) -> Integer.compare(s2.points, s1.points));
                    List<ScoreEntry> top10 = scores.subList(0, Math.min(scores.size(), 10));
                    serveur.envoyer(top10);
                } else if (req instanceof ScoreEntry) {
                    scores.add((ScoreEntry) req);
                    System.out.println("Score reçu : " + req);
                    serveur.envoyer("OK");
                }
                
                serveur.fermerClient();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ServeurScores().demarrer();
    }
}
