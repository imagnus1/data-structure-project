public class Rider extends Person implements IRider {

    private final int riderId; // not needed separately — id is inherited, see note below
    private String email;
    private String homeCity;

    public Rider(int id, String name, String phoneNumber, String email, String homeCity) {
        super(id, name, phoneNumber);
        this.email = email;
        this.homeCity = homeCity;
        this.riderId = id;
    }

    @Override
    public String getEmail() { return email; }

    @Override
    public void setEmail(String email) { this.email = email; }

    @Override
    public String getHomeCity() { return homeCity; }

    @Override
    public void setHomeCity(String homeCity) { this.homeCity = homeCity; }

    @Override
    public int compareTo(IRider other) {
        return Integer.compare(this.getId(), other.getId());
    }

    @Override
    public String toString() {
        return "Rider[id=" + getId() + ", name=" + getName() + ", email=" + email
                + ", homeCity=" + homeCity + ", phone=" + getPhoneNumber() + "]";
    }
}