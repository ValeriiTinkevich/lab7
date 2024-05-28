package server;

import common.utility.Outputter;
import server.databaseinteraction.DataBase;
import server.utility.CollectionManager;
import server.utility.CommandManager;
import server.utility.FileManager;
import server.utility.RequestHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class App {

    public static final int PORT = 64532;
    public static final int connectionTimeout = 60 * 1000;
    public static final Logger logger = Logger.getLogger(Server.class.getName());


    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        FileHandler fh;
        fh = new FileHandler("server.log");
        logger.addHandler(fh);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        logger.setUseParentHandlers(false);
        consoleHandler.setEncoding("UTF-8");
        logger.addHandler(consoleHandler);
        DataBase dataBase = new DataBase("postgres", "admin123");
        String fileName = "data.csv";
        FileManager fileManager = new FileManager(fileName, ";");
        CollectionManager collectionManager = new CollectionManager(fileManager);
        CommandManager commandManager = new CommandManager(collectionManager, dataBase);
        RequestHandler requestHandler = new RequestHandler(commandManager);
        Server server = new Server(PORT, connectionTimeout, requestHandler);

        server.launch();



    }
}

