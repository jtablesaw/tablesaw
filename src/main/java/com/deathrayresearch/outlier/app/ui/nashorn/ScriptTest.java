package com.deathrayresearch.outlier.app.ui.nashorn;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileReader;

/**
 *
 */
public class ScriptTest {


  public static void main(String[] args) throws Exception {

    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    engine.eval("print('Hello World!');");

    engine.eval(new FileReader("Script.js"));

    Invocable invocable = (Invocable) engine;

    Object result = invocable.invokeFunction("fun1", "Peter Parker");
    System.out.println(result);
    System.out.println(result.getClass());



  }
}
