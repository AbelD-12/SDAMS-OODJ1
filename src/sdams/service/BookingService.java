package sdams.service;

import sdams.enums.*;
import sdams.model.*;
import sdams.util.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class BookingService extends BaseService<Booking> {
    @Override
    protected String getFileName() { return "data/bookings.txt"; }

    @Override
    protected Booking parse(String line) { return Booking.fromFileString(line); }

    public Booking book(String studentId, String classId) {
        User student = new UserService().getById(studentId);
        if (student == null || student.getRole() != Role.STUDENT) {
            throw new InvalidInputException("Student not found.");
        }
        ClassSession c = validateTarget(studentId, classId, null);
        String id = IdGenerator.next("BKG", getFileName());
        // Existing sample payments may refer to booking IDs not yet in bookings.txt.
        PaymentService payments = new PaymentService();
        while (payments.getByBooking(id) != null) {
            id = String.format("BKG%03d", Integer.parseInt(id.substring(3)) + 1);
        }
        Booking b = new Booking(id, studentId, classId, LocalDate.now(), BookingStatus.BOOKED);
        add(b);
        refreshCapacity(c.getId());
        return b;
    }

    public void reschedule(String bookingId, String newClassId) {
        Booking b = requireActive(bookingId);
        requireUpcoming(b);
        if (b.getClassId().equals(newClassId)) {
            throw new InvalidInputException("Select a different class.");
        }
        validateTarget(b.getStudentId(), newClassId, bookingId);
        String oldClassId = b.getClassId();
        b.setClassId(newClassId);
        update(b);
        refreshCapacity(oldClassId);
        refreshCapacity(newClassId);
    }

    public void cancel(String bookingId) {
        Booking b = requireActive(bookingId);
        requireUpcoming(b);
        cancelBooking(b);
    }

    public void cancelAllForClass(String classId) {
        for (Booking b : getAll()) {
            if (b.getClassId().equals(classId) && b.getStatus() == BookingStatus.BOOKED) {
                cancelBooking(b);
            }
        }
    }

    public List<Booking> getByStudent(String studentId) {
        List<Booking> results = new ArrayList<>();
        for (Booking b : getAll()) {
            if (b.getStudentId().equals(studentId)) results.add(b);
        }
        return results;
    }

    @Override
    public List<Booking> search(String keyword) { return super.search(keyword); }

    public int countActive(String classId) {
        int count = 0;
        for (Booking b : getAll()) {
            if (b.getClassId().equals(classId) && b.getStatus() == BookingStatus.BOOKED) count++;
        }
        return count;
    }

    public boolean hasActiveBookings(String studentId) {
        for (Booking b : getByStudent(studentId)) {
            if (b.getStatus() == BookingStatus.BOOKED) {
                ClassSession c = new ClassService().getById(b.getClassId());
                if (c == null || (c.getStatus() != ClassStatus.CANCELLED && c.getStatus() != ClassStatus.COMPLETED)) return true;
            }
        }
        return false;
    }

    public List<String> getRegisteredStudentIds(String classId) {
        List<String> ids = new ArrayList<>();
        PaymentService payments = new PaymentService();
        for (Booking b : getAll()) {
            if (b.getClassId().equals(classId) && b.getStatus() == BookingStatus.BOOKED
                    && payments.isPaid(b.getId()) && !ids.contains(b.getStudentId())) {
                ids.add(b.getStudentId());
            }
        }
        return ids;
    }

    private Booking requireActive(String id) {
        Booking b = getById(id);
        if (b == null || b.getStatus() != BookingStatus.BOOKED) {
            throw new InvalidInputException("Active booking not found.");
        }
        return b;
    }

    private boolean hasStarted(ClassSession c) {
        return c.getDate().isBefore(LocalDate.now()) ||
                (c.getDate().equals(LocalDate.now()) && !c.getStartTime().isAfter(LocalTime.now()));
    }

    private void requireUpcoming(Booking b) {
        ClassSession c = new ClassService().getById(b.getClassId());
        if (c == null || hasStarted(c) || c.getStatus() == ClassStatus.COMPLETED || c.getStatus() == ClassStatus.CANCELLED) {
            throw new InvalidInputException("Only upcoming bookings can be changed.");
        }
    }

    private ClassSession validateTarget(String studentId, String classId, String excludedBooking) {
        ClassService classes = new ClassService();
        ClassSession target = classes.getById(classId);
        if (target == null || target.getStatus() != ClassStatus.OPEN || hasStarted(target)) {
            throw new InvalidInputException("Select an upcoming OPEN class.");
        }
        if (countActive(classId) >= target.getCapacity()) {
            throw new InvalidInputException("This class is full.");
        }
        for (Booking b : getByStudent(studentId)) {
            if (b.getStatus() != BookingStatus.BOOKED || b.getId().equals(excludedBooking)) continue;
            if (b.getClassId().equals(classId)) throw new InvalidInputException("Class already booked.");
            ClassSession other = classes.getById(b.getClassId());
            if (other != null && other.getStatus() != ClassStatus.CANCELLED
                    && other.getDate().equals(target.getDate())
                    && target.getStartTime().isBefore(other.getEndTime())
                    && other.getStartTime().isBefore(target.getEndTime())) {
                throw new InvalidInputException("This class overlaps another booking.");
            }
        }
        return target;
    }

    private void cancelBooking(Booking b) {
        new PaymentService().refund(b.getId());
        b.setStatus(BookingStatus.CANCELLED);
        update(b);
        refreshCapacity(b.getClassId());
    }

    private void refreshCapacity(String classId) {
        ClassService classes = new ClassService();
        ClassSession c = classes.getById(classId);
        if (c == null || (c.getStatus() != ClassStatus.OPEN && c.getStatus() != ClassStatus.FULL)) return;
        ClassStatus status = countActive(classId) >= c.getCapacity() ? ClassStatus.FULL : ClassStatus.OPEN;
        if (c.getStatus() != status) classes.updateStatus(classId, status);
    }
}
