package client;

import client.utility.UserHandler;
import common.exceptions.ConnectionErrorException;
import common.exceptions.NotInDeclaredLimitsException;
import common.interaction.Request;
import common.interaction.Response;
import common.interaction.ResponseAuth;
import common.interaction.ResponseResult;
import common.utility.Outputter;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client {
    private final String host;
    private final int port;
    private final int reconnectionTimeout;
    private int reconnectionAttempts;
    private final int maxReconnectionAttempts;
    private SocketChannel socketChannel;
    private final UserHandler userHandler;
    private ObjectOutputStream serverWriter;
    private ObjectInputStream serverReader;
    public static int userID=-1;

    public Client(UserHandler userHandler,int port, int reconnectionTimeout, int maxReconnectionAttempts, String host) {
        this.host = host;
        this.port = port;
        this.reconnectionTimeout = reconnectionTimeout;
        this.maxReconnectionAttempts = maxReconnectionAttempts;
        this.userHandler = userHandler;
    }

    public void run() {
        try {
            boolean processingStatus = true;
            while (processingStatus) {
                try {
                    socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
                    Outputter.printLn("The connection to the server has been successfully established.");
                    Outputter.printLn("Waiting for permission to exchange data...");
                    serverWriter = new ObjectOutputStream(socketChannel.socket().getOutputStream());
                    serverReader = new ObjectInputStream(socketChannel.socket().getInputStream());
                    processingStatus = processRequestToServer();
                } catch (ConnectionErrorException exception) {
                    if (reconnectionAttempts >= maxReconnectionAttempts) {
                        Outputter.printError("Exceeded the number of connection attempts!");
                        break;
                    }
                    try {
                        Thread.sleep(reconnectionTimeout);
                    } catch (IllegalArgumentException timeoutException) {
                        Outputter.printError("Connection waiting time'" + reconnectionTimeout +
                                "' is beyond the limits of possible values!");
                        Outputter.printLn("Reconnection will be performed immediately.");
                    } catch (Exception timeoutException) {
                        Outputter.printError("An error occurred while trying to wait for connection!");
                        Outputter.printLn("Reconnection will be performed immediately.");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                reconnectionAttempts++;
            }
            if (socketChannel != null) socketChannel.close();
            Outputter.printLn("The client's work has been successfully completed.");
        } catch (NotInDeclaredLimitsException exception) {
            Outputter.printError("The client cannot be started!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void connectToServer() throws ConnectionErrorException, NotInDeclaredLimitsException, IOException {
            if (reconnectionAttempts >= 1) Outputter.printLn("Reconnecting to the server...");
            socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
            Outputter.printLn("The connection to the server has been successfully established.");
            Outputter.printLn("Waiting for permission to exchange data...");
            serverWriter = new ObjectOutputStream(socketChannel.socket().getOutputStream());
            serverReader = new ObjectInputStream(socketChannel.socket().getInputStream());
            Outputter.printLn("Permission to exchange data has been received.");

    }

    private boolean sendRequest(){
        Request requestToserverr = null;
        do {
            try {
                requestToserverr = userHandler.handle(null);
                serverWriter = new ObjectOutputStream(socketChannel.socket().getOutputStream());
                serverWriter.writeObject(requestToserverr);
                return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (!requestToserverr.getCommandName().equals("exit"));
    }

    private boolean receiveResponse() {
        Response response = null;
        try {
            serverReader = new ObjectInputStream(socketChannel.socket().getInputStream());
            serverReader.readObject();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    private boolean processRequestToServer() throws IOException {
        Request requestToServer = null;
        Response serverResponse = null;
        ResponseAuth authServerResponse = null;
        do {
            try {
                requestToServer = userHandler.handle(ResponseResult.OK);
                if (requestToServer.isEmpty()) continue;
                serverWriter.writeObject(requestToServer);
                serverResponse = (Response) serverReader.readObject();
                if (serverResponse instanceof ResponseAuth) {
                    userID = ((ResponseAuth) serverResponse).getUserID();
                }
                Outputter.print(serverResponse.getResponseBody());
            } catch (InvalidClassException | NotSerializableException exception) {
                Outputter.printError("An error occurred while sending data to the server!");
                Outputter.printError(exception);
                Outputter.printError(serverReader);
            } catch (ClassNotFoundException exception) {
                Outputter.printError("An error occurred while reading the received data!");
            } catch (IOException exception) {
                Outputter.printError("The connection to the server is broken!");
                try {
                    reconnectionAttempts++;
                    connectToServer();
                } catch (ConnectionErrorException | NotInDeclaredLimitsException reconnectionException) {
                    if (requestToServer.getCommandName().equals("exit"))
                        Outputter.printLn("The command will not be registered on the server.");
                    else Outputter.printLn("Try to repeat the command later.");
                }
            }
        } while (!requestToServer.getCommandName().equals("exit"));
        return false;
    }


}
