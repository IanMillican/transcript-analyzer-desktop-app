package ca.ianmillican.domain.interfaces;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonParser<T> {

    public List<T> parse(List<JsonNode> nodes) throws Exception;
    
}
