package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Long> subTasksIds;

    public Epic(long id, String name, String description, Status status) {
        super(id, name, description, status);
        subTasksIds = new ArrayList<>();
    }

    public List<Long> getSubTasksIds() {
        return new ArrayList<>(subTasksIds);
    }

    public void addSubTaskId(Long subTaskId) {
        subTasksIds.add(subTaskId);
    }
    public void deleteSubTaskId(long id) {
        if (subTasksIds.contains(id)) {
            subTasksIds.remove(id);
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                super.toString() +
                ", subTasks ID's=" + subTasksIds +
                '}';
    }
}
