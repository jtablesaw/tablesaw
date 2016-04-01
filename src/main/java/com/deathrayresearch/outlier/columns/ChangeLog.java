package com.deathrayresearch.outlier.columns;

import com.google.common.collect.ImmutableList;
import jdk.nashorn.internal.ir.annotations.Immutable;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Records the changes (transformations) made to a column
 */
public class ChangeLog {

  private List<ChangeLogEntry> entries = new ArrayList<>();

  public void addEntry(ChangeLogEntry entry) {
    entries.add(entry);
  }

  public void removeEntry(ChangeLogEntry entry) {
    entries.remove(entry);
  }

  public List<ChangeLogEntry> getEntries() {
    return ImmutableList.copyOf(entries);
  }

  @Immutable
  public static class ChangeLogEntry {

    private static ZoneId ZONE_ID = ZoneId.of("GMT");

    private final ZonedDateTime timeStamp = ZonedDateTime.now(ZONE_ID);

    private final String text;

    public ChangeLogEntry(String text) {
      this.text = text;
    }

    public ZonedDateTime getTimeStamp() {
      return timeStamp;
    }

    public String getText() {
      return text;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ChangeLogEntry that = (ChangeLogEntry) o;
      return Objects.equals(timeStamp, that.timeStamp) &&
          Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
      return Objects.hash(timeStamp, text);
    }
  }
}
