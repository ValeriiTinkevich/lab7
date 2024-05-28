package server.utility;

import common.interaction.Request;
import common.interaction.Response;
import common.interaction.ResponseAuth;
import common.interaction.ResponseResult;
import server.commands.AuthCommand;
import server.commands.ICommand;

import java.io.Serializable;

public class RequestHandler {
    private final CommandManager commandManager;

    public RequestHandler(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public Response handle(Request request){
        commandManager.addToHistory(request.getCommandName());
        if (request.getCommandName().equals("auth")){
            int userIDAuth = executeAuth("auth", request.getCommandArgument(), request.getUserID());
            if (userIDAuth != -1) return new ResponseAuth(ResponseResult.AUTH, ResponseOutputter.getAndClear(), userIDAuth);
            else return new ResponseAuth(ResponseResult.ERROR, ResponseOutputter.getAndClear(), userIDAuth);
        }
        ResponseResult responseResult = executeCommand(
                request.getCommandName(),
                request.getCommandArgument(), request.getUserID());
        return new Response(responseResult, ResponseOutputter.getAndClear());
    }

    /**
     * Executes a command from a request.
     *
     * @param commandName Name of command.
     * @param commandArgument Serializable argument for command.
     * @return Command execute status.
     */
    public ResponseResult executeCommand(String commandName, Serializable commandArgument, int UID) {
        ICommand command = commandManager.commands.get(commandName);
        if(command == null){
            ResponseOutputter.appendLn("Command '" + command + "' was not found. Try to write 'help' for more info.");
            return ResponseResult.ERROR;
        } else if(command.getName().equals("auth") && command.execute(UID, commandArgument)){
            return ResponseResult.AUTH;
        } else {
            if(command.execute(UID, commandArgument)) return ResponseResult.OK;
            else return ResponseResult.ERROR;
        }
    }

    public int executeAuth(String commandName, Serializable commandArgument, int UID) {
        ICommand command = commandManager.commands.get(commandName);
        AuthCommand authCommand = (AuthCommand) command;
        return authCommand.executeAuth(commandArgument);
    }


}
