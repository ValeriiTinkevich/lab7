package server.utility;


import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Receiver extends Thread {
    private ThreadPoolExecutor executor;
    private Socket socket;
    private CommandExecuter commandExecuter;

    public Receiver(Socket socket, CommandExecuter commandExecuter) {
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        this.socket = socket;
        this.commandExecuter = commandExecuter;
    }

    @Override
    public void run() {
        ReceiverTask task = new ReceiverTask(socket, commandExecuter);
        executor.execute(task);
    }


}
