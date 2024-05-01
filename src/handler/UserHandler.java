package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.List;

public class UserHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public UserHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        if (getMethod(h) != HttpMethods.GET) {
            sendNotFound(h);
        }
        String path = h.getRequestURI().getPath();
        switch (path) {
            case "/history": {
                List<Task> history = manager.getHistory();
                String historyJson = gson.toJson(history);
                sendText(h, 200, historyJson);
                break;
            }
            case "/prioritized": {
                List<Task> prioritizedTasks = manager.getPrioritizedTasks();
                String prioritizedTasksJson = gson.toJson(prioritizedTasks);
                sendText(h, 200, prioritizedTasksJson);
                break;
            }
            default: {
                sendNotFound(h);
            }
        }
    }
}
