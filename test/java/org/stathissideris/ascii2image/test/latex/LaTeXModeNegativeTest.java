package org.stathissideris.ascii2image.test.latex;

import org.junit.Test;
import org.scilab.forge.jlatexmath.ParseException;

import java.io.IOException;

import static com.github.dakusui.crest.Crest.asString;
import static com.github.dakusui.crest.Crest.assertThat;
import static com.github.dakusui.crest.Crest.substringAfterRegex;

public class LaTeXModeNegativeTest extends LaTeXModeTestBase {
  @Test(expected = ExpectedException.class)
  public void givenBrokenLaTeXmathExpression$whenDitaaIsRunLaTeXModeEnabled$thenAppropriateExceptionIsThrown() throws IOException {
    try {
      execute("_brokenmath", 0.98);
    } catch (ParseException e) {
      assertThat(
          e.getMessage(),
          asString(substringAfterRegex("Unknown symbol or command").after("unknownFunction").$()).isNotNull().$()
      );
      throw new ExpectedException(e);
    }
  }

  private static class ExpectedException extends RuntimeException {
    private ExpectedException(ParseException e) {
      super(e);
    }
  }
}