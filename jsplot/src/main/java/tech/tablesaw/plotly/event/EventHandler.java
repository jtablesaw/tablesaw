package tech.tablesaw.plotly.event;

public interface EventHandler {

  /**
   * Returns a string of Javascript code that implements a plotly event handler
   *
   * @param targetName name of the target document
   * @param divName target document id
   * @return A string that can be rendered in javascript
   */
  String asJavascript(String targetName, String divName);
}
