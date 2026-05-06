package ca.ianmillican.ui.analyzetranscript;

import javafx.scene.layout.Region;

import java.io.File;
import java.util.Map;

import ca.ianmillican.domain.interfaces.Controller;
import ca.ianmillican.exceptions.ParsingException;
import ca.ianmillican.service.CatalogueService;
import ca.ianmillican.service.DegreeService;
import ca.ianmillican.service.EvaluatorService;
import ca.ianmillican.service.TranscriptService;

public class AnalyzeTranscriptController implements Controller {
    
    private AnalyzeTranscriptModel model;
    private AnalyzeTranscriptView view;

    private final EvaluatorService evaluatorService;
    private final TranscriptService transcriptService;
    private final DegreeService degreeService;
    private final CatalogueService catalogueService;

    public AnalyzeTranscriptController(AnalyzeTranscriptModel model, AnalyzeTranscriptView view, TranscriptService transcriptService, DegreeService degService, EvaluatorService evalService, CatalogueService catalogueService) {
        this.model = model;
        this.evaluatorService = evalService;
        this.transcriptService = transcriptService;
        this.degreeService = degService;
        this.catalogueService = catalogueService;

        //View
        this.view = view;
        this.view.setUpload(this::handleUpload);
        this.view.setPWCourses(this::getPWCounts);;
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

    public Map<Character, Integer> getPWCounts() {
        try {
            return catalogueService.getPWCounts(model.getEvaluatedTranscript(), model.getSelectedDegree().toLowerCase() + "_catalogue");
        } catch (Exception e) {
            return Map.of('P', -1, 'W', -1);
        }
    }

    public void evaluateTranscript() {
        try {
            model.setTranscript(transcriptService.getTranscriptFromFile(model.getSelectedFileString()));
            model.setDegree(degreeService.getDegree(model.getSelectedDegree().toLowerCase()));
            model.setEvaluatedTranscript(evaluatorService.evaluateTranscript(model.getTranscript(), model.getDegree()));
            model.resultsReadyProperty().set(true);
        } catch (ParsingException pe) {
            pe.printStackTrace();
            model.errorMessageProperty().set(pe.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            model.errorMessageProperty().set(e.getMessage());
        }
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
            evaluateTranscript();
        }
    }

}
