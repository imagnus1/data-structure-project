/** Riders stored in increasing ID order. */
public class RiderList implements IRiderList {
    private LinkedList<IRider> data = new LinkedList<IRider>();
    private LinkedList<IRider> alphabetical = new LinkedList<IRider>();
    public boolean add(IRider person) {
        if (person == null || findById(person.getId()) != null
                || findByEmail(person.getEmail()) != null) { return false; }
        int index = 0;
        LinkedList<IRider>.Cursor cursor = data.cursor();
        while (cursor.hasNext() && cursor.next().compareTo(person) < 0) { index++; }
        data.add(index, person);
        int nameIndex = 0;
        LinkedList<IRider>.Cursor names = alphabetical.cursor();
        while (names.hasNext() && names.next().getName().compareTo(person.getName()) <= 0) { nameIndex++; }
        alphabetical.add(nameIndex, person);
        return true;
    }
    public IRider findById(int id) {
        LinkedList<IRider>.Cursor cursor = data.cursor();
        while (cursor.hasNext()) {
            IRider person = cursor.next();
            if (person.getId() == id) { return person; }
            if (person.getId() > id) { break; }
        }
        return null;
    }
    public IRider findByEmail(String value) {
        LinkedList<IRider>.Cursor cursor = data.cursor();
        while (cursor.hasNext()) {
            IRider person = cursor.next();
            if (person.getEmail().equals(value)) { return person; }
        }
        return null;
    }
    public LinkedList<IRider> getAll() { return alphabetical.copy(); }
    public int size() { return data.size(); }
    public boolean removeById(int id) {
        LinkedList<IRider>.Cursor cursor = data.cursor();
        while (cursor.hasNext()) {
            if (cursor.next().getId() == id) { cursor.remove(); removeNameIndex(id); return true; }
        }
        return false;
    }
    public boolean removeByEmail(String value) {
        LinkedList<IRider>.Cursor cursor = data.cursor();
        while (cursor.hasNext()) {
            IRider person = cursor.next();
            if (person.getEmail().equals(value)) { cursor.remove(); removeNameIndex(person.getId()); return true; }
        }
        return false;
    }
    public LinkedList<IRider> findByName(String value) {
        LinkedList<IRider> result = new LinkedList<IRider>();
        LinkedList<IRider>.Cursor cursor = data.cursor();
        while (cursor.hasNext()) {
            IRider person = cursor.next();
            if (person.getName().equals(value)) { result.add(person); }
        }
        return result;
    }
    // Names/types/cities can repeat: remove EVERY matching node.
    public int removeByName(String value) {
        int count = 0;
        LinkedList<IRider>.Cursor cursor = data.cursor();
        while (cursor.hasNext()) {
            IRider person = cursor.next();
            if (person.getName().equals(value)) { cursor.remove(); count++; }
        }
        LinkedList<IRider>.Cursor names = alphabetical.cursor();
        while (names.hasNext()) {
            IRider person = names.next();
            if (person.getName().equals(value)) { names.remove(); }
        }
        return count;
    }
    public LinkedList<IRider> findByHomeCity(String value) {
        LinkedList<IRider> result = new LinkedList<IRider>();
        LinkedList<IRider>.Cursor cursor = data.cursor();
        while (cursor.hasNext()) {
            IRider person = cursor.next();
            if (person.getHomeCity().equals(value)) { result.add(person); }
        }
        return result;
    }
    // Names/types/cities can repeat: remove EVERY matching node.
    public int removeByHomeCity(String value) {
        int count = 0;
        LinkedList<IRider>.Cursor cursor = data.cursor();
        while (cursor.hasNext()) {
            IRider person = cursor.next();
            if (person.getHomeCity().equals(value)) { cursor.remove(); count++; }
        }
        LinkedList<IRider>.Cursor names = alphabetical.cursor();
        while (names.hasNext()) {
            IRider person = names.next();
            if (person.getHomeCity().equals(value)) { names.remove(); }
        }
        return count;
    }
    private void removeNameIndex(int id) {
        LinkedList<IRider>.Cursor cursor = alphabetical.cursor();
        while (cursor.hasNext()) {
            if (cursor.next().getId() == id) { cursor.remove(); return; }
        }
    }
}
