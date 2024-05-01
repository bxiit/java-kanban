package server;

import com.sun.net.httpserver.HttpServer;
import handler.TaskHandler;
import handler.UserHandler;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final TaskManager manager;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
    }

    public void listenAndServe() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
            httpServer.createContext("/tasks", new TaskHandler(manager));
            httpServer.createContext("/epics", new TaskHandler(manager));
            httpServer.createContext("/subtasks", new TaskHandler(manager));
            httpServer.createContext("/history", new UserHandler(manager));
            httpServer.createContext("/prioritized", new UserHandler(manager));
            httpServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopServer() {
        httpServer.stop(0);
    }
}
