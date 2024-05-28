package server;

import common.data.User;
import common.exceptions.ClosingSocketException;
import common.exceptions.ConnectionErrorException;
import common.exceptions.OpeningServerSocketException;
import common.interaction.Request;
import common.interaction.Response;
import common.interaction.ResponseResult;
import common.utility.Outputter;
import server.utility.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;

import static server.App.logger;

public class Server {

    private final int port;
    private final int soTimeout;
    private ServerSocket serverSocket;
    private RequestHandler requestHandler;
    public static HashSet<Integer> authorizedUsers;

    public Server(int port, int soTimeout, RequestHandler requestHandler) {
        this.port = port;
        this.soTimeout = soTimeout;
        this.requestHandler = requestHandler;
        authorizedUsers = new HashSet<Integer>();
    }

    public static boolean addUser(Integer userID) {
        return authorizedUsers.add(userID);
    }


    public void launch() {
        try {
            openServerSocket();
            try {
                Socket clientSocket = connectToClient();
                if (clientSocket != null) {
                    logger.info("The server is running");
                    Responser responser = new Responser(clientSocket, 10);
                    CommandExecuter commandExecuter = new CommandExecuter(requestHandler, clientSocket, responser);
                    commandExecuter.start();
                    Receiver receiver = new Receiver(clientSocket, commandExecuter);
                    receiver.setDaemon(true);
                    receiver.start();
                    shutDownHook();

                }
            } catch (ConnectionErrorException e) {
                logger.severe("Connectionerror");
            } catch (SocketTimeoutException e) {
                Outputter.printError("An error occurred while trying to terminate the connection with the client!");
                logger.severe("An error occurred while trying to terminate the connection with the client!");
            }
        } catch (OpeningServerSocketException e) {
            Outputter.printError("The server cannot be started!");
            logger.severe("The server cannot be started!");
        }
    }


    public void run() {
        try {
            openServerSocket();
            boolean processingStatus = true;
            while(processingStatus) {
                try(Socket clientSocket = connectToClient()) {
                    processingStatus = processClientRequest(clientSocket);
                } catch (ConnectionErrorException | SocketTimeoutException e) {
                    break;
                } catch (IOException e) {
                    Outputter.printError("An error occurred while trying to terminate the connection with the client!");
                    logger.severe("An error occurred while trying to terminate the connection with the client!");
                }

            }

            stop();
        } catch (OpeningServerSocketException e) {
            Outputter.printError("The server cannot be started!");
            logger.severe("The server cannot be started!");
        }
    }

    /**
     * Open server socket.
     */
    private void openServerSocket() throws OpeningServerSocketException {
        try{
            logger.info("Starting the server...");
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(soTimeout);
            logger.info("The server has been successfully started.");
        } catch (IllegalArgumentException exception) {
            Outputter.printError("Port '" + port + "' is beyond the limits of possible values!");
            logger.severe("Port '" + port + "' is beyond the limits of possible values!");
            throw new OpeningServerSocketException();
        } catch (IOException exception) {
            Outputter.printError("An error occurred while trying to use the port '" + port + "'!");
            logger.severe("An error occurred while trying to use the port '" + port + "'!");
            throw new OpeningServerSocketException();
        }
    }

    private Socket connectToClient() throws ConnectionErrorException, SocketTimeoutException {
        try{
            Outputter.printLn("Listening port '" + port + "'...");
            logger.info("Listening port '" + port + "'...");
            Socket clientSocket = serverSocket.accept();
            Outputter.printLn("The connection with the client has been successfully established.");
            logger.info("The connection with the client has been successfully established.");
            return clientSocket;
        } catch (SocketTimeoutException exception) {
            Outputter.printError("Connection timeout exceeded!");
            logger.warning("Connection timeout exceeded!");
            throw new SocketTimeoutException();
        } catch (IOException exception) {
            Outputter.printError("An error occurred while connecting to the client!");
            logger.severe("An error occurred while connecting to the client!");
            throw new ConnectionErrorException();
        }
    }

    private boolean processClientRequest(Socket clientSocket) {
        Request userRequest = null;
        Response responseToUser;
        try (ObjectInputStream clientReader = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream clientWriter = new ObjectOutputStream(clientSocket.getOutputStream())) {
            do {
                userRequest = (Request) clientReader.readObject();
                responseToUser = requestHandler.handle(userRequest);
                logger.info("Request '" + userRequest.getCommandName() + "' has been successfully processed.");
                clientWriter.writeObject(responseToUser);
                clientWriter.flush();
            } while(responseToUser.getResponseResult() != ResponseResult.SERVER_EXIT);
            return false;
        } catch (ClassNotFoundException exception){
            Outputter.printError("An error occurred while reading the received data!");
            logger.severe("An error occurred while reading the received data!");

        } catch (IOException exception) {
            if (userRequest == null) {
                Outputter.printError("Unexpected disconnection from the client!");
                logger.warning("Unexpected disconnection from the client!");
            } else {
                Outputter.printLn("The client has been successfully disconnected from the server!");
                logger.info("The client has been successfully disconnected from the server!");
            }
        }
        return true;
    }

    /**
     * Finishes server operation.
     */
    private void stop() {
        try{
            logger.info("Shutting down the server...");
            if(serverSocket == null) throw new ClosingSocketException();
            serverSocket.close();
            Outputter.printLn("The server operation has been successfully completed.");
        } catch (ClosingSocketException exception) {
            Outputter.printError("It is impossible to shut down a server that has not yet started!");
            logger.severe("It is impossible to shut down a server that has not yet started!");
        } catch (IOException exception) {
            Outputter.printError("An error occurred when shutting down the server!");
            logger.severe("An error occurred when shutting down the server!");
        }
    }

    private void shutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("The server is stopped")));
    }


}
