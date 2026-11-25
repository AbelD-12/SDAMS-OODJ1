package sdams.service;

import sdams.enums.ClassStatus;
import sdams.enums.Discipline;
import sdams.model.ClassSession;
import sdams.util.FileHandler;
import sdams.util.IdGenerator;
import sdams.util.InvalidInputException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/** Manages class schedules and availability shared by instructors and students. */
public class ClassService extends BaseService<ClassSession> {
    @Override protected String getFileName() { return "data/classes.txt"; }
    @Override protected ClassSession parse(String line) { return ClassSession.fromFileString(line); }

    public List<ClassSession> getOpenClasses() {
        return getAll().stream()
                .filter(c -> c.getStatus() == ClassStatus.OPEN && isUpcoming(c))
                .collect(Collectors.toList());
    }

    public List<ClassSession> getByInstructor(String instructorId) {
        return getAll().stream().filter(c -> c.getInstructorId().equals(instructorId))
                .collect(Collectors.toList());
    }

    public boolean hasUpcomingClasses(String instructorId) {
        return getByInstructor(instructorId).stream()
                .anyMatch(c -> isBookable(c) && isUpcoming(c));
    }

    public ClassSession schedule(String instructorId, Discipline discipline, LocalDate date,
                                 LocalTime start, LocalTime end, int capacity, double fee) {
        String id = IdGenerator.next("CLS", getFileName());
        ClassSession session = new ClassSession(id, instructorId, discipline, date,
                start, end, capacity, fee, ClassStatus.OPEN);
        validateSchedule(session, null);
        List<String> lines = FileHandler.readLines(getFileName());
        lines.add(session.toFileString());
        FileHandler.writeLines(getFileName(), lines);
        return session;
    }

    public void revise(String instructorId, String classId, Discipline discipline, LocalDate date,
                       LocalTime start, LocalTime end, int capacity, double fee) {
        ClassSession current = requireOwned(instructorId, classId);
        if (!isBookable(current) || !isUpcoming(current)) {
            throw new InvalidInputException("Only upcoming open or full classes can be updated.");
        }
        ClassSession revised = new ClassSession(classId, instructorId, discipline, date,
                start, end, capacity, fee, ClassStatus.OPEN);
        validateSchedule(revised, classId);
        int bookings = new BookingService().countActive(classId);
        if (capacity < bookings) {
            throw new InvalidInputException("Capacity must cover all active bookings.");
        }
        if (bookings > 0 && (discipline != current.getDiscipline()
                || !date.equals(current.getDate()) || !start.equals(current.getStartTime())
                || !end.equals(current.getEndTime()) || Double.compare(fee, current.getFee()) != 0)) {
            throw new InvalidInputException("Only capacity can change while the class has bookings.");
        }
        revised.setStatus(bookings == capacity ? ClassStatus.FULL : ClassStatus.OPEN);
        update(revised);
    }

    public void updateStatus(String classId, ClassStatus status) {
        ClassSession session = getById(classId);
        if (session == null || status == null) {
            throw new InvalidInputException("Invalid class or status.");
        }
        session.setStatus(status);
        update(session);
    }

    public void cancel(String instructorId, String classId) {
        requireOwned(instructorId, classId);
        cancel(classId);
    }

    public void cancel(String classId) {
        ClassSession session = getById(classId);
        if (session == null || session.getStatus() == ClassStatus.COMPLETED
                || (session.getStatus() != ClassStatus.CANCELLED && !isUpcoming(session))) {
            throw new InvalidInputException("Only upcoming classes can be cancelled.");
        }
        updateStatus(classId, ClassStatus.CANCELLED);
        // Retrying a cancelled class finishes any outstanding cancellations and refunds.
        new BookingService().cancelAllForClass(classId);
    }

    private ClassSession requireOwned(String instructorId, String classId) {
        ClassSession session = getById(classId);
        if (session == null || !session.getInstructorId().equals(instructorId)) {
            throw new InvalidInputException("Select one of your classes.");
        }
        return session;
    }

    private void validateSchedule(ClassSession session, String excludedId) {
        LocalTime start = session.getStartTime();
        LocalTime end = session.getEndTime();
        if (session.getInstructorId() == null || session.getInstructorId().isBlank()
                || session.getDiscipline() == null || session.getDate() == null
                || start == null || end == null) {
            throw new InvalidInputException("Enter all class details.");
        }
        if (start.getSecond() != 0 || end.getSecond() != 0
                || start.getNano() != 0 || end.getNano() != 0) {
            throw new InvalidInputException("Use HH:mm for class times.");
        }
        if (!isUpcoming(session) || !end.isAfter(start)) {
            throw new InvalidInputException("Start must be in the future and end must follow start.");
        }
        if (session.getCapacity() <= 0 || !Double.isFinite(session.getFee()) || session.getFee() < 0) {
            throw new InvalidInputException("Enter a positive capacity and a non-negative fee.");
        }
        for (ClassSession other : getByInstructor(session.getInstructorId())) {
            if (!other.getId().equals(excludedId) && isBookable(other)
                    && other.getDate().equals(session.getDate())
                    && start.isBefore(other.getEndTime()) && end.isAfter(other.getStartTime())) {
                throw new InvalidInputException("This class overlaps another class on your schedule.");
            }
        }
    }

    private boolean isUpcoming(ClassSession session) {
        return LocalDateTime.of(session.getDate(), session.getStartTime()).isAfter(LocalDateTime.now());
    }

    private boolean isBookable(ClassSession session) {
        return session.getStatus() == ClassStatus.OPEN || session.getStatus() == ClassStatus.FULL;
    }
}
