package com.deathrayresearch.outlier.app.ui;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.app.events.AppEvent;
import com.deathrayresearch.outlier.app.events.AppEventListener;
import com.deathrayresearch.outlier.app.events.AppEventType;
import com.deathrayresearch.outlier.app.events.Notifier;
import com.deathrayresearch.outlier.app.model.Project;
import com.deathrayresearch.outlier.app.ui.project.ProjectView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Outlier extends Application implements AppEventListener{

  private BorderPane mainWorkspace;
  private ProjectView projectView;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {

    Notifier.getInstance().subscribe(AppEventType.PROJECT_CHANGED, this);
    Notifier.getInstance().subscribe(AppEventType.TABLE_LOADED, this);

    primaryStage.setTitle("Outlier!");
    MenuBar menuBar = new MainMenu();
    mainWorkspace = new BorderPane();
    mainWorkspace.setTop(menuBar);

    HBox main = new HBox();
    mainWorkspace.setCenter(main);
    primaryStage.setScene(new Scene(mainWorkspace, 640, 480));
    primaryStage.show();
  }

  @Override
  public void handleEvent(AppEvent event) {
    switch (event.getType()) {
      case PROJECT_CHANGED:
        projectView = new ProjectView((Project) event.getPayload());
        mainWorkspace.setCenter(projectView);
        break;
      case TABLE_LOADED:
        projectView.displayTable((Table) event.getPayload());
        break;
    }
  }
}