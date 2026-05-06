package ca.ianmillican.ui.analyzetranscript;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import ca.ianmillican.service.CatalogueService;
import ca.ianmillican.service.DegreeService;
import ca.ianmillican.service.EvaluatorService;
import ca.ianmillican.service.TranscriptService;

import java.util.Map;

import ca.ianmillican.domain.pojo.degree.Degree;
import ca.ianmillican.domain.pojo.results.ComparisonResult;
import ca.ianmillican.domain.pojo.transcript.Transcript;
import ca.ianmillican.exceptions.ParsingException;

public class AnalyzeTranscriptModel {
    
    //UI State
    private final SimpleStringProperty selectedFileString = new SimpleStringProperty("No file choosen");
    private final SimpleStringProperty errorMessage = new SimpleStringProperty("");
    private final SimpleStringProperty selectedDegree = new SimpleStringProperty("");
    private final SimpleBooleanProperty errorVisible = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty resultsReady = new SimpleBooleanProperty(false);

    //Data
    private Transcript transcript = null;
    private Degree degree = null;
    private ComparisonResult evaluatedTranscript = null;

    //Services
    private final EvaluatorService evaluatorService;
    private final TranscriptService transcriptService;
    private final DegreeService degreeService;
    private final CatalogueService catalogueService;

    public AnalyzeTranscriptModel(TranscriptService transcriptService, DegreeService degService, EvaluatorService evalService, CatalogueService catalogueService) {
        this.transcriptService = transcriptService;
        this.degreeService = degService;
        this.evaluatorService = evalService;
        this.catalogueService = catalogueService;
        this.errorMessage.addListener((obs, oldVal, newVal) -> {
            errorVisible.set(newVal != null && !newVal.isEmpty());
        });
    }

    //Data methods

    public Map<Character, Integer> getPWCounts() {
        try {
            return catalogueService.getPWCounts(evaluatedTranscript, getSelectedDegree().toLowerCase() + "_catalogue");
        } catch (Exception e) {
            return Map.of('P', -1, 'W', -1);
        }
    }

    public Transcript getTranscript() {
        return transcript;
    }

    public Degree getDegree() {
        return degree;
    }

    public ComparisonResult getEvaluatedTranscript() {
        return evaluatedTranscript;
    }

    public void evaluateTranscript() {
        try {
            transcript = transcriptService.getTranscriptFromFile(getSelectedFileString());
            degree = degreeService.getDegree(getSelectedDegree().toLowerCase());
            evaluatedTranscript = evaluatorService.evaluateTranscript(transcript, degree);
            System.out.println(evaluatedTranscript);
            resultsReady.set(true);
        } catch (ParsingException pe) {
            pe.printStackTrace();
            errorMessage.set(pe.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage.set(e.getMessage());
        }
    }

    //UI State Methods

    public SimpleBooleanProperty resultsReadyProperty() {
        return resultsReady;
    }

    public boolean resultsReady() {
        return resultsReady.get();
    }

    public void updateFileString(String fileString) {
        selectedFileString.set(fileString);
    }

    public void updateErrorMessage(String error) {
        errorMessage.set(error);
    }

    public SimpleStringProperty selectedFileStringProperty() {
        return selectedFileString;
    }

    public String getSelectedFileString() {
        return selectedFileString.get();
    }

    public SimpleStringProperty errorMessageProperty() {
        return errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage.get();
    }

    public SimpleStringProperty selectedDegreeProperty() {
        return selectedDegree;
    }

    public String getSelectedDegree() {
        return selectedDegree.get();
    }

    public SimpleBooleanProperty errorVisibleProperty() {
        return errorVisible;
    }

    public boolean isErrorVisible() {
        return errorVisible.get();
    }

}
