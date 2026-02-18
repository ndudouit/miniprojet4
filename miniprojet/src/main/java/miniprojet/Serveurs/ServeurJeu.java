package miniprojet.Serveurs;

public class ServeurJeu {
    
    public static void main(String[] args) {
        int port = 5400;
        if (args.length > 0) port = Integer.parseInt(args[0]);
        new ServeurJeu().lancer(port);
    }

    public void lancer(int port) {
        try {
            ClientTCP clientM = new ClientTCP("127.0.0.1", 5100);
            clientM.send("REGISTER:" + port);
            String resp = (String) clientM.receive();
            System.out.println("Réponse Middleware : " + resp);
            clientM.close();

            ServeurTCP serveur = new ServeurTCP(port);
            System.out.println("Serveur de Jeu prêt sur le port " + port);

            while (true) {
                serveur.attendreClient();
                gererPartie(serveur);
                serveur.fermerClient();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gererPartie(ServeurTCP jeu) throws Exception {
        Object signal = jeu.recevoir(); // START

        ClientTCP clientDico = new ClientTCP("127.0.0.1", 5001);
        String motSecret = (String) clientDico.receive();
        clientDico.close();
        
        motSecret = motSecret.toUpperCase();
        int taille = motSecret.length();
        char premiereLettre = motSecret.charAt(0);

        StringBuilder indiceDepart = new StringBuilder();
        indiceDepart.append(premiereLettre);
        for (int i = 1; i < taille; i++) indiceDepart.append("_");
        
        jeu.envoyer(indiceDepart.toString());

        int scoreMax = 600;
        int scoreFinalCalcul = 0; // Variable pour stocker le score final (victoire ou défaite)
        boolean gagne = false;

        for (int essai = 1; essai <= 6; essai++) {
            String tentative = (String) jeu.recevoir();
            if (tentative == null) break; 
            tentative = tentative.toUpperCase();

            if (tentative.length() != taille) {
                jeu.envoyer("TAILLE_INCORRECTE:Le mot doit faire " + taille + " lettres.");
                essai--;
                continue;
            }

            char[] resultat = new char[taille];
            boolean[] lettreMotSecretUtilisee = new boolean[taille];
            boolean[] lettreTentativeTraitee = new boolean[taille];

            // 1. Bien placé (Majuscule)
            for (int i = 0; i < taille; i++) {
                if (tentative.charAt(i) == motSecret.charAt(i)) {
                    resultat[i] = tentative.charAt(i);
                    lettreMotSecretUtilisee[i] = true;
                    lettreTentativeTraitee[i] = true;
                } else {
                    resultat[i] = '_';
                }
            }

            // 2. Mal placé (Minuscule)
            for (int i = 0; i < taille; i++) {
                if (lettreTentativeTraitee[i]) continue;

                char c = tentative.charAt(i);
                boolean trouveMalPlace = false;
                for (int j = 0; j < taille; j++) {
                    if (!lettreMotSecretUtilisee[j] && motSecret.charAt(j) == c) {
                        resultat[i] = Character.toLowerCase(c);
                        lettreMotSecretUtilisee[j] = true;
                        trouveMalPlace = true;
                        break;
                    }
                }
                if (!trouveMalPlace) {
                    resultat[i] = '_';
                }
            }

            String reponse = new String(resultat);
            
            if (motSecret.equals(tentative)) {
                scoreFinalCalcul = scoreMax; // Score total si gagné
                jeu.envoyer("WIN:" + scoreFinalCalcul);
                gagne = true;
                break;
            } else {
                scoreMax -= 100;
                if (essai == 6) {
                    // Calcul des points de consolation sur la DERNIÈRE tentative
                    int scoreDefaite = 0;
                    for (char c : resultat) {
                        if (Character.isUpperCase(c)) scoreDefaite += 10;
                        else if (Character.isLowerCase(c)) scoreDefaite += 5;
                    }
                    scoreFinalCalcul = scoreDefaite; // On sauvegarde ce score pour l'envoi au serveur
                    jeu.envoyer("LOSE:" + motSecret + ":" + scoreFinalCalcul);
                } else {
                    jeu.envoyer("CONTINUE:" + reponse);
                }
            }
        }
    
        try {
            ClientTCP clientScore = new ClientTCP("127.0.0.1", 5003);
            clientScore.send(new ServeurScores.ScoreEntry("Joueur_" + System.currentTimeMillis(), scoreFinalCalcul));
            clientScore.receive();
            clientScore.close();
        } catch(Exception e) {
            System.err.println("Erreur envoi score: " + e.getMessage());
        }
    }
}
