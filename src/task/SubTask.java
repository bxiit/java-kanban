package task;
public class SubTask extends Task {
    private final long epicId;

    public SubTask(String name, String description, Epic epic) {
        super(name, description);
        this.epicId = epic.getId();
    }

    public long getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", " + super.toString() +
                '}';
    }
}
