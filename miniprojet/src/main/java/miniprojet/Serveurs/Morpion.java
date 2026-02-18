package miniprojet.Serveurs;


//Classe Morpion pour le serveur.

public class Morpion extends MorpionBase {

    public Morpion() {
        super();
    }

    /**
     * Joue un coup à la position indiquée
     * @param position Position de 1 à 9
     * @return true si le coup a été joué avec succès
     */
    public boolean jouerCoup(int position) {
        if (jeuTermine || position < 1 || position > 9) {
            return false;
        }

        int ligne = (position - 1) / 3;
        int colonne = (position - 1) % 3;

        // Vérifier si la case est libre
        if (grille[ligne][colonne].equals("X") || grille[ligne][colonne].equals("O")) {
            return false;
        }

        // Placer le symbole du joueur
        grille[ligne][colonne] = joueurActuel;

        // Vérifier si le joueur a gagné
        if (verifierVictoire()) {
            jeuTermine = true;
            gagnant = joueurActuel;
            return true;
        }

        // Vérifier match nul
        if (verifierMatchNul()) {
            jeuTermine = true;
            gagnant = "MATCH NUL";
            return true;
        }

        // Changer de joueur
        joueurActuel = joueurActuel.equals("X") ? "O" : "X";
        return true;
    }

    /**
     * Vérifie si un joueur a gagné
     * @return true si victoire détectée
     */
    private boolean verifierVictoire() {
        // Vérifier les lignes
        for (int i = 0; i < 3; i++) {
            if (grille[i][0].equals(grille[i][1]) && grille[i][1].equals(grille[i][2])) {
                return true;
            }
        }

        // Vérifier les colonnes
        for (int j = 0; j < 3; j++) {
            if (grille[0][j].equals(grille[1][j]) && grille[1][j].equals(grille[2][j])) {
                return true;
            }
        }

        // Vérifier les diagonales
        if (grille[0][0].equals(grille[1][1]) && grille[1][1].equals(grille[2][2])) {
            return true;
        }
        if (grille[0][2].equals(grille[1][1]) && grille[1][1].equals(grille[2][0])) {
            return true;
        }

        return false;
    }

    /**
     * Vérifie si la grille est pleine (match nul)
     * @return true si match nul
     */
    private boolean verifierMatchNul() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!grille[i][j].equals("X") && !grille[i][j].equals("O")) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Définit le joueur actuel (réservé au serveur)
     * @param joueurActuel "X" ou "O"
     */
    public void setJoueurActuel(String joueurActuel) {
        this.joueurActuel = joueurActuel;
    }

    /**
     * Réinitialise le jeu (réservé au serveur)
     */
    public void reinitialiser() {
        initialiserGrille();
        joueurActuel = "X";
        jeuTermine = false;
        gagnant = null;
    }

}
