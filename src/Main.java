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
        Epic epic1 = new Epic(taskManager.takeId(), "Epic1", "Epic1 desc", Status.NEW);
        Epic epic2 = new Epic(taskManager.takeId(), "Epic2", "Epic2 desc", Status.NEW);
        SubTask subTask2 = new SubTask(taskManager.takeId(), "SubTask1", "SubTask1 desc", String.valueOf(epic1.getId()), Status.NEW);
        SubTask subTask3 = new SubTask(taskManager.takeId(), "SubTask2", "SubTask2 desc", String.valueOf(epic1.getId()), Status.NEW);
        SubTask subTask4 = new SubTask(taskManager.takeId(), "SubTask3", "SubTask3 desc", String.valueOf(epic1.getId()), Status.NEW);
        SubTask subTask5 = new SubTask(taskManager.takeId(), "SubTask4", "SubTask4 desc", String.valueOf(epic2.getId()), Status.NEW);

        // adding
        epic1.addSubTaskId(subTask2.getId());
        epic1.addSubTaskId(subTask3.getId());
        epic1.addSubTaskId(subTask4.getId());

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        taskManager.addSubTask(subTask4);

        // printing all epics and subs
        System.out.println("printing all epics and subs");
        taskManager.printAllEpics();
        taskManager.printAllSubTasks();

        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.DONE);
        subTask4.setStatus(Status.DONE);
        taskManager.deleteSubTaskById(3);
        taskManager.deleteSubTaskById(4);
        taskManager.deleteSubTaskById(5);

        taskManager.deleteEpicById(1);


        System.out.println("printing all epics and subs after deleting epic1 and subs:2,3,4");
        taskManager.printAllEpics();
        taskManager.printAllSubTasks();

        epic2.addSubTaskId(subTask5.getId());
        taskManager.addSubTask(subTask5);
        taskManager.addEpic(epic2);

        System.out.println("print all epics and subs after adding epic2 and sub5");
        taskManager.printAllEpics();
        taskManager.printAllSubTasks();

        System.out.println("getting sub with id = 6");
        System.out.println("taskManager.getSubTaskById(6) = " + taskManager.getSubTaskById(6));

        taskManager.updateEpicNew(2, "newname", "newdesc");

        System.out.println("taskManager.getEpicById(2) = " + taskManager.getEpicById(2));
    }
}
