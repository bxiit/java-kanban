import history.InMemoryHistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
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
    void shouldNotReturnNullOfInMemoryHistoryManager() {
        assertNotNull(inMemoryHistoryManager.getHistory());
    }

    @Test
    void addHistory() {
        assertEquals(10, inMemoryHistoryManager.getHistory().size(), "History storage works incorrect");
    }

    @Test
    void removingFirstNodeInHistory() {
        Task first = inMemoryHistoryManager.getHistory().getFirst();
        inMemoryHistoryManager.remove(first.getId());

        assertFalse(inMemoryHistoryManager.getHistory().contains(first), "First Task deleting works incorrectly");
    }

    @Test
    void removingLastNodeInHistory() {
        Task last = inMemoryHistoryManager.getHistory().getLast();
        inMemoryHistoryManager.remove(last.getId());

        assertFalse(inMemoryHistoryManager.getHistory().contains(last), "Last Task deleting works incorrectly");
    }

    @Test
    void removingMiddleNodeInHistory() {
        Task middleNode = inMemoryHistoryManager.getHistory().get(inMemoryHistoryManager.getHistory().size() / 2);

        inMemoryHistoryManager.remove(middleNode.getId());

        assertFalse(inMemoryHistoryManager.getHistory().contains(middleNode), "Middle Task deleting works incorrectly");
    }

    @Test
    void removingSingleNodeInHistory() {
        int size = inMemoryHistoryManager.getHistory().size();
        for (int i = 2; i <= size; i++) {
            inMemoryHistoryManager.remove(i);
        }

        List<Task> history = inMemoryHistoryManager.getHistory();
        assertEquals(1, history.size(), "Tasks deleting works wrong");

        inMemoryHistoryManager.remove(history.getFirst().getId());
    }

    @Test
    void shouldDeleteSubTaskWhenItsEpicIsDeleted() {
        TaskManager taskManager = Managers.getDefaultManager();
        Epic epic = new Epic("epic11", "epic11 desc");
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("subtask 12", "subtask 12 desc", epic.getId(), Status.NEW);

        taskManager.addSubTask(subTask);

        inMemoryHistoryManager.remove(11);


        assertFalse(taskManager.getHistory().contains(subTask), "Подзадача не удаляется при удалении его эпика");
    }

    @Test
    void shouldKeepTheNewestVersionOfTaskIfTaskChangedByAnySetter() {
        Task task = new Task("task11", "task11 desc", Status.NEW);
        task.setId(11);

        inMemoryHistoryManager.add(task);

        task.setStatus(Status.IN_PROGRESS);

        List<Task> history = inMemoryHistoryManager.getHistory();
        Task last = history.getLast();
        assertEquals(task, last);

        task.setName("new title");

        assertEquals(task, inMemoryHistoryManager.getHistory().getLast());
    }

    @Test
    void shouldDeleteAllTasksFromHistoryWhenAllTasksAreDeleted() {
        TaskManager taskManager = Managers.getDefaultManager();
        Task task =  new Task("task to delete 1", "task to delete 1 desc", Status.NEW);
        task.setId(1);
        taskManager.addTask(task);
        taskManager.getTaskById(1);
        List<Task> historyBeforeDelete = taskManager.getHistory();
        assertEquals(historyBeforeDelete, List.of(task));

        taskManager.deleteAllTasks();
        List<Task> historyAfterDelete = taskManager.getHistory();
        assertTrue(historyAfterDelete.isEmpty());
    }

    @Test
    void shouldDeleteAllEpicsAndTheirSubTasksFromHistoryWhenAllEpicsWereDeleted() {
        TaskManager taskManager = Managers.getDefaultManager();
        Epic epic = new Epic("epic to delete 1", "epic to delete 1 desc");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask(
                "subtask of epic to delete 1",
                "subtask of epic to delete 1 desc",
                epic.getId(),
                Status.NEW);

        taskManager.addSubTask(subTask);

        taskManager.getEpicById(1);
        taskManager.getSubTaskById(2);
        assertEquals(List.of(epic, subTask), taskManager.getHistory());

        taskManager.deleteAllEpics();
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void shouldDeleteAllSubTasksFromHistoryWhenAllSubTasksWereDeleted() {
        TaskManager taskManager = Managers.getDefaultManager();
        Epic epic = new Epic("epic to delete 1", "epic to delete 1 desc");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask(
                "subtask of epic to delete 1",
                "subtask of epic to delete 1 desc",
                epic.getId(),
                Status.NEW);

        taskManager.addSubTask(subTask);

        taskManager.getEpicById(1);
        taskManager.getSubTaskById(2);
        assertEquals(List.of(epic, subTask), taskManager.getHistory());

        taskManager.deleteAllSubTasks();
        assertEquals(List.of(epic), taskManager.getHistory());
    }

    @Test
    void shouldBeFalseIfTheOrderOfHistoryIsWrong() {
        List<Task> history = inMemoryHistoryManager.getHistory();
        for (int i = 0; i < history.size(); i++) {
            Task task = history.get(i);
            assertEquals((long) task.getId(), i + 1);
        }
    }
}
