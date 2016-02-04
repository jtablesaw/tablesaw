package com.deathrayresearch.outlier.lang;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 */
public class CommandLine {

  Scanner in;
  PrintStream out;
  Context context;

  public CommandLine(InputStream in, PrintStream out) {
    this.in = new Scanner(in);
    this.out = out;
    context = new Context();
    try {
      start();
    } catch (InvocationTargetException | IllegalAccessException | IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Unable to handle message");
    }
  }

  void start() throws InvocationTargetException, IllegalAccessException, IOException {
    while (true) {
      System.out.print("\t");
      String line = in.nextLine();
      // create a CharStream that reads from standard input
      ANTLRInputStream input = new ANTLRInputStream(line); // create a lexer that feeds off of input CharStream
      SmalltalkLexer lexer = new SmalltalkLexer(input); // create a buffer of tokens pulled from the lexer
      CommonTokenStream tokens = new CommonTokenStream(lexer); // create a parser that feeds off the tokens buffer
      SmalltalkParser parser = new SmalltalkParser(tokens);
      ParseTree tree = parser.script(); // begin parsing at init rule
      System.out.println(tree.toStringTree(parser)); // print LISP-style tree }

      // Create a generic parse tree walker that can trigger callbacks
      ParseTreeWalker walker = new ParseTreeWalker();
      // Walk the tree created during the parse, trigger callbacks
      walker.walk(new Interpreter(), tree);
      System.out.println(); // print a \n after translation

/*
      try {
        System.out.println(methodName);
        Class<?> contextClass = Context.class;
        System.out.println(Arrays.toString(contextClass.getDeclaredMethods()));

        Method method = contextClass.getDeclaredMethod(methodName, String.class);
        System.out.println(method.toString());
        method.setAccessible(true);
        Object retVal = method.invoke(null, tokens.get(1));
      } catch(NoSuchMethodException name) {
        throw new CommandLineException("Can't find method " + methodName + " in Context");
      }*/
    }
  }

  void startMe() throws InvocationTargetException, IllegalAccessException {
    while (true) {
      System.out.print("\t");
      String line = in.nextLine();
      List<String> tokens = tokenize(line);
      String methodName = tokens.get(0);
      try {
        System.out.println(methodName);
        Class<?> contextClass = Context.class;
        System.out.println(Arrays.toString(contextClass.getDeclaredMethods()));

        Method method = contextClass.getDeclaredMethod(methodName, String.class);
        System.out.println(method.toString());
        method.setAccessible(true);
        Object retVal = method.invoke(null, tokens.get(1));
      } catch(NoSuchMethodException name) {
        throw new CommandLineException("Can't find method " + methodName + " in Context");
      }
    }
  }

  private List<String> tokenize(String line) {
    StringTokenizer st = new StringTokenizer( line );
    List<String> tokens = new ArrayList<>();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      tokens.add( token );
    }
    return tokens;
  }
}
