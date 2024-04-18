package manager;

import exception.ManagerSaveException;
import history.HistoryManager;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;
import task.TaskType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String COLUMNS = "id,type,name,status,description,epic,duration,start_time";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public FileBackedTaskManager() {
        file = new File("src", "resources\\" + UUID.randomUUID() + ".csv");
        try {
            boolean createdNewFile = file.createNewFile();
            if (!createdNewFile) {
                throw new RuntimeException("Файл не создан " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long addTask(Task task) {
        super.addTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return task.getId();
    }

    @Override
    public Optional<Task> getTaskById(long id) {
        Optional<Task> task = super.getTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return task;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTaskById(long id) {
        super.deleteTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    // EPIC
    @Override
    public Long addEpic(Epic epic) {
        Long id = super.addEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    @Override
    public Optional<Epic> getEpicById(long id) {
        Optional<Epic> epic = super.getEpicById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return epic;
    }

    @Override
    public void updateEpic(Epic epicUpdated) {
        super.updateEpic(epicUpdated);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void deleteEpicById(long id) {
        super.deleteEpicById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    // SUBTASK
    @Override
    public Long addSubTask(SubTask subTask) {
        Long id = super.addSubTask(subTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    @Override
    public Optional<SubTask> getSubTaskById(long id) {
        Optional<SubTask> subTask = super.getSubTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return subTask;
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSubTaskById(long id) {
        super.deleteSubTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    private void save() throws ManagerSaveException {
        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            StringBuilder builder = new StringBuilder();
            builder.append(COLUMNS + "\n");

            tasks.values().forEach(task -> builder.append(toString(task)).append("\n"));
            epics.values().forEach(epic -> builder.append(toString(epic)).append("\n"));
            subtasks.values().forEach(subTask -> builder.append(toString(subTask)).append("\n"));

            builder.append("\n").append(historyToString(inMemoryHistoryManager));

            writer.write(builder.toString());
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private String toString(Task task) {
        TaskType taskType = task.getType();

        String epicId = taskType == TaskType.SUBTASK ? String.valueOf(((SubTask) task).getEpicId()) : "";
        String duration = task.getDuration() != null ?
                durationToString(task.getDuration()) : "";
        String startTime = task.getStartTime() != null ?
                localDateTimeToString(task.getStartTime()) : "";

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                task.getId(), taskType, task.getName(), task.getStatus().name(),
                task.getDescription(), epicId, duration,
                startTime
        );
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            if (!Files.exists(file.toPath()) && !file.createNewFile()) {
                System.out.println("Нельзя создать файл " + file.getName());
            }
            FileBackedTaskManager manager = new FileBackedTaskManager(file);

            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty()) {
                throw new IOException("Файл пуст");
            }
            lines.removeFirst();

            lines.stream()
                    .takeWhile(line -> !line.isEmpty() || !line.isBlank())
                    .forEach(line -> {
                        Task task = manager.fromString(line);
                        if (task.getStartTime() != null) {
                            manager.sortedTasks.put(task.getStartTime(), task);
                        }
                        if (task instanceof SubTask subTask) {
                            manager.subtasks.put(subTask.getId(), subTask);
                            manager.epics.get(subTask.getEpicId()).addSubTaskId(subTask.getId());
                        } else if (task instanceof Epic epic) {
                            manager.epics.put(epic.getId(), epic);
                        } else {
                            manager.tasks.put(task.getId(), task);
                        }
                    });

            String historyIds = lines.getLast();
            if (historyIds.isEmpty()) {
                return manager;
            }

            FileBackedTaskManager.historyFromString(historyIds)
                    .forEach(historyId -> {
                        manager.getTaskById(historyId);
                        manager.getEpicById(historyId);
                        manager.getSubTaskById(historyId);
                    });

            return manager;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Task fromString(String value) {
        String[] parts = value.split(",");
        long id = Long.parseLong(parts[0]);
        checkTheMaxId(id);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        String duration = parts[6];
        String startTime = parts[7];

        Duration durationConverted = durationFromString(duration);
        LocalDateTime startTimeConverted = localDateTimeFromString(startTime);

        Task task;
        switch (type) {
            case TASK -> task = new Task(id, name, description, status);
            case EPIC -> task = new Epic(id, name, description, status);
            case SUBTASK -> {
                long epicId = Long.parseLong(parts[5]);
                task = new SubTask(id, name, description, epicId, status);
            }
            default -> throw new RuntimeException("Неизвестный тип задачи " + type);
        }

        if (durationConverted != null) {
            task.setDuration(durationConverted);
        }
        if (startTimeConverted != null) {
            task.setStartTime(startTimeConverted);
        }
        return task;
    }

    private LocalDateTime localDateTimeFromString(String startTime) {
        if (startTime.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(startTime, DATE_TIME_FORMATTER);
    }

    private String localDateTimeToString(LocalDateTime localDateTime) {
        return localDateTime.format(DATE_TIME_FORMATTER);
    }

    private Duration durationFromString(String duration) {
        if (duration.isEmpty()) {
            return null;
        }
        String[] dayHourMinute = duration.split(":");
        int days = Integer.parseInt(dayHourMinute[0]);
        int hours = Integer.parseInt(dayHourMinute[1]);
        int minutes = Integer.parseInt(dayHourMinute[2]);

        return Duration.ofDays(days)
                .plusHours(hours)
                .plusMinutes(minutes);
    }

    private String durationToString(Duration duration) {
        if (duration == null) {
            return null;
        }
        return "%02d:%02d:%02d".formatted(duration.toDays(), duration.toHours(), duration.toMinutesPart());
    }

    private static String historyToString(HistoryManager manager) throws ManagerSaveException {
        try {
            List<Task> history = manager.getHistory();
            if (history.isEmpty()) {
                return "";
            }

            return history.stream()
                    .map(Task::getId)
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
        } catch (Exception e) {
            throw new ManagerSaveException("метод historyToString сработал некорректно");
        }
    }

    private void checkTheMaxId(Long currentId) {
        if (currentId > manageId) manageId = currentId;
    }

    private static List<Long> historyFromString(String history) {
        String[] ids = history.split(",");

        return Arrays.stream(ids)
                .map(Long::parseLong)
                .toList();
    }

    public static void main(String[] args) {
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(new File("test.csv"));

        taskManager.printAllTasks();
        taskManager.printAllEpics();
        taskManager.printAllSubTasks();

        System.out.println("HISTORY");
        List<Task> history = taskManager.getHistory();
        history.forEach(System.out::println);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        System.out.println("prioritizedTasks");
        prioritizedTasks.forEach(System.out::println);
    }
}
