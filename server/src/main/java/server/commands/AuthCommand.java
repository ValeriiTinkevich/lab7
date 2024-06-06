package server.commands;

import common.data.User;
import common.exceptions.UserNotFoundException;
import common.exceptions.WrongAmountOfArgumentsException;
import server.databaseinteraction.DataBase;
import server.utility.ResponseOutputter;

import java.io.Serializable;
import java.sql.SQLException;

public class AuthCommand extends AbstractCommand {
    DataBase dataBase;

    public AuthCommand(DataBase dataBase) {
        super("auth", "Authenticates a user");
        this.dataBase = dataBase;
    }

    @Override
    public boolean execute(int user, Serializable argument) {
        return false;
    }

    public int executeAuth(Serializable argument) {
        try {
            if (!(argument instanceof User)) throw new WrongAmountOfArgumentsException();
            User recievedUser = (User) argument;
            int userID = dataBase.selectUserID(recievedUser.getUsername(), recievedUser.getPassword());
            ResponseOutputter.appendLn("User has been authorized with UID: " + userID);
            return userID;
        } catch (WrongAmountOfArgumentsException e) {
            throw new RuntimeException(e);
        } catch (UserNotFoundException e) {
            ResponseOutputter.appendError("User not found!");
        } catch (SQLException e) {
            ResponseOutputter.appendError("Error in the database");
        }
        return -1;
    }

}
