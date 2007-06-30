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

/**
 * Represents the <a target="_blank" href="http://www.w3.org/TR/html401/intro/sgmltut.html#didx-element-2">start tag</a> of an {@link Element}.
 * <p>
 * Created using one of the following methods:
 * <ul>
 *  <li>{@link Element#getStartTag()}
 *  <li>{@link Source#findPreviousStartTag(int pos)}
 *  <li>{@link Source#findPreviousStartTag(int pos, String name)}
 *  <li>{@link Source#findNextComment(int pos)}
 *  <li>{@link Source#findNextStartTag(int pos)}
 *  <li>{@link Source#findNextStartTag(int pos, String name)}
 *  <li>{@link Source#findEnclosingComment(int pos)}
 *  <li>{@link Source#findEnclosingStartTag(int pos)}
 *  <li>{@link Segment#findAllStartTags(String name)}
 *  <li>{@link Segment#findAllComments()}
 * </ul>
 * Note that an HTML {@linkplain Segment#isComment() comment} is represented as a StartTag object.
 * <p>
 * The <i><code>name</code></i> argument in the above methods can be literal text strings
 * specifying the name of a start tag to search for.
 * <p>
 * Specifying a name parameter ending in a colon (<code>:</code>) searches for all start tags in the specified XML namespace.
 * <p>
 * The constants defined in the {@link Tag} class can be used as <i><code>name</code></i> arguments.
 * For example:
 * <br /><code>source.findAllStartTags(</code>{@link Tag#A}<code>)</code> or <code>source.findAllStartTags("a")</code> - finds all hyperlink start tags
 * <br /><code>source.findAllStartTags(</code>{@link Tag#PROCESSING_INSTRUCTION}<code>)</code> - finds all processing instructions <code>&lt;? ... ?&gt;</code>
 * <br /><code>source.findAllStartTags(</code>{@link Tag#SERVER_COMMON}<code>)</code> - finds all common server tags <code>&lt;% ... %&gt;</code>
 * <p>
 * Note however that the end of a {@linkplain #isPHPTag() PHP} tag can not be reliably found without the use
 * of a PHP parser, meaning any PHP tag found by this library is not guaranteed to have the correct end position.
 * <p>
 * See also the XML 1.0 specification for <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-stag">start tags</a>.
 *
 * @see Element
 * @see EndTag
 */
public final class StartTag extends Tag {
	private Attributes attributes;
	private SpecialTag specialTag;

	static final StartTag CACHED_NULL=new StartTag();

	private StartTag(Source source, int begin, int end, String name, SpecialTag specialTag, Attributes attributes) {
		super(source,begin,end,name);
		this.attributes=attributes;
		this.specialTag=specialTag;
	}

	private StartTag() {} // used when creating CACHED_NULL

	private static StartTag constructWithAttributes(Source source, int begin, String name, SpecialTag specialTag) {
		// it is necessary to get the attributes so that we can be sure that the search on the closing ">"
		// character doesn't pick up anything from the attribute values, which can legally contain
		// ">" characters.
		// From the HTML 4.01 specification section 5.3.2 - Character Entity References:
		//  Authors wishing to put the "<" character in text should use "&lt;" (ASCII decimal 60) to avoid
		//  possible confusion with the beginning of a tag (start tag open delimiter). Similarly, authors
		//  should use "&gt;" (ASCII decimal 62) in text instead of ">" to avoid problems with older user
		//  agents that incorrectly perceive this as the end of a tag (tag close delimiter) when it appears
		//  in quoted attribute values.
		Attributes attributes=Attributes.construct(source,begin,name);
		if (attributes==null) return null; // happens if attributes not properly formed
		String lsource=source.getParseTextLowerCase();
		int end=attributes.end + (lsource.charAt(attributes.end)=='>' ? 1 : 2);
		if (name.equals(SpecialTag.COMMENT.getName())) name=SpecialTag.COMMENT.getName();
		StartTag startTag=new StartTag(source,begin,end,name,specialTag,attributes);
		return startTag;
	}

	/**
	 * Indicates whether the corresponding end tag is forbidden.
	 * <p>
	 * This is the case if one of {@link #isComment()}, {@link #isEmptyElementTag()},
	 * {@link #isProcessingInstruction()}, {@link #isDocTypeDeclaration()},
	 * {@link EndTag#isForbidden(String name) EndTag.isForbidden(getName())},
	 * or {@link #isServerTag()} is <code>true</code>, unless
	 * {@link #isMasonNamedBlock()} or {@link #isMasonComponentCalledWithContent()} is <code>true</code>.
	 * <p>
	 * Note that as of version 1.1, this method also takes the name of the tag into account
	 * by checking whether the HTML specification forbids an end tag of this name.
	 *
	 * @return  <code>true</code> if the corresponding end tag is <i>forbidden</i>, otherwise <code>false</code>.
	 */
	public boolean isEndTagForbidden() {
		return isEmptyElementTag() || (specialTag!=null && !specialTag.hasEndTag()) || isEndTagForbidden(name);
	}

