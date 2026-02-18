package miniprojet.Serveurs;

public class Agent {
    private ClientTCP connexion;
    public String monSymbole;

    public Agent(String ipJeu, int portJeu) throws Exception {
        this.connexion = new ClientTCP(ipJeu, portJeu);
        this.monSymbole = (String) this.connexion.receive();
    }
  
    public MorpionBase recevoirEtatJeu() throws Exception {
        return (MorpionBase) connexion.receive();
    }

    public void envoyerCoup(int position) throws Exception {
        connexion.send(position);
    }
  
    public void deconnecter() throws Exception {
        connexion.close();
    }
}
