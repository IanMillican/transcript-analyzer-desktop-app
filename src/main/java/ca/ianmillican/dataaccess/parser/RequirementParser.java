package ca.ianmillican.dataaccess.parser;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;

import ca.ianmillican.domain.interfaces.JsonParser;
import ca.ianmillican.domain.pojo.degree.Constraint;
import ca.ianmillican.domain.pojo.degree.CourseReq;
import ca.ianmillican.domain.pojo.degree.Requirement;
import ca.ianmillican.domain.pojo.degree.Requirement.TYPE;

public class RequirementParser implements JsonParser<Requirement> {
    
    @Override
    public List<Requirement> parse(List<JsonNode> t) throws Exception {
        
        List<Requirement> requirements = new ArrayList<>();

        for(JsonNode node : t) {
            if(!node.path("and").isMissingNode()) {
                requirements.add(parseRequirement(node.path("and"), TYPE.AND));
            } else if (!node.path("or").isMissingNode()) {
                requirements.add(parseRequirement(node.path("or"), TYPE.OR));
            } else if (!node.path("xor").isMissingNode()) {
                requirements.add(parseRequirement(node.path("xor"), TYPE.XOR));
            } else if (!node.path("Subject").isMissingNode()) {
                requirements.add(parseRequirement(node, TYPE.COURSE));
            } else if (!node.path("constraint").isMissingNode()) {
                requirements.add(parseRequirement(node.path("constraint"), TYPE.CONSTRAINT));
            } else {
                throw new Exception("Invalid requirement node: " + node.toString());
            }
        }

        return requirements;
    }

    private Requirement parseRequirement(JsonNode node, TYPE type) {
            switch(type) {
                case AND, OR, XOR:
                     List<Requirement> subrequirements = StreamSupport.stream(node.spliterator(), false)
                                                        .map(n -> {
                                                            try {
                                                                return parse(List.of(n)).get(0);
                                                            } catch (Exception e) {
                                                                throw new RuntimeException(e);
                                                            }
                                                        })
                                                        .collect(Collectors.toList());
                    return new Requirement(type, subrequirements, null, null);
                case COURSE:
                    String subject = node.path("Subject").isMissingNode() ? null : node.path("Subject").asText();
                    int number = node.path("Number").isMissingNode() ? -1 : node.path("Number").asInt();
                    String name = node.path("Name").isMissingNode() ? null : node.path("Name").asText();
                    int ch = node.path("CreditHours").isMissingNode() ? -1 : node.path("CreditHours").asInt();
                    boolean coop = node.path("Coop").isMissingNode() ? false : node.path("Coop").asBoolean();
                    CourseReq courseReq = new CourseReq(subject, number, name, ch, coop);
                    return new Requirement(type, null, courseReq, null);
                case CONSTRAINT:
                    int count = node.path("count").isMissingNode() ? -1 : node.path("count").asInt();
                    List<String> excludeSubjects = node.path("exclude_subject").isMissingNode() ? new ArrayList<>() :
                                            StreamSupport.stream(node.path("exclude_subject").spliterator(), false)
                                            .map(JsonNode::asText)
                                            .collect(Collectors.toList());
                    List<String> includeSubjects = node.path("include_subject").isMissingNode() ? new ArrayList<>() :
                                            StreamSupport.stream(node.path("include_subject").spliterator(), false)
                                            .map(JsonNode::asText)
                                            .collect(Collectors.toList());
                    int minCH = node.path("min_ch").isMissingNode() ? -1 : node.path("min_ch").asInt();
                    int minLevel2000 = node.path("min_level_2000").isMissingNode() ? -1 : node.path("min_level_2000").asInt();
                    int minLevel3000 = node.path("min_level_3000").isMissingNode() ? -1 : node.path("min_level_3000").asInt();
                    int minLevel4000 = node.path("min_level_4000").isMissingNode() ? -1 : node.path("min_level_4000").asInt();
                    Constraint constraint = new Constraint(count, minCH, includeSubjects, excludeSubjects, minLevel2000, minLevel3000, minLevel4000);
                    return new Requirement(type, null, null, constraint);
                default:
                    throw new IllegalArgumentException("Invalid requirement type: " + type);
            }
    
    }

}
