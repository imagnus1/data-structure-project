public abstract class Ride implements IRide {

    private static int nextId = 1; // simple auto-increment for internal ride IDs
    private final int rideId;
    private String pickupLocation;
    private final IDateTime pickupTime;
    private final IDateTime dropoffTime;
    private String dropoffLocation;
    private IDriver driver;

    public Ride(String pickupLocation, IDateTime pickupTime, IDateTime dropoffTime,
                String dropoffLocation, IDriver driver) {
        this.rideId = nextId++;
        this.pickupLocation = pickupLocation;
        this.pickupTime = pickupTime;
        this.dropoffTime = dropoffTime;
        this.dropoffLocation = dropoffLocation;
        this.driver = driver;
    }

    @Override
    public int getRideId() { return rideId; }

    @Override
    public String getPickupLocation() { return pickupLocation; }

    @Override
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    @Override
    public IDateTime getPickupTime() { return pickupTime; }

    @Override
    public IDateTime getDropoffTime() { return dropoffTime; }

    @Override
    public String getDropoffLocation() { return dropoffLocation; }

    @Override
    public void setDropoffLocation(String dropoffLocation) { this.dropoffLocation = dropoffLocation; }

    @Override
    public IDriver getDriver() { return driver; }

    @Override
    public void setDriver(IDriver driver) { this.driver = driver; }

    @Override
    public int compareTo(IRide other) {
        // Alphabetical by pickup location, per IRide spec
        return this.pickupLocation.compareTo(other.getPickupLocation());
    }
}