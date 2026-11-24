package sdams.model;

import sdams.enums.ClassStatus;
import sdams.enums.Discipline;
import sdams.util.Storable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Temporary local class model following Member 5's shared contract. */
public class ClassSession implements Storable {
    private String classId;
    private String instructorId;
    private Discipline discipline;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;
    private double fee;
    private ClassStatus status;

    /** Creates a class session in the same order as classes.txt. */
    public ClassSession(String classId, String instructorId, Discipline discipline,
                        LocalDate date, LocalTime startTime, LocalTime endTime,
                        int capacity, double fee, ClassStatus status) {
        this.classId = classId;
        this.instructorId = instructorId;
        this.discipline = discipline;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.fee = fee;
        this.status = status;
    }

    @Override public String getId() { return classId; }
    public String getClassId() { return classId; }
    public String getInstructorId() { return instructorId; }
    public Discipline getDiscipline() { return discipline; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public int getCapacity() { return capacity; }
    public double getFee() { return fee; }
    public ClassStatus getStatus() { return status; }

    public void setClassId(String classId) { this.classId = classId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }
    public void setDiscipline(Discipline discipline) { this.discipline = discipline; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setFee(double fee) { this.fee = fee; }
    public void setStatus(ClassStatus status) { this.status = status; }

    /** Serializes the class to a pipe-delimited record. */
    @Override public String toFileString() {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        return String.join("|", classId, instructorId, discipline.name(), date.toString(),
                startTime.format(timeFormat), endTime.format(timeFormat),
                Integer.toString(capacity), Double.toString(fee), status.name());
    }

    /** Reads a class record using the shared file format. */
    public static ClassSession fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length != 9) throw new IllegalArgumentException("Invalid class record: " + line);
        return new ClassSession(p[0], p[1], Discipline.valueOf(p[2]), LocalDate.parse(p[3]),
                LocalTime.parse(p[4]), LocalTime.parse(p[5]), Integer.parseInt(p[6]),
                Double.parseDouble(p[7]), ClassStatus.valueOf(p[8]));
    }

    @Override public String toString() { return toFileString(); }
}
