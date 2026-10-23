public class Datetime implements IDateTime {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    // Constructor to set the values once (since they cannot be modified later)
    public Datetime(int year, int month, int day, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    @Override
    public int getYear() { return year; }

    @Override
    public int getMonth() { return month; }

    @Override
    public int getDay() { return day; }

    @Override
    public int getHour() { return hour; }

    @Override
    public int getMinute() { return minute; }

    @Override
    public String format() {
        // Formats as "MM/DD/YYYY HH:MM" with leading zeros where needed
        return String.format("%02d/%02d/%04d %02d:%02d", month, day, year, hour, minute);
    }

    @Override
    public int compareTo(IDateTime other) {
        // Compares chronologically: year, then month, then day, then hour, then minute
        if (this.year != other.getYear()) return Integer.compare(this.year, other.getYear());
        if (this.month != other.getMonth()) return Integer.compare(this.month, other.getMonth());
        if (this.day != other.getDay()) return Integer.compare(this.day, other.getDay());
        if (this.hour != other.getHour()) return Integer.compare(this.hour, other.getHour());

        return Integer.compare(this.minute, other.getMinute());
    }
}