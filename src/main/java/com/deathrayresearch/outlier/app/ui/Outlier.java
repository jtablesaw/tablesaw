package com.deathrayresearch.outlier.app.ui;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
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
    MenuBar menuBar = new MainMenu(primaryStage);
    VBox root = new VBox();
    root.getChildren().add(menuBar);

    HBox main = new HBox();
    root.getChildren().add(main);
    primaryStage.setScene(new Scene(root, 640, 480));
    primaryStage.show();
  }
}