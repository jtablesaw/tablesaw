package com.deathrayresearch.outlier.app.ui.jscharts;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;


public class WebViewSample extends Application {
  private Scene scene;
  @Override public void start(Stage stage) {
    // create the scene
    stage.setTitle("Web View");
    scene = new Scene(new Browser(),900,600, Color.web("#666970"));
    stage.setScene(scene);
    scene.getStylesheets().add("webviewsample/BrowserToolbar.css");
    stage.show();
  }

  public static void main(String[] args){
    launch(args);
  }
}
class Browser extends Region {

  final WebView browser = new WebView();
  final WebEngine webEngine = browser.getEngine();

  public Browser() {
    //apply the styles
    getStyleClass().add("browser");
    // load the web page
    //webEngine.load("http://www.oracle.com/products/index.html");

    URL url = getClass().getResource("HelloWorld.html");
    webEngine.load(url.toExternalForm());
    //webEngine.load("file://Users/larrywhite/IdeaProjects/ColumnStorm/src/main/java/com/deathrayresearch/outlier/app/ui/jscharts/HelloWorld.html");

    //add the web view to the scene
    getChildren().add(browser);

  }
  private Node createSpacer() {
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    return spacer;
  }

  @Override protected void layoutChildren() {
    double w = getWidth();
    double h = getHeight();
    layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
  }

  @Override protected double computePrefWidth(double height) {
    return 900;
  }

  @Override protected double computePrefHeight(double width) {
    return 600;
  }
}