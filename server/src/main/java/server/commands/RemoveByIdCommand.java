package server.commands;


import common.data.User;
import common.exceptions.DataBaseNotUpdatedException;
import common.exceptions.MustNotBeEmptyException;
import common.exceptions.NotAuthorizedException;
import common.exceptions.WrongAmountOfArgumentsException;
import common.utility.Outputter;
import server.databaseinteraction.DataBase;
import server.utility.CollectionManager;
import server.utility.ResponseOutputter;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * Command that removes element from collection with given id.
 */
public class RemoveByIdCommand extends AbstractCommand {
    CollectionManager collectionManager;
    DataBase dataBase;

    /**
     * Remove_by_id command constructor.
     * @param collectionManager Collection manager for remove_by_id command.
     */
    public RemoveByIdCommand(CollectionManager collectionManager, DataBase dataBase) {
        super("remove_by_id", "Removes element by id");
        this.collectionManager = collectionManager;
        this.dataBase = dataBase;
    }

    /**
     * Removes element by id from collection.
     * @param argument The argument passed to the command.
     * @return the response of right execution.
     */
    @Override
    public boolean execute(int user, Serializable argument) {
        try {
            if (user == -1) throw new NotAuthorizedException();
            if (argument == null) throw new WrongAmountOfArgumentsException();
            String argument_str = (String) argument;
            int id = Integer.parseInt(argument_str);
            ResponseOutputter.appendLn("Deleted " + dataBase.removeById(id) + " rows");
            collectionManager.updateCollection(dataBase.selectCollection());
        } catch (WrongAmountOfArgumentsException | NotAuthorizedException | DataBaseNotUpdatedException | SQLException e) {
            ResponseOutputter.appendError(e.getMessage());
        } catch (NumberFormatException e) {
            ResponseOutputter.appendError("The id value must be int!");
        }
        return false;
    }
}
