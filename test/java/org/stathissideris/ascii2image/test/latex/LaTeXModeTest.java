package org.stathissideris.ascii2image.test.latex;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

@RunWith(Parameterized.class)
public class LaTeXModeTest extends LaTeXModeTestBase {
  private final String testName;
  private final double threshold;

  @Parameters
  public static Object[] parameters() {
    return Arrays.stream(requireNonNull(new File("tests/latex").listFiles()))
        .filter(File::isDirectory)
        .filter(d -> !d.getName().startsWith("_"))
        .map(d -> new Object[] { d.getName(), 0.98 })
        .toArray();
  }

  public LaTeXModeTest(String testName, double threshold) {
    this.testName = testName;
    this.threshold = threshold;
  }

  @Test
  public void executeTest() throws IOException {
    execute(testName, threshold);
  }
}