	/**
	 * Indicates whether the corresponding end tag is <i>optional</i> according to the HTML specification.
	 * <p>
	 * This is equivalent to {@link EndTag#isOptional(String name) EndTag.isOptional(getName())}
	 *
	 * @return  <code>true</code> if the corresponding end tag is <i>optional</i>, otherwise <code>false</code>.
	 */
	public boolean isEndTagOptional() {
		return isEndTagOptional(name);
	}

	/**
	 * Indicates whether the corresponding end tag is <i>required</i> according to the HTML specification.
	 * <p>
	 * This is equivalent to {@link EndTag#isRequired(String name) EndTag.isRequired(getName())}
	 *
	 * @return  <code>true</code> if the corresponding end tag is <i>required</i>, otherwise <code>false</code>.
	 */
	public boolean isEndTagRequired() {
		return isEndTagRequired(name);
	}

	/**
	 * Indicates whether the start tag is an <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-eetag">empty element tag</a>.
	 * <p>
	 * This is signified by the characters "/&gt;" at the end of the start tag.
	 *
	 * @return  <code>true</code> if the StartTag is an empty element tag, otherwise <code>false</code>.
	 */
	public boolean isEmptyElementTag() {
		return source.getSourceText().charAt(end-2)=='/';
	}

	// Documentation inherited by Segment
	public boolean isComment() {
		return specialTag==SpecialTag.COMMENT;
	}

	/**
	 * Indicates whether the start tag is a <a target="_blank" href="http://www.w3.org/TR/REC-xml#sec-pi">processing instruction</a>.
	 * <p>
	 * Although an XML processing instruction technically requires a
	 * <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-PITarget">PITarget</a> (essentially a name),
	 * this library considers any tag starting with a question mark (?) to be a processing instruction, including
	 * {@linkplain Tag#SERVER_PHP standard} and
	 * <a target="_blank" href="http://au2.php.net/manual/en/configuration.directives.php#ini.short-open-tag">short-form</a>
	 * <a target="_blank" href="http://www.php.net">PHP</a> tags.
	 * <p>
	 * An {@linkplain #isXMLDeclaration XML declaration} is a special type of processing instruction with the reserved
	 * <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-PITarget">PITarget</a> name of "xml".
	 * <p>
	 * The following code is an example of a processing instruction:
	 * <pre>
	 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
	 * </pre>
	 *
	 * @return  <code>true</code> if the start tag is a processing instruction, otherwise <code>false</code>.
	 */
	public boolean isProcessingInstruction() {
		return name.charAt(0)=='?';
	}

	/**
	 * Indicates whether the start tag is an <a target="_blank" href="http://www.w3.org/TR/REC-xml/#sec-prolog-dtd">XML declaration</a>.
	 * <p>
	 * The following code is an example of an XML declaration:
	 * <pre>
	 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
	 * </pre>
	 *
	 * @return  <code>true</code> if the start tag is an XML declaration, otherwise <code>false</code>.
	 * @see  #isProcessingInstruction()
	 */
	public boolean isXMLDeclaration() {
		return name==Tag.XML_DECLARATION;
	}

	/**
	 * Indicates whether the start tag is a <a target="_blank" href="http://www.w3.org/TR/REC-xml#dt-doctype">document type declaration</a>.
	 * <p>
	 * The following code is an example of a document type declaration:
	 * <pre>
	 * &lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"&gt;
	 * </pre>
	 *
	 * @return  <code>true</code> if the start tag is a document type declaration, otherwise <code>false</code>.
	 */
	public boolean isDocTypeDeclaration() {
		return name==Tag.DOCTYPE_DECLARATION;
	}

	/**
	 * Indicates whether the start tag is a server tag.
	 * <p>
	 * Recognised server tags include
	 * <a target="_blank" href="http://msdn.microsoft.com/asp/">ASP</a>,
	 * <a target="_blank" href="http://java.sun.com/products/jsp/">JSP</a>,
	 * <a target="_blank" href="http://www.modpython.org/">PSP</a>,
	 * <a target="_blank" href="http://www.php.net">PHP</a> and
	 * <a target="_blank" href="http://www.masonhq.com/">Mason</a>.
	 * <p>
	 * <code>&lt;script&gt;</code> tags are never regarded as server tags, regardless of
	 * whether they have a <code>runat="server"</code> or equivalent attribute.
	 * <p>
	 * <a target="_blank" href="http://au2.php.net/manual/en/configuration.directives.php#ini.short-open-tag">Short-form</a>
	 * PHP tags are not recognised as server tags, but as {@linkplain #isProcessingInstruction() processing instructions}.
	 * <p>
	 * Returns <code>true</code> if one of {@link #isCommonServerTag()}, {@link #isPHPTag()} or
	 * {@link #isMasonTag()} is <code>true</code>;
	 *
	 * @return  <code>true</code> if the start tag is a server tag, otherwise <code>false</code>.
	 */
	public boolean isServerTag() {
		return specialTag!=null && specialTag.isServerTag();
	}

