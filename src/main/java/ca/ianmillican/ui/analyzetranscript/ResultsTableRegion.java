package ca.ianmillican.ui.analyzetranscript;

import java.util.List;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.util.Builder;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import ca.ianmillican.domain.pojo.degree.Requirement;
import ca.ianmillican.domain.pojo.degree.Requirement.TYPE;
import ca.ianmillican.domain.pojo.results.RequirementResult;
import ca.ianmillican.domain.pojo.results.SectionResult;
import ca.ianmillican.domain.pojo.transcript.CourseAttempt;
import ca.ianmillican.util.Components;

public class ResultsTableRegion implements Builder<Region> {

    private AnalyzeTranscriptModel model;

    //DTO for viewing the analyzed transcript
    private record Row(String name, String creditHours, String status) {}

    public ResultsTableRegion(AnalyzeTranscriptModel model) {
        this.model = model;
    }

    @Override
    public Region build() {
        return buildAnalyzeView();
    }

    public Region buildAnalyzeView() {
        HBox container = new HBox();
        container.setAlignment(Pos.TOP_CENTER);
        StackPane.setAlignment(container, Pos.CENTER);

        VBox leftBox = new VBox();
        leftBox.setAlignment(Pos.TOP_CENTER);
        leftBox.prefWidthProperty().bind(container.widthProperty().divide(2));
        VBox rightBox = new VBox();
        rightBox.setAlignment(Pos.TOP_CENTER);
        rightBox.prefWidthProperty().bind(container.widthProperty().divide(2));

        List<SectionResult> sections = model.getEvaluatedTranscript().getSectionResults();
        for(SectionResult sec : sections) {
            TableView<Row> table = buildSectionTable(sec);
            Label tableTitle = Components.label(sec.getOriginalRequirement().getName(), "table-title");
            tableTitle.setAlignment(Pos.CENTER);
            if(sec.getOriginalRequirement().getPriority() <= 3) {
                VBox wrapper = new VBox(tableTitle, table);
                table.prefWidthProperty().bind(leftBox.widthProperty());
                tableTitle.prefWidthProperty().bind(leftBox.widthProperty());
                Components.autoSizeTable(table);
                leftBox.getChildren().add(wrapper);
            } else {
                VBox wrapper = new VBox(tableTitle, table);
                table.prefWidthProperty().bind(rightBox.widthProperty());
                tableTitle.prefWidthProperty().bind(rightBox.widthProperty());
                Components.autoSizeTable(table);
                rightBox.getChildren().add(wrapper);
            }
        }
        rightBox.getChildren().add(new AnalysisDataRegion(model).build());
        container.getChildren().addAll(leftBox, rightBox);
        container.setMaxWidth(Double.MAX_VALUE);
        container.setMaxHeight(Double.MAX_VALUE);
        HBox.setHgrow(leftBox, Priority.ALWAYS);
        HBox.setHgrow(rightBox, Priority.ALWAYS);
        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setMaxWidth(Double.MAX_VALUE);
        scrollPane.setMaxHeight(Double.MAX_VALUE);
        return scrollPane;
    }

    private TableView<Row> buildSectionTable(SectionResult sec) {
        TableView<Row> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        TableColumn<Row, String> nameCol = Components.createColumn("Course", Row::name);
        TableColumn<Row, String> chCol = Components.createColumn("Credit Hours", Row::creditHours, "non-name-col");
        TableColumn<Row, String> statusCol = Components.createColumn("Status", Row::status, "cleanon-name-col");
        nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.75));
        chCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        statusCol.prefWidthProperty().bind(table.widthProperty().multiply(0.10));

        table.getColumns().add(nameCol);
        table.getColumns().add(chCol);
        table.getColumns().add(statusCol);

        ObservableList<Row> rows = FXCollections.observableArrayList();
        collectRows(sec.getRootRequirement(), rows);
        table.setItems(rows);
        return table;
    }

    private void collectRows(RequirementResult req, ObservableList<Row> rows) {
        switch (req.getOriginalRequirement().getType()) {
            case TYPE.COURSE:
                Requirement originalReq = req.getOriginalRequirement();
                rows.add(new Row(originalReq.getCourse().toString(), ""+originalReq.getCourse().getCreditHours(), req.isSatisfied() ? "✓" : "✗"));
                break;
            case TYPE.CONSTRAINT:
                int totalNeeded = req.getOriginalRequirement().getConstraint().getCount();
                List<CourseAttempt> matched = req.getConstraintMatches()
                    .getOrDefault(totalNeeded, List.of());
                for (int i = 0; i < totalNeeded; i++) {
                    if (i < matched.size()) {
                        CourseAttempt ca = matched.get(i);
                        rows.add(new Row(
                            ca.getCourseCode() + ": " + ca.getName(),
                            String.valueOf(ca.getCreditHours()),
                            "✓"
                        ));
                    } else {
                        rows.add(new Row("To be determined", "", "✗"));
                    }
                }
                break;
            case TYPE.AND:
                req.getChildRequirements().forEach(child -> collectRows(child, rows));
                break;
            case TYPE.OR, TYPE.XOR:
                if(req.isSatisfied()) {
                    collectRows(req.getSelectedResult(), rows);
                } else {
                    collectRows(req.getChildRequirements().get(0), rows);
                }
                break;
            default:
                break;
        }
    }

    
}
