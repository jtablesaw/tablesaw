package com.deathrayresearch.outlier.app.ui;
import com.deathrayresearch.outlier.app.ui.project.NewProjectDialog;
import com.deathrayresearch.outlier.app.ui.table.ImportTableDialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Outlier extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Outlier!");
    MenuBar menuBar = getMenuBar(primaryStage);
    VBox root = new VBox();
    root.getChildren().add(menuBar);

    HBox main = new HBox();
    root.getChildren().add(main);
    primaryStage.setScene(new Scene(root, 640, 480));
    //this makes all stages close and the app exit when the main stage is closed
    primaryStage.setOnCloseRequest(e -> Platform.exit());

    primaryStage.show();
  }

  private MenuBar getMenuBar(Stage stage) {
    MenuBar menuBar = new MenuBar();
    Menu project = new Menu("Project");
    MenuItem newProject = new MenuItem("New Project");

    newProject.setOnAction(event -> {
      NewProjectDialog dialog = new NewProjectDialog();
      dialog.show();
      System.out.println("New Project Created!");
    });

    MenuItem openProject = new MenuItem("Open Project");
    MenuItem openRecentProject = new MenuItem("Open Recent Project...");
    MenuItem closeProject = new MenuItem("Close Project");
    MenuItem saveProject = new MenuItem("Save Project");
    MenuItem saveProjectAs = new MenuItem("Save Project as...");
    MenuItem exit = new MenuItem("Exit");

    exit.setOnAction(event -> Platform.exit());

    project.getItems().add(newProject);
    project.getItems().add(openProject);
    project.getItems().add(openRecentProject);
    project.getItems().add(new SeparatorMenuItem());
    project.getItems().add(closeProject);
    project.getItems().add(new SeparatorMenuItem());
    project.getItems().add(saveProject);
    project.getItems().add(saveProjectAs);
    project.getItems().add(new SeparatorMenuItem());
    project.getItems().add(exit);

    Menu table = new Menu("Table");
    MenuItem loadTable = new MenuItem("Import Table...");

    loadTable.setOnAction(event -> {
      Dialog dialog = new ImportTableDialog(stage);
      dialog.show();
    });

    MenuItem openTable = new MenuItem("Open Table...");
    MenuItem renameTable = new MenuItem("Rename Table...");
    MenuItem copyTable = new MenuItem("Copy Table...");
    MenuItem exportTable = new MenuItem("Export Table...");
    MenuItem closeTable = new MenuItem("Close Table");
    MenuItem deleteTable = new MenuItem("Delete Table");

    table.getItems().add(loadTable);
    table.getItems().add(openTable);
    table.getItems().add(new SeparatorMenuItem());
    table.getItems().add(renameTable);
    table.getItems().add(copyTable);
    table.getItems().add(exportTable);
    table.getItems().add(new SeparatorMenuItem());
    table.getItems().add(closeTable);
    table.getItems().add(new SeparatorMenuItem());
    table.getItems().add(deleteTable);

    Menu view = new Menu("View");
    Menu help = new Menu("Help");
    menuBar.getMenus().add(project);
    menuBar.getMenus().add(table);
    menuBar.getMenus().add(view);
    menuBar.getMenus().add(help);
    return menuBar;
  }
}