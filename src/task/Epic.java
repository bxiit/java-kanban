package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    public List<SubTask> subTasks;
    public Epic(String title, String description) {
        super(title, description);
        subTasks = new ArrayList<>();
    }

    public void addSubTask(SubTask subTask) {
        subTask.epic = this;
        subTask.setState(State.NEW);
        subTasks.add(subTask);
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public SubTask getSubTask(Long id) {
        return subTasks.get(id.intValue() - 1);
    }

    public State getState() {
        if (checkForDone()) {
            setState(State.DONE);
            return State.DONE;
        } else if (checkForNew()) {
            setState(State.NEW);
            return State.NEW;
        } else {
            setState(State.IN_PROGRESS);
            return State.IN_PROGRESS;
        }
    }

    public boolean checkForDone() {
        for (SubTask subTask : subTasks) {
            if (subTask.getState() != State.DONE) {
                return false;
            }
        }
        return true;
    }

    public boolean checkForNew() {
        for (SubTask subTask : subTasks) {
            if (subTask.getState() != State.NEW) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Epic{" + super.toString() +
                "subTasks=" + subTasks +
                '}';
    }
}
