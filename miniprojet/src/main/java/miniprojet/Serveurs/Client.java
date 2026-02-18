package miniprojet.Serveurs;

import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        String ip = "127.0.0.1";
        int port = 5200;

        if (args.length >= 1) ip = args[0];
        if (args.length >= 2) port = Integer.parseInt(args[1]);

        try {
            Agent agent = new Agent(ip, port);
            Scanner scanner = new Scanner(System.in);

            System.out.println("Connecté au serveur !");
            System.out.println("Votre symbole : " + agent.monSymbole);

            while (true) {

                MorpionBase etatJeu = agent.recevoirEtatJeu();

                System.out.println(etatJeu.affichage());
                System.out.println(etatJeu.getEtatJeu());

                if (etatJeu.estTermine()) {
                    System.out.println("Partie terminée !");
                    break;
                }

                if (etatJeu.getJoueurActuel().equals(agent.monSymbole)) {

                    int position;

                    while (true) {
                        try {
                            System.out.print("Choisissez une position (1-9) : ");
                            position = Integer.parseInt(scanner.nextLine());

                            if (position < 1 || position > 9) {
                                System.out.println("Position invalide !");
                                continue;
                            }

                            int ligne = (position - 1) / 3;
                            int colonne = (position - 1) % 3;

                            String valeurCase = etatJeu.grille[ligne][colonne];

                            if (valeurCase.equals("X") || valeurCase.equals("O")) {
                                System.out.println("⚠ Cette position est déjà prise !");
                                continue;
                            }

                            break;

                        } catch (Exception e) {
                            System.out.println("Entrée invalide !");
                        }
                    }

                    agent.envoyerCoup(position);
                } else {
                    System.out.println("En attente du joueur adverse...");
                }
            }

            agent.deconnecter();
            scanner.close();

        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
        }
    }
}
