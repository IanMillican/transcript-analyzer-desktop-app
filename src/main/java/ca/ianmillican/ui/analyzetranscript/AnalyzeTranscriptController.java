package ca.ianmillican.ui.analyzetranscript;

import javafx.scene.layout.Region;

import java.io.File;

import ca.ianmillican.domain.interfaces.Controller;

public class AnalyzeTranscriptController implements Controller {
    
    private AnalyzeTranscriptModel model;
    private AnalyzeTranscriptView view;

    public AnalyzeTranscriptController(AnalyzeTranscriptModel model, AnalyzeTranscriptView view) {
        this.model = model;

        //View
        this.view = view;
        this.view.setUpload(this::handleUpload);
        this.view.setFileSelectionMethod(this::handleFileSelection);
        this.model.resultsReadyProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal) {
                view.getRoot().getChildren().setAll(view.buildAnalyzeView());
            }
        });
        this.view.getErrorLabel().visibleProperty().bindBidirectional(model.errorVisibleProperty());
        this.view.getErrorLabel().textProperty().bindBidirectional(model.errorMessageProperty());
    }

    @Override
    public Region getView() {
        return view.build();
    }

    public void handleFileSelection(File file) {
        model.updateFileString(file.getAbsolutePath());
    }

    public void handleUpload() {
        if (model.getSelectedFileString().equals("No file chosen")) {
            model.updateErrorMessage("Please select a file before uploading.");
        } else if (model.getSelectedDegree().isEmpty() || model.getSelectedDegree().equals("Select Degree")) {
            model.updateErrorMessage("Please select a degree before uploading.");
        } else {
            model.updateErrorMessage("");
            model.evaluateTranscript();
        }
    }

}
