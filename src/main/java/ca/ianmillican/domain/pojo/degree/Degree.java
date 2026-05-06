package ca.ianmillican.domain.pojo.degree;

import java.util.List;

public class Degree {
    private final String name;
    private final List<Section> sections;
    private final List<String> excludeSubjects;
    private final List<String> excludeCourses;

    public Degree(String name, List<Section> sections, List<String> excludeSubjects, List<String> excludeCourses) {
        this.name = name;
        this.sections = sections;
        this.excludeSubjects = excludeSubjects;
        this.excludeCourses = excludeCourses;
    }

    public List<String> getExcludeSubjects() {
        return excludeSubjects;
    }

    public List<String> getExcludeCourses() {
        return excludeCourses;
    }

    public String getName() {
        return name;
    }

    public List<Section> getSections() {
        return sections;
    }

    @Override
    public String toString() {
        return prettyPrint();
    }

    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== DEGREE: ").append(name).append(" ===\n");
        if (!excludeSubjects.isEmpty())
            sb.append("Excluded Subjects: ").append(String.join(", ", excludeSubjects)).append("\n");
        if (!excludeCourses.isEmpty())
            sb.append("Excluded Courses:  ").append(String.join(", ", excludeCourses)).append("\n");
        sb.append("\n");
        for (Section s : sections) {
            sb.append(s.prettyPrint());
        }
        return sb.toString();
    }

}
