/** Rides are kept alphabetically by pickup location at insertion. */
public class RideList implements IRideList {
    private LinkedList<IRide> rides = new LinkedList<IRide>();
    public boolean addRide(IRide ride) {
        if (ride == null) { return false; }
        int index = 0;
        LinkedList<IRide>.Cursor cursor = rides.cursor();
        while (cursor.hasNext()) {
            IRide current = cursor.next();
            if (current.getRideId() == ride.getRideId()) { return false; }
            if (current.compareTo(ride) <= 0) { index++; }
        }
        rides.add(index, ride);
        return true;
    }
    public boolean removeRideById(int id) {
        LinkedList<IRide>.Cursor cursor = rides.cursor();
        while (cursor.hasNext()) {
            if (cursor.next().getRideId() == id) { cursor.remove(); return true; }
        }
        return false;
    }
    public LinkedList<IRide> getAllAlphabetically() { return rides.copy(); }
    public LinkedList<IRide> findByPickupLocation(String location) {
        LinkedList<IRide> result = new LinkedList<IRide>();
        LinkedList<IRide>.Cursor cursor = rides.cursor();
        while (cursor.hasNext()) {
            IRide ride = cursor.next();
            if (ride.getPickupLocation().equals(location)) { result.add(ride); }
        }
        return result;
    }
    public LinkedList<IRide> findByRiderName(String name) {
        LinkedList<IRide> result = new LinkedList<IRide>();
        LinkedList<IRide>.Cursor cursor = rides.cursor();
        while (cursor.hasNext()) {
            IRide ride = cursor.next();
            boolean found = false;
            if (ride instanceof IPrivateRide) {
                found = ((IPrivateRide) ride).getRider().getName().equals(name);
            } else if (ride instanceof ISharedRide) {
                LinkedList<IRider>.Cursor people = ((ISharedRide) ride).getParticipants().cursor();
                while (people.hasNext()) {
                    if (people.next().getName().equals(name)) { found = true; break; }
                }
            }
            if (found) { result.add(ride); }
        }
        return result;
    }
    public int size() { return rides.size(); }
}
