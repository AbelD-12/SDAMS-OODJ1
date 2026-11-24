package sdams.ui.student;

import sdams.ui.common.BaseDashboard;

/** Student menu using the existing shared dashboard. */
public class StudentDashboard extends BaseDashboard {
    private final BookClassPanel book = new BookClassPanel();
    private final MyBookingsPanel bookings = new MyBookingsPanel();
    private final PaymentPanel payments = new PaymentPanel();
    private final RatingPanel ratings = new RatingPanel();

    /** Builds the four student screens and logout menu. */
    public StudentDashboard() {
        super();
        addMenuItem("Book Class", book);
        addMenuItem("My Bookings", bookings);
        addMenuItem("Payments", payments);
        addMenuItem("Ratings", ratings);
        addLogoutButton();
        showPanel("Book Class");
    }

    /** Reloads the selected screen when the student switches menus. */
    @Override
    protected void showPanel(String name) {
        super.showPanel(name);
        switch (name) {
            case "Book Class": book.refresh(); break;
            case "My Bookings": bookings.refresh(); break;
            case "Payments": payments.refresh(); break;
            case "Ratings": ratings.refresh(); break;
        }
    }
}
