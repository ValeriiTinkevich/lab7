package server.utility;

import server.commands.*;
import server.databaseinteraction.DataBase;

import java.util.*;

public class CommandManager {
    public static final Map<String, ICommand> commands = new HashMap<>();
    CollectionManager collectionManager;
    private DataBase dataBase;

    private static LinkedList<String> commandHistory;


    public CommandManager (CollectionManager collectionManager, DataBase dataBase) {
        commandHistory = new LinkedList<>();
        this.collectionManager = collectionManager;
        this.dataBase = dataBase;
    }

    public void initializeCommands() {
        commands.put("help", new HelpCommand(commands));
        commands.put("info", new InfoCommand(this.collectionManager));
        commands.put("add", new AddElementCommand(this.collectionManager, this.dataBase));
        commands.put("show", new ShowCommand(this.collectionManager, this.dataBase));
        commands.put("update", new UpdateByIdCommand(this.collectionManager, this.dataBase));
        commands.put("remove_by_id", new RemoveByIdCommand(this.collectionManager, this.dataBase));
        commands.put("exit", new ExitCommand());
        commands.put("remove_at", new RemoveAtCommand(this.collectionManager));
        commands.put("add_if_max", new AddIfMaxCommand(this.collectionManager, this.dataBase));
        commands.put("remove_greater", new RemoveGreaterCommand(this.collectionManager, this.dataBase));
        commands.put("filter_by_chapter", new FilterByChapterCommand(this.collectionManager, this.dataBase));
        commands.put("print_unique_heart_count", new PrintUniqueHeartCountCommand(this.collectionManager));
        commands.put("filter_less_than_health", new FilterLessThanHealthCommand(this.collectionManager, this.dataBase));
        commands.put("execute", new ExecuteScriptCommand());
        commands.put("register", new RegisterCommand(this.dataBase));
        commands.put("auth", new AuthCommand(this.dataBase));

    }

    public static void addToHistory(String CommandName) {
        commandHistory.addFirst(CommandName);
        if (commandHistory.size() > 5) commandHistory.pollLast();
    }

    public static ICommand getCommand(String commandName) {
        return commands.get(commandName.toLowerCase());
    }

    public void setDataBase(DataBase dataBase) {
        this.dataBase = dataBase;
    }

}
