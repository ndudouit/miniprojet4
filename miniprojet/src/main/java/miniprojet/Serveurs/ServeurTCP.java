package miniprojet.Serveurs;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurTCP {
    ServerSocket servSocket;
    Socket socket;
    ObjectInputStream input;
    ObjectOutputStream output;
    int port;

    public ServeurTCP(int port) throws Exception {
        this.port = port;
        servSocket = new ServerSocket(this.port);
    }

    public void attendreClient() throws Exception {
        socket = servSocket.accept();
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }

    public void fermerClient() throws Exception {
        if (input != null) input.close();
        if (output != null) output.close();
        if (socket != null) socket.close();
    }

    public void arreterServeur() throws Exception {
        fermerClient();
        if (servSocket != null) servSocket.close();
    }

    public void envoyer(Object message) {
        try {
            output.writeObject(message);
            output.flush();
        } catch (Exception e) {
            System.err.println("Erreur envoi : " + e.getMessage());
        }
    }

    public Object recevoir() {
        try {
            return input.readObject();
        } catch (Exception e) {
            System.err.println("Erreur réception : " + e.getMessage());
            return null;
        }
    }
}
