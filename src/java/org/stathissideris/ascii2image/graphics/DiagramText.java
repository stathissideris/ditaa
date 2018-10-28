/**
 * ditaa - Diagrams Through Ascii Art
 * <p>
 * Copyright (C) 2004-2011 Efstathios Sideris
 * <p>
 * ditaa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * <p>
 * ditaa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with ditaa.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.stathissideris.ascii2image.graphics;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.stathissideris.ascii2image.core.RenderingOptions;
import org.stathissideris.ascii2image.text.StringUtils;

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.stathissideris.ascii2image.graphics.SVGBuilder.colorToHex;

/**
 * @author Efstathios Sideris
 */
public class DiagramText extends DiagramComponent {

  public static final Color   DEFAULT_COLOR        = Color.black;
  public static final Pattern TEXT_SPLITTING_REGEX = Pattern.compile("([^$]+|\\$[^$]*\\$)");
  private final       boolean latexMathEnabled;

  private String text;
  private Font   font;
  private int    xPos, yPos;
  private Color   color        = Color.black;
  private boolean isTextOnLine = false;
  private boolean hasOutline   = false;
  private Color   outlineColor = Color.white;

  public DiagramText(int x, int y, String text, Font font, boolean latexMathEnabled) {
    if (text == null)
      throw new IllegalArgumentException("DiagramText cannot be initialised with a null string");
    if (font == null)
      throw new IllegalArgumentException("DiagramText cannot be initialised with a null font");

    this.xPos = x;
    this.yPos = y;
    this.text = text;
    this.font = font;
    this.latexMathEnabled = latexMathEnabled;
  }

  public void centerInBounds(Rectangle2D bounds) {
    centerHorizontallyBetween((int) bounds.getMinX(), (int) bounds.getMaxX());
    centerVerticallyBetween((int) bounds.getMinY(), (int) bounds.getMaxY());
  }

  public void centerHorizontallyBetween(int minX, int maxX) {
    int width = FontMeasurer.instance().getWidthFor(text, font);
    int center = Math.abs(maxX - minX) / 2;
    xPos += Math.abs(center - width / 2);

  }

  public void centerVerticallyBetween(int minY, int maxY) {
    int zHeight = FontMeasurer.instance().getZHeight(font);
    int center = Math.abs(maxY - minY) / 2;
    yPos -= Math.abs(center - zHeight / 2);
  }

  public void alignRightEdgeTo(int x) {
    int width = FontMeasurer.instance().getWidthFor(text, font);
    xPos = x - width;
  }


  /**
   * @return
   */
  public Color getColor() {
    return color;
  }

  /**
   * @return
   */
  public Font getFont() {
    return font;
  }

  /**
   * @return
   */
  public String getText() {
    return text;
  }

  public void drawOn(Graphics2D g2) {
    g2.setFont(this.getFont());
    if (this.hasOutline()) {
      g2.setColor(this.getOutlineColor());
      Stream.of(1, -1)
          .peek(d -> draw(g2, this.getXPos() + d, this.getYPos(), this.getColor()))
          .peek(d -> draw(g2, this.getXPos(), this.getYPos() + d, this.getColor()))
          .forEach(d -> {
          });
    }
    g2.setColor(this.getColor());
    draw(g2, this.getXPos(), this.getYPos(), getColor());
  }

  private void draw(Graphics2D g2, int xPos, int yPos, Color color) {
    Iterator<String> i = StringUtils.createTextSplitter(TEXT_SPLITTING_REGEX, this.getText());
    int x = xPos;
    while (i.hasNext()) {
      String text = i.next();
      if (isTeXFormula(text))
        x += drawTeXFormula(g2,
            text,
            x, yPos, color,
            font.getSize());
      else
        x += drawString(g2,
            text,
            x, yPos, color,
            font);
    }
  }

  public void renderOn(StringBuilder svgBuildingBuffer, RenderingOptions options) {
    if (this.hasOutline()) {
      Stream.of(1, -1)
          .peek(d -> render(svgBuildingBuffer, options,
              this.getXPos() + d, this.getYPos(),
              this.getOutlineColor()))
          .peek(d -> render(svgBuildingBuffer, options,
              this.getXPos(), this.getYPos() + d,
              this.getOutlineColor()))
          .forEach(d -> {
          });
    }
    render(svgBuildingBuffer, options, this.getXPos(), this.getYPos(), getColor());
  }

