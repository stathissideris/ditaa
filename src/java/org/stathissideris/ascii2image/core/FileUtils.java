/**
 * ditaa - Diagrams Through Ascii Art
 * 
 * Copyright (C) 2004-2011 Efstathios Sideris
 *
 * ditaa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * ditaa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with ditaa.  If not, see <http://www.gnu.org/licenses/>.
 *   
 */
package org.stathissideris.ascii2image.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * 
 * @author Efstathios Sideris
 */
public class FileUtils {
	
	//private static final 
	
	public static String makeTargetPathname(String sourcePathname, String extension, boolean overwrite){
		return makeTargetPathname(sourcePathname, extension, "", overwrite);
	}
	
	public static String makeTargetPathname(String sourcePathname, String extension, String postfix, boolean overwrite){
		File sourceFile =
			new File(sourcePathname);
		
		String path = "";
		if(sourceFile.getParentFile() != null){
			path = sourceFile.getParentFile().getAbsolutePath();
			if(!path.endsWith(File.separator)) path += File.separator;
		}
		String baseName = getBaseName(sourceFile.getName());
		
		String targetName = path + baseName + postfix + "." + extension;
		if(new File(targetName).exists() && !overwrite)
			targetName = makeAlternativePathname(targetName);
		return targetName;
	}
	
	public static String makeAlternativePathname(String pathName){
		int limit = 100;
		
		for(int i = 2; i <= limit; i++){
			String alternative = getBaseName(pathName)+"_"+i;
			String extension = getExtension(pathName);
			if(extension != null) alternative += "."+extension;
			if(!(new File(alternative).exists())) return alternative; 
		}
		return null;
	}

	public static String getExtension(String pathName){
		if(pathName.lastIndexOf('.') == -1) return null;
		return pathName.substring(pathName.lastIndexOf('.') + 1);
	}
	
	public static String getBaseName(String pathName){
		if(pathName.lastIndexOf('.') == -1) return pathName;
		return pathName.substring(0, pathName.lastIndexOf('.'));
	}
	
	public static String readFile(File file) throws IOException {
		return readFile(file, null);
	}
	
	public static String readFile(File file, String encoding) throws IOException {
        long length = file.length();
        
        if (length > Integer.MAX_VALUE) {
            // File is too large
        	// TODO: we need some feedback for the case of the file being too large
        }

		return readFile(new FileInputStream(file), file.getName(), encoding, length);
	}

	public static String readFile(InputStream is, String name, String encoding) throws IOException {
		return readFile(is, name, encoding, -1);
	}

	public static String readFile(InputStream is, String name, String encoding, long length) throws IOException {

		if (length < 0) {
			LineNumberReader reader = new LineNumberReader(new InputStreamReader(is));
			StringBuilder builder = new StringBuilder();
			while (true) {
				String line = reader.readLine();
				if (line == null) break;
				else builder.append(line).append("\n");
			}
			return builder.toString();
		}

		else {
			// Create the byte array to hold the data
			byte[] bytes = new byte[(int)length];
    
			// Read in the bytes
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
				   && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
				offset += numRead;
			}
    
			// Ensure all the bytes have been read in
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file "+name);
			}
    
			// Close the input stream and return bytes
			is.close();
			if(encoding == null){
        		return new String(bytes);
			} else {
        		return new String(bytes, encoding);
			}
		}
	}
		
	public static void main(String[] args){
		System.out.println(makeTargetPathname("C:\\Files\\papar.txt", "jpg", false));
		System.out.println(makeTargetPathname("C:\\Files\\papar", "jpg", false));
		System.out.println(makeTargetPathname("papar.txt", "jpg", false));
		System.out.println(makeTargetPathname("/home/sideris/tsourekia/papar.txt", "jpg", false));
		System.out.println(makeTargetPathname("D:\\diagram.max", "jpg", false));
		System.out.println(makeAlternativePathname("C:\\Files\\papar.txt"));
		System.out.println(makeAlternativePathname("C:\\Files\\papar"));
		System.out.println(getExtension("pipi.jpeg"));
		System.out.println(getExtension("pipi"));
	}
}
