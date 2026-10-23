/** Custom singly linked list; no Java collection classes. */
public class LinkedList<T> {
    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }
    private Node<T> head;
    private Node<T> tail;
    private int size;
    public int size() { return size; }

    // A tail pointer makes append O(1).
    public void add(T item) {
        Node<T> node = new Node<T>(item);
        if (tail == null) { head = node; }
        else { tail.next = node; }
        tail = node;
        size++;
    }
    public void add(int index, T item) {
        if (index < 0 || index > size) { throw new IndexOutOfBoundsException(); }
        if (index == size) { add(item); return; }
        Node<T> node = new Node<T>(item);
        if (index == 0) { node.next = head; head = node; }
        else {
            Node<T> previous = head;
            for (int i = 1; i < index; i++) { previous = previous.next; }
            node.next = previous.next;
            previous.next = node;
        }
        size++;
    }
    public T get(int index) {
        if (index < 0 || index >= size) { throw new IndexOutOfBoundsException(); }
        Node<T> node = head;
        for (int i = 0; i < index; i++) { node = node.next; }
        return node.data;
    }
    public boolean remove(int index) {
        if (index < 0 || index >= size) { return false; }
        Cursor cursor = cursor();
        for (int i = 0; i <= index; i++) { cursor.next(); }
        cursor.remove();
        return true;
    }
    public LinkedList<T> copy() {
        LinkedList<T> result = new LinkedList<T>();
        Cursor cursor = cursor();
        while (cursor.hasNext()) { result.add(cursor.next()); }
        return result;
    }
    public Cursor cursor() { return new Cursor(); }

    /** Our own traversal helper. Each next/remove is O(1).
     * Do not otherwise modify this list while a cursor is in use.
     */
    public class Cursor {
        private Node<T> nextNode = head;
        private Node<T> previous;
        private Node<T> last;
        private Node<T> lastPrevious;
        public boolean hasNext() { return nextNode != null; }
        public T next() {
            if (nextNode == null) { throw new IllegalStateException("No next item"); }
            lastPrevious = previous;
            last = nextNode;
            previous = nextNode;
            nextNode = nextNode.next;
            return last.data;
        }
        public void remove() {
            if (last == null) { throw new IllegalStateException("Call next before remove"); }
            if (lastPrevious == null) { head = nextNode; }
            else { lastPrevious.next = nextNode; }
            if (tail == last) { tail = lastPrevious; }
            previous = lastPrevious;
            last = null;
            size--;
        }
    }
}