  private void render(StringBuilder svgBuildingBuffer, RenderingOptions options,
      int xPos, int yPos, Color color) {
    Iterator<String> i = StringUtils.createTextSplitter(TEXT_SPLITTING_REGEX, this.getText());
    int x = xPos;
    while (i.hasNext()) {
      String token = i.next();
      if (isTeXFormula(token))
        x += renderTeXFormula(svgBuildingBuffer, options,
            token,
            x, yPos, color,
            font.getSize());
      else
        x += renderString(svgBuildingBuffer, options,
            token,
            x, yPos, color,
            font);
    }
  }

  private boolean isTeXFormula(String text) {
    return this.latexMathEnabled && text.startsWith("$");
  }

  /**
   * @return
   */
  public int getXPos() {
    return xPos;
  }

  /**
   * @return
   */
  public int getYPos() {
    return yPos;
  }

  /**
   * @param color
   */
  public void setColor(Color color) {
    this.color = color;
  }

  /**
   * @param font
   */
  public void setFont(Font font) {
    this.font = font;
  }

  /**
   * @param string
   */
  public void setText(String string) {
    text = string;
  }

  /**
   * @param i
   */
  public void setXPos(int i) {
    xPos = i;
  }

  /**
   * @param i
   */
  public void setYPos(int i) {
    yPos = i;
  }

  public Rectangle2D getBounds() {
    Rectangle2D bounds = FontMeasurer.instance().getBoundsFor(text, font);
    bounds.setRect(
        bounds.getMinX() + xPos,
        bounds.getMinY() + yPos,
        bounds.getWidth(),
        bounds.getHeight());
    return bounds;
  }

  public String toString() {
    return "DiagramText, at (" + xPos + ", " + yPos + "), within " + getBounds() + " '" + text + "', " + color + " " + font;
  }

  /**
   * @return
   */
  public boolean isTextOnLine() {
    return isTextOnLine;
  }

  /**
   * @param b
   */
  public void setTextOnLine(boolean b) {
    isTextOnLine = b;
  }

  public boolean hasOutline() {
    return hasOutline;
  }

  public void setHasOutline(boolean hasOutline) {
    this.hasOutline = hasOutline;
  }

  public Color getOutlineColor() {
    return outlineColor;
  }

  public void setOutlineColor(Color outlineColor) {
    this.outlineColor = outlineColor;
  }

  public static int drawTeXFormula(Graphics2D g2, String text, int x, int y, Color color, float fontSize) {
    TeXFormula formula = new TeXFormula(text);
    TeXIcon icon = formula.new TeXIconBuilder()
        .setStyle(TeXConstants.STYLE_DISPLAY)
        .setSize(fontSize)
        .build();
    /* 12 is a magic number to adjust vertical position */
    icon.paintIcon(new JLabel() {{
      setForeground(color);
    }}, g2, x, y - 12);
    return icon.getIconWidth();
  }

  private static int drawString(Graphics2D g2, String text, int xPos, int yPos, Color color, Font font) {
    g2.setColor(color);
    g2.setFont(font);
    g2.drawString(text, xPos, yPos);
    return FontMeasurer.instance().getWidthFor(text, font);
  }

  @SuppressWarnings("unused")
  private static int renderTeXFormula(StringBuilder svgBuildingBuffer, RenderingOptions options, String text, int x, int yPos, Color color, int size) {
    throw new UnsupportedOperationException("Rendering LaTeX formula in .svg format is not currently supported.");
  }

  private static int renderString(StringBuilder svgBuildingBuffer, RenderingOptions options, String text, int xPos, int yPos, Color color, Font font) {
    String TEXT_ELEMENT = "    <text x='%d' y='%d' font-family='%s' font-size='%d' stroke='none' fill='%s' >" +
        "<![CDATA[%s]]></text>\n";
        /* Prefer normal font weight
        if (font.isBold()) {
            style = " font-weight='bold'";
        }
        */

    svgBuildingBuffer.append(
        String.format(TEXT_ELEMENT,
            xPos,
            yPos,
            options.getFontFamily(),
            font.getSize(),
            colorToHex(color),
            text
        )
    );
    return FontMeasurer.instance().getWidthFor(text, font);
  }
}
