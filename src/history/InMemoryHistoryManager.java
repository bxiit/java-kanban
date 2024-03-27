package history;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Long, Node> historyMap;
    private Node first;
    private Node last;

    public InMemoryHistoryManager() {
        this.historyMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        removeNode(task.getId());
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(long id) {
        removeNode(id);
    }

    private void removeNode(long id) {
        Node node = historyMap.remove(id);
        removeNode(node);
    }

    private void removeNode(Node node) {
        if (node == null) return;

        if (node.prev == null && node.next == null) linkNulls();
        else if (node.prev == null) linkAfter(node);
        else if (node.next == null) linkBefore(node);
        else linkBetween(node.prev, node.next);

        historyMap.remove(node.task.getId());
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>(historyMap.size());

        Node firstNode = first;
        while (firstNode != null) {
            tasks.add(firstNode.task);
            firstNode = firstNode.next;
        }

        return tasks;
    }

    private void linkNulls() {
        first = null;
        last = null;
    }

    private void linkLast(Task task) {
        Node node = new Node(last, task, null);
        if (first == null) first = node;
        else last.next = node;
        last = node;
        historyMap.put(task.getId(), node);
    }

    private void linkBefore(Node node) {
        Node preLastNode = node.prev;
        preLastNode.next = null;
        last = preLastNode;
    }

    private void linkAfter(Node node) {
        Node secondNode = node.next;
        secondNode.prev = null;
        first = secondNode;
    }

    private void linkBetween(Node prevNode, Node nextNode) {
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    private static class Node {
        Task task;
        Node prev;
        Node next;

        private Node(Node prev, Task task, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }
}
