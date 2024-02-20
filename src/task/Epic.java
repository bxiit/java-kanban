package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final long id;
    private String title;
    private String description;
    private Status status;
    private List<SubTask> subtasks;

    public Epic(long id, String title, String description, Status status) {
        super(id, title, description, status);
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(SubTask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubTask(SubTask subTask) {
        subtasks.remove(subTask);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    public void setSubtasks(List<SubTask> subtasks) {
        this.subtasks = subtasks;
    }

    public List<SubTask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtasks=" + subtasks +
                '}';
    }

    @Override
    public long getId() {
        return id;
    }
}