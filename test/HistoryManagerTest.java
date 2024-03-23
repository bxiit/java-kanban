import history.InMemoryHistoryManager;
import manager.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    private static InMemoryHistoryManager inMemoryHistoryManager;

    @BeforeEach
    public void initEach() {
        inMemoryHistoryManager = (InMemoryHistoryManager) Managers.getDefaultHistory();

        // simple 10 tasks
        for (int i = 1; i <= 10; i++) {
            Task task = new Task("task" + i, "task" + i + " Desc", Status.NEW);
            task.setId(i);
            inMemoryHistoryManager.add(task);
        }
    }

    @Test
    public void shouldNotReturnNullOfInMemoryHistoryManager() {
        assertNotNull(inMemoryHistoryManager.getHistory());
    }

    @Test
    public void addHistory() {
        assertEquals(10, inMemoryHistoryManager.getSize(), "History storage works incorrect");
    }

    @Test
    public void removingHistoryElements() {
        Task first = inMemoryHistoryManager.getHistory().getFirst();
        inMemoryHistoryManager.remove(1);

        assertFalse(inMemoryHistoryManager.getHistory().contains(first), "Task deleting works incorrectly");
    }

    @Test
    public void shouldDeleteSubTaskWhenItsEpicIsDeleted() {
        Epic epic = new Epic("epic11", "epic11 desc");
        epic.setId(11);

        SubTask subTask = new SubTask("subtask 12", "subtask 12 desc", epic.getId(), Status.NEW);
        subTask.setId(12);
        epic.addSubTaskId(subTask.getId());

        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.add(subTask);

        inMemoryHistoryManager.remove(11);

        assertFalse(inMemoryHistoryManager.getHistory().contains(subTask), "Подзадача не удаляется при удалении его эпика");
    }

    @Test
    public void shouldKeepTheNewestVersionOfTaskIfTaskChangedByAnySetter() {
        Task task = new Task("task11", "task11 desc", Status.NEW);
        task.setId(11);

        inMemoryHistoryManager.add(task);

        task.setStatus(Status.IN_PROGRESS);

        assertEquals(task, inMemoryHistoryManager.getHistory().getLast());

        task.setName("new title");

        assertEquals(task, inMemoryHistoryManager.getHistory().getLast());
    }
}
