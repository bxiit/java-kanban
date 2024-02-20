package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    public static long id = 0L;
    HashMap<Long, Task> tasks = new HashMap<>();
    HashMap<Epic, ArrayList<SubTask>> epics = new HashMap<>();
    public void printAllTasks() {
        System.out.println(tasks);
    }

    public void printAllEpics() {
        System.out.println(epics);
    }

    public long countId() {
        return ++id;
    }

    // (a)
    public HashMap<Long, Task> getTasks() {
        return tasks;
    }
    public HashMap<Epic, ArrayList<SubTask>> getEpics() {
        return epics;
    }

    // (b)
    public void removeAllTasks() {
        tasks.clear();
    }
    public void removeAllEpics() {
        epics.clear();
    }

    // (c)
    public Task getTaskById(long id) {
        if (tasks.get(id) != null) {
            return tasks.get(id);
        } else if (getEpicById(id) != null) {
            return getEpicById(id);
        } else {
            return null;
        }
    }
    public Epic getEpicById(long id) {
        for (Epic epic : epics.keySet()) {
            if (epic.getId() == id) {
                return epic;
            }
        }
        return null;
    }

    // (d)
    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }
    public void addEpic(Epic epic) {
        epics.put(epic, (ArrayList<SubTask>) epic.getSubtasks());
    }

    // (e)
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }
    public void updateEpic(Epic epic) {
        epics.put(epic, (ArrayList<SubTask>) epic.getSubtasks());
    }

    // (f)
    public void removeTaskById(long id) {
        tasks.remove(id);
    }
    public void removeEpicById(long id) {
        for (Epic epic : epics.keySet()) {
            if (epic.getId() == id) {
                epics.remove(epic);
            }
        }
    }

    public void changeTaskStatus(long id, Status status) {
        tasks.get(id).setStatus(status);
    }

    public void changeSubTaskStatus(Epic epic, SubTask subtask, Status status) {
        subtask.setStatus(status);

        if (checkEpicStatusForDONE()) {
            epic.setStatus(Status.DONE);
        } else if (checkEpicStatusForNEW()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private boolean checkEpicStatusForDONE() {
        for (Epic epic : epics.keySet()) {
            for (SubTask subtask : epic.getSubtasks()) {
                if (!Status.DONE.equals(subtask.getStatus())) {
                    epic.setStatus(Status.IN_PROGRESS);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkEpicStatusForNEW() {
        for (Epic epic : epics.keySet()) {
            for (SubTask subtask : epic.getSubtasks()) {
                if (!Status.NEW.equals(subtask.getStatus())) {
                    epic.setStatus(Status.IN_PROGRESS);
                    return false;
                }
            }
        }
        return true;
    }
}
