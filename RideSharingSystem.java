import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/** Role B: coordinates the lists; all console input/output belongs in Main. */
public class RideSharingSystem implements IRideSharingSystem {
    private RiderList riders;
    private DriverList drivers;
    private RideList rides;

    public RideSharingSystem() {
        riders = new RiderList();
        drivers = new DriverList();
        rides = new RideList();
    }

    private boolean overlaps(IDateTime pickup1, IDateTime dropoff1,
                             IDateTime pickup2, IDateTime dropoff2) {
        return pickup1.compareTo(dropoff2) < 0
                && pickup2.compareTo(dropoff1) < 0;
    }

    private boolean isAvailable(IPerson person, IDateTime pickup, IDateTime dropoff) {
        LinkedList<IRide> history = person.getRideHistory();
        LinkedList<IRide>.Cursor cursor = history.cursor();
        while (cursor.hasNext()) {
            IRide ride = cursor.next();
            if (overlaps(pickup, dropoff, ride.getPickupTime(), ride.getDropoffTime())) {
                return false;
            }
        }
        return true;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean validTime(IDateTime time) {
        if (time == null || time.getYear() < 1 || time.getMonth() < 1
                || time.getMonth() > 12 || time.getHour() < 0 || time.getHour() > 23
                || time.getMinute() < 0 || time.getMinute() > 59) {
            return false;
        }
        int[] days = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int year = time.getYear();
        if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
            days[1] = 29;
        }
        return time.getDay() >= 1 && time.getDay() <= days[time.getMonth() - 1];
    }

    private boolean validRide(String pickupLocation, IDateTime pickupTime,
                              IDateTime dropoffTime, String dropoffLocation) {
        return hasText(pickupLocation) && hasText(dropoffLocation)
                && validTime(pickupTime) && validTime(dropoffTime)
                && pickupTime.compareTo(dropoffTime) < 0;
    }

    @Override
    public boolean schedulePrivateRide(String pickupLocation, IDateTime pickupTime,
            IDateTime dropoffTime, String dropoffLocation, int riderId, int driverId) {
        if (!validRide(pickupLocation, pickupTime, dropoffTime, dropoffLocation)) {
            return false;
        }
        IRider rider = riders.findById(riderId);
        IDriver driver = drivers.findById(driverId);
        if (rider == null || driver == null) {
            return false;
        }
        if (!isAvailable(rider, pickupTime, dropoffTime)
                || !isAvailable(driver, pickupTime, dropoffTime)) {
            return false;
        }
        PrivateRide ride = new PrivateRide(pickupLocation, pickupTime, dropoffTime,
                dropoffLocation, driver, rider);
        if (!rides.addRide(ride)) {
            return false;
        }
        rider.getRideHistory().add(ride);
        driver.getRideHistory().add(ride);
        return true;
    }

    @Override
    public boolean scheduleSharedRide(String pickupLocation, IDateTime pickupTime,
            IDateTime dropoffTime, String dropoffLocation, int[] riderIds, int driverId) {
        if (!validRide(pickupLocation, pickupTime, dropoffTime, dropoffLocation)
                || riderIds == null || riderIds.length < 2) {
            return false;
        }
        IDriver driver = drivers.findById(driverId);
        if (driver == null || !isAvailable(driver, pickupTime, dropoffTime)) {
            return false;
        }
        IRider[] participants = new IRider[riderIds.length];
        for (int i = 0; i < riderIds.length; i++) {
            // Reject repeated IDs instead of registering a rider twice.
            for (int j = 0; j < i; j++) {
                if (riderIds[i] == riderIds[j]) {
                    return false;
                }
            }
            participants[i] = riders.findById(riderIds[i]);
            if (participants[i] == null
                    || !isAvailable(participants[i], pickupTime, dropoffTime)) {
                return false;
            }
        }
        // All checks finish before any list or history is changed.
        SharedRide ride = new SharedRide(pickupLocation, pickupTime, dropoffTime,
                dropoffLocation, driver);
        for (int i = 0; i < participants.length; i++) {
            if (!ride.addParticipant(participants[i])) {
                return false;
            }
        }
        if (!rides.addRide(ride)) {
            return false;
        }
        for (int i = 0; i < participants.length; i++) {
            participants[i].getRideHistory().add(ride);
        }
        driver.getRideHistory().add(ride);
        return true;
    }

