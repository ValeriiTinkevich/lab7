package server.utility;

import common.interaction.Response;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class ResponserTask implements Runnable{

    public static final Logger logger = Logger.getLogger(Receiver.class.getName());
    private Response response;
    private Socket socket;

    public ResponserTask(Response answer, Socket socket) {
        this.response = answer;
        this.socket = socket;
    }



    @Override
    public void run() {
        try {
            ObjectOutputStream clientWriter = new ObjectOutputStream(socket.getOutputStream());
            clientWriter.writeObject(response);
            clientWriter.flush();
            logger.info("A response has been sent to " + socket.getInetAddress() + ":" + socket.getPort());
            clientWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
}

