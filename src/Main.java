import manager.TaskManager;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

public class Main {

    public static void main(String[] args) {
        // Менеджер задач
        TaskManager taskManager = new TaskManager();


        // Создание задач
        Epic epic1 = new Epic("Epic1", "Epic1 desc");
        SubTask subTask2 = new SubTask("SubTask1", "SubTask1 desc", epic1);
        SubTask subTask3 = new SubTask("SubTask2", "SubTask2 desc", epic1);
        SubTask subTask4 = new SubTask("SubTask3", "SubTask3 desc", epic1);
        taskManager.addEpic(epic1);

        taskManager.addSubTask(epic1, subTask2);
        taskManager.addSubTask(epic1, subTask3);
        taskManager.addSubTask(epic1, subTask4);

        taskManager.printAllEpics();

        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.DONE);
        subTask4.setStatus(Status.DONE);
        taskManager.deleteSubTaskById(3);

        taskManager.printAllEpics();


    }
}