    @Override
    public boolean addRider(IRider rider) {
        if (rider == null || !hasText(rider.getName()) || !hasText(rider.getEmail())
                || !hasText(rider.getHomeCity()) || rider.getPhoneNumber() == null
                || !rider.getPhoneNumber().matches("[0-9]{10}")
                || riders.findById(rider.getId()) != null
                || riders.findByEmail(rider.getEmail()) != null) {
            return false;
        }
        return riders.add(rider);
    }

    @Override
    public boolean addDriver(IDriver driver) {
        if (driver == null || !hasText(driver.getName()) || driver.getVehicleType() == null
                || driver.getPhoneNumber() == null
                || !driver.getPhoneNumber().matches("[0-9]{10}")
                || driver.getVehiclePlate() == null
                || !driver.getVehiclePlate().matches("[A-Z]{3}[0-9]{4}")
                || drivers.findById(driver.getId()) != null
                || drivers.findByVehiclePlate(driver.getVehiclePlate()) != null) {
            return false;
        }
        return drivers.add(driver);
    }

    private void removeFromHistory(IPerson person, int rideId) {
        LinkedList<IRide> history = person.getRideHistory();
        LinkedList<IRide>.Cursor cursor = history.cursor();
        while (cursor.hasNext()) {
            if (cursor.next().getRideId() == rideId) { cursor.remove(); }
        }
    }

    private void deleteRide(IRide ride) {
        rides.removeRideById(ride.getRideId());
        removeFromHistory(ride.getDriver(), ride.getRideId());
        if (ride instanceof IPrivateRide) {
            removeFromHistory(((IPrivateRide) ride).getRider(), ride.getRideId());
        } else if (ride instanceof ISharedRide) {
            LinkedList<IRider> participants = ((ISharedRide) ride).getParticipants();
            LinkedList<IRider>.Cursor cursor = participants.cursor();
            while (cursor.hasNext()) { removeFromHistory(cursor.next(), ride.getRideId()); }
        }
    }

    @Override
    public boolean removeRider(int riderId) {
        IRider rider = riders.findById(riderId);
        if (rider == null) {
            return false;
        }
        LinkedList<IRide> all = rides.getAllAlphabetically();
        // Traverse a snapshot while deleting from the stored list.
        LinkedList<IRide>.Cursor cursor = all.cursor();
        while (cursor.hasNext()) {
            IRide ride = cursor.next();
            if (!ride.hasRider(riderId)) {
                continue;
            }
            if (ride instanceof ISharedRide) {
                ISharedRide shared = (ISharedRide) ride;
                shared.removeParticipantById(riderId);
                removeFromHistory(rider, ride.getRideId());
                if (shared.isEmpty()) {
                    deleteRide(ride);
                }
            } else {
                deleteRide(ride);
            }
        }
        return riders.removeById(riderId);
    }

    @Override
    public boolean removeDriver(int driverId) {
        if (drivers.findById(driverId) == null) {
            return false;
        }
        LinkedList<IRide> all = rides.getAllAlphabetically();
        LinkedList<IRide>.Cursor cursor = all.cursor();
        while (cursor.hasNext()) {
            IRide ride = cursor.next();
            if (ride.getDriver().getId() == driverId) {
                deleteRide(ride);
            }
        }
        return drivers.removeById(driverId);
    }

    @Override
    public IRider searchRiderById(int riderId) { return riders.findById(riderId); }
    @Override
    public IRider searchRiderByEmail(String email) { return riders.findByEmail(email); }
    @Override
    public LinkedList<IRider> searchRidersByName(String name) { return riders.findByName(name); }
    @Override
    public LinkedList<IRider> searchRidersByHomeCity(String city) { return riders.findByHomeCity(city); }
    @Override
    public LinkedList<IRider> getAllRiders() { return riders.getAll(); }
    @Override
    public IDriver searchDriverById(int driverId) { return drivers.findById(driverId); }
    @Override
    public IDriver searchDriverByVehiclePlate(String plate) { return drivers.findByVehiclePlate(plate); }
    @Override
    public LinkedList<IDriver> searchDriversByVehicleType(VehicleType type) { return drivers.findByVehicleType(type); }
    @Override
    public LinkedList<IDriver> getAllDrivers() { return drivers.getAll(); }
    @Override
    public LinkedList<IRide> searchRidesByPickupLocation(String location) { return rides.findByPickupLocation(location); }
    @Override
    public LinkedList<IRide> searchRidesByRiderName(String name) { return rides.findByRiderName(name); }
    @Override
    public LinkedList<IRide> getAllRidesAlphabetically() { return rides.getAllAlphabetically(); }

