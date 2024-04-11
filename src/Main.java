import manager.FileBackedTaskManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Менеджер задач
        TaskManager taskManager = new InMemoryTaskManager();


        // Создание задач
        Epic epic1 = new Epic("Epic1", "Epic1 desc");
        Epic epic2 = new Epic("Epic2", "Epic2 desc");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        SubTask subTask3 = new SubTask("SubTask3", "SubTask3 desc", epic1.getId(), Status.NEW);
        SubTask subTask4 = new SubTask("SubTask4", "SubTask4 desc", epic1.getId(), Status.NEW);
        SubTask subTask5 = new SubTask("SubTask5", "SubTask5 desc", epic1.getId(), Status.NEW);

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

        logger.info("PRINTING HISTORY");
        for (Task task : history) {
            logger.info(task.toString());
        }

        taskManager.deleteEpicById(1);

        List<Task> historyAfterDeletingAnEpic = taskManager.getHistory();

        logger.info("PRINTING HISTORY");
        for (Task task : historyAfterDeletingAnEpic) {
            logger.info(task.toString());
        }

        File file = new File("test.txt");
        TaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
    }
}
