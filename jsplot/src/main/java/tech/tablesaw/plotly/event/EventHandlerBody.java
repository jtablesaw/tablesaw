package tech.tablesaw.plotly.event;

public interface EventHandlerBody {

  /**
   * Returns a string of Javascript code that implements a plotly event handler
   *
   * @param targetName name of the target document
   * @param divName target document id
   * @param eventData name of the event data variable
   * @return A string that can be rendered in javascript
   */
  String asJavascript(String targetName, String divName, String eventData);
}
