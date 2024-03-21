import manager.InMemoryTaskManager;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Менеджер задач
        TaskManager taskManager = new InMemoryTaskManager();


        // Создание задач
        Epic epic1 = new Epic( "Epic1", "Epic1 desc");
        Epic epic2 = new Epic( "Epic2", "Epic2 desc");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        SubTask subTask3 = new SubTask( "SubTask3", "SubTask3 desc", epic1.getId(), Status.NEW);
        SubTask subTask4 = new SubTask( "SubTask4", "SubTask4 desc", epic1.getId(), Status.NEW);
        SubTask subTask5 = new SubTask( "SubTask5", "SubTask5 desc", epic1.getId(), Status.NEW);

        taskManager.addSubTask(subTask3);
        taskManager.addSubTask(subTask4);
        taskManager.addSubTask(subTask5);

        taskManager.getEpicById(1);
        taskManager.getEpicById(2);
        taskManager.getEpicById(1);

        taskManager.getSubTaskById(3);
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(3);
        taskManager.getSubTaskById(4);

        List<Task> history = taskManager.getHistory();

        System.out.println("PRINTING HISTORY");
        for (Task task : history) {
            System.out.println(task);
        }

        taskManager.deleteEpicById(1);
//        taskManager.deleteSubTaskById(3);

        List<Task> historyAfterDeletingAnEpic = taskManager.getHistory();

        System.out.println("PRINTING HISTORY");
        for (Task task : historyAfterDeletingAnEpic) {
            System.out.println(task);
        }
    }
}
