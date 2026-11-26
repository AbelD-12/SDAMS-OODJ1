package sdams.service;

import sdams.model.*;
import sdams.util.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RatingService extends BaseService<Rating> {
    @Override
    protected String getFileName() { return "data/ratings.txt"; }

    @Override
    protected Rating parse(String line) { return Rating.fromFileString(line); }

    public void addRating(Rating r) {
        if (r == null || !Validator.isIntInRange(r.getScore(), 1, 5)) {
            throw new InvalidInputException("Score must be between 1 and 5.");
        }
        ClassSession c = new ClassService().getById(r.getClassId());
        if (c == null || !c.getInstructorId().equals(r.getInstructorId())) {
            throw new InvalidInputException("Invalid class or instructor.");
        }
        if (!new AttendanceService().hasAttended(r.getStudentId(), r.getClassId())) {
            throw new InvalidInputException("You must attend the class before rating.");
        }
        for (Rating existing : getByStudent(r.getStudentId())) {
            if (existing.getClassId().equals(r.getClassId())) {
                throw new InvalidInputException("You already rated this class.");
            }
        }
        if (r.getComment() == null) r.setComment("");
        Validator.noPipe(r.getComment());
        if (r.getComment().contains("\n") || r.getComment().contains("\r")) {
            throw new InvalidInputException("Comment must be one line.");
        }
        r.setRatingId(IdGenerator.next("RAT", getFileName()));
        r.setDate(LocalDate.now());
        super.add(r);
    }

    @Override
    public void add(Rating r) { addRating(r); }

    public List<Rating> getByInstructor(String instructorId) {
        List<Rating> results = new ArrayList<>();
        for (Rating r : getAll()) if (r.getInstructorId().equals(instructorId)) results.add(r);
        return results;
    }

    public List<Rating> getByStudent(String studentId) {
        List<Rating> results = new ArrayList<>();
        for (Rating r : getAll()) if (r.getStudentId().equals(studentId)) results.add(r);
        return results;
    }

    public double getAverageScore(String instructorId) {
        List<Rating> ratings = getByInstructor(instructorId);
        if (ratings.isEmpty()) return 0;
        int total = 0;
        for (Rating r : ratings) total += r.getScore();
        return (double) total / ratings.size();
    }
}
