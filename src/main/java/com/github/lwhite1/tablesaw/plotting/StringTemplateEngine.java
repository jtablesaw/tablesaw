package com.github.lwhite1.tablesaw.plotting;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.util.Map;

/**
 *
 */
public class StringTemplateEngine {

  public final static StringTemplateEngine INSTANCE = new StringTemplateEngine();

  private final STGroup group = new STGroup('$', '$');

  public String render(String templateString, Map<String, Object> attributes) {

    ST template;

    template = new ST(group, templateString);

    for (Map.Entry<String, Object> entry : attributes.entrySet()) {
      template.add(entry.getKey(), entry.getValue());
    }

    return template.render();
  }
}
