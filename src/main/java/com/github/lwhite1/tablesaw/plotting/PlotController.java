package com.github.lwhite1.tablesaw.plotting;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 *
 */
public class PlotController extends Application {

  /**
   * The main entry point for all JavaFX applications.
   * The start method is called after the init method has returned,
   * and after the system is ready for the application to begin running.
   * <p>
   * <p>
   * NOTE: This method is called on the JavaFX Application Thread.
   * </p>
   *
   * @param primaryStage the primary stage for this application, onto which
   *                     the application scene can be set. The primary stage will be embedded in
   *                     the browser if the application was launched as an applet.
   *                     Applications may createFromCsv other stages, if needed, but they will not be
   *                     primary stages and will not be embedded in the browser.
   */
  @Override
  public void start(Stage primaryStage) throws Exception {

    Parameters parameters = getParameters();

    System.out.println(parameters);

    String pageTitle = parameters.getRaw().get(0);
    String page = parameters.getRaw().get(1);
    WebView webView = new WebView();

    String exceptionMessage = "";

    final WebEngine engine = webView.getEngine();
    if (engine.getLoadWorker().getException() != null) {
      exceptionMessage = ", " + engine.getLoadWorker().getException().toString();
    }
    System.out.println(exceptionMessage);

    engine.load(page);
    Scene scene = new Scene(webView, 400, 600);
    primaryStage.setTitle(pageTitle);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void doIt(String[] args) {
    launch(args);
  }
}
