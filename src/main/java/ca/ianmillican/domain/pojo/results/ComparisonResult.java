package ca.ianmillican.domain.pojo.results;

import java.util.List;

public class ComparisonResult {
    
    private List<SectionResult> sectionResults;

    public ComparisonResult(List<SectionResult> sectionResults) {
        this.sectionResults = sectionResults;
    }

    public List<SectionResult> getSectionResults() {
        return sectionResults;
    }

    @Override
    public String toString() {
        return prettyPrint();
    }

    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== EVALUATION RESULTS ===\n\n");
        for (SectionResult sr : sectionResults) {
            sb.append(sr.prettyPrint());
        }
        return sb.toString();
    }

}
