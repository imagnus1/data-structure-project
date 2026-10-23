/** Drivers stored in increasing ID order. */
public class DriverList implements IDriverList {
    private LinkedList<IDriver> data = new LinkedList<IDriver>();
    private LinkedList<IDriver> alphabetical = new LinkedList<IDriver>();
    public boolean add(IDriver person) {
        if (person == null || findById(person.getId()) != null
                || findByVehiclePlate(person.getVehiclePlate()) != null) { return false; }
        int index = 0;
        LinkedList<IDriver>.Cursor cursor = data.cursor();
        while (cursor.hasNext() && cursor.next().compareTo(person) < 0) { index++; }
        data.add(index, person);
        int nameIndex = 0;
        LinkedList<IDriver>.Cursor names = alphabetical.cursor();
        while (names.hasNext() && names.next().getName().compareTo(person.getName()) <= 0) { nameIndex++; }
        alphabetical.add(nameIndex, person);
        return true;
    }
    public IDriver findById(int id) {
        LinkedList<IDriver>.Cursor cursor = data.cursor();
        while (cursor.hasNext()) {
            IDriver person = cursor.next();
            if (person.getId() == id) { return person; }
            if (person.getId() > id) { break; }
        }
        return null;
    }
    public IDriver findByVehiclePlate(String value) {
        LinkedList<IDriver>.Cursor cursor = data.cursor();
        while (cursor.hasNext()) {
            IDriver person = cursor.next();
            if (person.getVehiclePlate().equals(value)) { return person; }
        }
        return null;
    }
    public LinkedList<IDriver> getAll() { return alphabetical.copy(); }
    public int size() { return data.size(); }
    public boolean removeById(int id) {
        LinkedList<IDriver>.Cursor cursor = data.cursor();
        while (cursor.hasNext()) {
            if (cursor.next().getId() == id) { cursor.remove(); removeNameIndex(id); return true; }
        }
        return false;
    }
    public boolean removeByVehiclePlate(String value) {
        LinkedList<IDriver>.Cursor cursor = data.cursor();
        while (cursor.hasNext()) {
            IDriver person = cursor.next();
            if (person.getVehiclePlate().equals(value)) { cursor.remove(); removeNameIndex(person.getId()); return true; }
        }
        return false;
    }
    public LinkedList<IDriver> findByName(String value) {
        LinkedList<IDriver> result = new LinkedList<IDriver>();
        LinkedList<IDriver>.Cursor cursor = data.cursor();
        while (cursor.hasNext()) {
            IDriver person = cursor.next();
            if (person.getName().equals(value)) { result.add(person); }
        }
        return result;
    }
    // Names/types/cities can repeat: remove EVERY matching node.
    public int removeByName(String value) {
        int count = 0;
        LinkedList<IDriver>.Cursor cursor = data.cursor();
        while (cursor.hasNext()) {
            IDriver person = cursor.next();
            if (person.getName().equals(value)) { cursor.remove(); count++; }
        }
        LinkedList<IDriver>.Cursor names = alphabetical.cursor();
        while (names.hasNext()) {
            IDriver person = names.next();
            if (person.getName().equals(value)) { names.remove(); }
        }
        return count;
    }
    public LinkedList<IDriver> findByVehicleType(VehicleType value) {
        LinkedList<IDriver> result = new LinkedList<IDriver>();
        LinkedList<IDriver>.Cursor cursor = data.cursor();
        while (cursor.hasNext()) {
            IDriver person = cursor.next();
            if (person.getVehicleType() == value) { result.add(person); }
        }
        return result;
    }
    // Names/types/cities can repeat: remove EVERY matching node.
    public int removeByVehicleType(VehicleType value) {
        int count = 0;
        LinkedList<IDriver>.Cursor cursor = data.cursor();
        while (cursor.hasNext()) {
            IDriver person = cursor.next();
            if (person.getVehicleType() == value) { cursor.remove(); count++; }
        }
        LinkedList<IDriver>.Cursor names = alphabetical.cursor();
        while (names.hasNext()) {
            IDriver person = names.next();
            if (person.getVehicleType() == value) { names.remove(); }
        }
        return count;
    }
    private void removeNameIndex(int id) {
        LinkedList<IDriver>.Cursor cursor = alphabetical.cursor();
        while (cursor.hasNext()) {
            if (cursor.next().getId() == id) { cursor.remove(); return; }
        }
    }
}
