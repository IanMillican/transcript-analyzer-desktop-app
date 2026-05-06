package ca.ianmillican.domain.pojo.degree;

public class Section {
    private final String name;
    private final Requirement requirement;
    private final int priority;

    public Section(String name, Requirement requirement, int priority) {
        this.name = name;
        this.requirement = requirement;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Priority ").append(priority).append("] ").append(name).append("\n");
        sb.append(requirement.prettyPrint(2));
        sb.append("\n");
        return sb.toString();
    }

}
