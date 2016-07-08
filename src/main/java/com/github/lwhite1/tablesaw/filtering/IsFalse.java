package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.util.BitmapBackedSelection;
import com.github.lwhite1.tablesaw.util.Selection;

import javax.annotation.concurrent.Immutable;

/**
 * A boolean filtering, returns true if the filtering it wraps returns false, and vice-versa.
 */
@Immutable
public class IsFalse extends CompositeFilter {

  private final Filter filter;

  private IsFalse(Filter filter) {
    this.filter = filter;
  }

  public static IsFalse isFalse(Filter filter) {
    return new IsFalse(filter);
  }

  /**
   * Returns true if the element in the given row in my {@code column} is true
   */
  @Override
  public Selection apply(Table relation) {
    Selection selection = new BitmapBackedSelection();
    selection.addRange(0, relation.rowCount());
    selection.andNot(filter.apply(relation));
    return selection;
  }
}
