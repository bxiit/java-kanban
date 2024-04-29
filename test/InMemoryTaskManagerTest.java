import exception.NotFoundException;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void shouldBeEqualInheritorsOfTaskIfTheirIdsAreEqual() {
        Epic epic = new Epic("epic1", "epic1 desc");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("subtask1", "subtask1 desc", epic.getId(), Status.NEW);
        manager.addSubTask(subTask);
        epic.addSubTaskId(subTask.getId());

        assertEquals(11, epic.getId(), "Epics are not the same even if their id's are the same");
    }

    @Test
    void shouldReturnNegativeIfAddEpicToEpicAddSubtasksId() {
        Epic epic = new Epic("epic1", "epic1 desc");
        manager.addEpic(epic);
        long addedSubTaskId = epic.addSubTaskId(epic.getId());

        assertTrue(addedSubTaskId < 0);
    }

    @Test
    void shouldReturnNegativeIfAddSubTaskIdToSubTasksEpicId() {
        long idOfNextTask = 11;
        SubTask subTask = new SubTask("subtask1", "subtask1 desc", idOfNextTask, Status.NEW);
        assertTrue(manager.addSubTask(subTask) < 0);
    }

    @Test
    void shouldReturnTaskById() throws NotFoundException {
        Optional<Task> task5 = manager.getTaskById(5);

        assertTrue(task5.isPresent());
        assertEquals(5, task5.get().getId());
    }

    @Test
    void shouldBeIdIncrementingInManager() {
        Epic epic1 = new Epic("epic1", "epic1 desc");
        Epic epic2 = new Epic("epic2", "epic2 desc");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        assertNotEquals(epic1.getId(), epic2.getId());
        assertEquals(epic2.getId(), epic1.getId() + 1);
    }

    @Test
    void addNewTask() throws NotFoundException {
        Task task = new Task("addtest",
                "addtest description",
                Status.NEW);
        manager.addTask(task);
        final Optional<Task> savedTask = manager.getTaskById(11);

        assertTrue(savedTask.isPresent());
        assertEquals(11, savedTask.get().getId(), "Task not found!");

        List<Task> taskList = manager.getAllTasks();
        assertNotNull(taskList, "Tasks don't return");

        assertEquals(savedTask.get(), taskList.get(10), "Task store is incorrect");
    }

    @Test
    void shouldBeTheSameTaskWhenAddedByTaskManager() throws NotFoundException {
        Task task = new Task("task11", "task11 desc", Status.NEW);
        manager.addTask(task);

        Optional<Task> addedTask = manager.getTaskById(11);

        assertTrue(addedTask.isPresent());
        assertEquals(task, addedTask.get(), "Task changes when added to task manager");
    }

    @Test
    void shouldDeleteAllTasksFromTasksMap() {
        manager.deleteAllTasks();
        assertEquals(
                0,
                manager.getAllTasks().size(),
                "Tasks do not delete when when deleteAllTasks is called"
        );
    }
}