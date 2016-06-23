package com.github.lwhite1.tablesaw.plotting.plots;

import com.github.lwhite1.tablesaw.plotting.StringTemplateEngine;
import com.google.common.io.Resources;
import javafx.application.Application;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class Plot extends Application {

  Map<String, Object> attributeMap = new HashMap<>();

  String render(String templateFileName) {

    URL template = getClass().getResource(templateFileName);

    try {
      String output = Resources.toString(template, StandardCharsets.UTF_8);
      return StringTemplateEngine.INSTANCE.render(output, attributeMap);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Cant find template file", e);
    }
  }

  private Map<String, Object> getAttributeMap() {
    return attributeMap;
  }

}
