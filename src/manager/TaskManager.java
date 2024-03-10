package manager;

import history.HistoryManager;
import task.Epic;
import task.SubTask;
import task.Task;

import java.util.List;

public interface TaskManager {

    Long addTask(Task task);

    void updateTask(Task task);

    void printAllTasks();

    Task getTaskById(long id);

    void deleteTaskById(long id);

    List<Task> getAllTasks();

    // EPICS
    Long addEpic(Epic epic);

//    void updateEpic(long id, Epic epic);
    void updateEpic(Epic epic);

    void printAllEpics();

    Epic getEpicById(long id);

    void deleteEpicById(long id);

    List<Epic> getAllEpics();

    // SUBTASKS
    Long addSubTask(SubTask subTask);

    void printAllSubTasks();

    SubTask getSubTaskById(long id);

    void updateSubTask(SubTask subTask);

    void deleteSubTaskById(long id);

    List<SubTask> getAllSubTasks();

    List<SubTask> getSubTasksByEpicId(long epicId);

    HistoryManager getHistoryManager();

    List<Task> getHistory();
}