	/**
	 * Indicates whether the start tag is a {@linkplain Tag#SERVER_PHP standard PHP tag} (<code>&lt;&#63;php &#46;&#46;&#46; &#63;&gt;</code>).
	 * <p>
	 * <a target="_blank" href="http://au2.php.net/manual/en/configuration.directives.php#ini.short-open-tag">Short-form</a>
	 * and <a target="_blank" href="http://au2.php.net/manual/en/configuration.directives.php#ini.asp-tags">ASP-style</a>
	 * PHP tags will return <code>false</code>, but can be recognised using the {@link #isProcessingInstruction()}
	 * and {@link #isCommonServerTag()} methods respectively.  PHP code blocks denoted using
	 * <code>&lt;script language="php"&gt; ... &lt;/script&gt;</code> tags are also not recognised by this method.
	 * <p>
	 * This library only correctly recognises PHP tags that comply with the XML syntax for processing instructions.
	 * Specifically, the tag is terminated by the first "<code>?&gt;</code>" character sequence, regardless of
	 * whether it occurs within a PHP string expression.
	 * Unfortunately there is no reliable way to determine the end of a PHP tag without the use of a PHP parser.
	 * <p>
	 * Note that the standard PHP processor removes newline characters following PHP tags, but PHP tags recognised by
	 * this library do not include trailing newlines.  They must be removed manually if required.
	 * <p>
	 * The following code is an example of a PHP tag:
	 * <pre>
	 * &lt;?php echo '&lt;p&gt;Hello World&lt;/p&gt;'; ?&gt;
	 * </pre>
	 *
	 * @return  <code>true</code> if the start tag is a standard PHP tag, otherwise <code>false</code>.
	 */
	public boolean isPHPTag() {
		return specialTag==SpecialTag.PHP_TAG;
	}

	/**
	 * Indicates whether the start tag is a {@linkplain Tag#SERVER_COMMON common server tag} (<code>&lt;% &#46;&#46;&#46; %&gt;</code>).
	 * <p>
	 * Common server tags include
	 * <a target="_blank" href="http://msdn.microsoft.com/asp/">ASP</a>,
	 * <a target="_blank" href="http://java.sun.com/products/jsp/">JSP</a>,
	 * <a target="_blank" href="http://www.modpython.org/">PSP</a>,
	 * <a target="_blank" href="http://au2.php.net/manual/en/configuration.directives.php#ini.asp-tags">ASP-style PHP</a> and
	 * <a target="_blank" href="http://www.masonbook.com/book/chapter-2.mhtml#CHP-2-SECT-3.1">Mason substitution</a> tags.
	 * <p>
	 * The following code is an example of a JSP server tag:
	 * <pre>
	 * &lt;%@ include file="relativeURL" %&gt;
	 * </pre>
	 *
	 * @return  <code>true</code> if the start tag is a server tag, otherwise <code>false</code>.
	 */
	public boolean isCommonServerTag() {
		return specialTag==SpecialTag.COMMON_SERVER_TAG;
	}

	/**
	 * Indicates whether the start tag is any type of <a target="_blank" href="http://www.masonhq.com/">Mason</a> tag.
	 * <p>
	 * This returns <code>true</code> if any one of {@link #isMasonNamedBlock()}, {@link #isMasonComponentCall()},
	 * {@link #isMasonComponentCalledWithContent()} or {@link #isCommonServerTag()}
	 * is <code>true</code>.
	 *
	 * @return  <code>true</code> if the start tag is any type of Mason tag, otherwise <code>false</code>.
	 * @see  #isServerTag()
	 */
	public boolean isMasonTag() {
		return isCommonServerTag() || isMasonNamedBlock() || isMasonComponentCall() || isMasonComponentCalledWithContent();
	}

	/**
	 * Indicates whether the start tag is a
	 * <a target="_blank" href="http://www.masonbook.com/book/chapter-2.mhtml#CHP-2-SECT-3.4">Mason named block</a>.
	 * <p>
	 * The following code is an example of a Mason named block:
	 * <pre>
	 * &lt;%perl&gt; print "hello world"; &lt;/%perl&gt;
	 * </pre>
	 * <p>
	 *
	 * @return  <code>true</code> if the start tag is a Mason named block, otherwise <code>false</code>.
	 * @see  #isMasonTag()
	 */
	public boolean isMasonNamedBlock() {
		return specialTag==SpecialTag.MASON_NAMED_BLOCK;
	}

