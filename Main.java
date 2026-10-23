import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.EOFException;

/** Role C: the only application class that reads/prints console input/output. */
public class Main {
    private static final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    private static String read(String prompt) throws IOException {
        System.out.print(prompt);
        String line = input.readLine();
        if (line == null) { throw new EOFException(); }
        return line.trim();
    }
    private static int number(String prompt) throws IOException {
        return Integer.parseInt(read(prompt));
    }
    private static <T> void display(LinkedList<T> list) {
        if (list.size() == 0) { System.out.println("No results."); return; }
        LinkedList<T>.Cursor cursor = list.cursor();
        while (cursor.hasNext()) { System.out.println(cursor.next().toString()); }
    }
    private static void outcome(boolean success) {
        System.out.println(success ? "Operation succeeded." : "Operation failed. Check details, duplicates, or conflicts.");
    }
    private static void loadResult(String name, boolean success) {
        System.out.println(name + (success ? ": loaded." : ": load failed or some rows were rejected."));
    }
    private static void menu() {
        System.out.println("\n1. List all riders.");
        System.out.println("2. Search for riders by home city.");
        System.out.println("3. Search for riders by name.");
        System.out.println("4. Add a rider.");
        System.out.println("5. List all drivers.");
        System.out.println("6. Search for drivers by vehicle type.");
        System.out.println("7. Add a driver.");
        System.out.println("8. List all rides alphabetically by pickup location.");
        System.out.println("9. Search for rides by pickup location.");
        System.out.println("10. Search for rides by rider name.");
        System.out.println("11. List the participants of a shared ride by pickup location.");
        System.out.println("12. Add a ride.");
        System.out.println("0. Exit the program.");
    }
    private static void addRider(RideSharingSystem system) throws IOException {
        int id = number("Rider ID: ");
        String name = read("Full name: ");
        String phone = read("Phone (10 digits): ");
        String email = read("Email: ");
        String city = read("Home city: ");
        outcome(system.addRider(new Rider(id, name, phone, email, city)));
    }
    private static void addDriver(RideSharingSystem system) throws IOException {
        int id = number("Driver ID: ");
        String name = read("Full name: ");
        String phone = read("Phone (10 digits): ");
        String plate = read("Plate (e.g. ABC1234): ");
        VehicleType type = VehicleType.valueOf(read("Vehicle type (SEDAN/LUXURY_SEDAN/SUV/VAN): ").toUpperCase());
        outcome(system.addDriver(new Driver(id, name, phone, plate, type)));
    }
    private static void addRide(RideSharingSystem system) throws IOException {
        String type = read("Ride type (PRIVATE/SHARED): ").toUpperCase();
        if (!type.equals("PRIVATE") && !type.equals("SHARED")) {
            throw new IllegalArgumentException("Choose PRIVATE or SHARED.");
        }
        String pickup = read("Pickup location: ");
        IDateTime start = DateTime.parse(read("Pickup time (MM/DD/YYYY HH:MM): "));
        IDateTime end = DateTime.parse(read("Dropoff time (MM/DD/YYYY HH:MM): "));
        String dropoff = read("Dropoff location: ");
        int driver = number("Driver ID: ");
        if (type.equals("PRIVATE")) {
            outcome(system.schedulePrivateRide(pickup, start, end, dropoff, number("Rider ID: "), driver));
        } else {
            String[] parts = read("Rider IDs separated by semicolons (e.g. 1;2): ").split(";", -1);
            int[] ids = new int[parts.length];
            for (int i = 0; i < parts.length; i++) { ids[i] = Integer.parseInt(parts[i].trim()); }
            outcome(system.scheduleSharedRide(pickup, start, end, dropoff, ids, driver));
        }
    }
    public static void main(String[] args) throws IOException {
        RideSharingSystem system = new RideSharingSystem();
        String ridersFile = "riders_100.csv";
        String driversFile = "drivers_30.csv";
        String ridesFile = "rides_40.csv";
        if (args.length == 1 && args[0].equals("--demo")) {
            System.out.println("Practice data mode: these are synthetic examples, not the official CSV files.");
            ridersFile = "sample-data/riders_demo.csv";
            driversFile = "sample-data/drivers_demo.csv";
            ridesFile = "sample-data/rides_demo.csv";
        } else if (args.length == 3) {
            ridersFile = args[0]; driversFile = args[1]; ridesFile = args[2];
        } else if (args.length != 0) {
            System.out.println("Usage: java Main [--demo | riders.csv drivers.csv rides.csv]");
            return;
        }
        loadResult(ridersFile, system.loadRidersFromCSV(ridersFile));
        loadResult(driversFile, system.loadDriversFromCSV(driversFile));
        loadResult(ridesFile, system.loadRidesFromCSV(ridesFile));
        boolean running = true;
        while (running) {
            menu();
            try {
                switch (number("Choose an option: ")) {
                    case 1: display(system.getAllRiders()); break;
                    case 2: display(system.searchRidersByHomeCity(read("Home city: "))); break;
                    case 3: display(system.searchRidersByName(read("Exact full name: "))); break;
                    case 4: addRider(system); break;
                    case 5: display(system.getAllDrivers()); break;
                    case 6: display(system.searchDriversByVehicleType(VehicleType.valueOf(
                            read("Vehicle type (SEDAN/LUXURY_SEDAN/SUV/VAN): ").toUpperCase()))); break;
                    case 7: addDriver(system); break;
                    case 8: display(system.getAllRidesAlphabetically()); break;
                    case 9: display(system.searchRidesByPickupLocation(read("Pickup location: "))); break;
                    case 10: display(system.searchRidesByRiderName(read("Exact rider full name: "))); break;
                    case 11: display(system.getSharedRideParticipants(read("Shared pickup location: "))); break;
                    case 12: addRide(system); break;
                    case 0: running = false; break;
                    default: System.out.println("Choose 0 to 12.");
                }
            } catch (EOFException e) {
                running = false;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
    }
}
