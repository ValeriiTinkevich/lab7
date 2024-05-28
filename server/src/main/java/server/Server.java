package server;

import server.utility.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

import static server.App.logger;

public class Server {

    private final int port;
    private final int soTimeout;
    private static final ExecutorService requestPool = Executors.newFixedThreadPool(10);
    private static final ForkJoinPool responsePool = ForkJoinPool.commonPool();
    private static final AtomicBoolean isRunning = new AtomicBoolean(true);

    public Server(int port, int soTimeout) {
        this.port = port;
        this.soTimeout = soTimeout;
    }



    public void launch() {
        try {
        ServerSocket serverSocket = openServerSocket(port);
        while (isRunning.get()) {
            try {
                Socket socket = acceptClientConnection(serverSocket);
                if (socket != null) {
                    handleClientConnection(socket);
                }
            } catch (IOException e) {
                if (isRunning.get()) {
                    logger.severe("Error accepting client connection: " + e.getMessage());
                } else {
                    logger.severe("Server is shutting down.");
                }
            }
        }
        serverSocket.close();
        requestPool.shutdown();
        responsePool.shutdown();
        stopServer();
        shutDownHook();
    } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private ServerSocket openServerSocket(int port) throws IOException {
        logger.info("Starting the server...");
        logger.info("Listening port '" + port + "'...");
        ServerSocket serverSocket = new ServerSocket(port);
        logger.info("The server has been successfully started.");
        return serverSocket;
    }

    private Socket acceptClientConnection(ServerSocket serverSocket) throws IOException {
        if (!isRunning.get()) {
            return null;
        }
        Socket socket = serverSocket.accept();
        logger.info("Incoming connection from " + socket.getInetAddress());
        return socket;
    }

    private void handleClientConnection(Socket socket) {
        requestPool.execute(new RequestHandler(socket, responsePool));
    }

    public static void stopServer() {
        logger.severe("Stopping the server...");
        isRunning.set(false);
        System.exit(0);
    }


    /**
     * Open server socket.
     */


    /**
     * Finishes server operation.
     */
//    public static void stop() {
//        try{
//            logger.info("Shutting down the server...");
//            if(serverSocket == null) throw new ClosingSocketException();
//            serverSocket.close();
//            Outputter.printLn("The server operation has been successfully completed.");
//        } catch (ClosingSocketException exception) {
//            Outputter.printError("It is impossible to shut down a server that has not yet started!");
//            logger.severe("It is impossible to shut down a server that has not yet started!");
//        } catch (IOException exception) {
//            Outputter.printError("An error occurred when shutting down the server!");
//            logger.severe("An error occurred when shutting down the server!");
//        }
//    }

    private void shutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("The server is stopped")));
    }


}
