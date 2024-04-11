package manager;

import history.HistoryManager;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    protected long manageId = 0L;
    protected final Map<Long, Task> tasks;
    protected final Map<Long, SubTask> subtasks;
    protected final Map<Long, Epic> epics;
    protected final HistoryManager inMemoryHistoryManager;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        inMemoryHistoryManager = Managers.getDefaultHistory();
    }

    private void addHistory(Task task) {
        inMemoryHistoryManager.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    private long takeId() {
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
        if (tasks.containsKey(task.getId())) tasks.put(task.getId(), task);
    }

    @Override
    public void printAllTasks() {
        for (Map.Entry<Long, Task> longTaskEntry : tasks.entrySet()) System.out.println(longTaskEntry.getValue());
    }

    @Override
    public Task getTaskById(long id) {
        if (!tasks.containsKey(id)) return null;
        addHistory(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void deleteTaskById(long id) {
        if (tasks.containsKey(id)) {
            inMemoryHistoryManager.remove(id);
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

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) inMemoryHistoryManager.remove(task.getId());
        tasks.clear();
    }

    // EPICS
    @Override
    public Long addEpic(Epic epic) {
        epic.setId(takeId());
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic epicUpdated) {
        if (epics.containsKey(epicUpdated.getId())) {
            Epic epicToUpdate = epics.get(epicUpdated.getId());
            epicToUpdate.setName(epicUpdated.getName());
            epicToUpdate.setDescription(epicToUpdate.getDescription());

            epics.put(epicUpdated.getId(), epicUpdated);
        }
    }

    @Override
    public void printAllEpics() {
        for (Map.Entry<Long, Epic> longEpicEntry : epics.entrySet()) System.out.println(longEpicEntry.getValue());
    }

    @Override
    public Epic getEpicById(long id) {
        if (!epics.containsKey(id)) return null;
        addHistory(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void deleteEpicById(long id) {
        inMemoryHistoryManager.remove(id);
        Epic removedEpic = epics.remove(id);

        for (Long subTaskId : removedEpic.getSubTasksIds()) {
            inMemoryHistoryManager.remove(subTaskId);
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

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
                inMemoryHistoryManager.remove(epic.getId());
            for (Long subTasksId : epic.getSubTasksIds()) {
                inMemoryHistoryManager.remove(subTasksId);
            }
        }
        epics.clear();
    }

    // SUBTASKS

    @Override
    public Long addSubTask(SubTask subTask) {
        subTask.setId(takeId());
        if (subTask.getEpicId() == subTask.getId()) return (long) -1;

        subtasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpicId()).addSubTaskId(subTask.getId());

        checkSubTasksStatus(subTask.getEpicId());
        return subTask.getId();
    }

    @Override
    public void printAllSubTasks() {
        for (Map.Entry<Long, SubTask> longSubTaskEntry : subtasks.entrySet())
            System.out.println(longSubTaskEntry.getValue());
    }

    @Override
    public SubTask getSubTaskById(long id) {
        if (!subtasks.containsKey(id)) return null;
        addHistory(subtasks.get(id));
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
        if (subTask != null) {
            inMemoryHistoryManager.remove(id);
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

    @Override
    public void deleteAllSubTasks() {
        for (SubTask subTask : subtasks.values()) {
            inMemoryHistoryManager.remove(subTask.getId());
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                checkSubTasksStatus(epic.getId());
                epic.deleteSubTaskId(subTask.getId());
            }
        }
        subtasks.clear();
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
