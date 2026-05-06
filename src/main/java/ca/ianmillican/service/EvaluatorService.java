package ca.ianmillican.service;

import java.util.List;
import java.util.ArrayList;

import ca.ianmillican.domain.interfaces.Evaluator;
import ca.ianmillican.domain.pojo.degree.Degree;
import ca.ianmillican.domain.pojo.degree.Section;
import ca.ianmillican.domain.pojo.results.ComparisonResult;
import ca.ianmillican.domain.pojo.results.RequirementResult;
import ca.ianmillican.domain.pojo.results.SectionResult;
import ca.ianmillican.domain.pojo.transcript.CourseAttempt;
import ca.ianmillican.domain.pojo.transcript.Transcript;

public class EvaluatorService {

    private final Evaluator<RequirementResult> evaluator;
    
    public EvaluatorService(Evaluator<RequirementResult> evaluator) {
        this.evaluator = evaluator;
        
    }

    private SectionResult evaluateSection(Section section, List<CourseAttempt> coursePool) {
        RequirementResult result = evaluator.evaluate(section.getRequirement(), coursePool);
        return new SectionResult(section, result);
    }

    public ComparisonResult evaluateTranscript(Transcript transcript, Degree degree) {
        List<CourseAttempt> coursePool = new ArrayList<>(
            transcript.getCourses(degree.getName()).stream()
                .filter(ca -> ca.getCreditHours() >0 
                    && !degree.getExcludeCourses().contains(ca.getCourseCode()) 
                    && !degree.getExcludeSubjects().contains(ca.getSubject()) )
                .toList()
        );
        List<Section> sortedSections = degree.getSections().stream().sorted((s1, s2) -> Integer.compare(s1.getPriority(), s2.getPriority())).toList();
        List<SectionResult> sectionResults = sortedSections.stream().map(s -> evaluateSection(s, coursePool)).toList();
        return new ComparisonResult(sectionResults);
    }

}
