package manager;

import history.HistoryManager;
import history.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private static InMemoryHistoryManager inMemoryHistoryManager;
    private static InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    public void initEach() {
        inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryHistoryManager = inMemoryTaskManager.getInMemoryHistoryManager();

        // simple 10 tasks
        for (int i = 1; i <= 10; i++) {
            inMemoryTaskManager.addTask(new Task("task" + i, "task" + i + " Desc", Status.NEW));
        }
    }

    @Test
    public void shouldBeEqualTaskIfTheirIdsIsEqual() {
        Task taskWithId1 = inMemoryTaskManager.getTaskById(1);
        assertEquals(
                taskWithId1,
                inMemoryTaskManager.getTaskById(1),
                "Tasks are not equals even if their id's are the same"
        );
    }

    @Test
    public void shouldBeEqualInheritorsOfTaskIfTheirIdsAreEqual() {
        Epic epic = new Epic("epic1", "epic1 desc");
        inMemoryTaskManager.addEpic(epic);
        SubTask subTask = new SubTask("subtask1", "subtask1 desc", epic.getId(), Status.NEW);
        inMemoryTaskManager.addSubTask(
                subTask
        );
        epic.addSubTaskId(subTask.getId());

        assertEquals(11 , epic.getId(), "Epics are not the same even if their id's are the same");
    }

    @Test
    public void shouldReturnNegativeIfAddEpicToEpicAddSubtasksId() {
        Epic epic = new Epic("epic1", "epic1 desc");
        inMemoryTaskManager.addEpic(epic);
        final long addedSubTaskId = epic.addSubTaskId(epic.getId());

        assertTrue(addedSubTaskId < 0);
    }

    @Test
    public void shouldReturnNegativeIfAddSubTaskIdToSubTasksEpicId() {
        long idOfNextTask = 11;
        SubTask subTask = new SubTask("subtask1", "subtask1 desc", idOfNextTask, Status.NEW);
        assertTrue(inMemoryTaskManager.addSubTask(subTask) < 0);
    }

    @Test
    public void shouldNotReturnNullOfInMemoryHistoryManager() {
        assertNotNull(inMemoryHistoryManager);
    }

    @Test
    public void shouldReturnTaskById() {
        Task task5 = inMemoryTaskManager.getTaskById(5);

        assertNotNull(task5);
        assertEquals(task5, inMemoryTaskManager.getTaskById(5));
    }

    @Test
    public void shouldBeIdIncrementingInInMemoryTaskManager() {
        Epic epic1 = new Epic("epic1", "epic1 desc");
        Epic epic2 = new Epic("epic2", "epic2 desc");
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addEpic(epic2);

        assertNotEquals(epic1.getId(), epic2.getId());
        assertEquals(epic2.getId(), epic1.getId() + 1);
    }

    @Test
    void addNewTask() {
        final long taskId = inMemoryTaskManager.addTask(
                new Task("addtest",
                        "addtest description",
                        Status.NEW)
        );
        final Task savedTask = inMemoryTaskManager.getTaskById(taskId);

        assertNotNull(savedTask);
        assertEquals(taskId, savedTask.getId(), "Task not found!");

        List<Task> taskList = inMemoryTaskManager.getAllTasks();
        assertNotNull(taskList, "Tasks don't return");

        assertEquals(savedTask, taskList.get(10), "Task store is incorrect");
    }

    @Test
    public void shouldBeTheSameTaskWhenAddedByTaskManager() {
        Task task = new Task("task11", "task11 desc", Status.NEW);
        inMemoryTaskManager.addTask(task);

        Task addedTask = inMemoryTaskManager.getTaskById(11);

        assertEquals(task, addedTask, "Task changes when added to task manager");
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