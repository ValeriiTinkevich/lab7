package server.commands;


import common.exceptions.NotAuthorizedException;
import common.exceptions.WrongAmountOfArgumentsException;
import server.utility.ResponseOutputter;

import java.io.Serializable;

/**
 * Command for executing a script from a txt file.
 */
public class ExecuteScriptCommand extends AbstractCommand {
    public ExecuteScriptCommand() {
        super("execute", "Executes a script from given file");
    }

    @Override
    public boolean execute(int user, Serializable argument) {
        try {
            if (user == -1) throw new NotAuthorizedException();
            String strarg = (String) argument;
            if (strarg.isEmpty()) throw new WrongAmountOfArgumentsException();
            ResponseOutputter.appendLn("Execute script '" + strarg + "'...");
            return true;
        } catch (WrongAmountOfArgumentsException exception) {
            ResponseOutputter.appendLn("Usage: '" + getName() + " " + "<Script name>" + "'");
        } catch (NotAuthorizedException e) {
            ResponseOutputter.appendError(e.getMessage());
        }
        return false;
    }
    }
