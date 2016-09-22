package com.github.lwhite1.tablesaw.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.stream.Collector;

import static java.util.stream.Collector.Characteristics.UNORDERED;

// https://gist.github.com/JakeWharton/9734167

/** Stream {@link Collector collectors} for Guava types. */
public final class GuavaCollectors {
  /** Collect a stream of elements into an {@link ImmutableList}. */
  public static <T> Collector<T, ImmutableList.Builder<T>, ImmutableList<T>> immutableList() {
    return Collector.of(ImmutableList.Builder::new, ImmutableList.Builder::add,
        (l, r) -> l.addAll(r.build()), ImmutableList.Builder<T>::build);
  }

  /** Collect a stream of elements into an {@link ImmutableSet}. */
  public static <T> Collector<T, ImmutableSet.Builder<T>, ImmutableSet<T>> immutableSet() {
    return Collector.of(ImmutableSet.Builder::new, ImmutableSet.Builder::add,
        (l, r) -> l.addAll(r.build()), ImmutableSet.Builder<T>::build, UNORDERED);
  }

  private GuavaCollectors() {
    throw new AssertionError("No instances.");
  }
}
