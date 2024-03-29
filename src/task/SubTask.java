package task;

public class SubTask extends Task {
    private final long epicId;

    public SubTask(String name, String description, long epicId, Status status) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public SubTask(Long id, String name, String description, long epicId, Status status) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }
    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }


    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", " + super.toString() +
                '}';
    }
}
