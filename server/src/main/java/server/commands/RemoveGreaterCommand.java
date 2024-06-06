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
 * Command that removes all elements greater than inputted.
 */
public class RemoveGreaterCommand extends AbstractCommand{
    CollectionManager collectionManager;
    DataBase dataBase;

    /**
     * Remove_greater command constructor.
     * @param collectionManager Collection manager for remove_greater command.
     */
    public RemoveGreaterCommand(CollectionManager collectionManager, DataBase dataBase) {
        super("remove_greater", "removes all elements greater than input");
        this.collectionManager  = collectionManager;
        this.dataBase = dataBase;
    }
    /**
     * Removes all elements greater than inputted.
     * @param argument The argument passed to the command.
     * @return the response of right execution.
     */
    @Override
    public boolean execute(int user, Serializable argument) {
        try {
            if (user == -1) throw new NotAuthorizedException();
            if(!(argument instanceof SpaceMarine || argument == null)) throw new WrongAmountOfArgumentsException();
            SpaceMarine greaterSpaceMarine = (SpaceMarine) argument;
            long height = greaterSpaceMarine.getHeight();
            ResponseOutputter.appendLn("Deleted " + dataBase.removeByHeight(height) + " rows");
            collectionManager.updateCollection(dataBase.selectCollection());
        } catch (WrongAmountOfArgumentsException e) {
            ResponseOutputter.appendError(e.getMessage());
            return false;
        } catch (NotAuthorizedException | SQLException | DataBaseNotUpdatedException e) {
            ResponseOutputter.appendError(e.getMessage());
        }
        return false;
    }
}
