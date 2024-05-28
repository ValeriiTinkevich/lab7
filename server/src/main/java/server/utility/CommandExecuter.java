package server.utility;

import common.interaction.Request;

import java.io.Serializable;
import java.net.Socket;
import java.util.logging.Logger;

public class CommandExecuter extends Thread{
    public static final Logger logger = Logger.getLogger(Receiver.class.getName());
    private Request request;
    private boolean hasNewRequest=false;
    private RequestHandler requestHandler;
    private Socket socket;
    private Responser responser;


    public CommandExecuter(RequestHandler requestHandler, Socket socket, Responser responser) {
        this.request = null;
        this.requestHandler = requestHandler;
        this.socket = socket;
        this.responser = responser;
    }

    public void putCommand(Request request) {
        this.request = request;
        hasNewRequest = true;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            if(hasNewRequest) {
                executeCommand(request);
            }
        }
    }

    public void executeCommand(Request request) {
        hasNewRequest = false;
        responser.send(requestHandler.handle(request));
    }
}
