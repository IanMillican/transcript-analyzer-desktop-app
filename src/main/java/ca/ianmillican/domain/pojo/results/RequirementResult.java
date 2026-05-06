package ca.ianmillican.domain.pojo.results;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ianmillican.domain.pojo.degree.Requirement;
import ca.ianmillican.domain.pojo.degree.Requirement.TYPE;
import ca.ianmillican.domain.pojo.transcript.CourseAttempt;

public class RequirementResult {

    public static final int NO_SELECTION = -1;

    private Requirement originalRequirement;
    private CourseAttempt bestCourseAttempt;
    private boolean isSatisfied;
    private Map<Integer, List<CourseAttempt>> constraintMatches;
    private List<RequirementResult> childRequirements;
    private int selectedChildIndex;

    // Leaf Result
    public RequirementResult(Requirement originalRequirement, boolean isSatisfied, CourseAttempt bestCourseAttempt) {
        this.originalRequirement = originalRequirement;
        this.isSatisfied = isSatisfied;
        this.bestCourseAttempt = bestCourseAttempt;
        this.constraintMatches = null;
        this.childRequirements = null;
        this.selectedChildIndex = NO_SELECTION;
    }

    // Operator Result
    public RequirementResult(Requirement originalRequirement, boolean isSatisfied, List<RequirementResult> childRequirements, int selectedChildIndex) {
        this.originalRequirement = originalRequirement;
        this.isSatisfied = isSatisfied;
        this.constraintMatches = null;
        this.childRequirements = childRequirements;
        this.selectedChildIndex = selectedChildIndex;
        this.bestCourseAttempt = null;
    }

    // Constraint Result
    public RequirementResult(Requirement originalRequirement, boolean isSatisfied, Map<Integer, List<CourseAttempt>> constraintMatches) {
        this.originalRequirement = originalRequirement;
        this.isSatisfied = isSatisfied;
        this.constraintMatches = constraintMatches;
        this.childRequirements = null;
        this.selectedChildIndex = NO_SELECTION;
        this.bestCourseAttempt = null;
    }

    public List<Map<String, Boolean>> getBranchCourseStatus() {
        if (this.originalRequirement.getType() == TYPE.COURSE) {
            String courseCode = this.originalRequirement.getCourseCode();
            boolean passed = this.isSatisfied;
            Map<String, Boolean> courseStatus = new HashMap<>();
            courseStatus.put(courseCode, passed);
            return List.of(courseStatus);
        } else if (this.originalRequirement.getType() == TYPE.CONSTRAINT) {
            return List.of();
        } else {
            List<Map<String, Boolean>> statuses = new ArrayList<>();
            for(RequirementResult child : this.childRequirements) {
                statuses.addAll(child.getBranchCourseStatus());
            }
            return statuses;
        }

    }


    public CourseAttempt getBestCourseAttempt() {
        return bestCourseAttempt;
    }

    public Requirement getOriginalRequirement() {
        return originalRequirement;
    }

    public boolean isSatisfied() {
        return isSatisfied;
    }

    public Map<Integer, List<CourseAttempt>> getConstraintMatches() {
        return constraintMatches;
    }

    public List<RequirementResult> getChildRequirements() {
        return childRequirements;
    }

    public RequirementResult getSelectedResult() {
        return childRequirements.get(selectedChildIndex);
    }

    public String prettyPrint(int indent) {
        String pad = " ".repeat(indent);
        StringBuilder sb = new StringBuilder();
        String status = isSatisfied ? "✓" : "✗";
        switch (originalRequirement.getType()) {
            case COURSE:
                sb.append(pad).append(status).append(" ")
                .append(originalRequirement.getCourseCode())
                .append(" - ").append(originalRequirement.getCourse().getName());
                if (isSatisfied && bestCourseAttempt != null)
                    sb.append("  [").append(bestCourseAttempt.getGrade()).append("]");
                sb.append("\n");
                break;
            case AND:
                sb.append(pad).append(status).append(" AND:\n");
                for (RequirementResult child : childRequirements)
                    sb.append(child.prettyPrint(indent + 2));
                break;
            case OR:
                sb.append(pad).append(status).append(" OR:\n");
                for (RequirementResult child : childRequirements)
                    sb.append(child.prettyPrint(indent + 2));
                break;
            case XOR:
                sb.append(pad).append(status).append(" XOR:\n");
                for (RequirementResult child : childRequirements)
                    sb.append(child.prettyPrint(indent + 2));
                break;
            case CONSTRAINT:
                sb.append(pad).append(status).append(" CONSTRAINT:\n");
                if (constraintMatches != null) {
                    for (Map.Entry<Integer, List<CourseAttempt>> entry : constraintMatches.entrySet()) {
                        sb.append(pad).append("  needed=").append(entry.getKey())
                        .append(", matched=").append(entry.getValue().size()).append("\n");
                        for (CourseAttempt ca : entry.getValue()) {
                            sb.append(pad).append("    ").append(ca.getCourseCode())
                            .append(" - ").append(ca.getName())
                            .append(" [").append(ca.getGrade()).append("]").append("\n");
                        }
                    }
                }
                break;
        }
        return sb.toString();
    }

}
