package org.stathissideris.ascii2image.graphics;

import org.stathissideris.ascii2image.core.RenderingOptions;
import org.stathissideris.ascii2image.core.ShapeAreaComparator;
import org.stathissideris.ascii2image.core.Shape3DOrderingComparator;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

/**
 * Created by Jean Lazarou.
 */
public class SVGBuilder {

    SVGBuilder(Diagram diagram, RenderingOptions options) {

        this.diagram = diagram;
        this.options = options;

        float dashInterval = Math.min(diagram.getCellWidth(), diagram.getCellHeight()) / 2;
        float strokeWeight = diagram.getMinimumOfCellDimension() / 10;

        normalStroke = String.format("stroke-width='%s' stroke-linecap='round' stroke-linejoin='round' ",
                String.valueOf(strokeWeight));

        dashStroke = String.format(
                "stroke-width='%s' stroke-dasharray='%s,%s' stroke-miterlimit='0' " +
                        "stroke-linecap='butt' stroke-linejoin='round' ",
                String.valueOf(strokeWeight),
                String.valueOf(dashInterval),
                String.valueOf(dashInterval));

    }

    public String build() {

        return openSVGTag() + definitions() + render() + "</svg>";

    }

    private String definitions() {

        String DEFS =
                "  <defs>\n%s" +
                "    <filter id='f2' x='0' y='0' width='200%%' height='200%%'>\n" +
                "      <feOffset result='offOut' in='SourceGraphic' dx='5' dy='5' />\n" +
                "      <feGaussianBlur result='blurOut' in='offOut' stdDeviation='3' />\n" +
                "      <feBlend in='SourceGraphic' in2='blurOut' mode='normal' />\n" +
                "    </filter>\n" +
                "  </defs>\n";

        if (options.getFontURL() == null) {
            return String.format(DEFS, "");
        }

        String fontStyle =
            "    <style type='text/css'>\n" +
            "      @font-face {\n" +
            "        font-family: %s;\n" +
            "        src: url('%s');\n" +
            "      }\n" +
            "    </style>\n";

        return String.format(DEFS, String.format(fontStyle, options.getFontFamily(), options.getFontURL()));

    }

    private String openSVGTag() {

        String HEADER =
                "<?xml version='1.0' encoding='UTF-8' standalone='no'?>\n" +
                "<svg \n" +
                "    xmlns='http://www.w3.org/2000/svg'\n" +
                "    width='%s'\n" +
                "    height='%s'\n" +
                "    %s\n" +
                "    version='1.0'>\n";

        return String.format(
                HEADER,
                String.valueOf(diagram.getWidth()),
                String.valueOf(diagram.getHeight()),
                antialiasing()
        );

    }

    private String render() {

        backgroundLayer();

        renderStorageShapes();
        renderRestOfShapes();
        renderTexts();

        return "  <g stroke-width='1' stroke-linecap='square' stroke-linejoin='round'>\n" +
                layer0.toString() +
                layer1.toString() +
                layer2.toString() +
                layer3.toString() +
                "  </g>\n";

    }

    private void renderStorageShapes() {

        ArrayList<DiagramShape> shapes = diagram.getAllDiagramShapes();

        ArrayList<DiagramShape> storageShapes = findSorageShapes(shapes);

        storageShapes.sort(new Shape3DOrderingComparator());

        for (DiagramShape shape : storageShapes) {

            GeneralPath path = shape.makeIntoRenderPath(diagram, options);

            SVGCommands commands = new SVGCommands(path);

            String fill = "none";
            String color = "white";

            if(!shape.isStrokeDashed()) {

                renderShadow(commands);

                if(shape.getFillColor() != null)
                    fill = colorToHex(shape.getFillColor());
                else
                    fill = colorToHex(Color.white);

            }

            renderPath(shape, commands, color, fill);

            renderPath(shape, commands, colorToHex(shape.getStrokeColor()), "none");

        }

    }

    private ArrayList<DiagramShape> findSorageShapes(ArrayList<DiagramShape> shapes) {

        ArrayList<DiagramShape> storageShapes = new ArrayList<>();

        for (DiagramShape shape : shapes) {

            if(shape.getType() == DiagramShape.TYPE_STORAGE) {
                storageShapes.add(shape);
            }

        }

        return storageShapes;

    }

    private void renderRestOfShapes() {

        ArrayList<DiagramShape> shapes = diagram.getAllDiagramShapes();
        ArrayList<DiagramShape> pointMarkers = new ArrayList<>();

        shapes.sort(new ShapeAreaComparator());

        for (DiagramShape shape : shapes) {

            if (shape.getType() == DiagramShape.TYPE_POINT_MARKER) {
                pointMarkers.add(shape);
                continue;
            }
            if (shape.getType() == DiagramShape.TYPE_STORAGE) {
                continue;
            }
            if (shape.getType() == DiagramShape.TYPE_CUSTOM) {
                //renderCustomShape(shape, g2);
                //continue;
                throw new RuntimeException("Not yet implemented");
            }

            if (shape.getPoints().isEmpty()) continue;

            GeneralPath path = shape.makeIntoRenderPath(diagram, options);

            SVGCommands commands = new SVGCommands(path);

            renderPath(shape, commands);

        }

        renderPointMarkers(pointMarkers);

    }

