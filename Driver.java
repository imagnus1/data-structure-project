public class Driver extends Person implements IDriver {

    private String vehiclePlate;
    private VehicleType vehicleType;

    public Driver(int id, String name, String phoneNumber, String vehiclePlate, VehicleType vehicleType) {
        super(id, name, phoneNumber);
        setVehiclePlate(vehiclePlate);
        this.vehicleType = vehicleType;
    }

    @Override
    public String getVehiclePlate() { return vehiclePlate; }

    @Override
    public final void setVehiclePlate(String vehiclePlate) {
        // 3 uppercase letters followed by 4 digits, e.g. ABC1234
        if (vehiclePlate == null || !vehiclePlate.matches("^[A-Z]{3}\\d{4}$")) {
            throw new IllegalArgumentException("Vehicle plate must be 3 uppercase letters followed by 4 digits.");
        }
        this.vehiclePlate = vehiclePlate;
    }

    @Override
    public VehicleType getVehicleType() { return vehicleType; }

    @Override
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }

    @Override
    public int compareTo(IDriver other) {
        return Integer.compare(this.getId(), other.getId());
    }

    @Override
    public String toString() {
        return "Driver[id=" + getId() + ", name=" + getName() + ", plate=" + vehiclePlate
                + ", type=" + vehicleType + ", phone=" + getPhoneNumber() + "]";
    }
}