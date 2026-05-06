package ca.ianmillican.ui.analyzetranscript;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import ca.ianmillican.domain.pojo.degree.Degree;
import ca.ianmillican.domain.pojo.results.ComparisonResult;
import ca.ianmillican.domain.pojo.transcript.Transcript;

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

    public AnalyzeTranscriptModel() {
        this.errorMessage.addListener((obs, oldVal, newVal) -> {
            errorVisible.set(newVal != null && !newVal.isEmpty());
        });
    }

    //Data methods

    public void setTranscript(Transcript t) {
        this.transcript = t;
    }

    public void setDegree(Degree d) {
        this.degree = d;
    }

    public void setEvaluatedTranscript(ComparisonResult cr) {
        this.evaluatedTranscript = cr;
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
