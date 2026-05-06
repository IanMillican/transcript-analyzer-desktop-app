package ca.ianmillican.service;

import ca.ianmillican.domain.pojo.transcript.Transcript;
import ca.ianmillican.domain.interfaces.Parser;

public class TranscriptService {

    private final Parser<Transcript> parser;
    
    public TranscriptService(Parser<Transcript> parser) {
        this.parser = parser;
    }

    public Transcript getTranscriptFromFile(String filePath) throws Exception {
        return parser.parse(filePath);
    }

}
