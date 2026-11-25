package sdams.model;

import sdams.util.Storable;
import java.time.LocalDate;

public class Rating implements Storable {
    private String ratingId;
    private String studentId;
    private String instructorId;
    private String classId;
    private int score;
    private String comment;
    private LocalDate date;

    public Rating(String ratingId, String studentId, String instructorId, String classId, int score, String comment, LocalDate date) {
        this.ratingId = ratingId;
        this.studentId = studentId;
        this.instructorId = instructorId;
        this.classId = classId;
        this.score = score;
        this.comment = comment;
        this.date = date;
    }

    @Override
    public String getId() { return ratingId; }
    public String getRatingId() { return ratingId; }
    public void setRatingId(String ratingId) { this.ratingId = ratingId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }
    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    @Override
    public String toFileString() {
        return ratingId + "|" + studentId + "|" + instructorId + "|" + classId + "|" + score + "|" + comment + "|" + date;
    }

    public static Rating fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length != 7) {
            throw new IllegalArgumentException("Invalid rating record: " + line);
        }
        return new Rating(p[0], p[1], p[2], p[3], Integer.parseInt(p[4]), p[5], LocalDate.parse(p[6]));
    }

    @Override
    public String toString() { return toFileString(); }
}
