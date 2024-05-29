package server.commands;


import common.data.SpaceMarine;
import common.data.User;
import common.exceptions.*;
import common.utility.Outputter;
import server.databaseinteraction.DataBase;
import server.utility.CollectionManager;
import server.utility.ResponseOutputter;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * Command that updates element by id from user input.
 */
public class UpdateByIdCommand extends AbstractCommand {
    CollectionManager collectionManager;
    DataBase dataBase;

    /**
     * Update command constructor
     * @param collectionManager Collection manager for update command.
     */
    public UpdateByIdCommand(CollectionManager collectionManager, DataBase dataBase) {
        super("update", "Updates element by id");
        this.collectionManager = collectionManager;
        this.dataBase = dataBase;
    }

    /**
     * Updates element by id from user input.
     * @param argument The argument passed to the command.
     * @return the response of right execution.
     */
    @Override
    public boolean execute(int user, Serializable argument) {
        ResponseOutputter.appendError("Command needs 2 arguments!");
        return false;
    }

    public boolean execute(int user, Serializable argument_id, Serializable argument_Marine) {
        try {
            if (user == -1) throw new NotAuthorizedException();
            if (argument_id == null || argument_Marine == null) throw new WrongAmountOfArgumentsException();
            String argument_id_string = (String) argument_id;
            SpaceMarine argument_Marine_cast = (SpaceMarine) argument_Marine;
            long id = Long.parseLong(argument_id_string);
            if (dataBase.selectOwner(id) != user) throw new PermissionException("You don't have permission to do that");
            dataBase.updateSpaceMarine(id, argument_Marine_cast);
            collectionManager.updateCollection(dataBase.selectCollection());
            ResponseOutputter.appendLn("Space marine updated successfully!");
            return true;
        } catch (WrongAmountOfArgumentsException | NotAuthorizedException | DataBaseNotUpdatedException | SQLException |
                 UserNotFoundException | PermissionException e) {
            ResponseOutputter.appendError(e.getMessage());
        } catch (NumberFormatException e) {
            ResponseOutputter.appendError("id must be Integer!");
        } catch (IndexOutOfBoundsException e) {
            ResponseOutputter.appendError("Element with this id does not exist!");
        }
        return false;
    }
    }

