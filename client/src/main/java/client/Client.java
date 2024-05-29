package client;

import client.utility.UserHandler;
import common.interaction.Request;
import common.interaction.Response;
import common.interaction.ResponseAuth;
import common.utility.Outputter;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class Client {
    private final String host;
    private final int port;
    private int reconnectionAttempts;
    private final UserHandler userHandler;
    public static int userID = -1;

    public Client(UserHandler userHandler, int port, int reconnectionAttempts, String host) {
        this.host = host;
        this.port = port;
        this.userHandler = userHandler;
        this.reconnectionAttempts = reconnectionAttempts;
    }


    public void run() throws InterruptedException {
        while (reconnectionAttempts > 0) {
            Outputter.printLn("Trying to connect to the server");
            sleep(5000);
            try (Socket socket = new Socket(host, port);
                 ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

                Outputter.printLn("Connected to the server. Enter commands:");

                do {
                    System.out.print("> ");
                    Request request = userHandler.handle();
                    output.writeObject(request);
                    output.flush();
                    Response response = (Response) input.readObject();
                    if (response instanceof ResponseAuth) Client.userID = ((ResponseAuth) response).getUserID();
                    Outputter.printLn("Received response: " + response.getResponseBody() + "\n" + response.getResponseResult());
                } while (true);
            } catch (EOFException e) {
                Outputter.printError("Connection closed by server.");
                break;
            } catch (IOException | ClassNotFoundException ex) {
                Outputter.printError("Server is unavailable");
                reconnectionAttempts -= 1;
            }
        }
        Outputter.printError("Reconnection attempts exceeded the limit!");
    }


//    private void connectToServer() {
//        if (reconnectionAttempts >= 1) Outputter.printLn("Reconnecting to the server...");
//        try {
//            Outputter.printLn("The connection to the server has been successfully established.");
//            Outputter.printLn("Waiting for permission to exchange data...");
//            Socket socket = new Socket(host, port);
//            serverWriter = new ObjectOutputStream(socket.getOutputStream());
//            serverReader = new ObjectInputStream(socket.getInputStream());
//            Outputter.printLn("Permission to exchange data has been received.");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