	/**
	 * Indicates whether the start tag is a <a target="_blank" href="http://www.masonbook.com/book/chapter-2.mhtml#CHP-2-SECT-3.3">Mason component call</a> (<code>&lt;&amp; &#46;&#46;&#46; &amp;&gt;</code>).
	 * <p>
	 * Note that this method returns <code>false</code> for Mason substitution tags and named blocks.
	 * Use the {@link #isCommonServerTag()} method to detect these other Mason tags.
	 * <p>
	 * The following code is an example of a Mason component call:
	 * <pre>
	 * &lt;&amp; menu &amp;&gt;
	 * </pre>
	 *
	 * @return  <code>true</code> if the start tag is a Mason component call, otherwise <code>false</code>.
	 * @see  #isMasonTag()
	 */
	public boolean isMasonComponentCall() {
		return specialTag==SpecialTag.MASON_COMPONENT_CALL;
	}

	/**
	 * Indicates whether the start tag is a
	 * <a target="_blank" href="http://www.masonbook.com/book/chapter-2.mhtml#CHP-2-SECT-3.3.1">Mason component called with content</a>.
	 * <p>
	 * The following code is an example of a Mason component called with content:
	 * <pre>
	 * &lt;&amp;| /sql/select, query =&gt; 'SELECT name, age FROM User' &amp;&gt;
	 *   &lt;tr&gt;&lt;td&gt;%name&lt;/td&gt;&lt;td&gt;%age&lt;/td&gt;&lt;/tr&gt;
	 * &lt;/&amp;&gt;
	 * </pre>
	 *
	 * @return  <code>true</code> if the start tag is a Mason component called with content, otherwise <code>false</code>.
	 * @see  #isMasonTag()
	 */
	public boolean isMasonComponentCalledWithContent() {
		return specialTag==SpecialTag.MASON_COMPONENT_CALLED_WITH_CONTENT;
	}

	/**
	 * Returns the attributes specified in this start tag.
	 * <p>
	 * Guaranteed not <code>null</code> except in one of the following cases:
	 * <p>
	 * Returns <code>null</code> if this start tag represents an
	 * HTML {@linkplain #isComment() comment}, {@linkplain #isDocTypeDeclaration DocType} declaration,
	 * {@linkplain #isProcessingInstruction processing instruction} or {@linkplain #isServerTag() server tag}.
	 * <p>
	 * The only type of processing instruction that contains attributes by default is an {@linkplain #isXMLDeclaration() XML Declaration}.
	 * <p>
	 * To force the parsing of attributes in the above cases, use the {@link #parseAttributes()} method instead.
	 *
	 * @return  the attributes specified in this start tag, or <code>null</code> if this start tag represents an HTML comment, DocType declaration, processing instruction or server side tag.
	 * @see  #parseAttributes()
	 * @see  Source#parseAttributes(int pos, int maxEnd)
	 */
	public Attributes getAttributes() {
		return attributes;
	}

	/**
	 * Parses the attributes specified in this start tag, regardless of the type of start tag.
	 * This method is only required in the unusual situation where attributes exist in a start tag that
	 * normally doesn't contain attributes.
	 * These types of start tags are listed in the documentation of the {@link StartTag#getAttributes()} method.
	 * <p>
	 * This method returns the cached attributes from the {@link StartTag#getAttributes()} method
	 * if not <code>null</code>, otherwise the source is physically parsed with each call to this method.
	 * <p>
	 * This is equivalent to {@link #parseAttributes(int) parseAttributes(Attributes.getDefaultMaxErrorCount())}
	 *
	 * @return  the attributes specified in this start tag, or <code>null</code> if too many errors occur while parsing.
	 * @see  #getAttributes()
	 * @see  Source#parseAttributes(int pos, int maxEnd)
	 */
	public Attributes parseAttributes() {
		return parseAttributes(Attributes.getDefaultMaxErrorCount());
	}

	/**
	 * Parses the attributes specified in this start tag, regardless of the type of start tag.
	 * This method is only required in the unusual situation where attributes exist in a start tag that
	 * normally doesn't contain attributes.
	 * These types of start tags are listed in the documentation of the {@link StartTag#getAttributes()} method.
	 * <p>
	 * See {@link #parseAttributes()} for more information.
	 *
	 * @param  maxErrorCount  the maximum number of minor errors allowed while parsing
	 * @return  the attributes specified in this start tag, or <code>null</code> if too many errors occur while parsing.
	 * @see  #getAttributes()
	 */
	public Attributes parseAttributes(int maxErrorCount) {
		if (attributes!=null) return attributes;
		int maxEnd=end-(specialTag!=null ? specialTag.getEndDelimiter().length() : 1);
		int attributesBegin=begin+1+name.length();
		// skip any non-identifier characters directly after the name (which are quite common)
		while (!isIdentifierStart(source.getSourceText().charAt(attributesBegin))) {
			attributesBegin++;
			if (attributesBegin==maxEnd) return null;
		}
		return Attributes.construct(source,begin,attributesBegin,maxEnd,name,maxErrorCount);
	}

