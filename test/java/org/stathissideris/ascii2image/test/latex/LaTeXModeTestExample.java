package org.stathissideris.ascii2image.test.latex;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * This is a test class that illustrates how a test in {@code LaTeXModeTest}
 * failure is displayed in console.
 */
@Ignore
public class LaTeXModeTestExample extends LaTeXModeTestBase {
  /**
   * This test fails always with following message. (Irrelevant information is shortened
   * to fit screen width).
   *
   * <pre>
   * 1:x.diff(...expected.png,...diff.png).similarity(.../expected.png) >[0.98] was not met because x.diff(...expected.png,...diff.png).similarity(...expected.png,...)=<0.95...>:Double
   * 2:x=<...actual.png>:ImageFile
   * 3:x.diff(...expected.png,...diff.png).similarity(.../expected.png) >[0.98]
   * 4:                                  |                            |
   * 5:                                  |                            +-<0.9538961912448595>:Double
   * 6:                                  |
   * 7:                                  +----------------------------------------------------<...diff.png>:ImageFile
   * </pre>
   *
   * Given x is a {@code ImageFile} object contains imaged created by running ditaa
   * this time from test input ({@code actual.png}).
   * {@code x.diff(..expected.png,...diff.png)} creates a new {@code ImageFile} object
   * and returns it (l.7) and the image of it ({@code diff.png}) highlights pixels
   * which differ in {@code expected.png} and {@code actual.png}.
   *
   * Then, "similarity" between returned {@code ImageFile} for {@code diff.png}
   * object and {@code expected.png} is computed to {@code 0.9538...} (l.5).
   * However we expect the number is greater than {@code 0.98}, and therefore
   * this test must fail.
   *
   * @throws IOException Failed to access resources
   * @see LaTeXModeTestBase.ImageFile
   */
  @Test
  public void exampleTest() throws IOException {
    execute("_example", 0.98);
  }
}