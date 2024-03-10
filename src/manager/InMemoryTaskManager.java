package manager;

import history.InMemoryHistoryManager;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private long manageId = 0L;
    private final Map<Long, Task> tasks;
    private final Map<Long, SubTask> subtasks;
    private final Map<Long, Epic> epics;
    private final InMemoryHistoryManager inMemoryHistoryManager;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        inMemoryHistoryManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
    }

    public InMemoryHistoryManager getInMemoryHistoryManager() {
        return inMemoryHistoryManager;
    }

    @Override
    public long takeId() {
        return ++manageId;
    }

    @Override
    public Long addTask(Task task) {
        task.setId(takeId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void printAllTasks() {
        for (Map.Entry<Long, Task> longTaskEntry : tasks.entrySet()) {
            System.out.println("ID " + longTaskEntry.getKey() + ", " + longTaskEntry.getValue());
        }
    }

    @Override
    public Task getTaskById(long id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Task with id: " + id + " does not exist!");
            return null;
        }
        if (!inMemoryHistoryManager.isCapacityOkay(inMemoryHistoryManager.getHistory())) {
            inMemoryHistoryManager.getHistory().removeFirst();
        }
        inMemoryHistoryManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void deleteTaskById(long id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Task with id: " + id + " does not exist!");
        } else {
            tasks.remove(id);
        }
    }

    @Override
    public List<Task> getAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Task list is empty");
            return new ArrayList<>();
        } else {
            return new ArrayList<>(tasks.values());
        }
    }


    // EPICS
    @Override
    public Long addEpic(Epic epic) {
        epic.setId(takeId());
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateEpicNew(long id, Epic epic) {
        if (epics.containsKey(id)) {
            epics.get(id).setName(epic.getName());
            epics.get(id).setDescription(epic.getDescription());
        }
    }

    @Override
    public void printAllEpics() {
        for (Map.Entry<Long, Epic> longEpicEntry : epics.entrySet()) {
            System.out.println("ID " + longEpicEntry.getKey() + ", " + longEpicEntry.getValue());
        }
    }

    @Override
    public Epic getEpicById(long id) {
        if (!epics.containsKey(id)) {
            System.out.println("Epic with id: " + id + " does not exist");
            return null;
        }
        if (!inMemoryHistoryManager.isCapacityOkay(inMemoryHistoryManager.getHistory())) {
            inMemoryHistoryManager.getHistory().removeFirst();
        }
        inMemoryHistoryManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void deleteEpicById(long id) {
        if (!epics.containsKey(id)) {
            System.out.println("Task with id: " + id + " does not exist!");
            return;
        }
        Epic removedEpic = epics.remove(id);

        for (Long subTaskId : removedEpic.getSubTasksIds()) {
            subtasks.remove(subTaskId);
        }
    }

    @Override
    public List<Epic> getAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("Epics list is empty");
            return new ArrayList<>();
        } else {
            return new ArrayList<>(epics.values());
        }
    }


    // SUBTASKS
    @Override
    public Long addSubTask(SubTask subTask) {
        long id = takeId();
        subTask.setId(id);
        if (subTask.getEpicId() == id) {
            return (long) -1;
        }

        subtasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpicId()).addSubTaskId(subTask.getId());

        checkSubTasksStatus(subTask.getEpicId());
        return subTask.getId();
    }

    @Override
    public void printAllSubTasks() {
        for (Map.Entry<Long, SubTask> longSubTaskEntry : subtasks.entrySet()) {
            System.out.println(longSubTaskEntry.getValue());
        }
    }

    @Override
    public SubTask getSubTaskById(long id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("SubTask with id: " + id + " does not exist");
            return null;
        }
        if (!inMemoryHistoryManager.isCapacityOkay(inMemoryHistoryManager.getHistory())) {
            inMemoryHistoryManager.getHistory().removeFirst();
        }
        inMemoryHistoryManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subtasks.containsKey(subTask.getId())) {
            subtasks.put(subTask.getId(), subTask);
            checkSubTasksStatus(subTask.getEpicId());
        }
    }

    @Override
    public void deleteSubTaskById(long id) {
        SubTask subTask = subtasks.remove(id);
        if (subTask == null) {
            System.out.println("Task with id: " + id + " does not exist!");
        } else {
            Epic epic = epics.get(subTask.getEpicId());
            epic.deleteSubTaskId(id);
            checkSubTasksStatus(subTask.getEpicId());
        }
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        if (subtasks.isEmpty()) {
            System.out.println("SubTask list is empty");
            return new ArrayList<>();
        } else {
            return new ArrayList<>(subtasks.values());
        }
    }

    @Override
    public List<SubTask> getSubTasksByEpicId(long epicId) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        for (SubTask subTask : subtasks.values()) {
            if (subTask.getEpicId() == epicId)
                subTasks.add(subTask);
        }
        return subTasks;
    }

    // CHECKING SUBTASKS STATUS
    private void checkSubTasksStatus(long epicId) {
        Epic epicToCheck = epics.get(epicId);

        if (epicToCheck.getSubTasksIds() == null) {
            epicToCheck.setStatus(Status.NEW);
            return;
        }

        for (Long subTaskId : epicToCheck.getSubTasksIds()) {
            boolean allNew = true;
            boolean allDone = true;

            if (!subtasks.containsKey(subTaskId)) {
                return;
            }

            if (subtasks.get(subTaskId).getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subtasks.get(subTaskId).getStatus() != Status.DONE) {
                allDone = false;
            }

            if (allNew) {
                epicToCheck.setStatus(Status.NEW);
            } else if (allDone) {
                epicToCheck.setStatus(Status.DONE);
            } else {
                epicToCheck.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}
