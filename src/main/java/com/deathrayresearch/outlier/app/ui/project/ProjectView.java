package com.deathrayresearch.outlier.app.ui.project;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.app.model.Project;
import com.deathrayresearch.outlier.app.ui.table.TableView;
import com.deathrayresearch.outlier.columns.ColumnType;
import com.deathrayresearch.outlier.io.CsvReader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.deathrayresearch.outlier.columns.ColumnType.*;

/**
 *
 */
public class ProjectView extends TabPane {

  private Project project;

  private Map<Table, Integer> tableTabMap = new HashMap<>();

  public ProjectView(Project project) {
    this.project = project;
    Tab projectSummaryTab = new Tab(project.getName());
    projectSummaryTab.setClosable(false);
    getTabs().add(0, projectSummaryTab);

    //TODO(lwhite): Remove this test code
    ColumnType[] types = {LOCAL_DATE, INTEGER, CAT};
    try {
      Table table = CsvReader.read("/Users/larrywhite/IdeaProjects/Outlier/data/BushApproval.csv", types);
      displayTable(table);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void displayTable(Table table) {
    //TODO(lwhite): Confirm that the table is in the project and add it to the tableTabMap
    Tab tableTab = new Tab(table.name());
    tableTab.setContent(new TableView(table));
    getTabs().add(tableTab);
    getSelectionModel().select(tableTab);
  }
}
