package ca.ianmillican.ui.analyzetranscript;

import javafx.util.Builder;
import javafx.scene.layout.Region;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

import java.util.Map;

import ca.ianmillican.util.Components;
import ca.ianmillican.domain.pojo.degree.Degree;
import ca.ianmillican.domain.pojo.results.ComparisonResult;

public class AnalysisDataRegion implements Builder<Region> {
    
    private AnalyzeTranscriptModel model;
    private record Row(String name, int curr, int required) {}

    public AnalysisDataRegion(AnalyzeTranscriptModel model) {
        this.model = model;
    }

    @Override
    public Region build() {
        TableView<Row> table = new TableView<>();
        TableColumn<Row, String> nameCol = Components.createColumn("Attribute", Row::name);
        TableColumn<Row, String> currentCol = Components.createColumn("Current", row -> String.valueOf(row.curr()));
        TableColumn<Row, String> requiredCol = Components.createColumn("Required", row -> String.valueOf(row.required()));
        nameCol.prefWidthProperty().bind(table.widthProperty().multiply(2.0/3.0));
        currentCol.prefWidthProperty().bind(table.widthProperty().multiply(1.0/6.0));
        requiredCol.prefWidthProperty().bind(table.widthProperty().multiply(1.0/6.0));
        table.getColumns().add(nameCol);
        table.getColumns().add(currentCol);
        table.getColumns().add(requiredCol);

        ObservableList<Row> rows = FXCollections.observableArrayList();
        collectRows(model.getDegree(), model.getEvaluatedTranscript(), rows);

        return table;
    }

    private void collectRows(Degree degree, ComparisonResult result, ObservableList<Row> rows) {
        Map<Character, Integer> pwResult = model.getPWCounts();

    }

}
