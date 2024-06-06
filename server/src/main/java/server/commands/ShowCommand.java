package server.commands;


import common.data.SpaceMarine;
import common.data.User;
import common.exceptions.NotAuthorizedException;
import common.exceptions.UserNotFoundException;
import common.exceptions.WrongAmountOfArgumentsException;
import common.utility.Outputter;
import server.databaseinteraction.DataBase;
import server.utility.CollectionManager;
import server.utility.ResponseOutputter;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * Command that displays collection elements for user.
 */
public class ShowCommand extends AbstractCommand {
    CollectionManager collectionManager;
    DataBase dataBase;

    /**
     * Show command constructor.
     * @param collectionManager Collection manager for show command.
     */
    public ShowCommand(CollectionManager collectionManager, DataBase dataBase) {
        super("show", "Displays collection elements as Strings");
        this.collectionManager = collectionManager;
        this.dataBase = dataBase;
    }
    /**
     * Displays collection.
     * @param argument The argument passed to the command.
     * @return the response of right execution.
     */
    @Override
    public boolean execute(int user, Serializable argument) {
        try {
            if (user == -1) throw new NotAuthorizedException();
            String strarg = (String) argument;
            if (!(strarg.isEmpty())) throw new WrongAmountOfArgumentsException();
            if (collectionManager.getSpaceMarineCollection().isEmpty()) {
                ResponseOutputter.appendLn("Collection is empty.");
            } else {
                for (SpaceMarine spaceMarine : collectionManager.getSpaceMarineCollection()) {
                    ResponseOutputter.appendLn(spaceMarine.toString() + "\nOwner: " +
                            dataBase.selectUsername(spaceMarine.getCreatedByUser())+ "\n===============");

                }
            }
            return true;
        } catch (WrongAmountOfArgumentsException e) {
            ResponseOutputter.appendLn(e.getMessage());
        } catch (NotAuthorizedException | SQLException | UserNotFoundException e) {
            ResponseOutputter.appendError(e.getMessage());
        }
        return false;
    }

}
