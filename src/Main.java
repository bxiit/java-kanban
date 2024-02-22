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
        Task task1 = new Task("Task1", "Task1 desc");
        Task task2 = new Task("Task2", "Task2 desc");

        // Создание эпиков и подзадач
        Epic epic1 = new Epic("title epic1", "Эпик 1");
        SubTask subtask1 = new SubTask("title sub1", "sub1", epic1);
        SubTask subtask2 = new SubTask("title sub2", "sub2", epic1);
        epic1.addSubtask(subtask1);
        epic1.addSubtask(subtask2);

        Epic epic2 = new Epic("title epic2", "epic2");
        SubTask subtask3 = new SubTask("title sub3", "sub3", epic2);
        epic2.addSubtask(subtask3);

        // Добавление задач
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(epic1);
        taskManager.addTask(epic2);

        // Печать списков
        System.out.println("Tasks:");
        taskManager.printTasks();
        System.out.println("Epics:");
        taskManager.printEpics();
        System.out.println("Subtasks of Epic1:");
        taskManager.printSubtasks(epic1);

        // Изменение статусов
        task1.setStatus(Status.IN_PROGRESS);
        subtask1.setStatus(Status.DONE);

        // Обновление задач
        taskManager.updateTask(task1);
        taskManager.updateTask(subtask1);

        // Печать после изменений
        System.out.println("Tasks agter update:");
        taskManager.printTasks();
        System.out.println("Epics after update:");
        taskManager.printEpics();

        // Удаление задач
        taskManager.deleteTask(task2.getId());
        taskManager.deleteTask(epic2.getId());

        // Печать после удаления
        System.out.println("Tasks after delete:");
        taskManager.printTasks();
        System.out.println("Epics after delete:");
        taskManager.printEpics();
    }
}
