package ca.ianmillican.dataaccess.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.ianmillican.domain.interfaces.Parser;
import ca.ianmillican.domain.pojo.PWCatalogue;
import ca.ianmillican.exceptions.ParsingException;

public class CatalogueParser implements Parser<PWCatalogue> {

    @Override
    public PWCatalogue parse(String path) throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/config/catalogues/"+path+".json")) {
            if(in == null) {
                throw new Exception("Degree requirements file not found for degree: " + path);
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(in);
            if(root == null || !root.isObject()) {
                throw new Exception("Degree requirements root must be a json object for degree: " + path);
            }
            JsonNode rawPCourses = root.path("PCourses");
            JsonNode rawWCourses = root.path("WCourses");

            List<String> pCourses = rawPCourses.isMissingNode() ? new ArrayList<>() :
                                            StreamSupport.stream(rawPCourses.spliterator(), false)
                                            .map(JsonNode::asText)
                                            .collect(Collectors.toList());    
            List<String> wCourses = rawWCourses.isMissingNode() ? new ArrayList<>() :
                                            StreamSupport.stream(rawWCourses.spliterator(), false)
                                            .map(JsonNode::asText)
                                            .collect(Collectors.toList());    
            return new PWCatalogue(pCourses, wCourses);
        } catch (Exception e) {
            throw new ParsingException("Issue Parsing P/W Catalogue");
        }
    }

    @Override
    public List<PWCatalogue> parse(List<String> paths) throws Exception {
        throw new UnsupportedOperationException("Parsing multiple Degrees is not supported");
    }
    
}
