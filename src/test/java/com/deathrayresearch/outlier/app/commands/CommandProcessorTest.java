package com.deathrayresearch.outlier.app.commands;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class CommandProcessorTest {

  @Before
  public void setUp() throws Exception {
    commandProcessor.clear();
  }

  private final CommandProcessor commandProcessor = CommandProcessor.getInstance();

  @Test
  public void testDoIt() {
    commandProcessor.doIt(new NoOpCommand("A do-nothing"));
    assertEquals(1, commandProcessor.doneStackSize());
    assertEquals(0, commandProcessor.undoneStackSize());
  }

  @Test
  public void testUndoLast() {
    commandProcessor.doIt(new NoOpCommand("A do-nothing"));
    assertEquals(1, commandProcessor.doneStackSize());
    assertEquals(0, commandProcessor.undoneStackSize());

    commandProcessor.undoLast();
    assertEquals(0, commandProcessor.doneStackSize());
    assertEquals(1, commandProcessor.undoneStackSize());
  }

  @Test
  public void testRedoLast() {
    commandProcessor.doIt(new NoOpCommand("A do-nothing"));
    assertEquals(1, commandProcessor.doneStackSize());
    assertEquals(0, commandProcessor.undoneStackSize());

    commandProcessor.undoLast();
    assertEquals(0, commandProcessor.doneStackSize());
    assertEquals(1, commandProcessor.undoneStackSize());

    commandProcessor.redoLast();
    assertEquals(1, commandProcessor.doneStackSize());
    assertEquals(0, commandProcessor.undoneStackSize());
  }

  static class NoOpCommand extends AbstractCommand {

    public NoOpCommand(Object receiver) {
      super(receiver);
    }

    @Override
    public void execute() {
      System.out.println(receiver.toString());
    }

    @Override
    public void undo() {
      System.out.println(receiver.toString());
    }
  }
}