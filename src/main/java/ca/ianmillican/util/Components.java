package ca.ianmillican.util;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.beans.property.SimpleObjectProperty;

public final class Components {

	public static final double TABLE_ROW_HEIGHT = 27;
	
	private Components() {
		//Private constructor to avoid instantiating this object. 
		//Exception is thrown to avoid instantiating this object through other means such as reflection.
		throw new UnsupportedOperationException("Components cannot be instantiated");
	}

	public static void autoSizeTable(TableView<?> table) {
		double headerHeight = 32;
		double padding = table.getPadding().getTop() + table.getPadding().getBottom();
		int rowCount = table.getItems().size();
		double targetHeight = headerHeight + (TABLE_ROW_HEIGHT * rowCount) + padding;
		
		table.sceneProperty().addListener((obs, oldScene, newScene) -> {
			if (newScene != null) {
				table.setPrefHeight(targetHeight);
				table.setMaxHeight(targetHeight);
				table.setMinHeight(targetHeight);
			}
		});
	}

	public static <T> ComboBox<T> comboBox(List<T> options, Property<T> boundProperty) {
		ComboBox<T> result = new ComboBox<>();
		result.getItems().addAll(options);
		result.valueProperty().bindBidirectional(boundProperty);
		if(options.size() > 0) {
			result.getSelectionModel().select(0);
		} else {
			result.getSelectionModel().clearSelection();
		}
		return result;
	}

	public static <T> ComboBox<T> comboBox(List<T> options, Consumer<T> onSelect, Property<T> boundProperty) {
		ComboBox<T> result = new ComboBox<>();
		result.getItems().addAll(options);
		result.setOnAction(e -> {
			T selected = result.getSelectionModel().getSelectedItem();
			if (selected != null) {
				onSelect.accept(selected);
			}
		});
		result.valueProperty().bindBidirectional(boundProperty);
		return result;
	}

	public static Button button(String name, Runnable method) {
		Button result = new Button(name);
		result.setOnAction(e -> {method.run();});
		return result;
	}
	
	public static Button button(String name, Runnable method, String styleClass) {
		Button result = new Button(name);
		result.setOnAction(e -> {method.run();});
		result.getStyleClass().add(styleClass);
		return result;
	}
	
	public static <E> Button button(String name, String styleClass, Consumer<E> method, E methodInput) {
		Button result = new Button(name);
		result.setOnAction(e -> {method.accept(methodInput);});
		result.getStyleClass().add(styleClass);
		return result;
	}
	
	public static <E> Button button(String name, String styleClass, List<Consumer<E>> methods, E methodInput) {
		Button result = new Button(name);
		result.setOnAction(e -> {
			Consumer<E> chainedConsumer = null;
			if(!methods.isEmpty()) {
				chainedConsumer = methods.get(0);
				for(int i=1; i<methods.size(); i++) {
					chainedConsumer = chainedConsumer.andThen(methods.get(i));
				}
			}
		});
		result.getStyleClass().add(styleClass);
		return result;
	}

	public static Button fileChooserButton(String name, String styleClass, Consumer<File> onFileSelected) {
		Button result = new Button(name);
		result.getStyleClass().add(styleClass);
		result.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select Transcript PDF");
			fileChooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
			);
			File file = fileChooser.showOpenDialog(null);
			if (file != null) {
				onFileSelected.accept(file);
			}
		});
		return result;
	}
	
	public static Label label(String text, String styleClass) {
		Label result = new Label(text);
		result.getStyleClass().add(styleClass);
		return result;
	}
	
	public static Label boundLabel(String label, Property<String> val, String styleClass) {
		Label result = new Label(label);
		result.textProperty().bindBidirectional(val);
		result.getStyleClass().add(styleClass);
		return result;
	}
	
	public static <S,T> TableColumn<S,T> createColumn(String title, Function<S,T> populationMethod) {
		TableColumn<S,T> result = new TableColumn<>(title);
		result.setCellValueFactory(data -> {
			return new SimpleObjectProperty<T>(populationMethod.apply(data.getValue()));
		});
		return result;
	}
	
	public static <S,T> TableColumn<S,T> createColumn(String title, Function<S,T> populationMethod, String styleCLass) {
		TableColumn<S,T> result = new TableColumn<>(title);
		result.setCellValueFactory(data -> {
			return new SimpleObjectProperty<T>(populationMethod.apply(data.getValue()));
		});
		result.getStyleClass().add(styleCLass);
		return result;
	}
	
	public static <T> void addHighlighting(TableView<T> table, Function<T, Boolean> highlight, String styleClass) {
		table.setRowFactory(tv -> new TableRow<>() {
			@Override
			protected void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);
	            if (empty || item == null) {
	                getStyleClass().remove(styleClass);
	            } else if (highlight.apply(item)) {
	                if (!getStyleClass().contains(styleClass))
	                    getStyleClass().add(styleClass);
	            } else {
	                getStyleClass().remove(styleClass);
	            }
			}
		});
	}
	
}
