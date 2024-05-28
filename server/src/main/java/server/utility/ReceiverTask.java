package server.utility;

import common.interaction.Request;
import server.App;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class ReceiverTask implements Runnable{
    public static final Logger logger = Logger.getLogger(Receiver.class.getName());
    private Socket socket;
    private CommandExecuter commandExecuter;


    public ReceiverTask(Socket socket, CommandExecuter commandExecuter) {
        this.socket = socket;
        this.commandExecuter = commandExecuter;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ObjectInputStream clientReader = new ObjectInputStream(socket.getInputStream());
                Request request = (Request) clientReader.readObject();
                logger.info("Request '" + request.getCommandName() + "' has been successfully processed.");
                commandExecuter.putCommand(request);
            } catch (IOException e) {
                logger.severe(e.getMessage());
            } catch (ClassNotFoundException e) {
                logger.severe("Couldn't receive the request");
            }
        }
    }
}
