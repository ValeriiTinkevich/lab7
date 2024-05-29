package server;

import common.exceptions.DataBaseNotUpdatedException;
import server.databaseinteraction.DataBase;
import server.utility.CollectionManager;
import server.utility.CommandManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class App {

    public static final int PORT = 64532;
    public static final int connectionTimeout = 60 * 1000;
    public static final Logger logger = Logger.getLogger(Server.class.getName());


    public static void main(String[] args) throws IOException, SQLException, DataBaseNotUpdatedException {
        FileHandler fh;
        fh = new FileHandler("server.log");
        logger.addHandler(fh);
        CollectionManager collectionManager = new CollectionManager();
        ConsoleHandler consoleHandler = new ConsoleHandler();
        logger.setUseParentHandlers(false);
        consoleHandler.setEncoding("UTF-8");
        logger.addHandler(consoleHandler);
        DataBase dataBase = new DataBase("postgres", "admin123");
        collectionManager.updateCollection(dataBase.selectCollection());

        CommandManager commandManager = new CommandManager(collectionManager, dataBase);
        commandManager.initializeCommands();
        Server server = new Server(PORT, connectionTimeout);

        server.launch();



    }
}

