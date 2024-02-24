package task;
public class SubTask extends Task {
    private final long epicId;

    public SubTask(String name, String description, long epicId) {
        super(name, description, Status.NEW);
        this.epicId = epicId;
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
