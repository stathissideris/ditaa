package org.stathissideris.ascii2image.test.latex;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.stathissideris.ascii2image.graphics.DiagramText;
import org.stathissideris.ascii2image.text.StringUtils;
import org.stathissideris.ascii2image.text.TextGrid;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.github.dakusui.crest.Crest.asListOf;
import static com.github.dakusui.crest.Crest.asString;
import static com.github.dakusui.crest.Crest.assertThat;
import static com.github.dakusui.crest.Crest.sublistAfterElement;

public class LaTeXUtilsTest extends TestBase {
  @Rule
  public TestName name = new TestName();


  @Test
  public void givenStringContainingLaTeXFormula$whenTransform$thenResultIsCorrect() {
    assertThat(
        TextGrid.transformRowToModeRow("xyz$xyz^^^$xyz"),
        asString().equalTo("PPPLLLLLLLLPPP").$()
    );
  }

  @Test(expected = IllegalArgumentException.class)
  public void givenStringContainingUnfinishedLaTeXFormula$whenTransform$thenIllegalArgumentException() {
    TextGrid.transformRowToModeRow("xyz$xyz^^^_xyz");
  }

  @Test
  public void givenStringNotContainingLaTeXFormula$whenTransform$thenResultIsCorrect() {
    assertThat(
        TextGrid.transformRowToModeRow("xyz_xyz^^^_xyz"),
        asString().equalTo("PPPPPPPPPPPPPP").$()
    );
  }

  @Test
  public void givenEmptyString$whenTransform$thenResultIsEmpty() {
    assertThat(
        TextGrid.transformRowToModeRow(""),
        asString().equalTo("").$()
    );
  }

  @Test
  public void givenMathContainingText$whenTokenizeWithRegexUsedInDiagramText$thenTokenizedAsExpected() {
    assertThat(
        tokenizeTextUsingTextSplitter("hello$HELLO$ world$$", DiagramText.TEXT_SPLITTING_REGEX),
        asListOf(String.class,
            sublistAfterElement("hello")
                .afterElement("$HELLO$")
                .afterElement(" world")
                .afterElement("$$")
                .$())
            .isEmpty().$());
  }

  @Test
  public void givenEmptyText$whenTokenizeWithRegexUsedInDiagramText$thenNoTokenReturned() {
    assertThat(
        tokenizeTextUsingTextSplitter("", DiagramText.TEXT_SPLITTING_REGEX),
        asListOf(String.class).isEmpty().$()
    );
  }

  @Test
  public void givenTextContainingColorCodeLikeSubstring$whenTokenizeWithCOLORCODELIKE_REGEX$thenTokenizedAsExpected() {
    assertThat(
        tokenizeTextUsingTextSplitter("hellocXYZworld", TextGrid.COLORCODELIKE_REGEX),
        asListOf(String.class,
            sublistAfterElement("hello")
                .afterElement("cXYZ")
                .afterElement("world")
                .$())
            .isEmpty()
            .$());
  }

  @Test
  public void givenEmptyText$whenTokenizeWithCOLORCODELIKE_REGEX$thenNoTokenReturned() {
    assertThat(
        tokenizeTextUsingTextSplitter("", TextGrid.COLORCODELIKE_REGEX),
        asListOf(String.class).isEmpty().$());
  }

  private static List<String> tokenizeTextUsingTextSplitter(String s, Pattern textSplittingRegex) {
    return StreamSupport.stream(((Iterable<String>) () -> StringUtils.createTextSplitter(textSplittingRegex, s)).spliterator(), false).collect(Collectors.toList());
  }
}