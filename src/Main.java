import task.*;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Epic epicWithTwo = new Epic("Epic epicWithTwo", "Epic epicWithTwo Description");
        epicWithTwo.addSubTask(new SubTask("SubTask 1 epicWithTwo", "SubTask 1 epicWithTwo Description", epicWithTwo));
        epicWithTwo.addSubTask(new SubTask("SubTask 2 epicWithTwo", "SubTask 2 epicWithTwo Description", epicWithTwo));

        Task task = new Task("Task task", "Task one Description");

        taskManager.addTask(epicWithTwo);
        taskManager.addTask(task);

        taskManager.printAllTasks();
    }
}