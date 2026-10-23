public class SharedRide extends Ride implements ISharedRide {

    private LinkedList<IRider> participants;

    public SharedRide(String pickupLocation, IDateTime pickupTime, IDateTime dropoffTime,
                      String dropoffLocation, IDriver driver) {
        super(pickupLocation, pickupTime, dropoffTime, dropoffLocation, driver);
        this.participants = new LinkedList<IRider>();
    }

    @Override
    public LinkedList<IRider> getParticipants() { return participants; }

    @Override
    public boolean addParticipant(IRider rider) {
        if (hasRider(rider.getId())) return false;
        participants.add(rider);
        return true;
    }

    @Override
    public boolean removeParticipantById(int riderId) {
        for (int i = 0; i < participants.size(); i++) {
            if (participants.get(i).getId() == riderId) {
                participants.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return participants.size() == 0;
    }

    @Override
    public boolean hasRider(int riderId) {
        for (int i = 0; i < participants.size(); i++) {
            if (participants.get(i).getId() == riderId) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "SharedRide[id=" + getRideId() + ", pickup=" + getPickupLocation()
                + " @ " + getPickupTime().format() + ", dropoff=" + getDropoffLocation()
                + " @ " + getDropoffTime().format() + ", driver=" + getDriver().getId()
                + ", participants=" + participants.size() + "]";
    }
}