    private void renderPath(DiagramShape shape, SVGCommands commands) {

        String fill = "none";

        if (shape.isClosed() && !shape.isStrokeDashed()) {

            if(shape.getFillColor() != null)
                fill = colorToHex(shape.getFillColor());
            else
                fill = "white";

            if (shape.getType() == DiagramShape.TYPE_ARROWHEAD) {
                renderPath(shape, commands, "none", fill);
            }
        }

        if (shape.getType() != DiagramShape.TYPE_ARROWHEAD) {

            if (commands.isClosed && !shape.isStrokeDashed()) {
                renderShadow(commands);
            }

            renderPath(shape, commands, colorToHex(shape.getStrokeColor()), fill);

        }

    }

    private void renderPath(DiagramShape shape, SVGCommands commands, String stroke, String fill) {

        String path = "    <path stroke='" + stroke + "' ";

        if (shape.isStrokeDashed())
            path += dashStroke;
        else
            path += normalStroke;

        path += "fill='" + fill + "' d='" + commands.svgPath + "' />\n";

        layer2.append(path);

    }

    private void renderShadow(SVGCommands commands) {

        if (!options.dropShadows()) return;

        String path = "    <path stroke='gray' fill='gray' filter='url(#f2)' d='" + commands.svgPath + "' />\n";

        layer1.append(path);

    }

    private void renderPointMarkers(ArrayList<DiagramShape> pointMarkers) {

        for (DiagramShape shape : pointMarkers) {

            GeneralPath path = shape.makeIntoRenderPath(diagram, options);

            String fill = "white";

            if(shape.getFillColor() != null)
                fill = colorToHex(shape.getFillColor());

            renderPath(shape, new SVGCommands(path), colorToHex(shape.getStrokeColor()), fill);

        }

    }

    private String antialiasing() {
        String rendering = options.performAntialias() ? "geometricPrecision" : "optimizeSpeed";
        return String.format("shape-rendering='%s'", rendering);
    }

    private void backgroundLayer() {

        Color color = options.getBackgroundColor();

        if (color.getAlpha() == 0) return;

        layer0.append (
                String.format("    <rect x='0' y='0' width='%s' height='%s' style='fill: %s'/>\n",
                        String.valueOf(diagram.getWidth()),
                        String.valueOf(diagram.getHeight()),
                        colorToHex(color)
                )
        );

    }

    private void renderTexts() {
        diagram.getTextObjects().forEach(each -> each.renderOn(this.layer3, this.options));
    }

    static String colorToHex(Color color) {
        return String.format("#%s%s%s",
                toHex(color.getRed()),
                toHex(color.getGreen()),
                toHex(color.getBlue())
        );
    }

    private static String toHex(int n) {
        String hex = Integer.toHexString(n);

        return n > 15 ? hex : "0" + hex;
    }

    private final Diagram diagram;
    private final RenderingOptions options;

    private final StringBuilder layer0 = new StringBuilder();
    private final StringBuilder layer1 = new StringBuilder();
    private final StringBuilder layer2 = new StringBuilder();
    private final StringBuilder layer3 = new StringBuilder();

    private final String normalStroke;
    private final String dashStroke;

    class SVGCommands {

        final String svgPath;
        final boolean isClosed;

        SVGCommands(GeneralPath path) {

            boolean closed = false;

            float[] coords = new float[6];

            StringBuilder builder = new StringBuilder();

            PathIterator pathIterator = path.getPathIterator(null);

            while (!pathIterator.isDone()) {

                String commands;

                switch(pathIterator.currentSegment(coords)) {
                    case PathIterator.SEG_MOVETO:
                        commands = "M" + coords[0] + " " + coords[1] + " ";
                        break;
                    case PathIterator.SEG_LINETO:
                        commands = "L" + coords[0] + " " + coords[1] + " ";
                        break;
                    case PathIterator.SEG_QUADTO:
                        commands = "Q" + coords[0] + " " + coords[1] + " " + coords[2] + " " + coords[3] + " ";
                        break;
                    case PathIterator.SEG_CUBICTO:
                        commands = "C" + coords[0] + " " + coords[1] + " " + coords[2] + " " + coords[3] + " " + coords[4] + " " + coords[5] + " ";
                        break;
                    case PathIterator.SEG_CLOSE:
                        commands = "z";
                        closed = true;
                        break;
                    default:
                        commands = "";
                        break;
                }

                builder.append(commands);

                pathIterator.next();

            }

            isClosed = closed;
            svgPath = builder.toString();

        }

    }

}
