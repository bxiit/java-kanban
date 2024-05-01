package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public static final String EPICS = "epics";
    public static final String TASKS = "tasks";
    public static final String SUBTASKS = "subtasks";
    public static final int GET_ALL_LENGTH = 2;
    public static final int GET_BY_ID_LENGTH = 3;
    private final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange h) {
        HttpMethods method = getMethod(h);
        String[] pathSplit = h.getRequestURI().getPath().split("/");
        switch (method) {
            case POST -> handlePost(h, pathSplit);
            case GET -> handleGet(h, pathSplit);
            case DELETE -> handleDelete(h, pathSplit);
            default -> throw new RuntimeException("Неизвестный метод " + method);
        }
    }

    // HANDLERS BY METHOD
    private void handlePost(HttpExchange h, String[] pathSplit) {
        switch (pathSplit.length) {
            case GET_ALL_LENGTH -> handlePostCreate(h, pathSplit);
            case GET_BY_ID_LENGTH -> handlePostUpdate(h, pathSplit);
            default -> sendNotFound(h);
        }
    }

    private void handlePostCreate(HttpExchange h, String[] pathSplit) {
        switch (pathSplit[1]) {
            case TASKS -> createATask(h, manager::addTask, Task.class);
            case EPICS -> createATask(h, manager::addEpic, Epic.class);
            case SUBTASKS -> createATask(h, manager::addSubTask, SubTask.class);
            case null, default -> sendNotFound(h);
        }
    }

    private void handlePostUpdate(HttpExchange h, String[] pathSplit) {
        Integer id = extractId(h, pathSplit[GET_ALL_LENGTH]);
        switch (pathSplit[1]) {
            case TASKS -> updateATask(h, id, manager::updateTask, Task.class);
            case EPICS -> updateATask(h, id, manager::updateEpic, Epic.class);
            case SUBTASKS -> updateATask(h, id, manager::updateSubTask, SubTask.class);
            case null, default -> sendNotFound(h);
        }
    }

    private void handleGet(HttpExchange h, String[] pathSplit) {
        switch (pathSplit.length) {
            case GET_ALL_LENGTH -> handleGetAll(h, pathSplit);
            case GET_BY_ID_LENGTH -> handleGetById(h, pathSplit);
            default -> sendNotFound(h);
        }
    }

    private void handleGetById(HttpExchange h, String[] pathSplit) {
        switch (pathSplit[1]) {
            case TASKS -> getATaskById(h, pathSplit[GET_ALL_LENGTH], manager::getTaskById);
            case EPICS -> getATaskById(h, pathSplit[GET_ALL_LENGTH], manager::getEpicById);
            case SUBTASKS -> getATaskById(h, pathSplit[GET_ALL_LENGTH], manager::getSubTaskById);
            default -> sendNotFound(h);
        }
    }

    private void handleGetAll(HttpExchange h, String[] pathSplit) {
        switch (pathSplit[1]) {
            case TASKS -> getAllTheTasks(h, manager::getAllTasks);
            case EPICS -> getAllTheTasks(h, manager::getAllEpics);
            case SUBTASKS -> getAllTheTasks(h, manager::getAllSubTasks);
            default -> sendNotFound(h);
        }
    }

    private void handleDelete(HttpExchange h, String[] pathSplit) {
        if (pathSplit.length != GET_BY_ID_LENGTH) {
            sendNotFound(h);
            return;
        }
        switch (pathSplit[1]) {
            case TASKS -> deleteATaskById(h, pathSplit[GET_ALL_LENGTH], manager::deleteTaskById);
            case EPICS -> deleteATaskById(h, pathSplit[GET_ALL_LENGTH], manager::deleteEpicById);
            case SUBTASKS -> deleteATaskById(h, pathSplit[GET_ALL_LENGTH], manager::deleteSubTaskById);
            default -> sendNotFound(h);
        }
    }

    // CREATE
    private <T> void createATask(HttpExchange h, Function<T, Long> function, Class<T> aClass) {
        T aTask = extractATaskFromBody(h, aClass);
        function.apply(aTask);
        if (((Task) aTask).getId() == null) {
            sendHasInteractions(h);
        }
        sendText(h, 201, aClass.getSimpleName() + " created");
    }

    // GET
    private <T> void getATaskById(HttpExchange h, String stringId, Function<Integer, Optional<T>> function) {
        int id = extractId(h, stringId);
        Optional<T> aTask = function.apply(id);
        aTask.ifPresentOrElse(
                t -> sendText(h, 200, gson.toJson(t)),
                () -> sendNotFound(h)
        );
    }

    private <T> void getAllTheTasks(HttpExchange h, Supplier<T> supplier) {
        T t = supplier.get();
        String allTheTasks = gson.toJson(t);
        sendText(h, 200, allTheTasks);
    }

    // UPDATE
    private <T> void updateATask(HttpExchange h, Integer id, Consumer<T> function, Class<T> aClass) {
        T aTask = extractATaskFromBody(h, aClass);
        function.accept(aTask);
        sendText(h, 200, aClass.getSimpleName() + " updated");
    }

    // DELETE
    private void deleteATaskById(HttpExchange h, String stringId, Consumer<Integer> consumer) {
        Integer id = extractId(h, stringId);
        consumer.accept(id);
        sendText(h, 200, "");
    }

    private Integer extractId(HttpExchange h, String stringId) {
        int id = 0;
        try {
            id = Integer.parseInt(stringId);
        } catch (Exception e) {
            sendNotFound(h);
        }
        return id;
    }

    private <T> T extractATaskFromBody(HttpExchange h, Class<T> aClass) {
        T aTask = null;

        try (InputStream requestBody = h.getRequestBody()) {
            String taskFromReqBody = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
            aTask = gson.fromJson(taskFromReqBody, aClass);
        } catch (IOException e) {
            sendNotFound(h);
        }
        if (aTask == null) {
            sendNotFound(h);
        }
        return aTask;
    }
}