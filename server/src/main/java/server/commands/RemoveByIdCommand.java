package server.commands;


import common.data.User;
import common.exceptions.MustNotBeEmptyException;
import common.exceptions.NotAuthorizedException;
import common.exceptions.WrongAmountOfArgumentsException;
import common.utility.Outputter;
import server.utility.CollectionManager;
import server.utility.ResponseOutputter;

import java.io.Serializable;

/**
 * Command that removes element from collection with given id.
 */
public class RemoveByIdCommand extends AbstractCommand {
    CollectionManager collectionManager;

    /**
     * Remove_by_id command constructor.
     * @param collectionManager Collection manager for remove_by_id command.
     */
    public RemoveByIdCommand(CollectionManager collectionManager) {
        super("remove_by_id", "Removes element by id");
        this.collectionManager = collectionManager;
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
            if (collectionManager.getById(id) == null) throw new MustNotBeEmptyException();
            collectionManager.removeByIDFromCollection(id);
            ResponseOutputter.appendLn("Successfully removed the element");
            return true;

        } catch (WrongAmountOfArgumentsException e) {
            ResponseOutputter.appendError(e.getMessage());
        } catch (MustNotBeEmptyException e) {
            ResponseOutputter.appendError("No space marine with this id");
        } catch (NumberFormatException e) {
            ResponseOutputter.appendError("The id value must be int!");
        } catch (NotAuthorizedException e) {
            ResponseOutputter.appendError(e.getMessage());
        }
        return false;
    }
}
