package server.utility;

import common.interaction.Response;

import java.net.Socket;
import java.util.concurrent.ForkJoinPool;

public class Responser {
    private ForkJoinPool pool;
    private Socket socket;


    public Responser(Socket socket, int nThreads) {
        pool = new ForkJoinPool(nThreads);
        this.socket = socket;
    }

    public void send(Response response) {
        ResponserTask task = new ResponserTask(response, socket);
        pool.execute(task);
    }

}
