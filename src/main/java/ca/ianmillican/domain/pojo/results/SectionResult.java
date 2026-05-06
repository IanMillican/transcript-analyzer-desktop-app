package ca.ianmillican.domain.pojo.results;

import ca.ianmillican.domain.pojo.degree.Section;

public class SectionResult {
    
    private Section originalRequirement;
    private RequirementResult rootRequirement;

    public SectionResult(Section originalRequirement, RequirementResult rootRquirement) {
        this.originalRequirement = originalRequirement;
        this.rootRequirement = rootRquirement;
    }

    public RequirementResult getRootRequirement() {
        return rootRequirement;
    }

    public Section getOriginalRequirement() {
        return originalRequirement;
    }

    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- ").append(originalRequirement.getName()).append(" ---\n");
        sb.append(rootRequirement.prettyPrint(2));
        sb.append("\n");
        return sb.toString();
    }

}
