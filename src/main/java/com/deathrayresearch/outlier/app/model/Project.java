package com.deathrayresearch.outlier.app.model;

import com.google.common.base.MoreObjects;

import java.time.LocalDate;

/**
 *
 */
public class Project {

  private String name;
  private LocalDate creationDate;
  private String goals;
  private String notes;
  private String folderName;

  public Project(String name, LocalDate startDate, String goals, String notes, String folderName) {
    this.creationDate = startDate;
    this.goals = goals;
    this.name = name;
    this.notes = notes;
    this.folderName = folderName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public String getFolderName() {
    return folderName;
  }

  public void setFolderName(String folderName) {
    this.folderName = folderName;
  }

  public LocalDate getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }

  public String getGoals() {
    return goals;
  }

  public void setGoals(String goals) {
    this.goals = goals;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("name", name)
        .add("creationDate", creationDate)
        .add("goals", goals)
        .add("notes", notes)
        .add("folderName", folderName)
        .toString();
  }
}
