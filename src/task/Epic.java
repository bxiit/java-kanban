package task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Epic extends Task {
    private final Set<Long> subTasksIds;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subTasksIds = new HashSet<>();
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
