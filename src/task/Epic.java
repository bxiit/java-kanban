package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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

    public void setEarliestSubTask(List<SubTask> subTasks) {
        Comparator<SubTask> comparator = (s1, s2) -> {
            // Если даты и времени начала ИЛИ конца нет, то не сортируем
            if (s1.getStartTime() == null || s2.getStartTime() == null ||
                s1.getEndTime() == null || s2.getEndTime() == null) {
                return 0;
            }

            return s1.getStartTime().compareTo(s2.getStartTime());
        };
        subTasks.sort(comparator);
        setStartTime(subTasks.getFirst().getStartTime());
    }

    public void setLatestSubTask(List<SubTask> subTasks) {
        Comparator<SubTask> comparator = (s1, s2) -> {
            // Если даты и времени начала ИЛИ конца ИЛИ продолжительности нет, то не сортируем
            if (s1.getStartTime() == null || s2.getStartTime() == null ||
                s1.getEndTime() == null || s2.getEndTime() == null ||
                s1.getDuration() == null || s2.getDuration() == null) {
                return 0;
            }
            if (s1.getStartTime().plus(s1.getDuration()).isAfter(s2.getStartTime().plus(s2.getDuration())))
                return 1;
            else if (s1.getStartTime().plus(s2.getDuration()).isBefore(s2.getStartTime().plus(s1.getDuration())))
                return -1;
            else
                return 0;
        };
        subTasks.sort(comparator);
        setEndTime(subTasks.getLast().getEndTime());
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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
