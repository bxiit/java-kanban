import manager.TaskManager;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task(taskManager.countId(), "Task", "Description", Status.NEW);
        Epic epic1 = new Epic(taskManager.countId(), "Epic", "Description", Status.NEW);
        SubTask subTask1 = new SubTask(epic1.getId(), "SubTask", "Description", Status.NEW);
        SubTask subTask2 = new SubTask(epic1.getId(), "SubTask", "Description", Status.NEW);
        epic1.addSubtask(subTask1);
        epic1.addSubtask(subTask2);
        taskManager.addEpic(epic1);
        taskManager.addTask(task1);

//        taskManager.changeSubTaskStatus(epic1, subTask1, Status.DONE);

        taskManager.printAllTasks();
//        taskManager.changeTaskStatus(1, Status.IN_PROGRESS);
        taskManager.printAllTasks();

        taskManager.printAllEpics();

        System.out.println("taskManager.getEpicById(1) = " + taskManager.getTaskById(1));

        taskManager.removeTaskById(1);
        taskManager.printAllTasks();

        System.out.println("taskManager.getEpicById(1) = " + taskManager.getTaskById(1));
    }
}