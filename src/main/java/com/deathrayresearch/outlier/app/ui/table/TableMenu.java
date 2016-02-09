package com.deathrayresearch.outlier.app.ui.table;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 *
 */
public class TableMenu extends MenuBar {

  public TableMenu() {
    Menu mungeMenu = new Menu("Munge data");
    Menu viewMenu = new Menu("View records");
    Menu analyzeMenu = new Menu("Analyze");
    Menu visualizeMenu = new Menu("Visualize");
    Menu modelMenu = new Menu("Model");

    MenuItem cleanItem = new MenuItem("Clean");
    mungeMenu.getItems().add(cleanItem);

    mungeMenu.getItems().add(new SeparatorMenuItem());

    MenuItem sortItem = new MenuItem("Sort");
    //sortItem.setOnAction(event -> new NewProjectDialog());
    mungeMenu.getItems().add(sortItem);

    MenuItem newColumnItem = new MenuItem("New Column...");
    mungeMenu.getItems().add(newColumnItem);

    mungeMenu.getItems().add(new SeparatorMenuItem());

    MenuItem filterColumnsItem = new MenuItem("Filter Columns...");
    mungeMenu.getItems().add(filterColumnsItem);

    MenuItem filterRowsItem = new MenuItem("Filter Rows...");
    mungeMenu.getItems().add(filterRowsItem);

    mungeMenu.getItems().add(new SeparatorMenuItem());

    this.getMenus().add(mungeMenu);
    this.getMenus().add(viewMenu);
    this.getMenus().add(analyzeMenu);
    this.getMenus().add(visualizeMenu);
    this.getMenus().add(modelMenu);
  }
}
