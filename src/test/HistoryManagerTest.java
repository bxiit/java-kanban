package test;

import history.HistoryManager;
import manager.Managers;
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

    @BeforeEach
    public void initEach() {
        inMemoryHistoryManager = Managers.getDefaultHistory();

        // simple 10 tasks
        for (int i = 1; i <= 10; i++) {
            inMemoryHistoryManager.add(new Task("task" + i, "task" + i + " Desc", Status.NEW));
        }
    }

    @Test
    public void shouldNotReturnNullOfInMemoryHistoryManager() {
        assertNotNull(inMemoryHistoryManager.getHistory());
    }

    @Test
    public void addHistory() {

        List<Task> history = inMemoryHistoryManager.getHistory();

        assertEquals(10, history.size(), "History storage works incorrect");
    }

    @Test
    public void removingHistoryElements() {
        Task firstTaskInHistory = inMemoryHistoryManager.getHistory().getFirst();

        inMemoryHistoryManager.add(new Task("asd11", "dsa11", Status.NEW));
        Task lastTaskInHistory = inMemoryHistoryManager.getHistory().getLast();

        List<Task> historyList = inMemoryHistoryManager.getHistory();

        assertNotEquals(firstTaskInHistory, historyList.getFirst(), "First task doesnt remove");
        assertEquals(lastTaskInHistory, historyList.getLast(), "Last element did not add");
    }
}
