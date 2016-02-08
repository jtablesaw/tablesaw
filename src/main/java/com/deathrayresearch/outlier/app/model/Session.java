package com.deathrayresearch.outlier.app.model;

import com.google.common.base.MoreObjects;

/**
 * Singleton
 */
public class Session {

  private Project currentProject;

  public static Session instance = new Session();

  private Session() {}

  public Project getCurrentProject() {
    return currentProject;
  }

  public void setCurrentProject(Project currentProject) {
    this.currentProject = currentProject;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("currentProject", currentProject)
        .toString();
  }
}
