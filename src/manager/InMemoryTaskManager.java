package manager;

import history.HistoryManager;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected long manageId = 0L;
    protected final Map<Long, Task> tasks;
    protected final Map<Long, SubTask> subtasks;
    protected final Map<Long, Epic> epics;
    protected final HistoryManager inMemoryHistoryManager;
    protected final Map<LocalDateTime, Task> sortedTasks;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.inMemoryHistoryManager = Managers.getDefaultHistory();
        this.sortedTasks = new TreeMap<>();
    }

    private boolean isTaskIntersectOtherTask(Task task) {
        LocalDateTime p1 = task.getStartTime();
        LocalDateTime p2 = task.getEndTime();

        return getPrioritizedTasks().stream()
                .anyMatch(otherTask -> {
                    LocalDateTime p3 = otherTask.getStartTime();
                    LocalDateTime p4 = otherTask.getEndTime();
                    return (p1.isAfter(p3) && p1.isBefore(p4)) ||
                           (p4.isBefore(p1) && p4.isAfter(p2)) ||
                           (p3.isAfter(p1) && p3.isBefore(p2)) ||
                           (p2.isAfter(p3) && p2.isBefore(p4));
                });
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTasks.values());
    }

    private void addHistory(Task task) {
        inMemoryHistoryManager.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    private long takeId() {
        return ++manageId;
    }

    private boolean deleteTaskFromSortedTasks(Task task) {
        if (task.getStartTime() == null) {
            return true;
        }
        return sortedTasks.remove(task.getStartTime(), task);
    }

    private Long add(Task task) {
        if (TaskType.EPIC.equals(task.getType())) {
            Epic epic = (Epic) task;
            epic.setId(takeId());
            epics.put(epic.getId(), epic);
            return epic.getId();
        }
        if (task.getStartTime() != null && isTaskIntersectOtherTask(task)) {
            return null;
        }
        task.setId(takeId());
        if (task.getStartTime() == null) {
            switch (task.getType()) {
                case TASK -> tasks.put(task.getId(), task);
                case SUBTASK -> {
                    SubTask subtask = (SubTask) task;
                    subtasks.put(task.getId(), subtask);
                    epics.get(subtask.getEpicId()).addSubTaskId(subtask.getId());
                    checkSubTasksStatus(subtask.getEpicId());
                }
                default -> throw new RuntimeException("Неизвестный тип задачи: " + task.getType());
            }
            return task.getId();
        }
        addTimedTask(task);
        return task.getId();
    }

    private void addTimedTask(Task task) {
        if (TaskType.TASK.equals(task.getType())) {
            tasks.put(task.getId(), task);
        } else if (TaskType.SUBTASK.equals(task.getType())) {
            SubTask subtask = (SubTask) task;
            subtasks.put(task.getId(), subtask);
            epics.get(subtask.getEpicId()).addSubTaskId(subtask.getId());
            checkStartAndEndTimeOfEpic(subtask);
            checkSubTasksStatus(subtask.getEpicId());
        }
        sortedTasks.put(task.getStartTime(), task);
    }

    @Override
    public Long addTask(Task task) {
        return add(task);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            if (task.getStartTime() == null) {
                return;
            }
            deleteTaskFromSortedTasks(task);
            sortedTasks.put(task.getStartTime(), task);
        }
    }

    @Override
    public void printAllTasks() {
        tasks.values().forEach(System.out::println);
    }

    @Override
    public Optional<Task> getTaskById(long id) {
        Optional<Task> optionalTask = Optional.ofNullable(tasks.get(id));
        if (optionalTask.isEmpty()) {
            return Optional.empty();
        }
        addHistory(optionalTask.get());
        return optionalTask;
    }

    @Override
    public void deleteTaskById(long id) {
        if (tasks.containsKey(id)) {
            inMemoryHistoryManager.remove(id);
            Task removedTask = tasks.remove(id);
            if (removedTask.getStartTime() == null) {
                return;
            }
            deleteTaskFromSortedTasks(removedTask);
        }
    }

    @Override
    public List<Task> getAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Task list is empty");
            return new ArrayList<>();
        } else {
            return new ArrayList<>(tasks.values());
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.values().forEach(task -> {
            inMemoryHistoryManager.remove(task.getId());
            deleteTaskFromSortedTasks(task);
        });
        tasks.clear();
    }

    // EPICS
    @Override
    public Long addEpic(Epic epic) {
        return add(epic);
    }

    @Override
    public void updateEpic(Epic epicUpdated) {
        if (epics.containsKey(epicUpdated.getId())) {
            Epic epicToUpdate = epics.get(epicUpdated.getId());
            epicToUpdate.setName(epicUpdated.getName());
            epicToUpdate.setDescription(epicToUpdate.getDescription());

            epics.put(epicUpdated.getId(), epicUpdated);
        }
    }

    @Override
    public void printAllEpics() {
        epics.values().forEach(System.out::println);
    }

    @Override
    public Optional<Epic> getEpicById(long id) {
        Optional<Epic> optionalEpic = Optional.ofNullable(epics.get(id));

        if (optionalEpic.isEmpty()) {
            return Optional.empty();
        }
        addHistory(optionalEpic.get());
        return optionalEpic;
    }

    @Override
    public void deleteEpicById(long id) {
        inMemoryHistoryManager.remove(id);
        Epic removedEpic = epics.remove(id);
        List<Long> subTasksIds = removedEpic.getSubTasksIds();
        subTasksIds.stream()
                .map(subTaskId -> {
                    inMemoryHistoryManager.remove(subTaskId);
                    SubTask removedSubTask = subtasks.remove(subTaskId);
                    return removedSubTask.getStartTime();
                })
                .filter(Objects::nonNull)
                .forEach(sortedTasks::remove);
    }

    @Override
    public List<Epic> getAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("Epics list is empty");
            return new ArrayList<>();
        } else {
            return new ArrayList<>(epics.values());
        }
    }

    @Override
    public void deleteAllEpics() {
        epics.values().forEach(epic -> {
            epic.getSubTasksIds().forEach(inMemoryHistoryManager::remove);
            inMemoryHistoryManager.remove(epic.getId());
            deleteTaskFromSortedTasks(epic);
        });
        deleteAllSubTasks();
        epics.clear();
    }

    // SUBTASKS
    @Override
    public Long addSubTask(SubTask subTask) {
        if (subTask.getEpicId() - 1 == manageId) {
            return -1L;
        }
        return add(subTask);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subtasks.containsKey(subTask.getId())) {
            subtasks.put(subTask.getId(), subTask);
            checkSubTasksStatus(subTask.getEpicId());
            if (subTask.getStartTime() != null) {
                deleteTaskFromSortedTasks(subTask);
                sortedTasks.put(subTask.getStartTime(), subTask);
            }
        }
    }

    @Override
    public void printAllSubTasks() {
        subtasks.values().forEach(System.out::println);
    }

    @Override
    public Optional<SubTask> getSubTaskById(long id) {
        Optional<SubTask> optionalSubTask = Optional.ofNullable(subtasks.get(id));

        if (optionalSubTask.isEmpty()) {
            return Optional.empty();
        }
        addHistory(optionalSubTask.get());
        return optionalSubTask;
    }

    @Override
    public void deleteSubTaskById(long id) {
        SubTask subTask = subtasks.remove(id);
        if (subTask != null) {
            inMemoryHistoryManager.remove(id);
            Epic epic = epics.get(subTask.getEpicId());
            epic.deleteSubTaskId(id);
            checkSubTasksStatus(subTask.getEpicId());
            if (subTask.getStartTime() == null) {
                return;
            }
            deleteTaskFromSortedTasks(subTask);
        }
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        if (subtasks.isEmpty()) {
            System.out.println("SubTask list is empty");
            return new ArrayList<>();
        } else {
            return new ArrayList<>(subtasks.values());
        }
    }

    @Override
    public List<SubTask> getSubTasksByEpicId(long epicId) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        subtasks.values().stream().filter(subTask -> subTask.getEpicId() == epicId).forEach(subTasks::add);
        return subTasks;
    }

    @Override
    public void deleteAllSubTasks() {
        subtasks.values().forEach(subTask -> {
            inMemoryHistoryManager.remove(subTask.getId());
            Epic epic = epics.get(subTask.getEpicId());
            checkSubTasksStatus(epic.getId());
            checkStartAndEndTimeOfEpic(subTask);
            epic.deleteAllSubTaskIds();
            deleteTaskFromSortedTasks(subTask);
        });
        subtasks.clear();
    }

    // CHECKING SUBTASKS STATUS
    private void checkSubTasksStatus(long epicId) {
        Epic epicToCheck = epics.get(epicId);

        if (epicToCheck.getSubTasksIds() == null) {
            epicToCheck.setStatus(Status.NEW);
            return;
        }

        epicToCheck.getSubTasksIds().forEach(subTaskId -> {
            boolean allNew = true;
            boolean allDone = true;

            if (!subtasks.containsKey(subTaskId)) {
                return;
            }

            if (subtasks.get(subTaskId).getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subtasks.get(subTaskId).getStatus() != Status.DONE) {
                allDone = false;
            }

            if (allNew) {
                epicToCheck.setStatus(Status.NEW);
            } else if (allDone) {
                epicToCheck.setStatus(Status.DONE);
            } else {
                epicToCheck.setStatus(Status.IN_PROGRESS);
            }
        });
    }

    // SETTING START TIME AND END TIME OF AN EPIC OF THE SUBTASK
    private void checkStartAndEndTimeOfEpic(SubTask subTask) {
        if (subTask.getStartTime() == null || subTask.getDuration() == null) {
            return;
        }
        Epic epic = epics.get(subTask.getEpicId());
        setStartAndDurationOfEpic(epic);
    }

    private void setStartAndDurationOfEpic(Epic epic) {
        Comparator<SubTask> comparator = Comparator.comparing(Task::getStartTime);

        List<SubTask> subTaskList = epic.getSubTasksIds().stream()
                .map(subtasks::get)
                .filter(subTask -> subTask.getStartTime() != null && subTask.getDuration() != null)
                .sorted(comparator)
                .toList();

        LocalDateTime startTime = subTaskList.getFirst().getStartTime();
        LocalDateTime endTime = subTaskList.getLast().getEndTime();
        Duration duration = Duration.between(startTime, endTime);

        epic.setStartTime(startTime);
        epic.setDuration(duration);
    }
}