    @Override
    public LinkedList<IRider> getSharedRideParticipants(String pickupLocation) {
        LinkedList<IRider> result = new LinkedList<IRider>();
        LinkedList<IRide>.Cursor matches = rides.findByPickupLocation(pickupLocation).cursor();
        // Locations can repeat: merge matching shared rides, without repeated people.
        while (matches.hasNext()) {
            IRide ride = matches.next();
            if (ride instanceof ISharedRide) {
                LinkedList<IRider>.Cursor people = ((ISharedRide) ride).getParticipants().cursor();
                while (people.hasNext()) {
                    IRider person = people.next();
                    boolean found = false;
                    LinkedList<IRider>.Cursor existing = result.cursor();
                    while (existing.hasNext()) {
                        if (existing.next().getId() == person.getId()) { found = true; break; }
                    }
                    if (!found) { result.add(person); }
                }
            }
        }
        return result;
    }

    @Override
    public boolean loadRidersFromCSV(String path) { return loadCSV(path, 0); }
    @Override
    public boolean loadDriversFromCSV(String path) { return loadCSV(path, 1); }
    @Override
    public boolean loadRidesFromCSV(String path) { return loadCSV(path, 2); }

    // Valid rows are kept. Any invalid row makes the final return value false.
    private boolean loadCSV(String path, int fileType) {
        if (!hasText(path)) {
            return false;
        }
        boolean success = true;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first && line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    String[] fields = splitCSV(line);
                    String header = fileType == 0 ? "riderId" : fileType == 1 ? "driverId" : "rideType";
                    if (first && fields[0].equals(header)) {
                        first = false;
                        continue;
                    }
                    first = false;
                    if (!loadRow(fields, fileType)) {
                        success = false;
                    }
                } catch (IllegalArgumentException e) {
                    first = false;
                    success = false;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return success;
    }

    private boolean loadRow(String[] f, int fileType) {
        if (fileType == 0) {
            if (f.length != 5) { return false; }
            return addRider(new Rider(Integer.parseInt(f[0]), f[1], f[3], f[2], f[4]));
        }
        if (fileType == 1) {
            if (f.length != 5) { return false; }
            return addDriver(new Driver(Integer.parseInt(f[0]), f[1], f[2], f[3],
                    VehicleType.valueOf(f[4])));
        }
        if (f.length != 7) { return false; }
        IDateTime pickup = parseDateTime(f[2]);
        IDateTime dropoff = parseDateTime(f[3]);
        int driverId = Integer.parseInt(f[5]);
        String[] ids = f[6].split(";", -1);
        if (f[0].equals("PRIVATE") && ids.length == 1) {
            return schedulePrivateRide(f[1], pickup, dropoff, f[4],
                    Integer.parseInt(ids[0].trim()), driverId);
        }
        if (f[0].equals("SHARED")) {
            int[] riderIds = new int[ids.length];
            for (int i = 0; i < ids.length; i++) {
                riderIds[i] = Integer.parseInt(ids[i].trim());
            }
            return scheduleSharedRide(f[1], pickup, dropoff, f[4], riderIds, driverId);
        }
        return false;
    }

    private IDateTime parseDateTime(String value) { return DateTime.parse(value); }

    // Handles comma-separated fields, including quoted commas and doubled quotes.
    // Quoted fields must fit on a single line, as in the assignment's CSV layout.
    private String[] splitCSV(String line) {
        LinkedList<String> fields = new LinkedList<String>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        boolean closed = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quoted) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        quoted = false;
                        closed = true;
                    }
                } else {
                    field.append(c);
                }
            } else if (c == ',') {
                fields.add(field.toString().trim());
                field.setLength(0);
                closed = false;
            } else if (c == '"' && !closed && field.toString().trim().isEmpty()) {
                field.setLength(0);
                quoted = true;
            } else if (c == '"' || (closed && !Character.isWhitespace(c))) {
                throw new IllegalArgumentException("Invalid CSV quoting");
            } else {
                field.append(c);
            }
        }
        if (quoted) {
            throw new IllegalArgumentException("Unclosed CSV quote");
        }
        fields.add(field.toString().trim());
        String[] result = new String[fields.size()];
        LinkedList<String>.Cursor cursor = fields.cursor();
        int index = 0;
        while (cursor.hasNext()) { result[index++] = cursor.next(); }
        return result;
    }
}
