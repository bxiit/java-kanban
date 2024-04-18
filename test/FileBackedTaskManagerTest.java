import manager.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private static File file;

    private static FileBackedTaskManager fileBackedTaskManager;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            file = File.createTempFile("tempfile", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new FileBackedTaskManager(file);
    }

    @Test
    void shouldReturnTrueWhenFileToLoadFromIsEmpty() throws IOException {
        file = Files.createTempFile("test", ".csv").toFile();
        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> FileBackedTaskManager.loadFromFile(file)
        );

        assertTrue(exception.getMessage().contains("Файл пуст"));
    }

    @Test
    void shouldReturnTrueIfFileIsEmpty() throws IOException {
        file = Files.createTempFile("test", ".csv").toFile();
        FileBackedTaskManager emptyManager = new FileBackedTaskManager(file);
        List<String> lines = Files.readAllLines(file.toPath());

        assertTrue(lines.isEmpty());
    }

    @Test
    void shouldBeEqualIfTasksAndHistorySuccessfullyLoadedFromFile() throws IOException {
        file = Files.createTempFile("test", ".csv").toFile();
        Task task = new Task(1L, "task", "task desc", Status.NEW,
                Duration.ofMinutes(40), LocalDateTime.of(
                2024, 4, 12, 20, 47
        ));

        Epic epic = new Epic(2L, "epic", "epic desc", Status.NEW,
                Duration.ofHours(1).plusMinutes(30), LocalDateTime.of(
                2025, 12, 12, 12, 12
        ));
        epic.addSubTaskId(3);

        SubTask subTask = new SubTask(3L, "subtask", "subtask desc", epic.getId(), Status.NEW,
                Duration.ofHours(1).plusMinutes(30), LocalDateTime.of(
                2025, 12, 12, 12, 12
        ));

        Files.writeString(file.toPath(), "id,type,name,status,description,epic,duration,start_time\n",
                StandardOpenOption.APPEND);
        Files.writeString(file.toPath(), "1,TASK,task,NEW,task desc,,00:00:40,12.04.2024 - 20:47\n",
                StandardOpenOption.APPEND);
        Files.writeString(file.toPath(), "2,EPIC,epic,NEW,epic desc,,00:01:30,12.12.2025 - 12:12\n",
                StandardOpenOption.APPEND);
        Files.writeString(file.toPath(), "3,SUBTASK,subtask,NEW,subtask desc,2,00:01:30,12.12.2025 - 12:12\n",
                StandardOpenOption.APPEND);
        Files.writeString(file.toPath(), "\n", StandardOpenOption.APPEND);
        Files.writeString(file.toPath(), "1,2,3", StandardOpenOption.APPEND);

        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        // Чтобы получения задач по айди для проверки не влияли на историю задач
        List<Task> history = fileBackedTaskManager.getHistory();

        Task taskFromFile = fileBackedTaskManager.getTaskById(task.getId()).orElseThrow();
        Epic epicFromFile = fileBackedTaskManager.getEpicById(epic.getId()).orElseThrow();
        SubTask subTaskFromFile = fileBackedTaskManager.getSubTaskById(subTask.getId()).orElseThrow();

        assertEquals(task, taskFromFile);
        assertEquals(epic, epicFromFile);
        assertEquals(subTask, subTaskFromFile);


        assertEquals(task, history.getFirst());
        assertEquals(epic, history.get((int) (epic.getId() - 1)));
        assertEquals(subTask, history.getLast());
        assertEquals(3, history.size());
        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(file));
    }

    @Test
    void shouldBeEqualIfTasksAndHistorySuccessfullyLoadedToFile() throws IOException {
        fileBackedTaskManager = new FileBackedTaskManager(file);
        Task task = new Task("task", "task desc", Status.NEW);
        fileBackedTaskManager.addTask(task);
        Epic epic = new Epic("epic", "epic desc");
        fileBackedTaskManager.addEpic(epic);
        SubTask subTask = new SubTask("subtask", "subtask desc", epic.getId(), Status.NEW);
        fileBackedTaskManager.addSubTask(subTask);

        fileBackedTaskManager.getTaskById(task.getId());
        fileBackedTaskManager.getEpicById(epic.getId());

        List<String> lines = Files.readAllLines(file.toPath());

        assertEquals("1,TASK,task,NEW,task desc,,,", lines.get(Math.toIntExact(task.getId())));
        assertEquals("2,EPIC,epic,NEW,epic desc,,,", lines.get(Math.toIntExact(epic.getId())));
        assertEquals("3,SUBTASK,subtask,NEW,subtask desc,2,,", lines.get(Math.toIntExact(subTask.getId())));

        assertEquals("1,2", lines.getLast());
    }

    @Test
    void shouldBeEqualIfTasksAndHistorySuccessfullyLoadedToFile_WithDurationAndStartTime() throws IOException {
        fileBackedTaskManager = new FileBackedTaskManager(file);
        Task task = new Task("task", "task desc", Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 4, 15,
                        0, 5));
        fileBackedTaskManager.addTask(task);
        Epic epic = new Epic("epic", "epic desc");
        fileBackedTaskManager.addEpic(epic);
        SubTask subTask = new SubTask("subtask", "subtask desc", epic.getId(), Status.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2025, 4, 15,
                        0, 5));
        fileBackedTaskManager.addSubTask(subTask);

        fileBackedTaskManager.getTaskById(task.getId());
        fileBackedTaskManager.getEpicById(epic.getId());

        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals("1,TASK,task,NEW,task desc,,00:00:30,15.04.2024 - 00:05", lines.get(Math.toIntExact(task.getId())));
        assertEquals("2,EPIC,epic,NEW,epic desc,,00:01:00,15.04.2025 - 00:05", lines.get(Math.toIntExact(epic.getId())));
        assertEquals("3,SUBTASK,subtask,NEW,subtask desc,2,00:01:00,15.04.2025 - 00:05", lines.get(Math.toIntExact(subTask.getId())));

        assertEquals("1,2", lines.getLast());
    }

    @Test
    void loadFromFile_shouldThrowRuntimeExceptionWhenSaveFileDeleted() throws IOException {
        file = Files.createTempFile("test_exception", ".csv").toFile();
        boolean deleted = file.delete();

        assertTrue(deleted);
        assertThrows(RuntimeException.class, () -> FileBackedTaskManager.loadFromFile(file));
    }

    @Test
    void loadFromFile_shouldThrowRuntimeExceptionWhenFileIsEmpty() throws IOException {
        file = Files.createTempFile("test_exception", ".csv").toFile();

        assertThrows(RuntimeException.class, () -> FileBackedTaskManager.loadFromFile(file));
    }

    @AfterEach
    public void destroy() {
        if (file.delete()) {
            System.out.println("Temp file deleted");
        }
    }
}