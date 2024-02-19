package task;

import java.util.HashMap;

public class TaskManager {
    public static Long ID = 0L;
    HashMap<Long, Task> tasks = new HashMap<>();

    public TaskManager() {
    }

    public void printAllTasks() {
        for (Long key : tasks.keySet()) {
            System.out.println(tasks.get(key));
        }
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public Task getTask(Long ID) {
        return tasks.get(ID);
    }

    public void addTask(Task task) {
        task.setID(++ID);
        tasks.put(ID, task);
    }

    public void updateTask(Task task) {
        tasks.put(task.getID(), task);
    }

    public void removeTask(Long ID) {
        tasks.remove(ID);
    }

    public void changeState(Long ID, State state) {
        tasks.get(ID).setState(state);
    }

    public void printTask(Long ID) {
        System.out.println(tasks.get(ID).getTitle());
        System.out.println(tasks.get(ID).getDescription());
        System.out.println(tasks.get(ID).getState());
    }
}
