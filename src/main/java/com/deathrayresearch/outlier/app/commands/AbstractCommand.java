package com.deathrayresearch.outlier.app.commands;

/**
 *
 */
public abstract class AbstractCommand implements Command {

  protected Object receiver;

  public AbstractCommand(Object receiver) {
    this.receiver = receiver;
  }
}
