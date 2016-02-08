package com.deathrayresearch.outlier.app.ui.project;

import com.deathrayresearch.outlier.app.model.Project;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.time.LocalDate;
import java.util.Optional;

/**
 *
 */
public class NewProjectDialog extends Dialog<Project> {

  public NewProjectDialog() {
    // Create the custom dialog.
    this.setTitle("New Project Dialog");
    this.setHeaderText("Create a new Outlier project");

// Set the button types.
    ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
    this.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

// Create the projectName and notes labels and fields.
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    TextField projectName = new TextField();
    projectName.setPromptText("Project Name");

    DatePicker createDate = new DatePicker(LocalDate.now());

    TextField goals = new TextField();
    goals.setPromptText("goals");

    TextField notes = new TextField();
    notes.setPromptText("Notes");

    TextField selectedDirectoryText = new TextField();
    selectedDirectoryText.setPromptText("Folder");

    Button btnOpenDirectoryChooser = new Button();
    btnOpenDirectoryChooser.setText("Select Folder");

    btnOpenDirectoryChooser.setOnAction(event -> {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      File selectedDirectory =
          directoryChooser.showDialog(this.getOwner());

      if(selectedDirectory == null){
        selectedDirectoryText.setText("No Directory selected");
      }else{
        selectedDirectoryText.setText(selectedDirectory.getAbsolutePath());
      }
    });

    grid.add(new Label("Project Name:"), 0, 0);
    grid.add(projectName, 1, 0);

    grid.add(new Label("Start date:"), 0, 1);
    grid.add(createDate, 1, 1);

    grid.add(new Label("Goals:"), 0, 2);
    grid.add(goals, 1, 2);

    grid.add(new Label("Notes:"), 0, 3);
    grid.add(notes, 1, 3);

    grid.add(new Label("Folder:"), 0, 4);
    grid.add(selectedDirectoryText, 1, 4);
    grid.add(btnOpenDirectoryChooser, 2,4);

    // Enable/Disable create button depending on whether a projectName was entered.
    Node loginButton = this.getDialogPane().lookupButton(createButtonType);
    loginButton.setDisable(true);

    // Do some validation
    projectName.textProperty().addListener((observable, oldValue, newValue) -> {
      loginButton.setDisable(newValue.trim().isEmpty());
    });

    this.getDialogPane().setContent(grid);

    // Request focus on the projectName field by default.
    Platform.runLater(projectName::requestFocus);

    // Convert the result to a projectName-notes-pair when the login button is clicked.
    this.setResultConverter(dialogButton -> {
      if (dialogButton == createButtonType) {

        return new Project(projectName.getText(),
                            createDate.getValue(),
                            goals.getText(),
                            notes.getText(),
                            selectedDirectoryText.getText());
      }
      return null;
    });

    Optional<Project> result = this.showAndWait();

    result.ifPresent(projectData -> System.out.println(result.toString()));
  }
}
