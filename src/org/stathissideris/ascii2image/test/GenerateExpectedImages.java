package org.stathissideris.ascii2image.test;

/**
 * Run this to generate the expected (correct) images for unit testing
 * when the code is at a state that produces correct output.
 * 
 * @author sideris
 *
 */
public class GenerateExpectedImages {
	public static void main(String[] args) {
		VisualTester.generateImages(VisualTester.getFilesToRender(), "tests/images-expected");
		System.out.println("Done");	
	}
}
