package org.stathissideris.ascii2image.test;

public class GenerateExpectedImages {
	public static void main(String[] args) {
		VisualTester.generateImages(VisualTester.getFilesToRender(), "tests/images-expected");
		System.out.println("Done");	
	}
}
