package manager;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    private static long manageId = 0L;
    private Map<Long, Task> tasks;
    private Map<Long, SubTask> subtasks;
    private Map<Long, Epic> epics;

    public TaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public static long manageId() {
        return ++manageId;
    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public Task getTask(long id) {
        return tasks.get(id);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void deleteTask(long id) {
        tasks.remove(id);
    }

    // CRUD-операции для подзадач
    public void addSubtask(SubTask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = subtask.getEpic();
        epic.addSubtask(subtask);
        epics.put(epic.getId(), epic);
    }

    public SubTask getSubtask(long id) {
        return subtasks.get(id);
    }

    public List<SubTask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void updateSubtask(SubTask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = subtask.getEpic();
        epic.calculateStatus();
        epics.put(epic.getId(), epic);
    }

    public void deleteSubtask(long id) {
        SubTask subtask = subtasks.remove(id);
        Epic epic = subtask.getEpic();
        epic.removeSubtask(subtask);
        epic.calculateStatus();
        epics.put(epic.getId(), epic);
    }

    // CRUD-операции для эпиков
    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public Epic getEpic(long id) {
        return epics.get(id);
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void deleteEpic(long id) {
        Epic epic = epics.remove(id);
        for (SubTask subtask : epic.getSubtasks()) {
            subtasks.remove(subtask.getId());
        }
    }

    // Получение списка подзадач эпика
    public List<SubTask> getEpicSubtasks(Epic epic) {
        return epic.getSubtasks();
    }

    // Печать списков
    public void printTasks() {
        for (Task task : getAllTasks()) {
            System.out.println(task);
        }
    }

    public void printEpics() {
        for (Epic epic : getAllEpics()) {
            System.out.println(epic);
        }
    }

    public void printSubtasks(Epic epic) {
        for (SubTask subtask : epic.getSubtasks()) {
            System.out.println(subtask);
        }
    }
}
