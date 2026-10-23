public class LinkedList<T> {
    private Node<T> head;
    private int size = 0;

    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    public void add(T item) {
        Node<T> node = new Node<>(item);
        if (head == null) { head = node; }
        else {
            Node<T> cur = head;
            while (cur.next != null) cur = cur.next;
            cur.next = node;
        }
        size++;
    }

    public int size() { return size; }

    public T get(int index) {
        Node<T> cur = head;
        for (int i = 0; i < index; i++) cur = cur.next;
        return cur.data;
    }

    public boolean remove(int index) {
        if (index < 0 || index >= size) return false;
        if (index == 0) { head = head.next; size--; return true; }
        Node<T> prev = head;
        for (int i = 0; i < index - 1; i++) prev = prev.next;
        prev.next = prev.next.next;
        size--;
        return true;
    }
}