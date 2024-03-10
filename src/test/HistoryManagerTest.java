package test;

import history.HistoryManager;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HistoryManagerTest {
    private static HistoryManager inMemoryHistoryManager;
    private static InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    public void initEach() {
        inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryHistoryManager = inMemoryTaskManager.getHistoryManager();

        // simple 10 tasks
        for (int i = 1; i <= 10; i++) {
            inMemoryTaskManager.addTask(new Task("task" + i, "task" + i + " Desc", Status.NEW));
        }
    }

    @Test
    public void shouldNotReturnNullOfInMemoryHistoryManager() {
        assertNotNull(inMemoryHistoryManager);
    }

    @Test
    public void addHistory() {
        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.getTaskById(2);
        inMemoryTaskManager.getTaskById(3);
        inMemoryTaskManager.getTaskById(4);
        inMemoryTaskManager.getTaskById(5);

        List<Task> history = inMemoryHistoryManager.getHistory();

        assertEquals(5, history.size(), "History storage works incorrect");
    }

    @Test
    public void removingHistoryElements() {
        Task firstTaskInHistory = inMemoryTaskManager.getTaskById(1);

        for (int i = 2; i <= 10; i++) {
            inMemoryTaskManager.getTaskById(i);
        }

        inMemoryTaskManager.addTask(new Task("asd11", "dsa11", Status.NEW));
        Task lastTaskInHistory = inMemoryTaskManager.getTaskById(11);

        List<Task> historyList = inMemoryHistoryManager.getHistory();

        assertNotEquals(firstTaskInHistory, historyList.getFirst(), "First task doesnt remove");
        assertEquals(lastTaskInHistory, historyList.getLast(), "Last element did not add");
    }
}
