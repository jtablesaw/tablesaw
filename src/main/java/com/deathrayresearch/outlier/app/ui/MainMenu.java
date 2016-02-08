package com.deathrayresearch.outlier.app.ui;


import com.deathrayresearch.outlier.app.ui.project.NewProjectDialog;
import com.deathrayresearch.outlier.app.ui.table.ImportTableDialog;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 *
 */
public class MainMenu extends MenuBar {

  MainMenu() {
    Menu project = new Menu("Project");

    MenuItem newProject = new MenuItem("New Project");
    newProject.setOnAction(event -> new NewProjectDialog());

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

    loadTable.setOnAction(event -> new ImportTableDialog());

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
    getMenus().add(project);
    getMenus().add(table);
    getMenus().add(view);
    getMenus().add(help);
  }
}
