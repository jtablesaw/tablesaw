package com.deathrayresearch.outlier.app.commands;

/**
 *
 */
public interface Command {

  void execute();

  void undo();
}
