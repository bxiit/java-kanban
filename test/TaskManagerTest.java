import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T manager;

    @BeforeEach
    void setUp() {
        manager = createTaskManager();
        for (int i = 1; i <= 10; i++) {
            manager.addTask(new Task("task" + i, "task" + i + " Desc", Status.NEW));
        }
    }

    protected abstract T createTaskManager();

    @Test
    void addTask_ShouldReturnAddedTaskFromManager() {
        manager.addEpic(new Epic("epic", "epic desc"));
        System.out.println(manager);
        Optional<Epic> epicById = manager.getEpicById(11);

        assertTrue(epicById.isPresent());
        Assertions.assertEquals("epic", epicById.get().getName());
    }

    @Test
    void updateTaskTest_ShouldReturnUpdatedTaskFromManager() {
        Task task = manager.getTaskById(1).orElseThrow();
        Task taskUpdated = new Task(task.getId(), "task1 updated", "task1 updated desc", task.getStatus());
        manager.updateTask(taskUpdated);

        Optional<Task> taskById = manager.getTaskById(1);
        assertTrue(taskById.isPresent());
        assertEquals(taskUpdated, taskById.get());
    }

    @Test
    void getTaskById_ShouldReturnTaskFromManager() {
        Task task = manager.getTaskById(10).orElseThrow();
        assertEquals(10, task.getId());
    }

    @Test
    void deleteTaskById_ShouldBeTrueIfTaskSuccessfullyDeleted() {
        manager.deleteTaskById(10);
        assertTrue(manager.getTaskById(10).isEmpty());
    }

    @Test
    void getAllTasks_ShouldReturnTenSizedListAfterAddingTenTasks() {
        List<Task> tasks = manager.getAllTasks();

        assertEquals(10, tasks.size());
        assertEquals(1, tasks.getFirst().getId());
        assertEquals(10, tasks.getLast().getId());
        assertEquals("task1", tasks.getFirst().getName());
        assertEquals("task10", tasks.getLast().getName());
    }

    @Test
    void deleteAllTasks_ShouldBeEmptyTasksListAfterDelete() {
        assertEquals(10, manager.getAllTasks().size());
        manager.deleteAllTasks();
        assertEquals(0, manager.getAllTasks().size());
    }

    // EPICS
    @Test
    void addEpic_ShouldReturnAddedEpicFromManager() {
        Epic epic = new Epic("epic1", "epic1 desc");
        manager.addEpic(epic);

        Optional<Epic> epicById = manager.getEpicById(11);

        assertTrue(epicById.isPresent());
        assertEquals(epic, epicById.get());
    }

    @Test
    void updateEpic_ShouldReturnUpdatedData() {
        Epic epic1 = new Epic("epic1", "epic1 desc");
        manager.addEpic(epic1);

        Epic epicUpdated = new Epic(11L, "epic1 updated", "epic1 updated desc", Status.NEW);
        manager.updateEpic(epicUpdated);

        Optional<Epic> epic11 = manager.getEpicById(11);

        assertTrue(epic11.isPresent());
        assertEquals("epic1 updated", epic11.get().getName());
        assertEquals(11L, epic11.get().getId());
        assertEquals("epic1 updated desc", epic11.get().getDescription());
        assertEquals(Status.NEW, epic11.get().getStatus());
    }

    @Test
    void getEpicById_ShouldReturnEpicFromManager() {
        Epic epic1 = new Epic("epic1", "epic1 desc");
        manager.addEpic(epic1);

        Optional<Epic> epicFromManager = manager.getEpicById(11);

        assertTrue(epicFromManager.isPresent());
        assertNotNull(epicFromManager);
        assertEquals(11, epicFromManager.get().getId());
        assertEquals(epic1.getName(), epicFromManager.get().getName());
    }

    @Test
    void deleteEpicById_ShouldBeTrueIfEpicSuccessfullyDeleted() {
        Epic epic1 = new Epic("epic1", "epic1 desc");
        manager.addEpic(epic1);

        assertTrue(manager.getEpicById(11).isPresent());
        manager.deleteEpicById(11);
        assertTrue(manager.getEpicById(11).isEmpty());
    }

    @Test
    void getAllEpics_ShouldReturnThreeSizedListAfterAddingThreeEpics() {
        Epic epic1 = new Epic("epic1", "epic1 desc");
        Epic epic2 = new Epic("epic2", "epic2 desc");
        Epic epic3 = new Epic("epic3", "epic3 desc");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addEpic(epic3);

        List<Epic> epics = manager.getAllEpics();

        assertEquals(3, epics.size());
        assertEquals(epic1, epics.getFirst());
        assertEquals(epic3, epics.getLast());
    }

    @Test
    void deleteAllEpics_ShouldBeEmptyEpicsListAfterDelete() {
        Epic epic1 = new Epic("epic1", "epic1 desc");
        Epic epic2 = new Epic("epic2", "epic2 desc");
        Epic epic3 = new Epic("epic3", "epic3 desc");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addEpic(epic3);

        List<Epic> epics = manager.getAllEpics();
        assertEquals(3, epics.size());
        manager.deleteAllEpics();

        List<Epic> epicsAfterDelete = manager.getAllEpics();
        assertEquals(0, epicsAfterDelete.size());
    }

    // SUBTASKS
    @Test
    void addSubTask_ShouldReturnAddedSubTasksFromManager() {
        Epic epic1 = new Epic("epic1", "epic1 desc");
        manager.addEpic(epic1);
        SubTask subTask1 = new SubTask("subtask1", "subtask1 desc", epic1.getId(), Status.NEW);
        SubTask subTask2 = new SubTask("subtask2", "subtask2 desc", epic1.getId(), Status.IN_PROGRESS);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        Optional<SubTask> subTask1FromManager = manager.getSubTaskById(12);
        assertTrue(subTask1FromManager.isPresent());
        assertEquals(12, subTask1FromManager.get().getId());
        Optional<SubTask> subTask2FromManager = manager.getSubTaskById(13);
        assertTrue(subTask2FromManager.isPresent());
        assertEquals(13, subTask2FromManager.get().getId());
    }

    @Test
    void getSubTaskById_ShouldReturnSubTasksFromManager() {
        Epic epic1 = new Epic("epic1", "epic1 desc");
        manager.addEpic(epic1);
        SubTask subTask1 = new SubTask("subtask1", "subtask1 desc", epic1.getId(), Status.NEW);
        SubTask subTask2 = new SubTask("subtask2", "subtask2 desc", epic1.getId(), Status.IN_PROGRESS);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        Optional<SubTask> subTask1FromManager = manager.getSubTaskById(12);
        assertTrue(subTask1FromManager.isPresent());
        assertEquals(12, subTask1FromManager.get().getId());
        Optional<SubTask> subTask2FromManager = manager.getSubTaskById(13);
        assertTrue(subTask2FromManager.isPresent());
        assertEquals(13, subTask2FromManager.get().getId());
    }

    @Test
    void updateSubTask_ShouldReturnUpdatedData() {
        Epic epic1 = new Epic("epic1", "epic1 desc");
        manager.addEpic(epic1);
        SubTask subTask1 = new SubTask("subtask1", "subtask1 desc", epic1.getId(), Status.NEW);
        SubTask subTask2 = new SubTask("subtask2", "subtask2 desc", epic1.getId(), Status.IN_PROGRESS);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        SubTask subTask1Updated = new SubTask(12L, "subtask1 updated",
                "subtask1 updated desc", epic1.getId(), Status.NEW);
        manager.updateSubTask(subTask1Updated);

        Optional<SubTask> subTask1FromManager = manager.getSubTaskById(12L);
        assertTrue(subTask1FromManager.isPresent());
        assertEquals(12L, subTask1FromManager.get().getId());
        assertEquals("subtask1 updated", subTask1FromManager.get().getName());
        assertEquals("subtask1 updated desc", subTask1FromManager.get().getDescription());
    }

    @Test
    void deleteSubTaskById_ShouldBeTrueIfSubTaskSuccessfullyDeleted() {
        Epic epic1 = new Epic("epic1", "epic1 desc");
        manager.addEpic(epic1);
        SubTask subTask1 = new SubTask("subtask1", "subtask1 desc", epic1.getId(), Status.NEW);
        SubTask subTask2 = new SubTask("subtask2", "subtask2 desc", epic1.getId(), Status.IN_PROGRESS);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        assertTrue(manager.getSubTaskById(12L).isPresent());
        manager.deleteSubTaskById(12L);
        assertTrue(manager.getSubTaskById(12L).isEmpty());
    }

    @Test
    void getAllSubTasks_ShouldReturnTwoSizedListAfterAddingTwoSubTasks() {
        Epic epic1 = new Epic("epic1", "epic1 desc");
        manager.addEpic(epic1);
        SubTask subTask1 = new SubTask("subtask1", "subtask1 desc", epic1.getId(), Status.NEW);
        SubTask subTask2 = new SubTask("subtask2", "subtask2 desc", epic1.getId(), Status.IN_PROGRESS);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        List<SubTask> subTasks = manager.getAllSubTasks();
        assertEquals(2, subTasks.size());
        assertEquals(12L, subTasks.getFirst().getId());
        assertEquals(13L, subTasks.getLast().getId());
    }

    @Test
    void deleteAllSubTasks_ShouldBeEmptySubTasksListAfterDelete() {
        Epic epic1 = new Epic("epic1", "epic1 desc");
        manager.addEpic(epic1);
        SubTask subTask1 = new SubTask("subtask1", "subtask1 desc", epic1.getId(), Status.NEW);
        SubTask subTask2 = new SubTask("subtask2", "subtask2 desc", epic1.getId(), Status.IN_PROGRESS);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        List<SubTask> subTasks = manager.getAllSubTasks();
        assertEquals(2, subTasks.size());
        manager.deleteAllSubTasks();
        List<SubTask> subTasksAfterDelete = manager.getAllSubTasks();
        assertEquals(0, subTasksAfterDelete.size());
    }

    @Test
    void getSubTasksByEpicId() {
        Epic epic1 = new Epic("epic1", "epic1 desc");
        manager.addEpic(epic1);
        SubTask subTask1 = new SubTask("subtask1", "subtask1 desc", epic1.getId(), Status.NEW);
        SubTask subTask2 = new SubTask("subtask2", "subtask2 desc", epic1.getId(), Status.IN_PROGRESS);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        List<SubTask> subTasksByEpicId = manager.getSubTasksByEpicId(epic1.getId());
        assertEquals(2, subTasksByEpicId.size());
        assertEquals(subTask1, subTasksByEpicId.getFirst());
        assertEquals(subTask2, subTasksByEpicId.getLast());
    }

    @Test
    void getHistory_ShouldReturnEmptyListIfHistoryIsEmpty() {
        List<Task> history = manager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void getHistory_ShouldReturnThreeSizedHistoryListAfterGettingThreeTasks() {
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getTaskById(3);

        List<Task> history = manager.getHistory();

        assertFalse(history.isEmpty());
        assertEquals(3, history.size());
    }

    @Test
    void getPrioritizedTasks_ShouldReturnThreeSizedListAfterAddingThreeTasksWithStartTimeAndDuration() {
        Task task11 = new Task("task11", "task11 desc", Status.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        Task task12 = new Task("task12", "task12 desc", Status.IN_PROGRESS,
                Duration.ofHours(1), LocalDateTime.of(
                        2025, Month.JANUARY, 1, 0, 1
        ));
        Task task13 = new Task("task13", "task13 desc", Status.IN_PROGRESS,
                Duration.ofHours(2), LocalDateTime.of(
                        2023, Month.JANUARY, 1, 0, 1
        ));
        manager.addTask(task11);
        manager.addTask(task12);
        manager.addTask(task13);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertFalse(prioritizedTasks.isEmpty());
        assertEquals(3, prioritizedTasks.size());
        assertEquals(task13, prioritizedTasks.getFirst());
        assertEquals(task12, prioritizedTasks.getLast());
    }

    @Test
    void shouldNotATaskThatIntersectAnotherTask() {
        LocalDateTime january2024 = LocalDateTime.of(2024, Month.JANUARY,
                1, 0, 0, 0);
        LocalDateTime february2024 = LocalDateTime.of(2024, Month.FEBRUARY,
                1, 0, 0);

        Epic epic = new Epic("epic1", "epic1 desc");
        manager.addEpic(epic);
        SubTask subTask1 = new SubTask("subtask2", "subtask2 desc", epic.getId(),
                Status.IN_PROGRESS, Duration.ofDays(60), january2024);
        SubTask subTask2 = new SubTask("subtask3", "subtask3 desc", epic.getId(),
                Status.IN_PROGRESS, Duration.ofDays(60), february2024);

        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        assertEquals(1, manager.getAllSubTasks().size());
    }
}
