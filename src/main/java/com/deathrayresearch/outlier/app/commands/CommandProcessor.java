package com.deathrayresearch.outlier.app.commands;

import java.util.Queue;
import com.google.common.collect.EvictingQueue;

/**
 *
 */
public class CommandProcessor {

  private static CommandProcessor instance = new CommandProcessor();

  private static final int MAX_COMMAND_HISTORY_SIZE = 50;

  private Queue<Command> done = EvictingQueue.create(MAX_COMMAND_HISTORY_SIZE);
  private Queue<Command> undone = EvictingQueue.create(MAX_COMMAND_HISTORY_SIZE);

  private CommandProcessor() {}

  public static CommandProcessor getInstance() {
    return instance;
  }

  /**
   * Adds a command to the end of the done
   */
  public void doIt(Command command) {
    command.execute();
    done.add(command);
  }

  public void undoLast() {
    Command command = done.remove();
    if (command != null) {
      command.undo();
      undone.add(command);
    }
  }

  public void redoLast() {
    Command command = undone.remove();
    if (command != null) {
      command.execute();
      done.add(command);
    }
  }

  public int undoneStackSize() {
    return undone.size();
  }

  public int doneStackSize() {
    return done.size();
  }

  public void clear() {
    undone.clear();
    done.clear();
  }
}
