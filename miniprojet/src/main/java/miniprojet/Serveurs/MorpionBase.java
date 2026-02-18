package miniprojet.Serveurs;
import java.io.Serializable;

public class MorpionBase implements Serializable{

    protected String[][] grille;
    protected String joueurActuel;
    protected boolean jeuTermine;
    protected String gagnant;

    private static final String ROUGE = "\u001B[31m";
    private static final String BLEU = "\u001B[34m";
    private static final String RESET = "\u001B[0m";

    public MorpionBase() {
        grille = new String[3][3];
        joueurActuel = "X";
        jeuTermine = false;
        gagnant = null;
        initialiserGrille();
    }

    protected void initialiserGrille() {
        int compteur = 1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grille[i][j] = String.valueOf(compteur);
                compteur++;
            }
        }
    }

    /**
     * Affiche la grille de jeu formatée
     * @return La représentation textuelle de la grille
     */
    public String affichage() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (int i = 0; i < 3; i++) {
            sb.append(" ");
            for (int j = 0; j < 3; j++) {

                String valeur = grille[i][j];

                if ("X".equals(valeur)) {
                    sb.append(ROUGE).append("X").append(RESET);
                } else if ("O".equals(valeur)) {
                    sb.append(BLEU).append("O").append(RESET);
                } else {
                    sb.append(valeur);
                }

                if (j < 2) {
                    sb.append(" | ");
                }
            }
            sb.append("\n");
            if (i < 2) {
                sb.append("-----------\n");
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Vérifie si le jeu est terminé
     * @return true si le jeu est terminé
     */
    public boolean estTermine() {
        return jeuTermine;
    }

    /**
     * Retourne le gagnant
     * @return Le symbole du gagnant ("X", "O", "MATCH NUL") ou null si le jeu n'est pas terminé
     */
    public String getGagnant() {
        return gagnant;
    }

    /**
     * Retourne le joueur actuel
     * @return Le symbole du joueur actuel ("X" ou "O")
     */
    public String getJoueurActuel() {
        return joueurActuel;
    }

    /**
     * Retourne l'état actuel du jeu sous forme de message
     * @return Un message décrivant l'état du jeu
     */
    public String getEtatJeu() {
        if (jeuTermine) {
            if (gagnant.equals("MATCH NUL")) {
                return "Match nul !";
            } else {
                return "Le joueur " + gagnant + " a gagné !";
            }
        }
        return "C'est au tour du joueur " + joueurActuel;
    }
}

