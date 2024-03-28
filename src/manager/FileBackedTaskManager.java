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
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String HEADER = "id,type,name,status,description,epic";
    private Long maxId = 1L;
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Long addTask(Task task) {
        super.addTask(task);
        save();
        return task.getId();
    }

    @Override
    public Task getTaskById(long id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(long id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    // EPIC
    @Override
    public Long addEpic(Epic epic) {
        Long id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public Epic getEpicById(long id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void updateEpic(Epic epicUpdated) {
        super.updateEpic(epicUpdated);
        save();
    }


    @Override
    public void deleteEpicById(long id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    // SUBTASK
    @Override
    public Long addSubTask(SubTask subTask) {
        Long id = super.addSubTask(subTask);
        save();
        return id;
    }

    @Override
    public SubTask getSubTaskById(long id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteSubTaskById(long id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    public void save() {
        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            StringBuilder builder = new StringBuilder();
            builder.append(HEADER + "\n");

            for (Task task : tasks.values()) {
                builder.append(toString(task)).append("\n");
            }

            for (Epic epic : epics.values()) {
                builder.append(toString(epic)).append("\n");
            }

            for (SubTask subTask : subtasks.values()) {
                builder.append(toString(subTask)).append("\n");
            }

            builder.append("\n");
            builder.append(historyToString(inMemoryHistoryManager));

            writer.write(builder.toString());
        } catch (ManagerSaveException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            if (!Files.exists(file.toPath())) {
                if (!file.createNewFile()) {
                    System.out.println("Нельзя создать файл " + file.getName());
                }
            }
            FileBackedTaskManager manager = new FileBackedTaskManager(file);

            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty()) {
                throw new IOException("Файл пуст");
            }
            lines.removeFirst();
            for (String line : lines) {
                if (line.isEmpty() || line.isBlank()) {
                    break;
                }

                Task task = manager.fromString(line);
                if (task instanceof SubTask subTask) {
                    manager.addSubTask(subTask);
                } else if (task instanceof Epic epic) {
                    manager.addEpic(epic);
                } else {
                    manager.addTask(task);
                }
            }

            String historyIds = lines.getLast();
            if (historyIds.isEmpty()) return manager;
            FileBackedTaskManager.historyFromString(historyIds);
            for (Long historyId : FileBackedTaskManager.historyFromString(historyIds)) {
                manager.getTaskById(historyId);
                manager.getEpicById(historyId);
                manager.getSubTaskById(historyId);
            }

            return manager;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String toString(Task task) throws ManagerSaveException {
        //id,type,name,status,description,epic
        try {
            TaskType taskType = task instanceof Epic ? TaskType.EPIC : (
                    task instanceof SubTask ? TaskType.SUBTASK : TaskType.TASK
            );

            String epicId = taskType == TaskType.SUBTASK ? String.valueOf(((SubTask) task).getEpicId()) : "";

            return String.format("%d,%s,%s,%s,%s,%s",
                    task.getId(), taskType, task.getName(), task.getStatus().name(), task.getDescription(), epicId
            );
        } catch (Exception e) {
            throw new ManagerSaveException("метод toString сработал некорректно");
        }
    }

    public Task fromString(String value) {
        String[] parts = value.split(",");
        long id = Long.parseLong(parts[0]);
        checkTheMaxId(id);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        if (type == TaskType.TASK) {
            Task task = new Task(name, description, status);
            task.setId(id);
            return task;
        } else if (type == TaskType.EPIC) {
            Epic epic = new Epic(name, description);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        } else {
            long epicId = Long.parseLong(parts[5]);
            SubTask subTask = new SubTask(name, description, epicId, status);
            subTask.setId(id);
            return subTask;
        }
    }

    public static String historyToString(HistoryManager manager) throws ManagerSaveException {
        try {
            List<Task> history = manager.getHistory();
            if (history.isEmpty()) {
                return "";
            }
            String commaWithSpace = ", ";
            StringBuilder builder = new StringBuilder(history.size() + (history.size() - 1) * 2);

            for (int i = 0; i < history.size(); i++) {
                Task task = history.get(i);
                builder.append(task.getId());
                if (i < history.size() - 1) builder.append(commaWithSpace);
            }

            return builder.toString();
        } catch (Exception e) {
            throw new ManagerSaveException("метод historyToString сработал некорректно");
        }
    }

    private void checkTheMaxId(Long currentId) {
        if (currentId > manageId) {
            manageId = ++currentId;
        }
    }

    public static List<Long> historyFromString(String history) {
        String[] ids = history.split(", ");
        List<Long> idsList = new ArrayList<>(ids.length);
        for (String id : ids) {
            idsList.add(Long.parseLong(id));
        }
        return idsList;
    }

    public static void main(String[] args) {
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(new File("test.txt"));

        taskManager.printAllTasks();
        taskManager.printAllEpics();
        taskManager.printAllSubTasks();

        System.out.println("HISTORY");
        List<Task> history = taskManager.getHistory();
        history.forEach(System.out::println);
    }
}
