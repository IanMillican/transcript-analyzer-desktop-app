package ca.ianmillican.ui.analyzetranscript;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.util.Builder;
import javafx.geometry.Pos;

import ca.ianmillican.util.Components;

public class AnalyzeTranscriptView implements Builder<Region> {

    private AnalyzeTranscriptModel model;

    //Views
    private StackPane root = null;
    private Node uploadView = null;
    private Node analyzeView = null;

    //Components needing bindings
    private Label errorLabel = Components.label("", "error");

    //Methods
    private Runnable uploadMethod = null;
    private Consumer<File> fileSelectionMethod = null;

    public AnalyzeTranscriptView(AnalyzeTranscriptModel model) {
        this.model = model;
    }

    @Override
    public Region build() {
        uploadView = uploadComponents();
        root = new StackPane(uploadView);
        StackPane.setAlignment(uploadView, Pos.CENTER);
        return root;
    }

    private Node uploadComponents() {
        Node title = Components.label("Welcome to the Transcript Analyzer!", "title");
        Node browseButton = Components.fileChooserButton("Choose PDF", "file-label", fileSelectionMethod);
        Node fileName = Components.boundLabel("", model.selectedFileStringProperty(), "file-name");
        Node upload = Components.button("Upload", uploadMethod);
        Node degreeDropDown = Components.comboBox(List.of("Select Degree", "BCS", "BSC"), model.selectedDegreeProperty());
        VBox box = new VBox(title, browseButton, fileName, upload, errorLabel, degreeDropDown);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    public Node buildAnalyzeView() {
        return new ResultsTableRegion(model).build();
    }

    public Label getErrorLabel() {
        return errorLabel;
    }

    public void setUpload(Runnable r) {
        uploadMethod = r;
    }

    public void setFileSelectionMethod(Consumer<File> r) {
        fileSelectionMethod = r;
    }

    public Node uploadView() {
        return uploadView;
    }

    public Node getAnalyzeView() {
        return analyzeView;
    }

    public StackPane getRoot() {
        return root;
    }

}
