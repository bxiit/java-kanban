package server;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private static HttpTaskServer server;
    private static TaskManager manager;
    private static Gson gson;
    private static HttpClient client;

    @BeforeAll
    static void setUpGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .create();
        client = HttpClient.newHttpClient();
    }

    @BeforeEach
    void setUpServer() {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.listenAndServe();
    }

    @Test
    void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic 1 desc");
        String epicJson = gson.toJson(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic 1", epicsFromManager.getFirst().getName(), "Некорректное имя эпика");
    }

    @Test
    void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic 1 desc");
        String epicJson = gson.toJson(epic);

        URI urlPostEpics = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlPostEpics)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic 1", epicsFromManager.getFirst().getName(), "Некорректное имя эпика");

        SubTask subTask = new SubTask("Subtask 1", "Subtask 1 desc", 1, Status.NEW);
        String subtaskJson = gson.toJson(subTask);
        URI urlPostSubtasks = URI.create("http://localhost:8080/subtasks");
        HttpRequest requestPostSubtasks = HttpRequest.newBuilder()
                .uri(urlPostSubtasks)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response1 = client.send(requestPostSubtasks, ofString());
        assertEquals(200, response1.statusCode());

        List<SubTask> subtasksFromManager = manager.getAllSubTasks();
        assertNotNull(subtasksFromManager, "Сабтаски не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество сабтасок");
        assertEquals("Subtask 1", subtasksFromManager.getFirst().getName(), "Некорректное имя сабтаски");
    }

    @Test
    void testGetTask() throws IOException, InterruptedException {
        fillManager(manager);
        URI uri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, ofString());

        assertEquals(200, response.statusCode());
        Task taskJson = gson.fromJson(response.body(), Task.class);
        assertEquals("task1", taskJson.getName());
    }

    @Test
    void testGetEpic() throws IOException, InterruptedException {
        fillManager(manager);
        URI uri = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, ofString());

        assertEquals(200, response.statusCode());
        Epic epicJson = gson.fromJson(response.body(), Epic.class);
        assertEquals("epic1", epicJson.getName());
    }

    @Test
    void testGetSubTask() throws IOException, InterruptedException {
        fillManager(manager);
        URI uri = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, ofString());

        assertEquals(200, response.statusCode());

        SubTask subTaskJson = gson.fromJson(response.body(), SubTask.class);
        assertEquals("subtask1", subTaskJson.getName());
        assertEquals(2, subTaskJson.getEpicId());
    }

    // UPDATE
    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(task);

        task.setName("Updated Task");
        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, ofString());
        assertEquals(200, response.statusCode());

        Optional<Task> updatedTask = manager.getTaskById(task.getId());
        assertTrue(updatedTask.isPresent());
        assertEquals("Updated Task", updatedTask.get().getName());
    }

    @Test
    void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic 1 desc");
        manager.addEpic(epic);

        epic.setName("Updated Epic");
        String epicJson = gson.toJson(epic);

        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, ofString());
        assertEquals(200, response.statusCode());

        Optional<Epic> updatedEpic = manager.getEpicById(epic.getId());
        assertTrue(updatedEpic.isPresent());
        assertEquals("Updated Epic", updatedEpic.get().getName());
    }

    @Test
    void testUpdateSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("epic1", "epic1");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask 1", "Subtask 1 desc", epic.getId(), Status.NEW);
        manager.addSubTask(subTask);

        subTask.setName("Updated Subtask");
        String subtaskJson = gson.toJson(subTask);

        URI url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, ofString());
        assertEquals(200, response.statusCode());

        Optional<SubTask> updatedSubTask = manager.getSubTaskById(subTask.getId());
        assertTrue(updatedSubTask.isPresent());
        assertEquals("Updated Subtask", updatedSubTask.get().getName());
    }

    // DELETE
    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        fillManager(manager);
        URI uri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        List<Task> allTasksUntilDelete = manager.getAllTasks();
        assertEquals(1, allTasksUntilDelete.size());

        HttpResponse<String> response = client.send(request, ofString());
        assertEquals(200, response.statusCode());

        List<Task> allTasksAfterDelete = manager.getAllTasks();
        assertEquals(0, allTasksAfterDelete.size());
    }

    @Test
    void testDeleteEpic() throws IOException, InterruptedException {
        fillManager(manager);
        URI uri = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        List<Epic> allEpicsUntilDelete = manager.getAllEpics();
        assertEquals(2, allEpicsUntilDelete.size());

        HttpResponse<String> response = client.send(request, ofString());
        assertEquals(200, response.statusCode());

        List<Epic> allEpicsAfterDelete = manager.getAllEpics();
        assertEquals(1, allEpicsAfterDelete.size());
    }

    @Test
    void testDeleteSubTask() throws IOException, InterruptedException {
        fillManager(manager);
        URI uri = URI.create("http://localhost:8080/subtasks/4");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        List<SubTask> allSubTasksUntilDelete = manager.getAllSubTasks();
        assertEquals(4, allSubTasksUntilDelete.size());

        HttpResponse<String> response = client.send(request, ofString());
        assertEquals(200, response.statusCode());

        List<SubTask> allSubTasksAfterDelete = manager.getAllSubTasks();
        assertEquals(3, allSubTasksAfterDelete.size());
    }

    @Test
    void testGetHistory() throws IOException, InterruptedException {
        Type type = new TypeToken<List<Task>>() {
        }.getType();

        fillManager(manager);
        manager.getTaskById(1);
        manager.getSubTaskById(4);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/history"))
                .build();
        HttpResponse<String> response = client.send(request, ofString());
        assertEquals(200, response.statusCode());

        List<Task> historyJson = gson.fromJson(response.body(), type);
        assertEquals(2, historyJson.size());
        assertEquals("task1", historyJson.getFirst().getName());
        assertEquals("subtask2", historyJson.getLast().getName());
    }

    @Test
    void testGetPrioritizedTasks() throws IOException, InterruptedException {
        fillManager(manager);
        Type type = new TypeToken<List<Task>>() {
        }.getType();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, ofString());
        assertEquals(200, response.statusCode());

        List<Task> prioritizedTasks = gson.fromJson(response.body(), type);
        assertEquals(2, prioritizedTasks.size());
        assertEquals("subtask3", prioritizedTasks.getFirst().getName());
        assertEquals("subtask4", prioritizedTasks.getLast().getName());
    }

    private void fillManager(TaskManager manager) {
        if (!manager.getAllTasks().isEmpty()) {
            return;
        }
        Task task = new Task("task1", "task1 desc", Status.NEW);
        manager.addTask(task);
        Epic epic1 = new Epic("epic1", "epic1 desc");
        manager.addEpic(epic1);
        SubTask subTask1 = new SubTask("subtask1", "subtask1 desc", epic1.getId(), Status.NEW);
        SubTask subTask2 = new SubTask("subtask2", "subtask2 desc", epic1.getId(), Status.NEW);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        Epic epic2 = new Epic("epic2", "epic2 desc");
        manager.addEpic(epic2);
        SubTask subTask3 = new SubTask("subtask3", "subtask3 desc", epic2.getId(), Status.IN_PROGRESS,
                Duration.ofMinutes(40), LocalDateTime.now());
        SubTask subTask4 = new SubTask("subtask4", "subtask4 desc", epic2.getId(), Status.IN_PROGRESS,
                Duration.ofMinutes(30), LocalDateTime.of(
                2025, 4, 30, 15, 0
        ));
        manager.addSubTask(subTask3);
        manager.addSubTask(subTask4);
    }

    @AfterEach
    void shutServer() {
        server.stopServer();
    }

//    @AfterAll
//    static void shutDownServer() {
//        server.stopServer();
//    }
}