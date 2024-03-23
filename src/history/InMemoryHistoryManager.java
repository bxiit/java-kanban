package history;

import task.Epic;
import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Long, Node> historyMap;
    private Node first;
    private Node last;

    public InMemoryHistoryManager() {
        this.historyMap = new HashMap<>();
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
        Node node = historyMap.get(id);

        if (node.task instanceof Epic epic) {
            for (Long subTasksId : epic.getSubTasksIds()) {
                Node subTaskNode = historyMap.remove(subTasksId);
                removeNode(subTaskNode);
            }
        }
        historyMap.remove(id);
        removeNode(node);
    }

    private void removeNode(Node node) {
        if (node.prev == null) {
            linkAfter(node);
        } else if (node.next == null) {
            linkBefore(node);
        } else {
            linkBetween(node.prev, node.next);
        }
        historyMap.remove(node.task.getId());
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>(historyMap.size());

        for (Node node : historyMap.values()) {
            tasks.add(node.task);
        }

        return tasks;
    }

    private void linkLast(Task task) {
        Node node;
        if (last == null) {
            node = new Node(null, task, null);
            first = node;
        } else {
            node = new Node(last, task, null);
            last.next = node;
        }
        last = node;
        historyMap.put(task.getId(), node);
    }

    private void linkBefore(Node node) {
        Node preLastNode = node.prev;
        preLastNode.next = null;
    }

    private void linkAfter(Node node) {
        Node secondNode = node.next;
        secondNode.prev = null;
    }

    private void linkBetween(Node prevNode, Node nextNode) {
        prevNode.next = nextNode.prev;
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
