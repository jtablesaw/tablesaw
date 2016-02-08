package com.deathrayresearch.outlier.app.ui.table;

import com.deathrayresearch.outlier.app.model.CsvFile;
import com.deathrayresearch.outlier.columns.ColumnType;
import com.deathrayresearch.outlier.io.CsvReader;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 *
 */
public class ImportTableDialog extends Dialog<CsvFile> {

  private final TextField columnTypes = new TextField();
  private final TextField selectedFilePath = new TextField();

  public ImportTableDialog() {

    selectedFilePath.setPrefColumnCount(36);

    this.setTitle("Import Table Dialog");
    this.setHeaderText("Import a table from a file");

    // Set the button types.
    ButtonType importButtonType = new ButtonType("Import", ButtonBar.ButtonData.OK_DONE);
    this.getDialogPane().getButtonTypes().addAll(importButtonType, ButtonType.CANCEL);

    // Enable/Disable create button depending on whether a columnTypes was entered.
    Node importButton = this.getDialogPane().lookupButton(importButtonType);
    importButton.setDisable(true);

    // validate
    columnTypes.textProperty().addListener((observable, oldValue, newValue) -> {
      importButton.setDisable(newValue.trim().isEmpty());
    });

    this.getDialogPane().setContent(getForm());

    // Request focus on the columnTypes field by default.
    Platform.runLater(selectedFilePath::requestFocus);

    // Convert the result to a columnTypes-filePath-pair when the import button is clicked.
    this.setResultConverter(dialogButton -> {
      if (dialogButton == importButtonType) {

        String[] typeNames = columnTypes.getText().split(",");
        ColumnType[] types = new ColumnType[typeNames.length];
        for (int i = 0; i < types.length; i++) {
          types[i] = ColumnType.valueOf(typeNames[i].trim());
        }
        return new CsvFile(selectedFilePath.getText(), types, true);
      }
      return null;
    });

    Optional<CsvFile> result = this.showAndWait();

    result.ifPresent(projectData -> {
      System.out.println(result.toString());
      try {
        CsvReader.read(result.get().getPath(), result.get().getColumnTypes());
      } catch (IOException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to open");
        alert.show();
      }
      this.close();
    });
  }

  private GridPane getForm() {
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    columnTypes.setPromptText("Column Types");
    selectedFilePath.setPromptText("Table");

    Button btnOpenFileChooser = new Button();

    btnOpenFileChooser.setText("Select File");
    btnOpenFileChooser.setOnAction(event -> {
      FileChooser fileChooser = new FileChooser();
      File selectedFile = fileChooser.showOpenDialog(this.getOwner());
      selectedFilePath.setText(selectedFile.getAbsolutePath());
    });

    grid.add(new Label("File path:"), 0, 0);
    grid.add(selectedFilePath, 1, 0);
    grid.add(new Label("Column types:"), 0, 1);
    grid.add(columnTypes, 1, 1);
    grid.add(btnOpenFileChooser, 2, 0);
    return grid;
  }
}
