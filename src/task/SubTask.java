package task;
public class SubTask extends Task {

    private Epic epic;

    public SubTask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
    }

    public SubTask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        return String.format("Подзадача №%d: %s (%s) - Эпик: %s", getId(), getName(), getStatus(), epic.getName());
    }
}
