import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

/** Integration checks using real classes. Run with assertions enabled or disabled. */
public class ProjectTest {
    private static void check(boolean ok, String message) {
        if (!ok) { throw new AssertionError(message); }
    }
    private static Rider rider(int id, String name, String city) {
        return new Rider(id, name, "0551234567", id + "@example.com", city);
    }
    private static Driver driver(int id, String name, String plate, VehicleType type) {
        return new Driver(id, name, "0551234567", plate, type);
    }
    private static DateTime time(int hour, int minute) {
        return new DateTime(2026, 10, 24, hour, minute);
    }
    private static void invalid(Runnable action, String message) {
        boolean failed = false;
        try { action.run(); } catch (IllegalArgumentException e) { failed = true; }
        check(failed, message);
    }
    private static void listTests() {
        LinkedList<Integer> list = new LinkedList<Integer>();
        list.add(2); list.add(0, 1); list.add(3); list.add(2, 9);
        check(list.size() == 4 && list.get(0) == 1 && list.get(2) == 9, "List insert");
        LinkedList<Integer>.Cursor cursor = list.cursor();
        while (cursor.hasNext()) { cursor.next(); cursor.remove(); }
        check(list.size() == 0, "Remove head, middle, tail via cursor");
        list.add(7); check(list.get(0) == 7 && list.remove(0), "Tail reset after empty");
        check(!list.remove(0) && !list.remove(-1), "Invalid removal");
        boolean bounds = false;
        try { list.get(-1); } catch (IndexOutOfBoundsException e) { bounds = true; }
        check(bounds, "get bounds");
        RiderList riders = new RiderList();
        riders.add(rider(3, "Zed", "Riyadh"));
        riders.add(rider(1, "Ahmed Ali", "Riyadh"));
        riders.add(rider(2, "Ahmed Ali", "Jeddah"));
        check(riders.getAll().get(0).getId() == 1, "Alphabetical rider index");
        check(riders.findById(3) != null && riders.findById(0) == null, "ID lookup");
        check(!riders.add(rider(1, "Other", "X")), "Duplicate rider ID");
        check(!riders.add(new Rider(4, "Other", "0551234567", "1@example.com", "X")), "Duplicate email");
        check(riders.findByName("Ahmed Ali").size() == 2 && riders.findByName("Ahmed").size() == 0, "Exact names");
        check(riders.findByHomeCity("Riyadh").size() == 2, "City lookup");
        LinkedList<IRider> copy = riders.getAll(); copy.remove(0);
        check(riders.size() == 3, "Returned list is a copy");
        check(riders.removeByName("Ahmed Ali") == 2 && riders.getAll().size() == 1, "Remove every name from both indexes");
        riders.add(rider(4, "Another", "Riyadh"));
        check(riders.removeByHomeCity("Riyadh") == 2 && riders.size() == 0, "Remove every city");
        riders.add(rider(1, "A", "X"));
        check(riders.removeByEmail("1@example.com") && !riders.removeById(1), "Remove email");
        DriverList drivers = new DriverList();
        drivers.add(driver(20, "Zed", "ABC1234", VehicleType.SUV));
        drivers.add(driver(10, "Ali", "DEF1234", VehicleType.SUV));
        drivers.add(driver(30, "Ali", "GHI1234", VehicleType.VAN));
        check(drivers.getAll().get(0).getId() == 10 && drivers.findById(20) != null, "Driver indexes");
        check(!drivers.add(driver(40, "New", "ABC1234", VehicleType.SEDAN)), "Duplicate plate");
        check(!drivers.add(driver(20, "New", "JKL1234", VehicleType.SEDAN)), "Duplicate driver ID");
        check(drivers.findByVehicleType(VehicleType.SUV).size() == 2, "Type lookup");
        check(drivers.findByName("Ali").size() == 2, "Driver full name lookup");
        check(drivers.removeByName("Ali") == 2 && drivers.getAll().size() == 1, "Driver remove every name");
        drivers.add(driver(50, "Other", "MNO1234", VehicleType.SUV));
        check(drivers.removeByVehicleType(VehicleType.SUV) == 2 && drivers.size() == 0, "Remove every type");
        drivers.add(driver(60, "P", "PQR1234", VehicleType.VAN));
        check(drivers.removeByVehiclePlate("PQR1234") && drivers.getAll().size() == 0, "Plate removal sync");
    }
    private static void modelTests() {
        invalid(() -> rider(1, "A", "X").setPhoneNumber("123"), "Phone format");
        invalid(() -> driver(1, "A", "abc1234", VehicleType.SEDAN), "Plate format");
        invalid(() -> new DateTime(2026, 2, 29, 9, 0), "Nonleap February");
        invalid(() -> new DateTime(2026, 13, 1, 9, 0), "Invalid month");
        invalid(() -> new DateTime(2026, 1, 1, 24, 0), "Invalid hour");
        invalid(() -> DateTime.parse("2026-10-24 09:00"), "Date format");
        check(DateTime.parse("02/29/2024 09:05").format().equals("02/29/2024 09:05"), "Leap day parse");
        check(new DateTime(2025, 12, 31, 23, 59).compareTo(time(0, 0)) < 0, "Year ordering");
        check(time(9, 0).compareTo(time(9, 1)) < 0 && time(9, 0).compareTo(time(9, 0)) == 0, "Minute and equality");
        check(rider(1, "Z", "X").compareTo(rider(2, "A", "X")) < 0, "Rider compares ID, not name");
        check(driver(2, "A", "ABC1234", VehicleType.SEDAN).compareTo(driver(1, "Z", "DEF1234", VehicleType.SUV)) > 0, "Driver ID comparison");
    }
    private static RideSharingSystem system() {
        RideSharingSystem s = new RideSharingSystem();
        for (int i = 1; i <= 4; i++) { check(s.addRider(rider(i, i <= 2 ? "Same Name" : "Rider " + i, "Riyadh")), "Add rider"); }
        check(s.addDriver(driver(10, "Driver One", "ABC1234", VehicleType.SEDAN)), "Add driver");
        check(s.addDriver(driver(20, "Driver Two", "DEF1234", VehicleType.SUV)), "Add driver two");
        return s;
    }
    private static void schedulingTests() {
        RideSharingSystem s = system();
        check(!s.addRider(null) && !s.addDriver(null), "Null people");
        check(!s.addRider(rider(1, "Other", "X")), "System duplicate rider");
        check(!s.addDriver(driver(30, "Other", "ABC1234", VehicleType.VAN)), "System duplicate plate");
        check(!s.schedulePrivateRide("A", time(9, 0), time(10, 0), "B", 99, 10), "Missing rider");
        check(!s.schedulePrivateRide("A", time(9, 0), time(10, 0), "B", 1, 99), "Missing driver");
        check(!s.schedulePrivateRide("A", time(10, 0), time(9, 0), "B", 1, 10), "Reversed time");
        check(!s.schedulePrivateRide("A", time(9, 0), time(9, 0), "B", 1, 10), "Zero duration");
        check(!s.schedulePrivateRide("", time(9, 0), time(10, 0), "B", 1, 10), "Blank pickup");
        check(!s.schedulePrivateRide("A", null, time(10, 0), "B", 1, 10), "Null time");
        check(s.schedulePrivateRide("Zebra", time(9, 0), time(10, 0), "B", 1, 10), "Initial private ride");
        check(!s.schedulePrivateRide("A", time(9, 30), time(10, 30), "B", 1, 20), "Rider conflict");
        check(!s.schedulePrivateRide("A", time(9, 30), time(10, 30), "B", 2, 10), "Driver conflict");
        check(!s.schedulePrivateRide("A", time(8, 0), time(11, 0), "B", 1, 20), "Enclosing interval");
        check(!s.schedulePrivateRide("A", time(9, 10), time(9, 20), "B", 1, 20), "Contained interval");
        check(!s.schedulePrivateRide("A", time(9, 0), time(10, 0), "B", 1, 20), "Equal interval");
        check(s.schedulePrivateRide("Before", time(8, 0), time(9, 0), "B", 1, 10), "Touching before");
        check(!s.scheduleSharedRide("A", time(9, 30), time(10, 30), "B", new int[]{2, 1}, 20), "Shared rider conflict");
        check(!s.scheduleSharedRide("A", time(9, 30), time(10, 30), "B", new int[]{2, 3}, 10), "Shared driver conflict");
        check(!s.scheduleSharedRide("A", time(10, 0), time(11, 0), "B", new int[]{2, 2}, 10), "Repeated shared ID");
        check(!s.scheduleSharedRide("A", time(10, 0), time(11, 0), "B", new int[]{2, 99}, 10), "Missing shared ID");
        check(!s.scheduleSharedRide("A", time(10, 0), time(11, 0), "B", new int[]{2}, 10), "One shared ID rejected");
        check(!s.scheduleSharedRide("A", time(10, 0), time(11, 0), "B", null, 10), "Null shared IDs");
        check(s.getAllRidesAlphabetically().size() == 2 && s.searchRiderById(2).getRideHistory().size() == 0, "No partial changes");
        check(s.scheduleSharedRide("Airport", time(10, 0), time(11, 0), "B", new int[]{1, 2}, 10), "Touching after");
        check(s.getAllRidesAlphabetically().get(0).getPickupLocation().equals("Airport"), "Alphabetical rides");
        check(s.searchRidesByRiderName("Same Name").size() == 3, "Each matching ride only once");
        check(s.getSharedRideParticipants("Airport").size() == 2, "Shared participants");
        check(s.searchRidesByPickupLocation("missing").size() == 0 && s.getSharedRideParticipants("Zebra").size() == 0, "No results");
        check(s.scheduleSharedRide("Airport", time(12, 0), time(13, 0), "B", new int[]{2, 3}, 20), "Repeated pickup");
        check(s.getSharedRideParticipants("Airport").size() == 3, "Merge without duplicate participants");
        check(s.removeRider(1), "Rider cascade");
        check(s.getAllRidesAlphabetically().size() == 2, "Both private rides deleted, shared remains");
        check(s.searchDriverById(10).getRideHistory().size() == 1, "Driver histories cleaned");
        check(s.removeRider(2) && s.getAllRidesAlphabetically().size() == 1, "Empty shared deleted");
        check(s.searchDriverById(10).getRideHistory().size() == 0, "Empty shared driver history cleaned");
        check(s.removeDriver(20) && s.getAllRidesAlphabetically().size() == 0, "Driver cascade");
        check(s.searchRiderById(3).getRideHistory().size() == 0, "Remaining rider history cleaned");
        check(!s.removeRider(99) && !s.removeDriver(99), "Missing removals");
        check(s.schedulePrivateRide("A", time(12, 0), time(13, 0), "B", 3, 10), "Freed time reusable");
        check(s.searchRiderByEmail("3@example.com").getId() == 3, "Email delegation");
        check(s.searchDriverByVehiclePlate("ABC1234").getId() == 10, "Plate delegation");
        check(s.searchDriversByVehicleType(VehicleType.SEDAN).size() == 1, "Type delegation");
        check(s.searchRidersByHomeCity("Riyadh").size() == 2 && s.searchRidersByName("Same Name").size() == 0, "Rider search delegation");
        RideList direct = new RideList();
        IRide ride = s.getAllRidesAlphabetically().get(0);
        check(direct.addRide(ride) && !direct.addRide(ride), "Unique ride ID");
        check(direct.removeRideById(ride.getRideId()) && !direct.removeRideById(ride.getRideId()), "Ride remove");
    }
    private static Path csv(Path dir, String name, String text) throws Exception {
        Path path = dir.resolve(name);
        Files.write(path, text.getBytes(StandardCharsets.UTF_8));
        return path;
    }
    private static void csvTests() throws Exception {
        Path dir = Files.createTempDirectory("ride-sharing-check-");
        try {
            RideSharingSystem s = new RideSharingSystem();
            String riders = "\uFEFFriderId,name,email,phoneNumber,homeCity\n1,\"Ali, Ahmed\",a@x.com,0551234567,Riyadh\n2,Sara,b@x.com,0551234567,Jeddah\n";
            check(s.loadRidersFromCSV(csv(dir, "riders.csv", riders).toString()), "BOM header quoted comma");
            check(s.searchRiderById(1).getName().equals("Ali, Ahmed"), "CSV quoted field");
            check(!s.loadRidersFromCSV(csv(dir, "bad-riders.csv", "3,Good,c@x.com,0551234567,Riyadh\nbad,row\n4,Good,d@x.com,0551234567,Riyadh\n").toString()), "Partial CSV failure");
            check(s.searchRiderById(3) != null && s.searchRiderById(4) != null, "Valid rows retained");
            check(s.loadDriversFromCSV(csv(dir,"drivers.csv","driverId,name,phoneNumber,vehiclePlate,vehicleType\n10,Fahad,0551234567,ABC1234,SEDAN\n20,Majed,0551234567,DEF1234,LUXURY_SEDAN\n").toString()), "Driver CSV");
            check(!s.loadDriversFromCSV(csv(dir,"bad-drivers.csv","30,X,0551234567,ABC1234,SEDAN\n31,X,123,AAA1234,SUV\n32,X,0551234567,ZZZ1234,BOAT\n").toString()), "Reject bad driver rows");
            check(s.loadRidesFromCSV(csv(dir,"rides.csv","rideType,pickupLocation,pickupDateTime,dropoffDateTime,dropoffLocation,driverId,riderIds\nPRIVATE,A,10/24/2026 09:00,10/24/2026 10:00,B,10,1\nSHARED,C,10/24/2026 10:00,10/24/2026 11:00,D,10,1;2\n").toString()), "Ride CSV both types");
            check(!s.loadRidesFromCSV(csv(dir,"bad-rides.csv","PRIVATE,A,10/24/2026 09:30,10/24/2026 10:30,B,20,1\nPRIVATE,A,02/29/2026 09:00,02/29/2026 10:00,B,20,2\nSHARED,A,10/24/2026 12:00,10/24/2026 13:00,B,20,1;99\nBOGUS,A,10/24/2026 12:00,10/24/2026 13:00,B,20,1\n").toString()), "Reject conflicts dates references and type");
            check(s.getAllRidesAlphabetically().size() == 2, "Failed CSV rides not stored");
            check(!s.loadRidersFromCSV(dir.resolve("missing.csv").toString()) && !s.loadRidesFromCSV(null), "Missing CSV");
            check(!s.loadRidersFromCSV(csv(dir,"quotes.csv","5,\"Unclosed,e@x.com,0551234567,X\n").toString()), "Malformed quotes");
        } finally {
            // Test files are the only items created inside this temporary directory.
            try (java.nio.file.DirectoryStream<Path> entries = Files.newDirectoryStream(dir)) {
                for (Path entry : entries) { Files.delete(entry); }
            }
            Files.delete(dir);
        }
    }
    public static void main(String[] args) throws Exception {
        listTests(); modelTests(); schedulingTests(); csvTests();
    }
}
