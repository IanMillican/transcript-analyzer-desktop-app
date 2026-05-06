package ca.ianmillican.service;

import java.util.Map;
import java.util.List;

import ca.ianmillican.domain.interfaces.Parser;
import ca.ianmillican.domain.pojo.PWCatalogue;
import ca.ianmillican.domain.pojo.results.ComparisonResult;
import ca.ianmillican.domain.pojo.results.RequirementResult;
import ca.ianmillican.domain.pojo.results.SectionResult;
import ca.ianmillican.domain.pojo.transcript.CourseAttempt;
import ca.ianmillican.domain.pojo.degree.Requirement.TYPE;

public class CatalogueService {

    private final Parser<PWCatalogue> parser;
    private PWCatalogue pwCatalogue = null;
    
    public CatalogueService(Parser<PWCatalogue> parser) {
        this.parser = parser;
    }

    public Map<Character, Integer> getPWCounts(ComparisonResult res, String cataloguePath) throws Exception {
        this.pwCatalogue = parser.parse(cataloguePath);
        return Map.of(
            'P', countCourses(res, 'p'),
            'W', countCourses(res, 'w')
        );
    }

    private int countCourses(ComparisonResult res, char pORw) {
        int count = 0;
        for(SectionResult sec : res.getSectionResults()) {
            count += countCourses(sec.getRootRequirement(), pORw);
        }
        return count;
    }

    private int countCourses(RequirementResult res, char pORw) {
        int count = 0;
        TYPE nodeType = res.getOriginalRequirement().getType();
        if(nodeType == TYPE.COURSE) {
            if (!res.isSatisfied() || res.getBestCourseAttempt() == null) return 0;
            String courseCode = res.getBestCourseAttempt().getCourseCode();
            if(pORw == 'p') {
                return pwCatalogue.isPCourse(courseCode) ? 1 : 0;
            } else {
                return pwCatalogue.isWCourse(courseCode) ? 1 : 0;
            }
        } else if (nodeType == TYPE.CONSTRAINT) {
            List<CourseAttempt> matches = res.getConstraintMatches().values().iterator().next();
            for(CourseAttempt ca : matches) {
                String courseCode = ca.getCourseCode();
                if(pORw == 'p') {
                    count += pwCatalogue.isPCourse(courseCode) ? 1 : 0;
                } else {
                    count += pwCatalogue.isWCourse(courseCode) ? 1 : 0;
                }
            }
        } else if (nodeType == TYPE.AND) {
            for(RequirementResult req : res.getChildRequirements()) {
                count += countCourses(req, pORw);
            }
        } else if (nodeType == TYPE.OR || nodeType == TYPE.XOR) {
            return countCourses(res.getSelectedResult(), pORw);
        }
        return count;
    }

}
