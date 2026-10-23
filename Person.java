public abstract class Person implements IPerson {

    private int id;
    private String name;
    private String phoneNumber;
    private LinkedList<IRide> rideHistory;

    public Person(int id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        setPhoneNumber(phoneNumber); // reuse the validation logic
        this.rideHistory = new LinkedList<IRide>();
    }

    @Override
    public int getId() { return id; }

    @Override
    public String getName() { return name; }

    @Override
    public void setName(String name) { this.name = name; }

    @Override
    public String getPhoneNumber() { return phoneNumber; }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        // Must be exactly 10 digits, numeric only
        if (phoneNumber == null || !phoneNumber.matches("^\\d{10}$")) {
            throw new IllegalArgumentException("Phone number must be exactly 10 digits.");
        }
        this.phoneNumber = phoneNumber;
    }

    @Override
    public LinkedList<IRide> getRideHistory() { return rideHistory; }
}