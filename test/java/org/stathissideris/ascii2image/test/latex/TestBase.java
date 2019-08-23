package org.stathissideris.ascii2image.test.latex;

import org.junit.After;
import org.junit.Before;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * A base class for JUnit tests. This class suppresses stdout and stderr only when
 * it is run under surefire but not otherwise.
 * This is useful if you want to see outputs to stdout and stderr but you don't
 * want them in normal build environment.
 */
public class TestBase {
  private static final PrintStream STDOUT = System.out;
  private static final PrintStream STDERR = System.err;

  @Before
  public void before() {
    suppressStdOutErrIfRunUnderSurefire();
  }

  @After
  public void after() {
    restoreStdOutErr();
  }

  private static final PrintStream NOP = new PrintStream(new OutputStream() {
    @Override
    public void write(int b) {
    }
  });

  /**
   * Typically called from a method annotated with {@literal @}{@code Before} method.
   */
  private static void suppressStdOutErrIfRunUnderSurefire() {
    if (isRunUnderSurefire()) {
      System.setOut(NOP);
      System.setErr(NOP);
    }
  }

  /**
   * Typically called from a method annotated with {@literal @}{@code After} method.
   */
  private static void restoreStdOutErr() {
    System.setOut(STDOUT);
    System.setErr(STDERR);
  }

  private static boolean isRunUnderSurefire() {
    return System.getProperty("surefire.real.class.path") != null;
  }
}