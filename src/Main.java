import manager.InMemoryTaskManager;
import manager.TaskManager;
import server.HttpTaskServer;
import task.Epic;
import task.Status;
import task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {

    public static void main(String[] args) throws IOException {
        // Менеджер задач
        TaskManager taskManager = new InMemoryTaskManager();

        LocalDateTime january2024 = LocalDateTime.of(2024, Month.JANUARY,
                1, 0, 0, 0);
        LocalDateTime february2024 = LocalDateTime.of(2024, Month.FEBRUARY,
                1, 0, 0);
        LocalDateTime march2024 = LocalDateTime.of(2024, Month.MARCH,
                1, 0, 0, 0);

        Epic epic = new Epic("epic1", "epic1 desc");
        taskManager.addEpic(epic);

        Task task1 = new Task("task3", "task3 desc",
                Status.IN_PROGRESS, Duration.ofDays(60), january2024);
        // task2 пересекает task1
        Task task2 = new Task("task2", "task2 desc",
                Status.IN_PROGRESS, Duration.ofDays(60), february2024);

        Task taskWithoutTime = new Task("task without time", "task without time desc",
                Status.IN_PROGRESS);

        Task task3 = new Task("task march", "task march desc",
                Status.IN_PROGRESS, Duration.ofDays(7), march2024);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(taskWithoutTime);
        taskManager.addTask(task3);

        System.out.println("PRINT ALL TASKS");
        taskManager.printAllTasks();
        System.out.println("PRIORITIZED TASKS");
        taskManager.getPrioritizedTasks().forEach(System.out::println);

        System.out.println("PRIORITIZED TASKS");
        taskManager.getPrioritizedTasks().forEach(System.out::println);

        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.listenAndServe();
    }
}
