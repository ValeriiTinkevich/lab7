package client.utility;

import common.exceptions.EmptyArgumentException;
import common.exceptions.IncorrectInputInScriptException;
import common.exceptions.NotInDeclaredLimitsException;
import common.exceptions.SecondEntryException;
import common.utility.Outputter;

import java.io.Console;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class AuthInputManager {
    private Scanner userScanner;
    private Console systemConsole;
    private boolean consoleInstance = true;
    private boolean scriptMode = false;


    public void setScriptMode() {
        this.scriptMode = true;
    }

    public void setUserMode() {
        this.scriptMode = false;
    }

    public AuthInputManager(Scanner scanner) {
        this.userScanner = scanner;
    }

    public String askUsername() throws IncorrectInputInScriptException {
        String username;
        while (true) {
            Outputter.print("Enter name: ");
            try {
                username = userScanner.nextLine().trim();
                if (scriptMode) Outputter.printLn(username);
                if (username.isEmpty()) throw new EmptyArgumentException();
                if (username.length() < 4) throw new NotInDeclaredLimitsException();
                break;
            } catch (EmptyArgumentException e) {
                Outputter.printError("The username can't be empty!");
                Outputter.printLn(e.getMessage());
                if (scriptMode) throw new IncorrectInputInScriptException();

            } catch (NoSuchElementException e) {
                Outputter.printError("The username can't be loaded or recognized");
                if (scriptMode) throw new IncorrectInputInScriptException();
                if (!userScanner.hasNext()) {
                    Outputter.printError("Ctrl + D Caused exit!");
                    System.exit(2);
                }
            } catch (IllegalStateException e) {
                Outputter.printError("Unexpected error!");
            } catch (NotInDeclaredLimitsException e) {
                Outputter.printError("Username must be longer than 4 characters");
            }
        }
        return username;

    }

    public void setSystemConsole () {
        Console console = System.console();
        if (console == null) {
            System.out.println("Couldn't get Console instance");
            consoleInstance = false;
        } else {
            this.systemConsole = console;
            consoleInstance = true;
        }
    }

    public String askNewPassword() throws IncorrectInputInScriptException {
        if (consoleInstance && !scriptMode) {
            while (true) {
                try {
                    char[] passwordArray = systemConsole.readPassword("Enter your password: ");
                    if(passwordArray.length == 0) throw new EmptyArgumentException();
                    if (passwordArray.length < 6) throw new NotInDeclaredLimitsException();
                    String password = new String(passwordArray);
                    passwordArray = systemConsole.readPassword("Confirm your password:");
                    String passwordSecondEntry = new String(passwordArray);
                    if (!password.equals(passwordSecondEntry)) throw new SecondEntryException();
                    else return password;
                } catch (SecondEntryException e) {
                    Outputter.printError("Couldn't confirm your password!");
                } catch (EmptyArgumentException e) {
                    Outputter.printError("Password can't be empty");
                } catch (NotInDeclaredLimitsException e) {
                    Outputter.printError("Password must be 6 characters or longer!");
                }
            }
        } else {
            String password;
            while (true) {
                Outputter.print("Enter password: ");
                try {
                    password = userScanner.nextLine().trim();
                    if (scriptMode) Outputter.printLn(password);
                    if (password.isEmpty()) throw new EmptyArgumentException();
                    if (password.length() < 6) throw new NotInDeclaredLimitsException();
                    Outputter.print("Confirm your password: ");
                    String secondEntry = userScanner.nextLine().trim();
                    if(!password.equals(secondEntry)) throw new SecondEntryException();
                    break;
                } catch (EmptyArgumentException e) {
                    Outputter.printError("The password can't be empty!");
                    Outputter.printLn(e.getMessage());
                    if (scriptMode) throw new IncorrectInputInScriptException();

                } catch (NoSuchElementException e) {
                    Outputter.printError("The password can't be loaded or recognized");
                    if (scriptMode) throw new IncorrectInputInScriptException();
                    if (!userScanner.hasNext()) {
                        Outputter.printError("Ctrl + D Caused exit!");
                        System.exit(2);
                    }
                } catch (IllegalStateException e) {
                    Outputter.printError("Unexpected error!");
                } catch (NotInDeclaredLimitsException e) {
                    Outputter.printError("Password must be 6 characters or longer!");
                } catch (SecondEntryException e) {
                    Outputter.printError("Couldn't confirm your password!");
                }
            }
            return password;
        }
    }
    public String askPassword() throws IncorrectInputInScriptException {
        if (consoleInstance && !scriptMode) {
            while (true) {
                try {
                    char[] passwordArray = systemConsole.readPassword("Enter your password: ");
                    if(passwordArray.length == 0) throw new EmptyArgumentException();
                    if (passwordArray.length < 6) throw new NotInDeclaredLimitsException();
                    return new String(passwordArray);
                } catch (EmptyArgumentException e) {
                    Outputter.printError("Password can't be empty");
                } catch (NotInDeclaredLimitsException e) {
                    Outputter.printError("Password must be 6 characters or longer!");
                }
            }
        } else {
            String password;
            while (true) {
                Outputter.print("Enter password: ");
                try {
                    password = userScanner.nextLine().trim();
                    if (scriptMode) Outputter.printLn(password);
                    if (password.isEmpty()) throw new EmptyArgumentException();
                    if (password.length() < 6) throw new NotInDeclaredLimitsException();
                    break;
                } catch (EmptyArgumentException e) {
                    Outputter.printError("The password can't be empty!");
                    Outputter.printLn(e.getMessage());
                    if (scriptMode) throw new IncorrectInputInScriptException();

                } catch (NoSuchElementException e) {
                    Outputter.printError("The password can't be loaded or recognized");
                    if (scriptMode) throw new IncorrectInputInScriptException();
                    if (!userScanner.hasNext()) {
                        Outputter.printError("Ctrl + D Caused exit!");
                        System.exit(2);
                    }
                } catch (IllegalStateException e) {
                    Outputter.printError("Unexpected error!");
                } catch (NotInDeclaredLimitsException e) {
                    Outputter.printError("Password must be 6 characters or longer!");
                }
            }
            return password;
        }
    }


    }

