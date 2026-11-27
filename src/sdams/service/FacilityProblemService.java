package sdams.service;

import sdams.enums.ProblemStatus;
import sdams.model.FacilityProblem;
import sdams.util.IdGenerator;
import sdams.util.FileHandler;
import sdams.util.InvalidInputException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FacilityProblemService
        extends BaseService<FacilityProblem> {

    @Override
    protected String getFileName() {
        return "data/facility_problems.txt";
    }

    @Override
    protected FacilityProblem parse(String line) {
        return FacilityProblem.fromFileString(line);
    }

    public void reportProblem(String instructorId,
                              String facilityName,
                              String description) {

        validateDetails(instructorId, facilityName, description);
        String id = IdGenerator.next("PRB", getFileName());
        FacilityProblem problem = new FacilityProblem(id, instructorId, facilityName.trim(),
                description.trim(), LocalDate.now(), ProblemStatus.REPORTED);
        List<String> lines = FileHandler.readLines(getFileName());
        lines.add(problem.toFileString());
        FileHandler.writeLines(getFileName(), lines);
    }

    private void validateDetails(String instructorId, String facilityName, String description) {
        if (instructorId == null || instructorId.isBlank() || facilityName == null || facilityName.isBlank()
                || description == null || description.isBlank()) {
            throw new InvalidInputException(
                    "Please fill in all fields.");
        }

        if (facilityName.contains("|")
                || description.contains("|")
                || facilityName.contains("\n")
                || facilityName.contains("\r")
                || description.contains("\n")
                || description.contains("\r")) {
            throw new InvalidInputException(
                    "Invalid characters in input.");
        }

    }

    public void revise(String instructorId, String problemId, String facilityName, String description) {
        FacilityProblem problem = requireEditable(instructorId, problemId);
        validateDetails(instructorId, facilityName, description);
        problem.setFacilityName(facilityName.trim());
        problem.setDescription(description.trim());
        update(problem);
    }

    public void remove(String instructorId, String problemId) {
        requireEditable(instructorId, problemId);
        delete(problemId);
    }

    private FacilityProblem requireEditable(String instructorId, String problemId) {
        FacilityProblem problem = getById(problemId);
        if (problem == null || !problem.getInstructorId().equals(instructorId)) {
            throw new InvalidInputException("Select one of your reports.");
        }
        if (problem.getStatus() != ProblemStatus.REPORTED) {
            throw new InvalidInputException("Only newly reported problems can be edited or deleted.");
        }
        return problem;
    }

    public List<FacilityProblem> getByInstructor(
            String instructorId) {

        List<FacilityProblem> results = new ArrayList<>();

        for (FacilityProblem problem : getAll()) {
            if (problem.getInstructorId().equals(instructorId)) {
                results.add(problem);
            }
        }

        return results;
    }

    public void updateStatus(String problemId,
                             ProblemStatus status) {

        FacilityProblem problem = getById(problemId);

        if (problem == null || status == null) {
            throw new InvalidInputException(
                    "Invalid problem or status.");
        }

        problem.setStatus(status);
        update(problem);
    }
}