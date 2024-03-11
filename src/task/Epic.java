package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Long> subTasksIds;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subTasksIds = new ArrayList<>();
    }

    public List<Long> getSubTasksIds() {
        return new ArrayList<>(subTasksIds);
    }

    public Long addSubTaskId(long subTaskId) {
        if (subTaskId == this.getId()) {
            return (long) -1;
        }
        subTasksIds.add(subTaskId);
        return subTaskId;
    }
    public void deleteSubTaskId(long id) {
        if (subTasksIds.contains(id)) {
            subTasksIds.remove(id);
        }
    }

    public void deleteAllSubTaskIds() {
        subTasksIds.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                super.toString() +
                ", subTasks ID's=" + subTasksIds +
                '}';
    }
}
