package server.utility;

import common.interaction.*;
import server.Server;
import server.commands.AuthCommand;
import server.commands.ICommand;
import server.commands.UpdateByIdCommand;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    private Socket socket;
    private ForkJoinPool responsePool;
    public static final Logger logger = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket socket, ForkJoinPool responsePool) {
        this.socket = socket;
        this.responsePool = responsePool;
    }

    public ResponseResult executeCommand(String commandName, Serializable commandArgument, int UID) {
        ICommand command = CommandManager.getCommand(commandName);
        if(command == null){
            ResponseOutputter.appendLn("Command '" + command + "' was not found. Try to write 'help' for more info.");
            return ResponseResult.ERROR;
        } else if(command.getName().equals("auth") && command.execute(UID, commandArgument)){
            return ResponseResult.AUTH;
        } else {
            if(command.execute(UID, commandArgument)) {
                CommandManager.addToHistory(commandName);
                return ResponseResult.OK;
            }
            else return ResponseResult.ERROR;
        }
    }

    public int executeAuth(String commandName, Serializable commandArgument, int UID) {
        ICommand command = CommandManager.getCommand(commandName);
        AuthCommand authCommand = (AuthCommand) command;
        return authCommand.executeAuth(commandArgument);
    }

    public boolean executeUpdate(String commandName, Serializable spaceMarineId, Serializable spaceMarine, int UID ){
        ICommand command = CommandManager.getCommand(commandName);
        UpdateByIdCommand updateByIdCommand = (UpdateByIdCommand) command;
        return updateByIdCommand.execute(UID, spaceMarineId, spaceMarine);

    }


    @Override
    public void run() {
        try (
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        ) {
            while (!socket.isClosed()) {
                try {
                    Response response = null;
                    Request request = (Request) input.readObject();
                    logger.info("Received request: " + request.getCommandName());


                    switch (request.getCommandName()) {
                        case "exit" -> {
                            response = new Response(ResponseResult.SERVER_EXIT, ResponseOutputter.getAndClear());
                            Server.stopServer();
                        }
                        case "auth" -> {
                            int userIDAuth = executeAuth("auth", request.getCommandArgument(), request.getUserID());
                            if (userIDAuth != -1)
                                response = new ResponseAuth(ResponseResult.AUTH, ResponseOutputter.getAndClear(), userIDAuth);
                            else
                                response = new ResponseAuth(ResponseResult.ERROR, ResponseOutputter.getAndClear(), userIDAuth);
                        }
                        case "update" -> {
                            if(request instanceof UpdateRequest) {
                                UpdateRequest updateRequest = (UpdateRequest) request;
                                if (executeUpdate(updateRequest.getCommandName(),
                                        updateRequest.getId(),
                                        updateRequest.getCommandArgument(),
                                        request.getUserID())) {
                                    response = new Response(ResponseResult.OK, ResponseOutputter.getAndClear());
                                } else response = new Response(ResponseResult.ERROR, ResponseOutputter.getAndClear());
                            }
                        }
                        default -> {
                            ResponseResult responseResult = executeCommand(
                                    request.getCommandName(),
                                    request.getCommandArgument(), request.getUserID());
                            response = new Response(responseResult, ResponseOutputter.getAndClear());
                        }
                    }
                    Response finalResponse = response;
                    responsePool.execute(() -> {
                        try {
                            output.writeObject(finalResponse);
                            output.flush(); // Ensure the data is sent
                            System.out.println("Sent response: " + finalResponse.getResponseResult() + " for " + request.getCommandName());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                } catch (EOFException e) {
                    System.out.println("Client disconnected.");
                    break;
                } catch (IOException | ClassNotFoundException e) {
                    logger.severe("Client has disconnected from the server!");
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}