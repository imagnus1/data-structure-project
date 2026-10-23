public class SharedRide extends Ride implements ISharedRide {
    private LinkedList<IRider> participants = new LinkedList<IRider>();
    public SharedRide(String pickupLocation, IDateTime pickupTime, IDateTime dropoffTime,
                      String dropoffLocation, IDriver driver) {
        super(pickupLocation, pickupTime, dropoffTime, dropoffLocation, driver);
    }
    public LinkedList<IRider> getParticipants() { return participants.copy(); }
    public boolean addParticipant(IRider rider) {
        if (rider == null || hasRider(rider.getId())) { return false; }
        participants.add(rider);
        return true;
    }
    public boolean removeParticipantById(int riderId) {
        LinkedList<IRider>.Cursor cursor = participants.cursor();
        while (cursor.hasNext()) {
            if (cursor.next().getId() == riderId) { cursor.remove(); return true; }
        }
        return false;
    }
    public boolean isEmpty() { return participants.size() == 0; }
    public boolean hasRider(int riderId) {
        LinkedList<IRider>.Cursor cursor = participants.cursor();
        while (cursor.hasNext()) {
            if (cursor.next().getId() == riderId) { return true; }
        }
        return false;
    }
    public String toString() {
        StringBuilder names = new StringBuilder();
        LinkedList<IRider>.Cursor cursor = participants.cursor();
        while (cursor.hasNext()) {
            IRider rider = cursor.next();
            if (names.length() > 0) { names.append("; "); }
            names.append(rider.getId()).append(": ").append(rider.getName());
        }
        return "SharedRide[id=" + getRideId() + ", pickup=" + getPickupLocation()
                + " @ " + getPickupTime().format() + ", dropoff=" + getDropoffLocation()
                + " @ " + getDropoffTime().format() + ", driver=" + getDriver().getId()
                + ", riders=[" + names + "]]";
    }
}
