package sdams.model;

import sdams.enums.ProblemStatus;
import sdams.util.Storable;
import java.time.LocalDate;

public class FacilityProblem implements Storable {

    private String problemId;
    private String instructorId;
    private String facilityName;
    private String description;
    private LocalDate date;
    private ProblemStatus status;

    public FacilityProblem(String problemId, String instructorId,
                           String facilityName, String description,
                           LocalDate date, ProblemStatus status) {

        this.problemId = problemId;
        this.instructorId = instructorId;
        this.facilityName = facilityName;
        this.description = description;
        this.date = date;
        this.status = status;
    }

    @Override
    public String getId() {
        return problemId;
    }

    public String getProblemId() {
        return problemId;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public ProblemStatus getStatus() {
        return status;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(ProblemStatus status) {
        this.status = status;
    }

    @Override
    public String toFileString() {
        return String.join("|",
                problemId,
                instructorId,
                facilityName,
                description,
                date.toString(),
                status.name());
    }

    public static FacilityProblem fromFileString(String line) {

        String[] p = line.split("\\|", -1);

        if (p.length != 6) throw new IllegalArgumentException("Invalid facility problem record.");

        return new FacilityProblem(
                p[0],
                p[1],
                p[2],
                p[3],
                LocalDate.parse(p[4]),
                ProblemStatus.valueOf(p[5])
        );
    }

    @Override
    public String toString() {
        return toFileString();
    }
}