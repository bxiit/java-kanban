package task;

public class SubTask extends Task {
    private long epicId;
    private String title;
    private String description;
    private Status status;

    public SubTask(long epicId, String title, String description, Status status) {
        super(epicId, title, description, status);
        this.title = title;
        this.description = description;
        this.status = status;
        this.epicId = epicId;
    }

    public void setEpicId(long epicId) {
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
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

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}