	/**
	 * Returns the {@link FormControlType} of this start tag.
	 * @return  the form control type of this start tag, or <code>null</code> if it is not a <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.2">control</a>.
	 */
	public FormControlType getFormControlType() {
		if (isComment() || !FormControlType.isPotentialControl(name)) return null;
		if (name.equals(TEXTAREA)) return FormControlType.TEXTAREA;
		Attributes attributes=getAttributes();
		if (name.equals(SELECT)) {
			Attribute multiple=attributes.get("multiple");
			return multiple!=null ? FormControlType.SELECT_MULTIPLE : FormControlType.SELECT_SINGLE;
		}
		Attribute type=attributes.get("type");
		if (name.equals("button"))
			return (type==null || type.getValue().equalsIgnoreCase("submit")) ? FormControlType.BUTTON : null;
		// assume tag name "input":
		if (type==null) return FormControlType.TEXT;
		return FormControlType.get(type.getValue().toLowerCase());
	}

	/**
	 * Returns the segment containing the text that immediately follows this start tag up until the start of the following tag.
	 * <p>
	 * Guaranteed not <code>null</code>.
	 * @return  the segment containing the text that immediately follows this start tag up until the start of the following tag.
	 */
	public Segment getFollowingTextSegment() {
		int endData=source.getParseTextLowerCase().indexOf('<',end);
		if (endData==-1) endData=source.length();
		return new Segment(source,end,endData);
	}

	/**
	 * Returns the end tag that corresponds to this start tag.
	 * <p>
	 * This method exists mainly for backward compatability with version 1.0.
	 * <p>
	 * The {@link #getElement()} method is much more useful as it will determine the span of the
	 * element even if the end tag is {@linkplain #isEndTagOptional() optional} and doesn't exist
	 * (This is a new feature in version 1.1).
	 * <p>
	 * This method on the other hand will just return <code>null</code> in the above case, and
	 * is equivalent to calling <code>getElement().getEndTag()</code>
	 *
	 * @return  the end tag that corresponds to this start tag, or <code>null</code> if none exists.
	 */
	public EndTag findEndTag() {
		return getElement().getEndTag();
	}

	/**
	 * Returns the element that corresponds to this start tag.
	 * Guaranteed not <code>null</code>.
	 * <p>
	 * Note that as of version 1.1, this method returns an element spanning the logical
	 * HTML <a target="_blank" href="http://www.w3.org/TR/html401/intro/sgmltut.html#h-3.2.1">element</a>
	 * if the end tag is {@linkplain #isEndTagOptional optional} but not present.
	 * In this case the version 1.0 method returned an element spanning only the start tag.
	 * <h4>Example 1: Elements that have {@linkplain #isEndTagRequired() required} end tags</h4>
	 * <pre>
	 * 1. &lt;div&gt;
	 * 2.   &lt;div&gt;
	 * 3.     &lt;div&gt;
	 * 4.       &lt;div&gt;This is line 4&lt;/div&gt;
	 * 5.     &lt;/div&gt;
	 * 6.     &lt;div&gt;This is line 6&lt;/div&gt;
	 * 7.   &lt;/div&gt;</pre>
	 * <ul>
	 *  <li>The start tag on line 1 returns an empty element spanning only the start tag.
	 *   This is because the end tag of a <code>&lt;div&gt;</code> element is required,
	 *   making the sample code invalid as all the end tags are matched with other start tags.
	 *  <li>The start tag on line 2 returns an element spanning to the end of line 7.
	 *  <li>The start tag on line 3 returns an element spanning to the end of line 5.
	 *  <li>The start tag on line 4 returns an element spanning to the end of line 4.
	 *  <li>The start tag on line 6 returns an element spanning to the end of line 6.
	 * </ul>
	 * <h4>Example 2: Elements that have {@linkplain #isEndTagOptional() optional} end tags</h4>
	 * <pre>
	 * 1. &lt;ul&gt;
	 * 2.   &lt;li&gt;item 1
	 * 3.   &lt;li&gt;item 2
	 * 4.     &lt;ul&gt;
	 * 5.       &lt;li&gt;subitem 1&lt;/li&gt;
	 * 6.       &lt;li&gt;subitem 2
	 * 7.     &lt;/ul&gt;
	 * 8.   &lt;li&gt;item 3&lt;/li&gt;
	 * 9. &lt;/ul&gt;</pre>
	 * <ul>
	 *  <li>The start tag on line 1 returns an element spanning to the end of line 9.
	 *  <li>The start tag on line 2 returns an element spanning to the start of the <code>&lt;li&gt;</code> start tag on line 3.
	 *  <li>The start tag on line 3 returns an element spanning to the start of the <code>&lt;li&gt;</code> start tag on line 8.
	 *  <li>The start tag on line 4 returns an element spanning to the end of line 7.
	 *  <li>The start tag on line 5 returns an element spanning to the end of line 5.
	 *  <li>The start tag on line 6 returns an element spanning to the start of the <code>&lt;/ul&gt;</code> end tag on line 7.
	 *  <li>The start tag on line 8 returns an element spanning to the end of line 8.
	 * </ul>
	 *
	 * @return  the element that corresponds to this start tag.
	 */
	public Element getElement() {
		String cacheKey=SearchCache.getElementKey(this);
		Element element=source.getSearchCache().getElement(cacheKey);
		if (element==null) {
			element=new Element(source,this,findEndTagInternal());
			source.getSearchCache().setElement(cacheKey,element);
		}
		return element;
	}

