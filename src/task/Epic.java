package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Long> subTasksIds = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(Long id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Epic(Long id, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
        endTime = startTime.plus(duration);
    }

    public List<Long> getSubTasksIds() {
        return new ArrayList<>(subTasksIds);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public Long addSubTaskId(long subTaskId) {
        if (subTaskId == this.getId()) {
            return (long) -1;
        }
        subTasksIds.add(subTaskId);
        return subTaskId;
    }

    public void deleteSubTaskId(long id) {
        subTasksIds.remove(id);
    }

    public void deleteAllSubTaskIds() {
        subTasksIds.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return super.equals(epic) &&
               Objects.equals(this.subTasksIds, epic.getSubTasksIds());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Epic{" +
               super.toString() +
               "subTasksIds=" + subTasksIds +
               ", endTime=" + endTime +
               '}';
    }
}
