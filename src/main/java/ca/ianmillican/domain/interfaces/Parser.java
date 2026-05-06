package ca.ianmillican.domain.interfaces;

import java.util.List;

public interface Parser<T> {
    
    public T parse(String filePath) throws Exception;

    public List<T> parse(List<String> t) throws Exception;
    
}
