package server.commands;

import common.data.User;
import common.exceptions.DataBaseNotUpdatedException;
import common.exceptions.WrongAmountOfArgumentsException;
import server.databaseinteraction.DataBase;
import server.utility.ResponseOutputter;

import java.io.Serializable;
import java.sql.SQLException;

public class RegisterCommand extends AbstractCommand {
    DataBase dataBase;


    public RegisterCommand(DataBase dataBase) {
        super("register", "Registers a new user");
        this.dataBase = dataBase;
    }

    @Override
    public boolean execute(int user, Serializable argument) {
        try {
            if (!(argument instanceof User)) throw new WrongAmountOfArgumentsException();
            User userArgument = (User) argument;
            dataBase.insert(userArgument);
            return true;
        } catch (WrongAmountOfArgumentsException e) {
            ResponseOutputter.appendLn("Argument is of wrong type");
            return false;
        } catch (SQLException | DataBaseNotUpdatedException e) {
            ResponseOutputter.appendError("Error in database! Please try again later!");
            return false;
        }
    }
}
