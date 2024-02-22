package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<SubTask> subtasks;

    public Epic(String name, String description, Status status, List<SubTask> subtasks) {
        super(name, description, status);
        this.subtasks = subtasks;
    }

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new ArrayList<>();
    }

    public List<SubTask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(SubTask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(SubTask subtask) {
        subtasks.remove(subtask);
    }

    @Override
    public String toString() {
        return String.format("Эпик №%d: %s (%s)", getId(), getName(), getStatus());
    }

    public void calculateStatus() {
        if (subtasks.isEmpty()) {
            setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;
        for (SubTask subtask : subtasks) {
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            setStatus(Status.NEW);
        } else if (allDone) {
            setStatus(Status.DONE);
        } else {
            setStatus(Status.IN_PROGRESS);
        }
    }
}
