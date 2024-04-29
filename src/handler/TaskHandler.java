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

    private final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange h) {
        HttpMethods method = getMethod(h);
        String[] pathSplit = h.getRequestURI().getPath().split("/");
        switch (method) {
            case POST: {
                if (pathSplit.length == 2) {
                    if ("tasks".equals(pathSplit[1])) {
                        createATask(h, manager::addTask, Task.class);
                    } else if ("epics".equals(pathSplit[1])) {
                        createATask(h, manager::addEpic, Epic.class);
                    } else if ("subtasks".equals(pathSplit[1])) {
                        createATask(h, manager::addSubTask, SubTask.class);
                    } else {
                        sendNotFound(h);
                    }
                } else if (pathSplit.length == 3) {
                    Integer id = extractId(h, pathSplit[2]);
                    if ("tasks".equals(pathSplit[1])) {
                        updateATask(h, id, manager::updateTask, Task.class);
                    } else if ("epics".equals(pathSplit[1])) {
                        updateATask(h, id, manager::updateEpic, Epic.class);
                    } else if ("subtasks".equals(pathSplit[1])) {
                        updateATask(h, id, manager::updateSubTask, SubTask.class);
                    } else {
                        sendNotFound(h);
                    }
                }
                break;
            }
            case GET: {
                if (pathSplit.length == 2 && "tasks".equals(pathSplit[1])) {
                    getAllTheTasks(h, manager::getAllTasks);
                } else if (pathSplit.length == 2 && "epics".equals(pathSplit[1])) {
                    getAllTheTasks(h, manager::getAllEpics);
                } else if (pathSplit.length == 2 && "subtasks".equals(pathSplit[1])) {
                    getAllTheTasks(h, manager::getAllSubTasks);
                } else if (pathSplit.length == 3 && "tasks".equals(pathSplit[1])) {
                    getATaskById(h, pathSplit[2], manager::getTaskById);
                } else if (pathSplit.length == 3 && "epics".equals(pathSplit[1])) {
                    getATaskById(h, pathSplit[2], manager::getEpicById);
                } else if (pathSplit.length == 3 && "subtasks".equals(pathSplit[1])) {
                    getATaskById(h, pathSplit[2], manager::getSubTaskById);
                } else {
                    sendNotFound(h);
                }
                break;
            }
            case DELETE: {
                if (pathSplit.length == 3 && "tasks".equals(pathSplit[1])) {
                    deleteATaskById(h, pathSplit[2], manager::deleteTaskById);
                } else if (pathSplit.length == 3 && "epics".equals(pathSplit[1])) {
                    deleteATaskById(h, pathSplit[2], manager::deleteEpicById);
                } else if (pathSplit.length == 3 && "subtasks".equals(pathSplit[1])) {
                    deleteATaskById(h, pathSplit[2], manager::deleteSubTaskById);
                } else {
                    sendNotFound(h);
                }
                break;
            }
            default: {
                throw new RuntimeException("Неизвестный метод " + method);
            }
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