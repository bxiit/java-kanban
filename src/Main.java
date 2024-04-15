import manager.InMemoryTaskManager;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.SubTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {

    void main() {
        // Менеджер задач
        TaskManager taskManager = new InMemoryTaskManager();

        LocalDateTime january2024 = LocalDateTime.of(2024, Month.JANUARY,
                1, 0, 0, 0);
        LocalDateTime february2024 = LocalDateTime.of(2024, Month.FEBRUARY,
                1, 0, 0);

        Epic epic = new Epic("epic1", "epic1 desc");
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("subtask3", "subtask3 desc", epic.getId(),
                Status.IN_PROGRESS, Duration.ofDays(60), january2024);
        // subtask2 пересекает subtask1
        SubTask subTask2 = new SubTask("subtask2", "subtask2 desc", epic.getId(),
                Status.IN_PROGRESS, Duration.ofDays(60), february2024);

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.printAllSubTasks();

        System.out.println(taskManager.getAllSubTasks().size());
    }
}
