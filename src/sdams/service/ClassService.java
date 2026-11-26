package sdams.service;

import sdams.enums.ClassStatus;
import sdams.model.ClassSession;
import sdams.util.InvalidInputException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/** Temporary class service for local student-module testing. */
public class ClassService extends BaseService<ClassSession> {
    @Override protected String getFileName() { return "data/classes.txt"; }
    @Override protected ClassSession parse(String line) { return ClassSession.fromFileString(line); }

    /** Returns open classes that have not started. */
    public List<ClassSession> getOpenClasses() {
        List<ClassSession> result = new ArrayList<>();
        for (ClassSession c : getAll()) {
            if (c.getStatus() == ClassStatus.OPEN && isUpcoming(c)) result.add(c);
        }
        return result;
    }

    /** Returns classes belonging to an instructor. */
    public List<ClassSession> getByInstructor(String instructorId) {
        List<ClassSession> result = new ArrayList<>();
        for (ClassSession c : getAll()) {
            if (c.getInstructorId().equals(instructorId)) result.add(c);
        }
        return result;
    }

    /** Persists a class status change, including capacity changes made by BookingService. */
    public void updateStatus(String classId, ClassStatus status) {
        ClassSession c = getById(classId);
        if (c == null || status == null) throw new InvalidInputException("Invalid class or status.");
        c.setStatus(status);
        update(c);
    }

    /** Checks for an instructor's upcoming open or full classes. */
    public boolean hasUpcomingClasses(String instructorId) {
        for (ClassSession c : getByInstructor(instructorId)) {
            if (isUpcoming(c) && (c.getStatus() == ClassStatus.OPEN || c.getStatus() == ClassStatus.FULL)) return true;
        }
        return false;
    }

    /** Cancels a class and its active bookings for local integration testing. */
    public void cancel(String classId) {
        ClassSession c = getById(classId);
        if (c == null || c.getStatus() == ClassStatus.COMPLETED) {
            throw new InvalidInputException("Class cannot be cancelled.");
        }
        updateStatus(classId, ClassStatus.CANCELLED);
        new BookingService().cancelAllForClass(classId);
    }

    private boolean isUpcoming(ClassSession c) {
        LocalDate today = LocalDate.now();
        return c.getDate().isAfter(today) ||
                (c.getDate().equals(today) && c.getStartTime().isAfter(LocalTime.now()));
    }
}
