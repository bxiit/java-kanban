package task;

public class SubTask extends Task {
    public Epic epic;

    public SubTask(String title, String description, Epic epic) {
        super(title, description);
        this.epic = epic;
    }
}
