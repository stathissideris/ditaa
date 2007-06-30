// Jericho HTML Parser - Java based library for analysing and manipulating HTML
// Version 1.4
// Copyright (C) 2004 Martin Jericho
// http://jerichohtml.sourceforge.net/
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// http://www.gnu.org/copyleft/lesser.html
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package au.id.jericho.lib.html;

import java.util.*;

final class SpecialTag {
	private String description;
	private String name;
	private String startDelimiter;
	private String endDelimiter;
	private boolean serverTag;
	private boolean hasEndTag;
	private boolean identifierCharacterAllowedAfterName;

	public static final SpecialTag COMMENT=new SpecialTag("comment","!--","<!--","-->",false,false); // note that according to the spec, whitespace may be present between the "--" and ">" of the end delimiter, but ignoring this will probably yield results consistent with most browsers
	public static final SpecialTag DOCTYPE=new SpecialTag("document type declaration",Tag.DOCTYPE_DECLARATION,"<!doctype",">",false,false); // note that according to the spec, whitespace may be present between the "--" and ">" of the end delimiter, but ignoring this will probably yield results consistent with most browsers
	public static final SpecialTag PROCESSING_INSTRUCTION=new SpecialTag("processing instruction","?","<?","?>",false,false);
	public static final SpecialTag XML_DECLARATION=new SpecialTag("XML declaration",Tag.XML_DECLARATION,"<?xml","?>",false,false);
	public static final SpecialTag PHP_TAG=new SpecialTag("PHP tag",Tag.SERVER_PHP,"<?php","?>",true,false);
	public static final SpecialTag COMMON_SERVER_TAG=new SpecialTag("common server tag",Tag.SERVER_COMMON,"<%","%>",true,false);
	public static final SpecialTag MASON_COMPONENT_CALL=new SpecialTag("mason component call",Tag.SERVER_MASON_COMPONENT_CALL,"<&","&>",true,false);
	public static final SpecialTag MASON_COMPONENT_CALLED_WITH_CONTENT=new SpecialTag("mason component called with content",Tag.SERVER_MASON_COMPONENT_CALLED_WITH_CONTENT,"<&|","&>",true,true);
	public static final SpecialTag MASON_NAMED_BLOCK=new SpecialTag("mason named block",Tag.SERVER_MASON_NAMED_BLOCK,"<%",">",true,true);

	private static SpecialTag[] mappedSpecialTags={COMMENT,DOCTYPE,PROCESSING_INSTRUCTION,XML_DECLARATION,PHP_TAG,COMMON_SERVER_TAG,MASON_COMPONENT_CALL,MASON_COMPONENT_CALLED_WITH_CONTENT};
	private static HashMap map;
	static {
		map=new HashMap();
		for (int i=0; i<mappedSpecialTags.length; i++) map.put(mappedSpecialTags[i].name,mappedSpecialTags[i]);
	}

	private SpecialTag(String description, String name, String startDelimiter, String endDelimiter, boolean serverTag, boolean hasEndTag) {
		this.description=description;
		this.name=name;
		this.startDelimiter=startDelimiter;
		this.endDelimiter=endDelimiter;
		this.serverTag=serverTag;
		this.hasEndTag=hasEndTag;
		identifierCharacterAllowedAfterName=!Character.isLetter(name.charAt(name.length()-1));
	}

	public String getDescription() {
		return description;
	}
	public String getName() {
		return name;
	}
	public String getStartDelimiter() {
		return startDelimiter;
	}
	public String getEndDelimiter() {
		return endDelimiter;
	}
	public boolean isServerTag() {
		return serverTag;
	}
	public boolean hasEndTag() {
		return hasEndTag;
	}
	public boolean isIdentifierCharacterAllowedAfterName() {
		return identifierCharacterAllowedAfterName;
	}

	public static SpecialTag get(String name) {
		if (name==MASON_NAMED_BLOCK.name) return MASON_NAMED_BLOCK; // check this separately because it has the same name as COMMON_SERVER_TAG
		return (SpecialTag)map.get(name.toLowerCase());
	}

	public static SpecialTag get(Source source, int pos) {
		String lsource=source.getParseTextLowerCase();
		char firstChar=lsource.charAt(pos);
		switch (firstChar) {
			case '!':
				if (lsource.startsWith(COMMENT.getName(),pos)) return COMMENT;
				if (lsource.startsWith(Tag.DOCTYPE_DECLARATION,pos)) return DOCTYPE;
				return null;
			case '?':
				if (lsource.startsWith(Tag.XML_DECLARATION,pos)) return XML_DECLARATION;
				if (lsource.startsWith(Tag.SERVER_PHP,pos)) return PHP_TAG;
				return PROCESSING_INSTRUCTION;
			case '%':
				return COMMON_SERVER_TAG; // could also be MASON_NAMED_BLOCK
			case '&':
				if (lsource.charAt(pos+1)=='|') return MASON_COMPONENT_CALLED_WITH_CONTENT;
				return MASON_COMPONENT_CALL;
		}
		return null;
	}
}

