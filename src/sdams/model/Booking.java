package sdams.model;

import sdams.util.Storable;
import java.time.LocalDate;
import sdams.enums.BookingStatus;

public class Booking implements Storable {
    private String bookingId;
    private String studentId;
    private String classId;
    private LocalDate bookingDate;
    private BookingStatus status;


    public Booking(String bookingId, String studentId, String classId, LocalDate bookingDate, BookingStatus status) {
        this.bookingId = bookingId;
        this.studentId = studentId;
        this.classId = classId;
        this.bookingDate = bookingDate;
        this.status = status;
    }


    @Override
    public String getId() { return bookingId; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }
    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    @Override
    public String toFileString() {
        return bookingId + "|" + studentId + "|" + classId + "|" + bookingDate + "|" + status;
    }

    public static Booking fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length != 5) {
            throw new IllegalArgumentException("Invalid booking record: " + line);
        }
        return new Booking(p[0], p[1], p[2], LocalDate.parse(p[3]), BookingStatus.valueOf(p[4]));
    }

    @Override
    public String toString() { return toFileString(); }
}