	private EndTag findEndTagInternal() {
		// This behaves the same as findEndTag(), except that a missing optional end tag will return a zero length EndTag instead of null
		if (isEndTagForbidden()) return null;
		TerminatorSets terminatorSets=getOptionalEndTagTerminatorSets();
		if (terminatorSets!=null) // end tag is optional
			return findOptionalEndTag(terminatorSets);
		// end tag is required
		String endTagName=name==Tag.SERVER_MASON_COMPONENT_CALLED_WITH_CONTENT ? "&" : name;
		Segment[] findResult=findEndTag(source.getParseTextLowerCase(),source.findNextEndTag(end,endTagName));
		if (findResult==null) return null;
		return (EndTag)findResult[0];
	}

	public String toString() {
		if (this==CACHED_NULL) return "CACHED_NULL";
		StringBuffer sb=new StringBuffer();
		sb.append('"').append(name).append("\" ");
		if (specialTag!=null) sb.append('(').append(specialTag.getDescription()).append(") ");
		sb.append(super.toString());
		return sb.toString();
	}

	private EndTag findOptionalEndTag(TerminatorSets terminatorSets) {
		Iterator i=source.getNextTagIterator(end);
		while (i.hasNext()) {
			Tag tag=(Tag)i.next();
			Set terminatorSet;
			if (tag instanceof EndTag) {
				if (tag.name.equals(name)) return (EndTag)tag;
				terminatorSet=terminatorSets.getEndTagTerminatorSet();
			} else {
				terminatorSet=terminatorSets.getIgnoredNestedElementSet();
				if (terminatorSet!=null && terminatorSet.contains(tag.name)) {
					Element ignoredNestedElement=((StartTag)tag).getElement();
					i=source.getNextTagIterator(ignoredNestedElement.end);
					continue;
				}
				terminatorSet=terminatorSets.getStartTagTerminatorSet();
			}
			if (terminatorSet!=null && terminatorSet.contains(tag.name)) return new EndTag(source,tag.begin,tag.begin,name);
		}
		// Ran out of tags. The only legitimate case of this happening is if the end HTML tag is missing, in which case the end of the element is the end of the source document
		return new EndTag(source,source.end,source.end,name);
	}

	static StartTag findPreviousOrNext(Source source, int pos, String name, boolean previous) {
		String cacheKey=SearchCache.getStartTagKey(pos,name,previous);
		StartTag startTag=source.getSearchCache().getStartTag(cacheKey);
		if (startTag==null) {
			// check if there is a tag at the specified position
			Tag tag=(Tag)source.getSearchCache().getTag(SearchCache.getTagKey(pos));
			if ((tag instanceof StartTag) && (name==null || name.equals(tag.name))) return (StartTag)tag;
			startTag=findPreviousOrNextUncached(source,pos,name,previous);
			source.getSearchCache().setStartTag(cacheKey,startTag);
			if (startTag!=null && name==null) {
				// set the named cache as well if we were searching for any name
				source.getSearchCache().setStartTag(SearchCache.getStartTagKey(pos,startTag.name,previous),startTag);
			}
		}
		return startTag==CACHED_NULL ? null : startTag;
	}

