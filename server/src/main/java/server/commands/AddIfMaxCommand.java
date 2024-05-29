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
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * Command for adding element to collection if its value is more than max in the collection.
 */
public class AddIfMaxCommand extends AbstractCommand{
    CollectionManager collectionManager;
    DataBase dataBase;

    /**
     * Add if max command constructor
     * @param collectionManager Collection manager for add_if_max command.
     */
    public AddIfMaxCommand(CollectionManager collectionManager, DataBase dataBase) {
        super("add_if_max", "adds Space Marine");
        this.collectionManager = collectionManager;
        this.dataBase = dataBase;
    }
    /**
     * Adds asked element if its value is more than collections max object.
     *
     * @param argument The argument passed to the command.
     * @return the response of right execution.
     */
    @Override
    public boolean execute(int user, Serializable argument) {
        try {
            if (user == -1) throw new NotAuthorizedException();
            if(!(argument instanceof SpaceMarine)) throw new WrongAmountOfArgumentsException();
            SpaceMarine askedMarine = (SpaceMarine) argument;
//            if (collectionManager.getSize() == 0) {
//                collectionManager.addToCollection(askedMarine);
//                ResponseOutputter.appendLn("Space marine was added successfully");
//                return true;
//            }
            SpaceMarine maxSpaceMarine = collectionManager.getSpaceMarineCollection()
                    .stream()
                    .max(Comparator.comparing(SpaceMarine::getHeight))
                    .orElseThrow(NoSuchElementException::new);
            if(askedMarine.compareTo(maxSpaceMarine) > 0) {
                dataBase.insert(askedMarine, user);
                collectionManager.updateCollection(dataBase.selectCollection());
                ResponseOutputter.appendLn("Space marine added successfully");
            } else {
                ResponseOutputter.appendLn("Height is not enough to add!");
            }
        } catch (WrongAmountOfArgumentsException e) {
            ResponseOutputter.appendLn(e.getMessage());
            return false;
        } catch (NotAuthorizedException | DataBaseNotUpdatedException | SQLException e) {
            ResponseOutputter.appendError(e.getMessage());
        }
        return false;

    }
}
