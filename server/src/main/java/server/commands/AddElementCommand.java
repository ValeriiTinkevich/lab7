package server.commands;



import common.data.SpaceMarine;
import common.exceptions.DataBaseNotUpdatedException;
import common.exceptions.NotAuthorizedException;
import common.exceptions.WrongAmountOfArgumentsException;
import server.databaseinteraction.DataBase;
import server.utility.CollectionManager;
import server.utility.ResponseOutputter;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * Command for adding an element to collection from user input.
 */
public class AddElementCommand extends AbstractCommand{
    private final CollectionManager collectionManager;
    private final DataBase dataBase;

    /**
     * Add command constructor.
     * @param collectionManager Collection manager for add command.
     */
    public AddElementCommand(CollectionManager collectionManager, DataBase dataBase) {
        super("add", "Adds and element to collection");
        this.collectionManager = collectionManager;
        this.dataBase = dataBase;
    }

    /**
     * Adds element from user input.
     *
     * @param argument The argument passed to the command.
     * @return the response of right execution.
     */
    @Override
    public boolean execute(int user, Serializable argument) {
        try {
            if (user == -1) throw new NotAuthorizedException();
            if (!(argument instanceof SpaceMarine)) throw new WrongAmountOfArgumentsException();
            SpaceMarine smArg = (SpaceMarine) argument;
            dataBase.insert(smArg, user);
            collectionManager.updateCollection(dataBase.selectCollection());
            ResponseOutputter.appendLn("Space marine was added successfully!");
            return true;
        } catch (WrongAmountOfArgumentsException e) {
            ResponseOutputter.appendLn(e.getMessage());
            return false;
        } catch (NotAuthorizedException | SQLException | DataBaseNotUpdatedException e) {
            ResponseOutputter.appendError(e.getMessage());
        }
        return false;
    }
}
