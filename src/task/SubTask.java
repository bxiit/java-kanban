package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

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

    public SubTask(String name, String description, long epicId, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(Long id,
                   String name,
                   String description,
                   long epicId,
                   Status status,
                   Duration duration,
                   LocalDateTime startTime
    ) {
        super(id, name, description, status, duration, startTime);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subTask = (SubTask) o;
        return super.equals(subTask) &&
               Objects.equals(this.getEpicId(), subTask.getEpicId());
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(this.epicId);
    }

    @Override
    public String toString() {
        return STR."SubTask{epicId=\{epicId}, \{super.toString()}\{'}'}";
    }
}
