package client;

import client.utility.AuthInputManager;
import client.utility.UserHandler;
import common.exceptions.ConnectionErrorException;
import common.exceptions.IncorrectInputInScriptException;
import common.exceptions.NotInDeclaredLimitsException;
import common.exceptions.WrongAmountOfArgumentsException;
import common.utility.Outputter;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class App {
    public static final String PS1 = "$ ";
    public static final String PS2 = "> ";
    private static final int RECONNECTION_TIMEOUT = 5 * 2000;
    private static final int MAX_RECONNECTION_ATTEMPTS = 6;
    private static String host;
    private static int port;


    private static boolean initializeConnectionAddress(String[] hostAndPortArgs) {
        try {
            if (hostAndPortArgs.length != 2) throw new WrongAmountOfArgumentsException();
            host = hostAndPortArgs[0];
            port = Integer.parseInt(hostAndPortArgs[1]);
            if (port < 0) throw new NotInDeclaredLimitsException();
            return true;
        } catch (WrongAmountOfArgumentsException exception) {
            String jarName = new java.io.File(App.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath())
                    .getName();
            Outputter.printLn("Usage: 'java -jar " + jarName + " <host> <port>'");
        } catch (NumberFormatException exception) {
            Outputter.printError("The port must be represented by a number!");
        } catch (NotInDeclaredLimitsException exception) {
            Outputter.printError("The port cannot be negative!");
        }
        return false;
    }


    public static void main(String[] args) throws IncorrectInputInScriptException, IOException {
        //if (!initializeConnectionAddress(args)) return;
        host = "localhost";
        port = 64532;
        Scanner userScanner = new Scanner(System.in);
        UserHandler userHandler = new UserHandler(userScanner);
        Client client = new Client(userHandler, port, RECONNECTION_TIMEOUT, MAX_RECONNECTION_ATTEMPTS, host);
        client.run();
        userScanner.close();
    }
}