package miniprojet.Serveurs;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientTCP {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public ClientTCP(String host, int port) throws Exception {
        socket = new Socket(host, port);
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }

    public void send(Object message) throws Exception {
        output.writeObject(message);
        output.flush();
    }

    public Object receive() throws Exception {
        return input.readObject();
    }

    public void close() throws Exception {
        if (input != null) input.close();
        if (output != null) output.close();
        if (socket != null) socket.close();
    }
}
