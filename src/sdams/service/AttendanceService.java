package sdams.service;

import sdams.enums.AttendanceStatus;
import sdams.util.FileHandler;
import java.time.LocalDate;

/** Temporary read-only attendance dependency for the student rating module. */
public class AttendanceService {
    /** Returns true only for a matching PRESENT or LATE attendance record. */
    public boolean hasAttended(String studentId, String classId) {
        if (studentId == null || classId == null) return false;
        boolean attended = false;
        for (String line : FileHandler.readLines("data/attendance.txt")) {
            if (line.isBlank()) continue;
            String[] p = line.split("\\|", -1);
            if (p.length == 5 && classId.equals(p[1]) && studentId.equals(p[2])) {
                AttendanceStatus status = AttendanceStatus.valueOf(p[3]);
                LocalDate date = LocalDate.parse(p[4]);
                attended = !date.isAfter(LocalDate.now()) &&
                        (status == AttendanceStatus.PRESENT || status == AttendanceStatus.LATE);
            }
        }
        return attended;
    }
}
