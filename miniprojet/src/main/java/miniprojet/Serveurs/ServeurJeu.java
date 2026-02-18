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
        System.out.println("Nouvelle partie démarrée");

        Object signal = jeu.recevoir(); // START
        System.out.println("Signal reçu: " + signal);

        Morpion morpionServeur = new Morpion();

        jeu.envoyer(morpionServeur.affichage());

        while (!morpionServeur.estTermine()) {
            Object coupObj = jeu.recevoir();

            if (coupObj == null) {
                System.out.println("Client déconnecté");
                break;
            }

            String coup = coupObj.toString();
            System.out.println("Coup reçu: " + coup);

            try {
                int position = Integer.parseInt(coup);
                boolean coupValide = morpionServeur.jouerCoup(position);

                if (coupValide) {
                    jeu.envoyer("OK");
                    jeu.envoyer(morpionServeur.affichage());
                    jeu.envoyer(morpionServeur.getEtatJeu());

                    if (morpionServeur.estTermine()) {
                        jeu.envoyer("FIN");
                        if (morpionServeur.getGagnant().equals("MATCH NUL")) {
                            jeu.envoyer("Match nul !");
                        } else {
                            jeu.envoyer("Le joueur " + morpionServeur.getGagnant() + " a gagné !");
                        }
                        System.out.println("Partie terminée: " + morpionServeur.getEtatJeu());
                        break;
                    }
                } else {
                    // Coup invalide
                    jeu.envoyer("INVALIDE");
                    jeu.envoyer("Coup invalide ! Veuillez réessayer.");
                }
            } catch (NumberFormatException e) {
                jeu.envoyer("INVALIDE");
                jeu.envoyer("Entrée invalide ! Veuillez entrer un nombre entre 1 et 9.");
            }
        }

        System.out.println("Partie terminée");
    }
}
