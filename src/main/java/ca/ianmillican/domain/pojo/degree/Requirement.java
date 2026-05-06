package ca.ianmillican.domain.pojo.degree;

import java.util.List;

public class Requirement {
    
    private TYPE type;
    private List<Requirement> subrequirements;
    private CourseReq course;
    private Constraint constraint;

    public enum TYPE {
        COURSE,
        XOR,
        OR,
        AND,
        CONSTRAINT
    }

    public Requirement(TYPE type, List<Requirement> subrequirements, CourseReq course, Constraint constraint) {
        this.type = type;
        this.subrequirements = subrequirements;
        this.course = course;
        this.constraint = constraint;
    }

    public TYPE getType() { return type; }
    public List<Requirement> getSubrequirements() { return subrequirements; }
    public CourseReq getCourse() { return course; }
    public Constraint getConstraint() { return constraint; }
    public String getCourseCode() { return this.type == TYPE.COURSE ? course.getCourseCode() : null; }   

    public String prettyPrint(int indent) {
        String pad = " ".repeat(indent);
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case COURSE:
                sb.append(pad).append("COURSE: ").append(course.getCourseCode())
                .append(" - ").append(course.getName())
                .append(" (").append(course.getCreditHours()).append(" ch)");
                if (course.isCoop()) sb.append(" [COOP]");
                sb.append("\n");
                break;
            case AND:
                sb.append(pad).append("AND:\n");
                for (Requirement r : subrequirements)
                    sb.append(r.prettyPrint(indent + 2));
                break;
            case OR:
                sb.append(pad).append("OR:\n");
                for (Requirement r : subrequirements)
                    sb.append(r.prettyPrint(indent + 2));
                break;
            case XOR:
                sb.append(pad).append("XOR:\n");
                for (Requirement r : subrequirements)
                    sb.append(r.prettyPrint(indent + 2));
                break;
            case CONSTRAINT:
                Constraint c = constraint;
                sb.append(pad).append("CONSTRAINT:\n");
                sb.append(pad).append("  count=").append(c.getCount())
                .append(", minCH=").append(c.getMinCreditHours()).append("\n");
                if (!c.getIncludeSubject().isEmpty())
                    sb.append(pad).append("  include: ").append(String.join(", ", c.getIncludeSubject())).append("\n");
                if (!c.getExcludeSubject().isEmpty())
                    sb.append(pad).append("  exclude: ").append(String.join(", ", c.getExcludeSubject())).append("\n");
                sb.append(pad).append("  min 2000=").append(c.getMinLevel2000())
                .append(", min 3000=").append(c.getMinLevel3000())
                .append(", min 4000=").append(c.getMinLevel4000()).append("\n");
                break;
        }
        return sb.toString();
    }

}
