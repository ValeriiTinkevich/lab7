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
 * Shows filtered collection by less health.
 */
public class FilterLessThanHealthCommand extends AbstractCommand{
    CollectionManager collectionManager;
    DataBase dataBase;

    /**
     * Filter_less_than command constructor.
     * @param collectionManager Collection manager for filter_less_than command.
     */
    public FilterLessThanHealthCommand(CollectionManager collectionManager, DataBase dataBase) {
        super("filter_less_than_health", "Prints all space marines with health less than input");
        this.collectionManager = collectionManager;
        this.dataBase = dataBase;
    }

    /**
     * Show filtered elements that have less health than value.
     *
     * @param argument The argument passed to the command.
     * @return the response of right execution.
     */
    @Override
    public boolean execute(int user, Serializable argument) {
        try {
            if (user == -1) throw new NotAuthorizedException();
            if (!(argument instanceof String)) throw new WrongAmountOfArgumentsException();
            String HealthAskedStr = (String) argument;
            Integer healthAsked = Integer.parseInt(HealthAskedStr);
            for(SpaceMarine spaceMarine: collectionManager.getSpaceMarineCollection()) {
                if (spaceMarine.getHealth() < healthAsked) {
                    ResponseOutputter.appendLn(spaceMarine.toString() + "\nOwner: " +
                            dataBase.selectUsername(spaceMarine.getCreatedByUser())+ "\n===============");
                }
            }
            return true;
        } catch (WrongAmountOfArgumentsException e) {
            ResponseOutputter.appendError(e.getMessage());
            return false;
        } catch (NotAuthorizedException | SQLException | UserNotFoundException e) {
            ResponseOutputter.appendError(e.getMessage());
        }
        return false;
    }
}

