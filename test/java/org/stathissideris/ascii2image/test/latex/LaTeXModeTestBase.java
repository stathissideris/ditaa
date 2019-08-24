package org.stathissideris.ascii2image.test.latex;

import org.stathissideris.ascii2image.core.CommandLineConverter;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import static com.github.dakusui.crest.Crest.asDouble;
import static com.github.dakusui.crest.Crest.assertThat;
import static com.github.dakusui.crest.Crest.call;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

abstract class LaTeXModeTestBase extends TestBase {
  void execute(String testName, double threshold) throws IOException {
    System.out.println("START:" + testName);
    boolean started = false;
    try {
      exercise(testName, this.options(testName));
      started = true;
      boolean succeeded = false;
      try {
        verify(testName, threshold);
        succeeded = true;
      } finally {
        if (succeeded)
          System.out.println("PASSED:" + testName);
        else
          System.out.println("FAILED:" + testName);
      }
    } finally {
      if (!started)
        System.err.println("ERROR:" + testName);
    }
  }

  private void exercise(String testName, String[] options) {
    String actualOutputImagePath = actualOutpuImagePath(testName);
    System.out.println("Create a directory for:" + actualOutputImagePath + ":"  + new File(actualOutputImagePath).getParentFile().mkdirs());
    String[] args = Stream.concat(
        Stream.of(inputFilePath(testName), actualOutputImagePath, "-o"),
        Arrays.stream(options))
        .toArray(String[]::new);
    System.out.println("  ditaa " + String.join(" ", args));
    CommandLineConverter.main(args);
  }

  private void verify(String testName, double threshold) {
    ImageFile expected = expectedImage(testName);
    assertThat(
        actualImage(testName),
        asDouble(
            call("diff", expected, diffImagePath(testName))
                .andThen("similarity", expected).$())
            .gt(threshold).$()
    );
  }

  private String inputFilePath(String s) {
    return String.format("test-resources/latex/%s/in.txt", s);
  }

  private ImageFile expectedImage(String s) {
    return new ImageFile(String.format("test-resources/latex/%s/expected.png", s));
  }

  private ImageFile actualImage(String s) {
    return new ImageFile(actualOutpuImagePath(s));
  }

  private String actualOutpuImagePath(String s) {
    return String.format("out/tests/latex/%s/actual.png", s);
  }


  private String diffImagePath(String s) {
    return String.format("out/tests/latex/%s/diff.png", s);
  }

  String[] options(String s) throws IOException {
    return Files.lines(Paths.get(String.format("test-resources/latex/%s/options.txt", s)))
        .collect(toList())
        .toArray(new String[0]);
  }

  public static class ImageFile extends File {
    private final BufferedImage image;

    ImageFile(String pathname) {
      super(pathname);
      try {
        this.image = ImageIO.read(this);
      } catch (IOException e) {
        throw new RuntimeException(pathname, e);
      }
    }

    private ImageFile(String pathname, BufferedImage image) {
      super(pathname);
      this.image = requireNonNull(image);
    }

    /**
     * Creates and returns a new {@code ImageFile} object that contains "diff" between
     * this and {@code another} image.
     * The image is saved in a file specified by {@code outPathname}.
     *
     * @param another     An image with which returned diff image is created.
     * @param outPathname A pathname that stores the created image.
     * @return This object
     */
    @SuppressWarnings("unused")
    public ImageFile diff(ImageFile another, String outPathname) {
      return new ImageFile(outPathname, diff(this.image, another.image)).write();
    }

    /**
     * Returns similarity between this image and {@code another} image.
     * If they are identical, {@code 1.0} will be returned. If completely different,
     * {@code 0.0} will be returned.
     *
     * This method is reflectively invoked by test methods in the enclosing class.
     *
     * @param another An image to be compared to this image
     * @return The similarity.
     */
    @SuppressWarnings("unused")
    public double similarity(ImageFile another) {
      return similarity(this.image, another.image);
    }

    private ImageFile write() {
      try {
        ImageIO.write(image, "png", this.getAbsoluteFile());
        return this;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }


    /**
     * This method is based on a discussion <a href="https://stackoverflow.com/questions/8567905/how-to-compare-images-for-similarity-using-java">How to compare images for similarity</a> made in the Stackoverflow.com
     * If 2 images are identical, this method will return 1.0.
     *
     * @param biA input image A
     * @param biB input image B
     * @return The similarity.
     */
    private static double similarity(BufferedImage biA, BufferedImage biB) {
      double percentage = 0;
      try {
        // take buffer data from both image files //
        DataBuffer dbA = extendIfNecessary(biA, biB).getData().getDataBuffer();
        int sizeA = dbA.getSize();
        DataBuffer dbB = extendIfNecessary(biB, biA).getData().getDataBuffer();
        int count = 0;
        for (int i = 0; i < sizeA; i++) {

          if (dbA.getElem(i) == dbB.getElem(i)) {
            count = count + 1;
          }

        }
        percentage = ((double) count) / sizeA;
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      return percentage;
    }

    /**
     * Generates an image that highlights diff between given 2 images ({@code img1}
     * and {@code img2}) and returns it.
     *
     * This method is based on a discussion found in stackoverflow.com,
     * "Highlight differences between images".
     *
     * @param img1 An image to be diffed.
     * @param img2 The other image to be diffed with {@code img2}
     * @return An image that highlights diff between {@code img1} and {@code img2}.
     * @see "https://stackoverflow.com/questions/25022578/highlight-differences-between-images"
     */
    private static BufferedImage diff(BufferedImage img1, BufferedImage img2) {
      // convert images to pixel arrays...
      final int w = img1.getWidth(),
          h = img1.getHeight(),
          highlight = Color.MAGENTA.getRGB();
      final int[] p1 = img1.getRGB(0, 0, w, h, null, 0, w);
      final int[] p2 = extendIfNecessary(img2, img1).getRGB(0, 0, w, h, null, 0, w);
      // compare img1 to img2, pixel by pixel. If different, highlight img1's pixel...
      for (int i = 0; i < p1.length; i++) {
        if (p1[i] != p2[i]) {
          p1[i] = highlight;
        }
      }
      // save img1's pixels to a new BufferedImage, and return it...
      // (May require TYPE_INT_ARGB)
      final BufferedImage out = new BufferedImage(w, h, img1.getType());
      out.setRGB(0, 0, w, h, p1, 0, w);
      return out;
    }

    private static BufferedImage extendIfNecessary(BufferedImage image, BufferedImage another) {
      if (another.getWidth() > image.getWidth() || another.getHeight() > image.getHeight()) {
        BufferedImage ret = new BufferedImage(
            Math.max(image.getWidth(), another.getWidth()),
            Math.max(image.getHeight(), another.getHeight()),
            image.getType()
        );
        final int p1[] = image.getRGB(
            0, 0, image.getWidth(), image.getHeight(),
            null,
            0, image.getWidth());
        ret.setRGB(
            0, 0, image.getWidth(), image.getHeight(),
            p1,
            0, image.getWidth());
        return ret;
      }
      return image;
    }
  }
}