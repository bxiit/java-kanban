package history;

import task.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Long,Node> historyMap;
    private Node first;
    private Node last;

    public InMemoryHistoryManager() {
        this.historyMap = new LinkedHashMap<>();
    }

    @Override
    public void add(Task task) {
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(long id) {
        historyMap.remove(id);
    }

    private void removeNode(Node node) {
        historyMap.remove(node.data.getId());
    }

    public List<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>(historyMap.size());

        for (Node node : historyMap.values()) {
            tasks.add(node.data);
        }

        return tasks;
    }

    private void linkLast(Task task) {
        Node l = last;
        Node newNode = new Node(l, task, null);
        last = newNode;

        if (l == null)
            first = newNode;
        else
            l.next = newNode;

        removeNode(newNode);

        // todo: не нравиться это добавление
        historyMap.put(task.getId(), newNode);
    }


    private static class Node {
        Task data;
        Node prev;
        Node next;

        private Node(Node prev, Task data, Node next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }
}