	private static StartTag findPreviousOrNextUncached(Source source, int pos, String searchName, boolean previous) {
		if (searchName==null) return findPreviousOrNext(source,pos,previous);
		String startDelimiter;
		// if the last character of the search name is a colon, we are searching for any start tag in the specified namespace
		boolean namespaceSearch=(searchName.charAt(searchName.length()-1)==':');
		SpecialTag searchSpecialTag=namespaceSearch ? null : SpecialTag.get(searchName);
		if (searchSpecialTag!=null) {
			searchName=searchSpecialTag.getName();
			startDelimiter=searchSpecialTag.getStartDelimiter();
		} else {
			searchName=searchName.toLowerCase();
			startDelimiter='<'+searchName;
		}
		try {
			String lsource=source.getParseTextLowerCase();
			int begin=pos;
			int nameEnd;
			do {
				begin=previous?lsource.lastIndexOf(startDelimiter,begin):lsource.indexOf(startDelimiter,begin);
				if (begin==-1) return null;
				Segment enclosingComment=source.findEnclosingComment(begin-1);
				if (enclosingComment!=null) {
					if (searchSpecialTag==SpecialTag.COMMENT && previous) return (StartTag)enclosingComment;
					begin=previous?enclosingComment.begin-2:enclosingComment.end;
					continue;
				}
				int searchNameEnd=begin+startDelimiter.length();
				SpecialTag specialTag=searchSpecialTag;
				String name=searchName;
				if (searchSpecialTag==SpecialTag.COMMENT) {
					nameEnd=searchNameEnd;
				} else {
					nameEnd=source.getIdentifierEnd(searchNameEnd,false);
					if (nameEnd==-1) {
						nameEnd=searchNameEnd;
					} else if (nameEnd!=searchNameEnd) {
						if (namespaceSearch) {
							name=lsource.substring(begin+1,nameEnd);
						} else {
							if (specialTag==null || !specialTag.isIdentifierCharacterAllowedAfterName()) continue; // full name of the found tag is longer than specified name
							name=lsource.substring(begin+1,nameEnd);
							specialTag=SpecialTag.get(name);
							if (specialTag==null)
								specialTag=searchSpecialTag;
							else
								name=specialTag.getName();
						}
				  }
				}
				if (specialTag!=null && specialTag!=SpecialTag.XML_DECLARATION) {
					int end=source.findEnd(nameEnd,specialTag);
					if (specialTag==SpecialTag.COMMON_SERVER_TAG && nameEnd!=searchNameEnd) {
						// also look for mason named blocks when looking for start tags with a name of "&"
						int masonNamedBlockEnd=source.findEnd(nameEnd,SpecialTag.MASON_NAMED_BLOCK);
						if (masonNamedBlockEnd!=-1 && (end==-1 || masonNamedBlockEnd<end)) {
							EndTag endTag=source.findNextEndTag(masonNamedBlockEnd,name);
							if (endTag!=null) return new StartTag(source,begin,masonNamedBlockEnd,name,SpecialTag.MASON_NAMED_BLOCK,null);
						}
					}
					if (end==-1) {
						source.log("StartTag",name,begin,"rejected because it has no matching end delimiter",-1);
						if (!previous) return null;
						begin=begin-4;
						continue;
					}
					if (specialTag==SpecialTag.MASON_NAMED_BLOCK) {
						// don't report any errors if looking for mason named blocks because they could be a mason substitution tag containing a '>'
						if (nameEnd==searchNameEnd) continue; // doesn't have a name.  Probably a common server tag containing a '>'
						if (lsource.charAt(end-2)=='%') continue; // common server tag, not a named block
						EndTag endTag=source.findNextEndTag(end,name);
						if (endTag==null) continue; // end tag missing
					} else if (specialTag==SpecialTag.MASON_COMPONENT_CALL && lsource.charAt(nameEnd)=='|') {
						specialTag=SpecialTag.MASON_COMPONENT_CALLED_WITH_CONTENT;
						name=specialTag.getName();
					}
					return new StartTag(source,begin,end,name,specialTag,null);
				}
				StartTag startTag=constructWithAttributes(source,begin,name,specialTag);
				if (startTag!=null) return startTag;
			} while (inRange(source,begin=previous?begin-2:begin+1));
		} catch (IndexOutOfBoundsException ex) {}
		return null;
	}

