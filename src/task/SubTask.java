package task;
public class SubTask extends Task {
    private final String epicId;

    public SubTask(long id, String name, String description, String epicId, Status status) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public String getEpicId() {
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
