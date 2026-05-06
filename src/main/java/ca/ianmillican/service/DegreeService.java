package ca.ianmillican.service;

import ca.ianmillican.domain.interfaces.Parser;
import ca.ianmillican.domain.pojo.degree.Degree;
import ca.ianmillican.domain.pojo.degree.Section;
import ca.ianmillican.domain.pojo.degree.Requirement;
import ca.ianmillican.domain.pojo.degree.Requirement.TYPE;
import ca.ianmillican.exceptions.ParsingException;

public class DegreeService {

    private final Parser<Degree> parser;

    public DegreeService(Parser<Degree> parser) {
        this.parser = parser;
    }

    public Degree getDegree(String degreeAbbr) throws Exception {

        if(getClass().getResourceAsStream("/config/requirements/"+degreeAbbr+".json") == null) {
            throw new ParsingException("Degree "+degreeAbbr+".json not found.");
        }
        return parser.parse(degreeAbbr);
    }

    public int getCountOfCourses(Degree degree) {
        int count = 0;
        for(Section sec : degree.getSections()) {
            count += countCoursesFromReq(sec.getRequirement());
        }
        return count;
    }

    private int countCoursesFromReq(Requirement req) {
        int count = 0;
        TYPE reqType = req.getType();
        if(reqType == TYPE.COURSE) {
            return 1;
        } else if (reqType == TYPE.CONSTRAINT) {
            return req.getConstraint().getCount();
        } else if (reqType == TYPE.AND) {
            for(Requirement subReq : req.getSubrequirements()) {
                count += countCoursesFromReq(subReq);
            }
        } else if (reqType == TYPE.OR || reqType == TYPE.XOR) {
            return countCoursesFromReq(req.getSubrequirements().get(0));
        }
        return count;
    }

}
