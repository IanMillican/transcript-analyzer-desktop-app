package ca.ianmillican.dataaccess.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.ianmillican.domain.interfaces.Parser;
import ca.ianmillican.domain.pojo.degree.Degree;
import ca.ianmillican.domain.pojo.degree.Section;

public class DegreeParser implements Parser<Degree> {

    private static final SectionParser sectionParser = new SectionParser();
    
    public DegreeParser() {
    }

    @Override
    public Degree parse(String degreeAbbr) throws Exception {
        try(InputStream in = getClass().getResourceAsStream("/config/requirements/"+degreeAbbr+".json")) {
            if(in == null) {
                throw new Exception("Degree requirements file not found for degree: " + degreeAbbr);
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(in);
            if(root == null || !root.isObject()) {
                throw new Exception("Degree requirements root must be a json object for degree: " + degreeAbbr);
            }

            JsonNode excludedSubjectsNode = root.path("excluded_subjects");
            List<String> excludedSubjects = excludedSubjectsNode.isMissingNode() ? new ArrayList<>() :
                                            StreamSupport.stream(excludedSubjectsNode.spliterator(), false)
                                            .map(JsonNode::asText)
                                            .collect(Collectors.toList());                  
            JsonNode excludedCoursesNode = root.path("excluded_courses");
            List<String> excludedCourses = excludedCoursesNode.isMissingNode() ? new ArrayList<>() :
                                            StreamSupport.stream(excludedCoursesNode.spliterator(), false)
                                            .map(JsonNode::asText)
                                            .collect(Collectors.toList());

            String program = root.path("program").isMissingNode() ? null : root.path("program").asText();
            if(program == null) {
                throw new Exception("Degree requirements must include a program name for degree: " + degreeAbbr);
            }

            List<JsonNode> sectionNodes = root.path("sections").isMissingNode() ? null : StreamSupport.stream(root.path("sections").spliterator(), false).collect(Collectors.toList());
            if(sectionNodes == null) {
                throw new Exception("Degree requirements must include a sections array for degree: " + degreeAbbr);
            }

            List<Section> sections = sectionParser.parse(sectionNodes);
            return new Degree(program, sections, excludedSubjects, excludedCourses);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error loading degree requirements for degree: " + degreeAbbr, e);
        }
    }

    @Override
    public List<Degree> parse(List<String> t) throws Exception {
        throw new UnsupportedOperationException("Parsing multiple degrees is not supported.");
    }

}
