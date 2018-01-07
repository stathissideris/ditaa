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

import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

// using SAX
public class DocBookConverter {

	class HowToHandler extends DefaultHandler {
    	boolean title = false;
    	boolean url   = false;

    	public void startElement(
    		String nsURI,
    		String strippedName,
			String tagName,
			Attributes attributes)
       			throws SAXException {
     		if (tagName.equalsIgnoreCase("title"))
        	title = true;
     		if (tagName.equalsIgnoreCase("url"))
        		url = true;
    		}

    	public void characters(char[] ch, int start, int length) {
     		if (title) {
       			System.out.println("Title: " + new String(ch, start, length));
       			title = false;
       		} else if (url) {
       			System.out.println("Url: " + new String(ch, start,length));
       			url = false;
			}
		}
    }

    public void list( ) throws Exception {
		XMLReader parser =
			XMLReaderFactory.createXMLReader
            	("org.apache.crimson.parser.XMLReaderImpl");
		parser.setContentHandler(new HowToHandler( ));
		parser.parse("howto.xml");
	}

	public static void main(String[] args) throws Exception {
		new DocBookConverter().list( );
	}
}
