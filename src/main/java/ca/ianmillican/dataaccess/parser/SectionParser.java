package ca.ianmillican.dataaccess.parser;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import ca.ianmillican.domain.interfaces.JsonParser;
import ca.ianmillican.domain.pojo.degree.Requirement;
import ca.ianmillican.domain.pojo.degree.Section;

public class SectionParser implements JsonParser<Section> {

    private static final JsonParser<Requirement> requirementParser = new RequirementParser();
    
    public SectionParser() {
    }

    @Override
    public List<Section> parse(List<JsonNode> nodes) throws Exception {
        List<Section> sections = new ArrayList<>();
        for(JsonNode node : nodes) {
            String name = node.path("name").isMissingNode() ? null : node.path("name").asText();
            int priority = node.path("priority").isMissingNode() ? 0 : node.path("priority").asInt();
            JsonNode requirementNode = node.path("requirements").isMissingNode() ? null : node.path("requirements");
            Requirement requirement = requirementParser.parse(List.of(requirementNode)).get(0);
            sections.add(new Section(name, requirement, priority));
        }
        return sections;
    }

}
