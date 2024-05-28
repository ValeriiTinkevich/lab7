package common.interaction;

import common.data.User;

import java.io.Serializable;

public class Request implements Serializable {
    String commandName;
    Serializable commandArgument;
    int userID;

    public Request() {
        this("","", -1);
    }

    public Request(String commandName, Serializable commandArgument, int userID) {
        this.commandName = commandName;
        this.commandArgument = commandArgument;
        this.userID = userID;
    }

    public String getCommandName() {
        return commandName;
    }

    public Serializable getCommandArgument() {
        return commandArgument;
    }

    public boolean isEmpty() {
        return commandName.isEmpty() && (commandArgument == null);
    }

    public int getUserID() {
        return userID;
    }

    @Override
    public String toString() {
        return "Request[" + commandName + ", " + commandArgument.toString() + "]";
    }

}
