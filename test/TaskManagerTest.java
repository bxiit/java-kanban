import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskManagerTest {
    private static TaskManager inMemoryTaskManager;

    @BeforeEach
    public void initEach() {
        inMemoryTaskManager = Managers.getDefaultManager();

        // simple 10 tasks
        for (int i = 1; i <= 10; i++) {
            inMemoryTaskManager.addTask(new Task("task" + i, "task" + i + " Desc", Status.NEW));
        }
    }

    @Test
    public void shouldBeEqualTaskIfTheirIdsIsEqual() {
        assertEquals(
                1,
                inMemoryTaskManager.getTaskById(1).getId(),
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

        assertEquals(11, epic.getId(), "Epics are not the same even if their id's are the same");
    }

    @Test
    public void shouldReturnNegativeIfAddEpicToEpicAddSubtasksId() {
        Epic epic = new Epic("epic1", "epic1 desc");
        inMemoryTaskManager.addEpic(epic);
        long addedSubTaskId = epic.addSubTaskId(epic.getId());

        assertTrue(addedSubTaskId < 0);
    }

    @Test
    public void shouldReturnNegativeIfAddSubTaskIdToSubTasksEpicId() {
        long idOfNextTask = 11;
        SubTask subTask = new SubTask("subtask1", "subtask1 desc", idOfNextTask, Status.NEW);
        assertTrue(inMemoryTaskManager.addSubTask(subTask) < 0);
    }

    @Test
    public void shouldReturnTaskById() {
        Task task5 = inMemoryTaskManager.getTaskById(5);

        assertNotNull(task5);
        assertEquals(5, task5.getId());
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
        Task task = new Task("addtest",
                "addtest description",
                Status.NEW);
        inMemoryTaskManager.addTask(task);
        final long taskId = inMemoryTaskManager.getTaskById(11).getId();
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
    public void shouldDeleteAllTasksFromTasksMap() {
        inMemoryTaskManager.deleteAllTasks();
        assertEquals(
                0,
                inMemoryTaskManager.getAllTasks().size(),
                "Tasks do not delete when when deleteAllTasks is called"
        );
    }
}