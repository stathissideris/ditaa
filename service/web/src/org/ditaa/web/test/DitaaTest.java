package org.ditaa.web.test;

import org.stathissideris.ascii2image.core.ConversionOptions;
import org.stathissideris.ascii2image.graphics.BitmapRenderer;
import org.stathissideris.ascii2image.graphics.Diagram;
import org.stathissideris.ascii2image.text.TextGrid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.ArrayList;

public class DitaaTest {
    public static void main(String[] args) throws UnsupportedEncodingException {
        ConversionOptions options = new ConversionOptions();

        TextGrid grid = new TextGrid();

        boolean noAntiAlias = false;
        boolean noShadows = false;
        boolean roundCorners = false;
        boolean noSeparation = false;
        float scale = 1f;
        String gridText = null;
        for (int i = 0; i < args.length; ++i) {
            String a = args[i];
            if ("-A".equals(a) || "-no-antialias".equals(a)) noAntiAlias = true;
            if ("-S".equals(a) || "-no-shadows".equals(a)) noShadows = true;
            if ("-E".equals(a) || "-no-separation".equals(a)) noSeparation = true;
            if ("-r".equals(a) || "-round-corners".equals(a)) roundCorners = true;
            if ("-scale".equals(a)) scale = Float.parseFloat(args[++i]);
            if ("-grid".equals(a)) gridText = args[++i];
        }
        options.renderingOptions.setAntialias(!noAntiAlias);
        options.renderingOptions.setDropShadows(!noShadows);
        options.renderingOptions.setScale(scale);
        options.processingOptions.setAllCornersAreRound(roundCorners);
        options.processingOptions.setPerformSeparationOfCommonEdges(!noSeparation);
//        options.renderingOptions.setRenderDebugLines(false);
//        options.processingOptions.setColorCodesProcessingMode(ProcessingOptions.USE_COLOR_CODES);
//        options.processingOptions.setPrintDebugOutput(true);
//        options.processingOptions.setVerbose(true);

        if (gridText == null) {
            try {
                InputStream is = DitaaTest.class.getResourceAsStream("complex-ditaa.properties");
                LineNumberReader reader = new LineNumberReader(new InputStreamReader(is));
                ArrayList<StringBuilder> lines = new ArrayList<StringBuilder>();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) break;
                    else lines.add(new StringBuilder(line));
                }
                grid.initialiseWithLines(lines, options.processingOptions);
            } catch(IOException e) {
                e.printStackTrace();
                grid.initialiseWithText("/-+\n| |\n+-/", options.processingOptions);
            }
        }
        else grid.initialiseWithText(gridText, options.processingOptions);

        grid.printDebug();

        long start = System.currentTimeMillis();
        Diagram diagram = new Diagram(grid, options);
        final RenderedImage image = new BitmapRenderer().renderToImage(diagram, options.renderingOptions);
        System.out.println("0. Rendered in " + (System.currentTimeMillis() - start) + " ms");
//        ImageIO.write(image, "png", os);

        JFrame frame = new JFrame("Ditaa Test");
        frame.setSize(image.getWidth()+30, image.getHeight()+80);
        Panel panel = new Panel() {
            public void paint(Graphics g) {
                g.drawImage((Image) image, 0, 0, null);
            }
        };
        frame.getContentPane().add(panel);
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.out.println("Mouse clicked.");
                System.exit(0);
            }
        });
        // this doesn't seem to be working:
        frame.addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                System.out.println("Window state changed: " + e.getNewState());
                if (e.getNewState() == WindowEvent.WINDOW_CLOSED)
                    System.exit(0);
            }
        });
        frame.setVisible(true);

        for (int i = 0; i < 10; ++i) {
            start = System.currentTimeMillis();
            Diagram nDiagram = new Diagram(grid, options);
            new BitmapRenderer().renderToImage(nDiagram, options.renderingOptions);
            System.out.println((i+1) + ". Rendered in " + (System.currentTimeMillis() - start) + " ms");
        }
    }
}