	private static StartTag findPreviousOrNext(Source source, int pos, boolean previous) {
		// This has a known bug that it will recognise a < in an attribute value as the start of the tag if previous==true.
		// See the comments in the documentation of the Source class for more details.
		try {
			StartTag previousComment=source.findPreviousStartTag(pos,SpecialTag.COMMENT.getName());
			if (previousComment!=null) {
				if (previousComment.end>pos) {
					// the current position lies within the comment
					if (previous) return previousComment; // return the comment if finding previous
					pos=previousComment.end; // skip all tags within the comment
				}
				if (!previous) previousComment=null; // the previous comment is now no longer relevant if we are searching forward
			}
			String lsource=source.getParseTextLowerCase();
			int begin=pos;
			do {
				begin=previous?lsource.lastIndexOf('<',begin):lsource.indexOf('<',begin);
				if (begin==-1) return null;
				if (previousComment!=null && previousComment.encloses(begin)) return previousComment; // return the comment if finding previous and current position lies within the comment
				String tagAtCacheKey=SearchCache.getTagKey(begin);
				Tag tag=(Tag)source.getSearchCache().getTag(tagAtCacheKey);
				if (tag instanceof StartTag) return (StartTag)tag;
				if (tag!=null || lsource.charAt(begin+1)=='/') continue; // end tag or CACHED_NULL
				int nameBegin=begin+1;
				int nameEnd;
				String name=null;
				StartTag startTag=null;
				SpecialTag specialTag=SpecialTag.get(source,nameBegin);
				try {
					if (specialTag!=null) {
						startTag=newSpecialStartTag(source,begin,nameBegin,specialTag);
						if (startTag!=null) return startTag;
						continue;
					}
					nameEnd=source.getIdentifierEnd(nameBegin,true);
					if (nameEnd==-1) {
						source.log("StartTag",null,begin,"rejected because it has an invalid first character in its name",-1);
						continue;
					}
					name=lsource.substring(nameBegin,nameEnd);
					startTag=constructWithAttributes(source,begin,name,null);
					if (startTag!=null) return startTag;
				} finally {
					source.getSearchCache().setTag(tagAtCacheKey,startTag);
				}
			} while (inRange(source,begin+=(previous?-2:2)));
		} catch (IndexOutOfBoundsException ex) {}
		return null;
	}

	private static StartTag newSpecialStartTag(Source source, int begin, int nameBegin, SpecialTag specialTag) {
		String name;
		int end=-1;
		String lsource=source.getParseTextLowerCase();
		if (specialTag==SpecialTag.COMMON_SERVER_TAG) {
			// check if it is a Mason named block
			// We can recognise this by the fact that the % character is immediately followed by a valid
			// identifier, and an end tag matching the identifier exists in the source following the start tag.
			int nameEnd=source.getIdentifierEnd(nameBegin+1,true);
			if (nameEnd!=-1) {
				name=lsource.substring(nameBegin,nameEnd);
				end=source.findEnd(nameEnd,SpecialTag.MASON_NAMED_BLOCK);
				if (end==-1) {
					source.log("StartTag",specialTag.getName(),begin,"rejected because it has no matching end delimiter",-1);
					return null;
				}
				EndTag endTag=source.findNextEndTag(end,name);
				if (endTag!=null) return new StartTag(source,begin,end,name,SpecialTag.MASON_NAMED_BLOCK,null);
			}
		}
		end=source.findEnd(nameBegin+1,specialTag);
		if (end==-1) {
			source.log("StartTag",specialTag.getName(),begin,"rejected because it has no matching end delimiter",-1);
			return null;
		}
		name=specialTag.getName();
		if (specialTag==SpecialTag.XML_DECLARATION)
			return constructWithAttributes(source,begin,name,specialTag);
		return new StartTag(source,begin,end,name,specialTag,null);
	}

	private static boolean inRange(Source source, int pos) {
		return pos>=0 && pos<=source.length();
	}

	private Segment[] findEndTag(String lsource, EndTag nextEndTag) {
		return findEndTag(lsource,end,source.findNextStartTag(end,name),nextEndTag);
	}

	private Segment[] findEndTag(String lsource, int afterPos, StartTag nextStartTag, EndTag nextEndTag) {
		// returns null if no end tag exists in the rest of the file, otherwise the following two segments:
		// first is the matching end tag to this start tag.  Must be present if array is returned.
		// second is the next occurrence after the returned end tag of a start tag of the same name. (null if none exists)
		if (nextEndTag==null) return null;  // no end tag in the rest of the file
		Segment[] returnArray={nextEndTag, nextStartTag};
		if (nextStartTag==null || nextStartTag.begin>nextEndTag.begin) return returnArray;  // no more start tags of the same name in rest of file, or they occur after the end tag that we found.  This means we have found the matching end tag.
		Segment[] findResult=nextStartTag.findEndTag(lsource,nextEndTag);  // find the matching end tag to the interloping start tag
		if (findResult==null) return null;  // no end tag in the rest of the file
		EndTag nextStartTagsEndTag=(EndTag)findResult[0];
		nextStartTag=(StartTag)findResult[1];
		nextEndTag=source.findNextEndTag(nextStartTagsEndTag.end, name);  // find end tag after the interloping start tag's end tag
		return findEndTag(lsource,nextStartTagsEndTag.end,nextStartTag,nextEndTag);  // recurse to see if this is the matching end tag
	}
}

