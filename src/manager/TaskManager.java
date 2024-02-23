package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private long takeId() {
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
        }
        else  {
            tasks.remove(id);
        }
    }
    public List<Task> getAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Task list is empty");
            return new ArrayList<>();
        }
        else {
            return new ArrayList<>(tasks.values());
        }
    }


    // EPICS
    public void addEpic(Epic epic) {
        epic.setId(takeId());
        epics.put(epic.getId(), epic);
    }
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic epicToUpdate = epics.get(epic.getId());
            epicToUpdate.setName(epic.getName());
            epicToUpdate.setDescription(epic.getDescription());
            epics.put(epic.getId(), epicToUpdate);
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
        }
        else  {
            epics.remove(id);
        }
    }
    public List<Epic> getAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("Epics list is empty");
            return new ArrayList<>();
        }
        else {
            return new ArrayList<>(epics.values());
        }
    }


    // SUBTASKS
    public void addSubTask(Epic epic, SubTask subTask) {
        subTask.setId(takeId());
        epic.addSubTaskId(subTask.getId());
        subtasks.put(subTask.getId(), subTask);
        checkSubTasksStatus();
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
            checkSubTasksStatus();
        }
    }
    public void deleteSubTaskById(long id) {
        if (subtasks.containsKey(id)) {
            subtasks.remove(id);
            for (Epic epic : epics.values()) {
                epic.deleteSubTaskId(id);
                checkSubTasksStatus();
            }
        }
        else  {
            System.out.println("Task with id: " + id + " does not exist!");
        }
    }
    public List<SubTask> getAllSubTasks() {
        if (subtasks.isEmpty()) {
            System.out.println("SubTask list is empty");
            return new ArrayList<>();
        }
        else {
            return new ArrayList<>(subtasks.values());
        }
    }

    // CHECKING SUBTASKS STATUS
    private void checkSubTasksStatus() {
        for (Epic epic : epics.values()) {
            if (epic.getSubTasksId().isEmpty()) {
                epic.setStatus(Status.NEW);
                return;
            }

            boolean allNew = true;
            boolean allDone = true;
            for (SubTask subtask : subtasks.values()) {
                if (subtask.getStatus() != Status.NEW) {
                    allNew = false;
                }
                if (subtask.getStatus() != Status.DONE) {
                    allDone = false;
                }
            }

            if (allNew) {
                epic.setStatus(Status.NEW);
            } else if (allDone) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}
