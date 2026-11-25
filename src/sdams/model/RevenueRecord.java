package sdams.model;

/**
 * One line of the revenue report: a period (a month or a year),
 * how many PAID payments it has and the revenue they add up to.
 */
public class RevenueRecord {

    private String period;
    private int registrations;
    private double totalRevenue;

    /**
     * Creates a revenue record.
     *
     * @param period         e.g. "January 2026" or "2026"
     * @param registrations  number of PAID payments in the period
     * @param totalRevenue   sum of the PAID amounts in the period
     */
    public RevenueRecord(String period, int registrations, double totalRevenue) {
        this.period = period;
        this.registrations = registrations;
        this.totalRevenue = totalRevenue;
    }

    /** Returns the period, e.g. "January 2026" or "2026". */
    public String getPeriod() {
        return period;
    }

    /** Returns the number of PAID payments in the period. */
    public int getRegistrations() {
        return registrations;
    }

    /** Returns the total revenue of the period. */
    public double getTotalRevenue() {
        return totalRevenue;
    }
}