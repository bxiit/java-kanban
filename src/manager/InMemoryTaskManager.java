package manager;

import history.HistoryManager;
import history.InMemoryHistoryManager;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected long manageId = 0L;
    protected final Map<Long, Task> tasks;
    protected final Map<Long, SubTask> subtasks;
    protected final Map<Long, Epic> epics;
    protected final HistoryManager inMemoryHistoryManager;
    protected final TreeMap<Task, LocalDateTime> sortedTasks;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.inMemoryHistoryManager = new InMemoryHistoryManager();
        this.sortedTasks = new TreeMap<>(Comparator.comparing(Task::getStartTime));
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
        return sortedTasks.keySet().stream().toList();
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

    @Override
    public Long addTask(Task task) {
        task.setId(takeId());
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null && !isTaskIntersectOtherTask(task)) {
            sortedTasks.put(task, task.getStartTime());
        }
        return task.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
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
            tasks.remove(id);
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
            sortedTasks.remove(task);
        });
        tasks.clear();
    }

    // EPICS
    @Override
    public Long addEpic(Epic epic) {
        epic.setId(takeId());
        epics.put(epic.getId(), epic);
//        if (epic.getStartTime() != null && !isTaskIntersectOtherTask(epic)) {
//            sortedTasks.put(epic, epic.getStartTime());
//        }
        return epic.getId();
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

        removedEpic.getSubTasksIds().forEach(subTaskId -> {
            inMemoryHistoryManager.remove(subTaskId);
            subtasks.remove(subTaskId);
        });
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
            inMemoryHistoryManager.remove(epic.getId());
            epic.getSubTasksIds().forEach(inMemoryHistoryManager::remove);
        });
        epics.clear();
    }

    // SUBTASKS
    @Override
    public Long addSubTask(SubTask subTask) {
        boolean intersection = isTaskIntersectOtherTask(subTask);

        if (intersection) {
            return null;
        }

        subTask.setId(takeId());
        if (subTask.getEpicId() == subTask.getId()) {
            return (long) -1;
        }

        subtasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpicId()).addSubTaskId(subTask.getId());
        if (subTask.getStartTime() != null) {
            sortedTasks.put(subTask, subTask.getStartTime());
            checkStartAndEndTimeOfEpic(subTask);
        }

        checkSubTasksStatus(subTask.getEpicId());
        return subTask.getId();
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
    public void updateSubTask(SubTask subTask) {
        if (subtasks.containsKey(subTask.getId())) {
            subtasks.put(subTask.getId(), subTask);
            checkSubTasksStatus(subTask.getEpicId());
        }
    }

    @Override
    public void deleteSubTaskById(long id) {
        SubTask subTask = subtasks.remove(id);
        if (subTask != null) {
            inMemoryHistoryManager.remove(id);
            Epic epic = epics.get(subTask.getEpicId());
            epic.deleteSubTaskId(id);
            checkSubTasksStatus(subTask.getEpicId());
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
            if (epic != null) {
                checkSubTasksStatus(epic.getId());
                epic.deleteAllSubTaskIds();
            }
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
        Epic epic = epics.get(subTask.getEpicId());
        List<SubTask> subTasksByEpicId = getSubTasksByEpicId(epic.getId());

        epic.setEarliestSubTask(subTasksByEpicId);
        epic.setLatestSubTask(subTasksByEpicId);
        if (epic.getDuration() == null) {
            epic.setDuration(subTask.getDuration());
            return;
        }
        epic.setDuration(epic.getDuration().plus(subTask.getDuration()));
    }
}
