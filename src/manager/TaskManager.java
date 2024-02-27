package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.*;

public class TaskManager {

    private long manageId = 0L;
    private final Map<Long, Task> tasks;
    private final Map<Long, SubTask> subtasks;
    private final Map<Long, Epic> epics;

    public TaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public long takeId() {
        return ++manageId;
    }

    public void addTask(Task task) {
        task.setId(takeId());
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void printAllTasks() {
        for (Map.Entry<Long, Task> longTaskEntry : tasks.entrySet()) {
            System.out.println("ID " + longTaskEntry.getKey() + ", " + longTaskEntry.getValue());
        }
    }

    public Task getTaskById(long id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Task with id: " + id + " does not exist!");
            return null;
        }
        return tasks.get(id);
    }

    public void deleteTaskById(long id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Task with id: " + id + " does not exist!");
        } else {
            tasks.remove(id);
        }
    }

    public List<Task> getAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Task list is empty");
            return new ArrayList<>();
        } else {
            return new ArrayList<>(tasks.values());
        }
    }


    // EPICS
    public void addEpic(Epic epic) {
        epic.setId(takeId());
        epics.put(epic.getId(), epic);
    }

    public void updateEpicNew(long id, Epic epic) {
        if (epics.containsKey(id)) {
            epics.get(id).setName(epic.getName());
            epics.get(id).setDescription(epic.getDescription());
        }
    }

    public void printAllEpics() {
        for (Map.Entry<Long, Epic> longEpicEntry : epics.entrySet()) {
            System.out.println("ID " + longEpicEntry.getKey() + ", " + longEpicEntry.getValue());
        }
    }

    public Epic getEpicById(long id) {
        if (!epics.containsKey(id)) {
            System.out.println("Epic with id: " + id + " does not exist");
            return null;
        }
        return epics.get(id);
    }

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

    public List<Epic> getAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("Epics list is empty");
            return new ArrayList<>();
        } else {
            return new ArrayList<>(epics.values());
        }
    }


    // SUBTASKS
    public void addSubTask(SubTask subTask) {
        subTask.setId(takeId());
        subtasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpicId()).addSubTaskId(subTask.getId());

        checkSubTasksStatus(subTask.getEpicId());
    }

    public void printAllSubTasks() {
        for (Map.Entry<Long, SubTask> longSubTaskEntry : subtasks.entrySet()) {
            System.out.println(longSubTaskEntry.getValue());
        }
    }

    public SubTask getSubTaskById(long id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("SubTask with id: " + id + " does not exist");
            return null;
        }
        return subtasks.get(id);
    }

    public void updateSubTask(SubTask subTask) {
        if (subtasks.containsKey(subTask.getId())) {
            subtasks.put(subTask.getId(), subTask);
            checkSubTasksStatus(subTask.getEpicId());
        }
    }

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

    public List<SubTask> getAllSubTasks() {
        if (subtasks.isEmpty()) {
            System.out.println("SubTask list is empty");
            return new ArrayList<>();
        } else {
            return new ArrayList<>(subtasks.values());
        }
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

            if (subtasks.get(subTaskId).getStatus() != Status.NEW) { // todo
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

    public List<SubTask> getSubTasksByEpicId(long epicId) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        for (SubTask subTask : subtasks.values()) {
            if (subTask.getEpicId() == epicId)
                subTasks.add(subTask);
        }
        return subTasks;
    }
}
