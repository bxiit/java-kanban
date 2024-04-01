import manager.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {
    private static File file;
    private static FileBackedTaskManager manager;

    @BeforeEach
    public void init() {
        try {
            file = File.createTempFile("tempfile", ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldReturnTrueWhenFileToLoadFromIsEmpty() {
        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> FileBackedTaskManager.loadFromFile(file)
        );

        assertTrue(exception.getMessage().contains("Файл пуст"));
    }

    @Test
    public void shouldReturnTrueIfFileIsEmpty() throws IOException {
        FileBackedTaskManager emptyManager = new FileBackedTaskManager(file);
        List<String> lines = Files.readAllLines(file.toPath());

        assertTrue(lines.isEmpty());
    }

    @Test
    public void shouldBeEqualIfTasksAndHistorySuccessfullyLoadedFromFile() throws IOException {
        Task task = new Task("task", "task desc", Status.NEW);
        task.setId(1);

        Epic epic = new Epic("epic", "epic desc");
        epic.setId(2);

        SubTask subTask = new SubTask("subtask", "subtask desc", epic.getId(), Status.NEW);
        subTask.setId(3);
        Files.writeString(file.toPath(), "id,type,name,status,description,epic\n", StandardOpenOption.APPEND);
        Files.writeString(file.toPath(), "1,TASK,task,NEW,task desc,\n", StandardOpenOption.APPEND);
        Files.writeString(file.toPath(), "2,EPIC,epic,NEW,epic desc,\n", StandardOpenOption.APPEND);
        Files.writeString(file.toPath(), "3,SUBTASK,subtask,NEW,subtask desc,2,\n", StandardOpenOption.APPEND);
        Files.writeString(file.toPath(), "\n", StandardOpenOption.APPEND);
        Files.writeString(file.toPath(), "1,2,3", StandardOpenOption.APPEND);

        manager = FileBackedTaskManager.loadFromFile(file);

        Task taskFromFile = manager.getTaskById(task.getId());
        Epic epicFromFile = manager.getEpicById(epic.getId());
        SubTask subTaskFromFile = manager.getSubTaskById(subTask.getId());

        assertEquals(task, taskFromFile);
        assertEquals(epic, epicFromFile);
        assertEquals(subTask, subTaskFromFile);

        List<Task> history = manager.getHistory();
        assertEquals(task, history.getFirst());
        assertEquals(epic, history.get((int) (epic.getId() - 1)));
        assertEquals(subTask, history.getLast());
    }

    @Test
    public void shouldBeEqualIfTasksAndHistorySuccessfullyLoadedToFile() throws IOException {
        manager = new FileBackedTaskManager(file);
        Task task = new Task("task", "task desc", Status.NEW);
        manager.addTask(task);
        Epic epic = new Epic("epic", "epic desc");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("subtask", "subtask desc", epic.getId(), Status.NEW);
        manager.addSubTask(subTask);

        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());

        List<String> lines = Files.readAllLines(file.toPath());

        assertEquals("1,TASK,task,NEW,task desc,", lines.get(Math.toIntExact(task.getId())));
        assertEquals("2,EPIC,epic,NEW,epic desc,", lines.get(Math.toIntExact(epic.getId())));
        assertEquals("3,SUBTASK,subtask,NEW,subtask desc,2", lines.get(Math.toIntExact(subTask.getId())));

        assertEquals("1,2", lines.getLast());
    }

    @AfterEach
    public void destroy() {
        if (file.delete()) {
            System.out.println("Temp file deleted");
        }
    }
}