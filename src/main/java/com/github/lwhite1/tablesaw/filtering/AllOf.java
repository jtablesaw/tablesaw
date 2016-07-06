package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.util.Selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A composite filtering that only returns {@code true} if all component filters return true
 */
public class AllOf extends CompositeFilter {

  private List<Filter> filterList = new ArrayList<>();

  private AllOf(Collection<Filter> filters) {
    this.filterList.addAll(filters);
  }

  public static AllOf allOf(Filter... filters) {
    List<Filter> filterList = new ArrayList<>();
    Collections.addAll(filterList, filters);
    return new AllOf(filterList);
  }

  public static AllOf allOf(Collection<Filter> filters) {
    return new AllOf(filters);
  }

  public Selection apply(Table relation) {
    Selection selection = null;
    for (Filter filter : filterList) {
      if (selection == null) {
        selection = filter.apply(relation);
      } else {
        selection.and(filter.apply(relation));
      }
    }
    return selection;
  }
}
