package miniprojet.Serveurs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServeurDictionnaire {
    private static final int PORT = 5001;
    private List<String> dictionnaire;
    private Random random;

    public ServeurDictionnaire() {
        this.dictionnaire = new ArrayList<>();
        this.random = new Random();
        dictionnaire.add("LETTRE");
        dictionnaire.add("MAISON");
        dictionnaire.add("JARDIN");
        dictionnaire.add("SOLEIL");
        dictionnaire.add("PIERRE");
        dictionnaire.add("CHEVAL");
        dictionnaire.add("LUMIERE");
    }

    public void demarrer() {
        System.out.println("Serveur Dictionnaire démarré sur le port " + PORT);
        try {
            ServeurTCP serveur = new ServeurTCP(PORT);
            while (true) {
                serveur.attendreClient();
                String mot = dictionnaire.get(random.nextInt(dictionnaire.size()));
                serveur.envoyer(mot);
                System.out.println("Mot envoyé : " + mot);
                serveur.fermerClient();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ServeurDictionnaire().demarrer();
    }
}
