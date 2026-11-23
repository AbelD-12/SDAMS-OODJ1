package sdams.model;

public class RevenueRecord {

    private String period;
    private int registrations;
    private double totalRevenue;

    public RevenueRecord(String period, int registrations, double totalRevenue) {
        this.period = period;
        this.registrations = registrations;
        this.totalRevenue = totalRevenue;
    }

    public String getPeriod() {
        return period;
    }

    public int getRegistrations() {
        return registrations;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}