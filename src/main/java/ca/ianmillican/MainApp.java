package ca.ianmillican;

import ca.ianmillican.dataaccess.parser.TranscriptParser;
import ca.ianmillican.dataaccess.parser.CatalogueParser;
import ca.ianmillican.dataaccess.parser.DegreeParser;
import ca.ianmillican.service.TranscriptService;
import ca.ianmillican.service.CatalogueService;
import ca.ianmillican.service.DegreeService;
import ca.ianmillican.service.EvaluatorService;
import ca.ianmillican.service.RequirementEvaluator;
import ca.ianmillican.ui.analyzetranscript.AnalyzeTranscriptController;
import ca.ianmillican.ui.analyzetranscript.AnalyzeTranscriptModel;
import ca.ianmillican.ui.analyzetranscript.AnalyzeTranscriptView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(@SuppressWarnings("exports") Stage stage) {

        TranscriptService tService = new TranscriptService(new TranscriptParser());
        DegreeService degService = new DegreeService(new DegreeParser());
        EvaluatorService evalService = new EvaluatorService(new RequirementEvaluator());
        CatalogueService cataService = new CatalogueService(new CatalogueParser());

        AnalyzeTranscriptModel model = new AnalyzeTranscriptModel();
        AnalyzeTranscriptView view = new AnalyzeTranscriptView(model);
        AnalyzeTranscriptController controller = new AnalyzeTranscriptController(model, view, tService, degService, evalService, cataService);

        Scene scene = new Scene(controller.getView(), 1280, 960);
        scene.getStylesheets().add(getClass().getResource("/styles/AnalyzeTranscript.css").toExternalForm());
        StackPane root = (StackPane) scene.getRoot();
        root.prefWidthProperty().bind(scene.widthProperty());
        root.prefHeightProperty().bind(scene.heightProperty());

        stage.setTitle("Transcript Analyzer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}