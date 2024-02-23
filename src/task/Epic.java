package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Long> subTasksId;

    public Epic(String name, String description) {
        super(name, description);
        subTasksId = new ArrayList<>();
    }

    public List<Long> getSubTasksId() {
        return new ArrayList<>(subTasksId);
    }

    public void addSubTaskId(long subTaskId) {
        subTasksId.add(subTaskId);
    }
    public void deleteSubTaskId(long id) {
        if (subTasksId.contains(id)) {
            subTasksId.remove(id);
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                super.toString() +
                ", subTasks ID's=" + subTasksId +
                '}';
    }
}
