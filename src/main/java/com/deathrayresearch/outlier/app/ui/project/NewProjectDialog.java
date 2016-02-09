package com.deathrayresearch.outlier.app.ui.project;

import com.deathrayresearch.outlier.app.events.AppEvent;
import com.deathrayresearch.outlier.app.events.AppEventPublisher;
import com.deathrayresearch.outlier.app.events.AppEventType;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.time.LocalDate;
import java.util.Optional;

/**
 *
 */
public class NewProjectDialog extends Dialog<Project> implements AppEventPublisher {

  private final TextField projectName = new TextField();
  private final DatePicker createDate = new DatePicker(LocalDate.now());
  private final TextArea goals = new TextArea();
  private final TextArea notes = new TextArea();
  private final TextField selectedDirectoryText = new TextField();

  public NewProjectDialog() {

    selectedDirectoryText.setPrefColumnCount(36);

    this.setTitle("New Project Dialog");
    this.setHeaderText("Create a new project");

    // Set the button types.
    ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
    this.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

    // Enable/Disable create button depending on whether a projectName was entered.
    Node newProjectButton = this.getDialogPane().lookupButton(createButtonType);
    newProjectButton.setDisable(true);

    // validate
    projectName.textProperty().addListener((observable, oldValue, newValue) -> {
      newProjectButton.setDisable(newValue.trim().isEmpty());
    });

    this.getDialogPane().setContent(getProjectForm());

    // Request focus on the projectName field
    Platform.runLater(projectName::requestFocus);

    // return the results when the create button is clicked.
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
    result.ifPresent(
        project -> publish(new AppEvent<>(AppEventType.PROJECT_CHANGED, project))
    );
  }

  private GridPane getProjectForm() {
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    projectName.setPromptText("Project Name");

    goals.setPromptText("goals");

    notes.setPromptText("Notes");

    selectedDirectoryText.setPromptText("Folder");

    Button btnOpenDirectoryChooser = new Button();
    btnOpenDirectoryChooser.setText("Select Folder");

    btnOpenDirectoryChooser.setOnAction(event -> {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      File selectedDirectory = directoryChooser.showDialog(this.getOwner());
      if(selectedDirectory != null) {
        selectedDirectoryText.setText(selectedDirectory.getAbsolutePath());
      }
    });

    grid.add(new Label("Project Name:"), 0, 0);
    grid.add(projectName, 1, 0);

    grid.add(new Label("Folder:"), 0, 1);
    grid.add(selectedDirectoryText, 1, 1);
    grid.add(btnOpenDirectoryChooser, 2, 1);

    grid.add(new Label("Start date:"), 0, 2);
    grid.add(createDate, 1, 2);

    grid.add(new Label("Goals:"), 0, 3);
    grid.add(goals, 1, 3);

    grid.add(new Label("Notes:"), 0, 4);
    grid.add(notes, 1, 4);

    return grid;
  }
}
