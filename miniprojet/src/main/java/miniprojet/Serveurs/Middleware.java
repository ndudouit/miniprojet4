package miniprojet.Serveurs;

import java.util.*;

public class Middleware {
    private static final int PORT_MIDDLEWARE = 5100;
    private Map<String, String> serveursEnregistres = new HashMap<>();

    public void demarrer() {
        System.out.println("Middleware démarré sur " + PORT_MIDDLEWARE);
        try {
            ServeurTCP serveur = new ServeurTCP(PORT_MIDDLEWARE);
            
            while (true) {
                serveur.attendreClient();
                Object message = serveur.recevoir();
                
                if (message instanceof String && ((String) message).startsWith("REGISTER")) {
                    String portJeu = ((String) message).split(":")[1];
                    String ipJeu = "127.0.0.1";
                    
                    String nomAttribue = demanderNomAuServeurNoms(ipJeu);
                    serveursEnregistres.put(nomAttribue, ipJeu + ":" + portJeu);
                    System.out.println("Enregistré : " + nomAttribue + " -> " + ipJeu + ":" + portJeu);
                    serveur.envoyer("OK:" + nomAttribue);
                    
                } else {
                    String langue = (String) message;
                    System.out.println("Client connecté (Langue: " + langue + ")");
                    
                    if (serveursEnregistres.isEmpty()) {
                        serveur.envoyer("ERROR;NO_SERVER");
                    } else {
                        List<String> keys = new ArrayList<>(serveursEnregistres.keySet());
                        String nomChoisi = keys.get(new Random().nextInt(keys.size()));
                        String adresse = serveursEnregistres.get(nomChoisi);
                        serveur.envoyer(adresse);
                    }
                }
                serveur.fermerClient();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String demanderNomAuServeurNoms(String ip) {
        try {
            ClientTCP clientNoms = new ClientTCP("127.0.0.1", 5002);
            clientNoms.send(ip);
            String nom = (String) clientNoms.receive();
            clientNoms.close();
            return nom;
        } catch (Exception e) {
            return "Serveur_" + System.currentTimeMillis();
        }
    }

    public static void main(String[] args) {
        new Middleware().demarrer();
    }
}
