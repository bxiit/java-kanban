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
        SubTask subTask2 = new SubTask("SubTask1", "SubTask1 desc", epic1.getId());
        SubTask subTask3 = new SubTask("SubTask2", "SubTask2 desc", epic1.getId());
        SubTask subTask4 = new SubTask("SubTask3", "SubTask3 desc", epic1.getId());

        epic1.addSubTaskId(subTask2.getId());
        epic1.addSubTaskId(subTask3.getId());
        epic1.addSubTaskId(subTask4.getId());

        taskManager.addEpic(epic1);

        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        taskManager.addSubTask(subTask4);



        taskManager.printAllEpics();

        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.DONE);
        subTask4.setStatus(Status.DONE);
        taskManager.deleteSubTaskById(3);

        taskManager.printAllEpics();


    }
}
