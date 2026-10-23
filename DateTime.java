/** Immutable calendar date/time, compared chronologically. */
public class DateTime implements IDateTime {
    private final int year, month, day, hour, minute;
    public DateTime(int year, int month, int day, int hour, int minute) {
        int[] days = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) { days[1] = 29; }
        if (year < 1 || month < 1 || month > 12 || day < 1 || day > days[month - 1]
                || hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Invalid date/time");
        }
        this.year = year; this.month = month; this.day = day;
        this.hour = hour; this.minute = minute;
    }
    public int getYear() { return year; }
    public int getMonth() { return month; }
    public int getDay() { return day; }
    public int getHour() { return hour; }
    public int getMinute() { return minute; }
    public String format() {
        return String.format("%02d/%02d/%04d %02d:%02d", month, day, year, hour, minute);
    }
    public int compareTo(IDateTime other) {
        if (year != other.getYear()) { return Integer.compare(year, other.getYear()); }
        if (month != other.getMonth()) { return Integer.compare(month, other.getMonth()); }
        if (day != other.getDay()) { return Integer.compare(day, other.getDay()); }
        if (hour != other.getHour()) { return Integer.compare(hour, other.getHour()); }
        return Integer.compare(minute, other.getMinute());
    }
    public static DateTime parse(String value) {
        if (value == null || !value.matches("[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}")) {
            throw new IllegalArgumentException("Use MM/DD/YYYY HH:MM");
        }
        return new DateTime(Integer.parseInt(value.substring(6, 10)),
                Integer.parseInt(value.substring(0, 2)), Integer.parseInt(value.substring(3, 5)),
                Integer.parseInt(value.substring(11, 13)), Integer.parseInt(value.substring(14, 16)));
    }
}
