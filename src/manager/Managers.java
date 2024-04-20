package manager;

import history.HistoryManager;
import history.InMemoryHistoryManager;

public class Managers {
    private Managers() {
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultManager() {
        return new FileBackedTaskManager();
    }
}
