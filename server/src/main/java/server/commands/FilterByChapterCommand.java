package server.commands;


import common.data.Chapter;
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
 * Shows filtered collection by chapter.
 */
public class FilterByChapterCommand extends AbstractCommand{
    CollectionManager collectionManager;
    DataBase dataBase;

    /**
     * Filter_by_chapter command constructor.
     * @param collectionManager Collection manager for filter_by_chapter command.
     */
    public FilterByChapterCommand(CollectionManager collectionManager, DataBase dataBase) {
        super("filter_by_chapter", "Shows elements with chapter == input");
        this.collectionManager = collectionManager;
        this.dataBase = dataBase;
    }


    /**
     * Show filtered collection by chapter.
     *
     * @param argument The argument passed to the command.
     * @return the response of right execution.
     */
    @Override
    public boolean execute(int user, Serializable argument) {
        try {
            if (user == -1) throw new NotAuthorizedException();
            if (!(argument instanceof Chapter)) throw new WrongAmountOfArgumentsException();
            Chapter askedChapter = (Chapter) argument;
            for(SpaceMarine spaceMarine: collectionManager.getSpaceMarineCollection()) {
                if (spaceMarine.getChapter().equals(askedChapter)) {
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
