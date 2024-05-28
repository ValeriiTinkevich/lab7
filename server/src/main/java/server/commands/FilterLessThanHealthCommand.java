package server.commands;


import common.data.SpaceMarine;
import common.data.User;
import common.exceptions.NotAuthorizedException;
import common.exceptions.WrongAmountOfArgumentsException;
import common.utility.Outputter;
import server.utility.CollectionManager;
import server.utility.ResponseOutputter;

import java.io.Serializable;

/**
 * Shows filtered collection by less health.
 */
public class FilterLessThanHealthCommand extends AbstractCommand{
    CollectionManager collectionManager;

    /**
     * Filter_less_than command constructor.
     * @param collectionManager Collection manager for filter_less_than command.
     */
    public FilterLessThanHealthCommand(CollectionManager collectionManager) {
        super("filter_less_than_health", "Prints all space marines with health less than input");
        this.collectionManager = collectionManager;
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
                    ResponseOutputter.appendLn(spaceMarine + "\n===============");
                }
            }
            return true;
        } catch (WrongAmountOfArgumentsException e) {
            ResponseOutputter.appendError(e.getMessage());
            return false;
        } catch (NotAuthorizedException e) {
            ResponseOutputter.appendError(e.getMessage());
        }
        return false;
    }
}

