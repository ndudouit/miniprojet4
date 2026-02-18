package miniprojet.Serveurs;

import java.util.*;

public class ServeurNoms {
    private static final int PORT = 5002;
    private List<String> nomsDisponibles;
    private Random random;

    public ServeurNoms() {
        this.random = new Random();
        this.nomsDisponibles = new ArrayList<>(Arrays.asList(
            "Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Omega"
        ));
    }

    public void demarrer() {
        System.out.println("Serveur Noms démarré sur le port " + PORT);
        try {
            ServeurTCP serveur = new ServeurTCP(PORT);
            while (true) {
                serveur.attendreClient();
                
                String ipServeurJeu = (String) serveur.recevoir();
                
                String nomAttribue = "Inconnu";
                if (!nomsDisponibles.isEmpty()) {
                    int index = random.nextInt(nomsDisponibles.size());
                    nomAttribue = nomsDisponibles.get(index);
                    nomsDisponibles.remove(index);
                }

                serveur.envoyer(nomAttribue);
                System.out.println("Nom '" + nomAttribue + "' attribué à " + ipServeurJeu);
                
                serveur.fermerClient();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ServeurNoms().demarrer();
    }
}
