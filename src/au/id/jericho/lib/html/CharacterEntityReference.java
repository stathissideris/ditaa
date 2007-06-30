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
 * Represents an HTML <a target="_blank" href="http://www.w3.org/TR/REC-html40/charset.html#h-5.3.2">Character Entity Reference</a>.
 * <p>
 * <b>Click <a href="#method_summary">here</a> to scroll down to the method summary.</b>
 * <p>
 * The full list of HTML character entity references can be found at the following URL:<br />
 * <a target="_blank" href="http://www.w3.org/TR/REC-html40/sgml/entities.html">http://www.w3.org/TR/REC-html40/sgml/entities.html</a>.
 * <p>
 * The {@link #_apos &amp;apos;} entity reference is however not defined for use in HTML.
 * It is defined in the <a target="_blank" href="http://www.w3.org/TR/xhtml1/dtds.html#a_dtd_Special_characters">XHTML Special Characters Entity Set</a>,
 * and is the only one that is not included in both.
 * For this reason, the <code>&amp;apos;</code> entity reference is recognised by this library in decoding functions, but in encoding functions
 * the numeric character reference <code>&amp;#39;</code> is used instead.
 * Most modern browsers support it in both XHTML and HTML, with the notable exception
 * of Microsoft Internet Explorer 6.0, which doesn't support it in either.
 * <p>
 * <code>CharacterEntityReference</code> objects are created using one of the following methods:
 * <ul>
 *  <li>{@link CharacterReference#parse(String characterReferenceString)}
 *  <li>{@link Source#findNextCharacterReference(int pos)}
 *  <li>{@link Source#findPreviousCharacterReference(int pos)}
 *  <li>{@link Segment#findAllCharacterReferences()}
 * </ul>
 *
 * @see  CharacterReference
 */
public class CharacterEntityReference extends CharacterReference {
	private String name;
	
	/** <samp>&nbsp;</samp> &amp;nbsp; = &amp;#160; -- no-break space = non-breaking space, U+00A0 ISOnum. */
	public static final char _nbsp='\u00A0';
	/** <samp>&iexcl;</samp> &amp;iexcl; = &amp;#161; -- inverted exclamation mark, U+00A1 ISOnum. */
	public static final char _iexcl='\u00A1';
	/** <samp>&cent;</samp> &amp;cent; = &amp;#162; -- cent sign, U+00A2 ISOnum. */
	public static final char _cent='\u00A2';
	/** <samp>&pound;</samp> &amp;pound; = &amp;#163; -- pound sign, U+00A3 ISOnum. */
	public static final char _pound='\u00A3';
	/** <samp>&curren;</samp> &amp;curren; = &amp;#164; -- currency sign, U+00A4 ISOnum. */
	public static final char _curren='\u00A4';
	/** <samp>&yen;</samp> &amp;yen; = &amp;#165; -- yen sign = yuan sign, U+00A5 ISOnum. */
	public static final char _yen='\u00A5';
	/** <samp>&brvbar;</samp> &amp;brvbar; = &amp;#166; -- broken bar = broken vertical bar, U+00A6 ISOnum. */
	public static final char _brvbar='\u00A6';
	/** <samp>&sect;</samp> &amp;sect; = &amp;#167; -- section sign, U+00A7 ISOnum. */
	public static final char _sect='\u00A7';
	/** <samp>&uml;</samp> &amp;uml; = &amp;#168; -- diaeresis = spacing diaeresis, U+00A8 ISOdia. */
	public static final char _uml='\u00A8';
	/** <samp>&copy;</samp> &amp;copy; = &amp;#169; -- copyright sign, U+00A9 ISOnum. */
	public static final char _copy='\u00A9';
	/** <samp>&ordf;</samp> &amp;ordf; = &amp;#170; -- feminine ordinal indicator, U+00AA ISOnum. */
	public static final char _ordf='\u00AA';
	/** <samp>&laquo;</samp> &amp;laquo; = &amp;#171; -- left-pointing double angle quotation mark = left pointing guillemet, U+00AB ISOnum. */
	public static final char _laquo='\u00AB';
	/** <samp>&not;</samp> &amp;not; = &amp;#172; -- not sign = angled dash, U+00AC ISOnum. */
	public static final char _not='\u00AC';
	/** <samp>&shy;</samp> &amp;shy; = &amp;#173; -- soft hyphen = discretionary hyphen, U+00AD ISOnum. */
	public static final char _shy='\u00AD';
	/** <samp>&reg;</samp> &amp;reg; = &amp;#174; -- registered sign = registered trade mark sign, U+00AE ISOnum. */
	public static final char _reg='\u00AE';
	/** <samp>&macr;</samp> &amp;macr; = &amp;#175; -- macron = spacing macron = overline = APL overbar, U+00AF ISOdia. */
	public static final char _macr='\u00AF';
	/** <samp>&deg;</samp> &amp;deg; = &amp;#176; -- degree sign, U+00B0 ISOnum. */
	public static final char _deg='\u00B0';
	/** <samp>&plusmn;</samp> &amp;plusmn; = &amp;#177; -- plus-minus sign = plus-or-minus sign, U+00B1 ISOnum. */
	public static final char _plusmn='\u00B1';
	/** <samp>&sup2;</samp> &amp;sup2; = &amp;#178; -- superscript two = superscript digit two = squared, U+00B2 ISOnum. */
	public static final char _sup2='\u00B2';
	/** <samp>&sup3;</samp> &amp;sup3; = &amp;#179; -- superscript three = superscript digit three = cubed, U+00B3 ISOnum. */
	public static final char _sup3='\u00B3';
	/** <samp>&acute;</samp> &amp;acute; = &amp;#180; -- acute accent = spacing acute, U+00B4 ISOdia. */
	public static final char _acute='\u00B4';
	/** <samp>&micro;</samp> &amp;micro; = &amp;#181; -- micro sign, U+00B5 ISOnum. */
	public static final char _micro='\u00B5';
	/** <samp>&para;</samp> &amp;para; = &amp;#182; -- pilcrow sign = paragraph sign, U+00B6 ISOnum. */
	public static final char _para='\u00B6';
	/** <samp>&middot;</samp> &amp;middot; = &amp;#183; -- middle dot = Georgian comma = Greek middle dot, U+00B7 ISOnum. */
	public static final char _middot='\u00B7';
	/** <samp>&cedil;</samp> &amp;cedil; = &amp;#184; -- cedilla = spacing cedilla, U+00B8 ISOdia. */
	public static final char _cedil='\u00B8';
	/** <samp>&sup1;</samp> &amp;sup1; = &amp;#185; -- superscript one = superscript digit one, U+00B9 ISOnum. */
	public static final char _sup1='\u00B9';
	/** <samp>&ordm;</samp> &amp;ordm; = &amp;#186; -- masculine ordinal indicator, U+00BA ISOnum. */
	public static final char _ordm='\u00BA';
	/** <samp>&raquo;</samp> &amp;raquo; = &amp;#187; -- right-pointing double angle quotation mark = right pointing guillemet, U+00BB ISOnum. */
	public static final char _raquo='\u00BB';
	/** <samp>&frac14;</samp> &amp;frac14; = &amp;#188; -- vulgar fraction one quarter = fraction one quarter, U+00BC ISOnum. */
	public static final char _frac14='\u00BC';
	/** <samp>&frac12;</samp> &amp;frac12; = &amp;#189; -- vulgar fraction one half = fraction one half, U+00BD ISOnum. */
	public static final char _frac12='\u00BD';
	/** <samp>&frac34;</samp> &amp;frac34; = &amp;#190; -- vulgar fraction three quarters = fraction three quarters, U+00BE ISOnum. */
	public static final char _frac34='\u00BE';
	/** <samp>&iquest;</samp> &amp;iquest; = &amp;#191; -- inverted question mark = turned question mark, U+00BF ISOnum. */
	public static final char _iquest='\u00BF';
	/** <samp>&Agrave;</samp> &amp;Agrave; = &amp;#192; -- latin capital letter A with grave = latin capital letter A grave, U+00C0 ISOlat1. */
	public static final char _Agrave='\u00C0';
	/** <samp>&Aacute;</samp> &amp;Aacute; = &amp;#193; -- latin capital letter A with acute, U+00C1 ISOlat1. */
	public static final char _Aacute='\u00C1';
	/** <samp>&Acirc;</samp> &amp;Acirc; = &amp;#194; -- latin capital letter A with circumflex, U+00C2 ISOlat1. */
	public static final char _Acirc='\u00C2';
	/** <samp>&Atilde;</samp> &amp;Atilde; = &amp;#195; -- latin capital letter A with tilde, U+00C3 ISOlat1. */
	public static final char _Atilde='\u00C3';
	/** <samp>&Auml;</samp> &amp;Auml; = &amp;#196; -- latin capital letter A with diaeresis, U+00C4 ISOlat1. */
	public static final char _Auml='\u00C4';
	/** <samp>&Aring;</samp> &amp;Aring; = &amp;#197; -- latin capital letter A with ring above = latin capital letter A ring, U+00C5 ISOlat1. */
	public static final char _Aring='\u00C5';
	/** <samp>&AElig;</samp> &amp;AElig; = &amp;#198; -- latin capital letter AE = latin capital ligature AE, U+00C6 ISOlat1. */
	public static final char _AElig='\u00C6';
	/** <samp>&Ccedil;</samp> &amp;Ccedil; = &amp;#199; -- latin capital letter C with cedilla, U+00C7 ISOlat1. */
	public static final char _Ccedil='\u00C7';
	/** <samp>&Egrave;</samp> &amp;Egrave; = &amp;#200; -- latin capital letter E with grave, U+00C8 ISOlat1. */
	public static final char _Egrave='\u00C8';
	/** <samp>&Eacute;</samp> &amp;Eacute; = &amp;#201; -- latin capital letter E with acute, U+00C9 ISOlat1. */
	public static final char _Eacute='\u00C9';
	/** <samp>&Ecirc;</samp> &amp;Ecirc; = &amp;#202; -- latin capital letter E with circumflex, U+00CA ISOlat1. */
	public static final char _Ecirc='\u00CA';
	/** <samp>&Euml;</samp> &amp;Euml; = &amp;#203; -- latin capital letter E with diaeresis, U+00CB ISOlat1. */
	public static final char _Euml='\u00CB';
	/** <samp>&Igrave;</samp> &amp;Igrave; = &amp;#204; -- latin capital letter I with grave, U+00CC ISOlat1. */
	public static final char _Igrave='\u00CC';
	/** <samp>&Iacute;</samp> &amp;Iacute; = &amp;#205; -- latin capital letter I with acute, U+00CD ISOlat1. */
	public static final char _Iacute='\u00CD';
	/** <samp>&Icirc;</samp> &amp;Icirc; = &amp;#206; -- latin capital letter I with circumflex, U+00CE ISOlat1. */
	public static final char _Icirc='\u00CE';
	/** <samp>&Iuml;</samp> &amp;Iuml; = &amp;#207; -- latin capital letter I with diaeresis, U+00CF ISOlat1. */
	public static final char _Iuml='\u00CF';
	/** <samp>&ETH;</samp> &amp;ETH; = &amp;#208; -- latin capital letter ETH, U+00D0 ISOlat1. */
	public static final char _ETH='\u00D0';
	/** <samp>&Ntilde;</samp> &amp;Ntilde; = &amp;#209; -- latin capital letter N with tilde, U+00D1 ISOlat1. */
	public static final char _Ntilde='\u00D1';
	/** <samp>&Ograve;</samp> &amp;Ograve; = &amp;#210; -- latin capital letter O with grave, U+00D2 ISOlat1. */
	public static final char _Ograve='\u00D2';
	/** <samp>&Oacute;</samp> &amp;Oacute; = &amp;#211; -- latin capital letter O with acute, U+00D3 ISOlat1. */
	public static final char _Oacute='\u00D3';
	/** <samp>&Ocirc;</samp> &amp;Ocirc; = &amp;#212; -- latin capital letter O with circumflex, U+00D4 ISOlat1. */
	public static final char _Ocirc='\u00D4';
	/** <samp>&Otilde;</samp> &amp;Otilde; = &amp;#213; -- latin capital letter O with tilde, U+00D5 ISOlat1. */
	public static final char _Otilde='\u00D5';
	/** <samp>&Ouml;</samp> &amp;Ouml; = &amp;#214; -- latin capital letter O with diaeresis, U+00D6 ISOlat1. */
	public static final char _Ouml='\u00D6';
	/** <samp>&times;</samp> &amp;times; = &amp;#215; -- multiplication sign, U+00D7 ISOnum. */
	public static final char _times='\u00D7';
	/** <samp>&Oslash;</samp> &amp;Oslash; = &amp;#216; -- latin capital letter O with stroke = latin capital letter O slash, U+00D8 ISOlat1. */
	public static final char _Oslash='\u00D8';
	/** <samp>&Ugrave;</samp> &amp;Ugrave; = &amp;#217; -- latin capital letter U with grave, U+00D9 ISOlat1. */
	public static final char _Ugrave='\u00D9';
	/** <samp>&Uacute;</samp> &amp;Uacute; = &amp;#218; -- latin capital letter U with acute, U+00DA ISOlat1. */
	public static final char _Uacute='\u00DA';
	/** <samp>&Ucirc;</samp> &amp;Ucirc; = &amp;#219; -- latin capital letter U with circumflex, U+00DB ISOlat1. */
	public static final char _Ucirc='\u00DB';
	/** <samp>&Uuml;</samp> &amp;Uuml; = &amp;#220; -- latin capital letter U with diaeresis, U+00DC ISOlat1. */
	public static final char _Uuml='\u00DC';
	/** <samp>&Yacute;</samp> &amp;Yacute; = &amp;#221; -- latin capital letter Y with acute, U+00DD ISOlat1. */
	public static final char _Yacute='\u00DD';
	/** <samp>&THORN;</samp> &amp;THORN; = &amp;#222; -- latin capital letter THORN, U+00DE ISOlat1. */
	public static final char _THORN='\u00DE';
	/** <samp>&szlig;</samp> &amp;szlig; = &amp;#223; -- latin small letter sharp s = ess-zed, U+00DF ISOlat1. */
	public static final char _szlig='\u00DF';
	/** <samp>&agrave;</samp> &amp;agrave; = &amp;#224; -- latin small letter a with grave = latin small letter a grave, U+00E0 ISOlat1. */
	public static final char _agrave='\u00E0';
	/** <samp>&aacute;</samp> &amp;aacute; = &amp;#225; -- latin small letter a with acute, U+00E1 ISOlat1. */
	public static final char _aacute='\u00E1';
	/** <samp>&acirc;</samp> &amp;acirc; = &amp;#226; -- latin small letter a with circumflex, U+00E2 ISOlat1. */
	public static final char _acirc='\u00E2';
	/** <samp>&atilde;</samp> &amp;atilde; = &amp;#227; -- latin small letter a with tilde, U+00E3 ISOlat1. */
	public static final char _atilde='\u00E3';
	/** <samp>&auml;</samp> &amp;auml; = &amp;#228; -- latin small letter a with diaeresis, U+00E4 ISOlat1. */
	public static final char _auml='\u00E4';
	/** <samp>&aring;</samp> &amp;aring; = &amp;#229; -- latin small letter a with ring above = latin small letter a ring, U+00E5 ISOlat1. */
	public static final char _aring='\u00E5';
	/** <samp>&aelig;</samp> &amp;aelig; = &amp;#230; -- latin small letter ae = latin small ligature ae, U+00E6 ISOlat1. */
	public static final char _aelig='\u00E6';
	/** <samp>&ccedil;</samp> &amp;ccedil; = &amp;#231; -- latin small letter c with cedilla, U+00E7 ISOlat1. */
	public static final char _ccedil='\u00E7';
	/** <samp>&egrave;</samp> &amp;egrave; = &amp;#232; -- latin small letter e with grave, U+00E8 ISOlat1. */
	public static final char _egrave='\u00E8';
	/** <samp>&eacute;</samp> &amp;eacute; = &amp;#233; -- latin small letter e with acute, U+00E9 ISOlat1. */
	public static final char _eacute='\u00E9';
	/** <samp>&ecirc;</samp> &amp;ecirc; = &amp;#234; -- latin small letter e with circumflex, U+00EA ISOlat1. */
	public static final char _ecirc='\u00EA';
	/** <samp>&euml;</samp> &amp;euml; = &amp;#235; -- latin small letter e with diaeresis, U+00EB ISOlat1. */
	public static final char _euml='\u00EB';
	/** <samp>&igrave;</samp> &amp;igrave; = &amp;#236; -- latin small letter i with grave, U+00EC ISOlat1. */
	public static final char _igrave='\u00EC';
	/** <samp>&iacute;</samp> &amp;iacute; = &amp;#237; -- latin small letter i with acute, U+00ED ISOlat1. */
	public static final char _iacute='\u00ED';
	/** <samp>&icirc;</samp> &amp;icirc; = &amp;#238; -- latin small letter i with circumflex, U+00EE ISOlat1. */
	public static final char _icirc='\u00EE';
	/** <samp>&iuml;</samp> &amp;iuml; = &amp;#239; -- latin small letter i with diaeresis, U+00EF ISOlat1. */
	public static final char _iuml='\u00EF';
	/** <samp>&eth;</samp> &amp;eth; = &amp;#240; -- latin small letter eth, U+00F0 ISOlat1. */
	public static final char _eth='\u00F0';
	/** <samp>&ntilde;</samp> &amp;ntilde; = &amp;#241; -- latin small letter n with tilde, U+00F1 ISOlat1. */
	public static final char _ntilde='\u00F1';
	/** <samp>&ograve;</samp> &amp;ograve; = &amp;#242; -- latin small letter o with grave, U+00F2 ISOlat1. */
	public static final char _ograve='\u00F2';
	/** <samp>&oacute;</samp> &amp;oacute; = &amp;#243; -- latin small letter o with acute, U+00F3 ISOlat1. */
	public static final char _oacute='\u00F3';
	/** <samp>&ocirc;</samp> &amp;ocirc; = &amp;#244; -- latin small letter o with circumflex, U+00F4 ISOlat1. */
	public static final char _ocirc='\u00F4';
	/** <samp>&otilde;</samp> &amp;otilde; = &amp;#245; -- latin small letter o with tilde, U+00F5 ISOlat1. */
	public static final char _otilde='\u00F5';
	/** <samp>&ouml;</samp> &amp;ouml; = &amp;#246; -- latin small letter o with diaeresis, U+00F6 ISOlat1. */
	public static final char _ouml='\u00F6';
	/** <samp>&divide;</samp> &amp;divide; = &amp;#247; -- division sign, U+00F7 ISOnum. */
	public static final char _divide='\u00F7';
	/** <samp>&oslash;</samp> &amp;oslash; = &amp;#248; -- latin small letter o with stroke, = latin small letter o slash, U+00F8 ISOlat1. */
	public static final char _oslash='\u00F8';
	/** <samp>&ugrave;</samp> &amp;ugrave; = &amp;#249; -- latin small letter u with grave, U+00F9 ISOlat1. */
	public static final char _ugrave='\u00F9';
	/** <samp>&uacute;</samp> &amp;uacute; = &amp;#250; -- latin small letter u with acute, U+00FA ISOlat1. */
	public static final char _uacute='\u00FA';
	/** <samp>&ucirc;</samp> &amp;ucirc; = &amp;#251; -- latin small letter u with circumflex, U+00FB ISOlat1. */
	public static final char _ucirc='\u00FB';
	/** <samp>&uuml;</samp> &amp;uuml; = &amp;#252; -- latin small letter u with diaeresis, U+00FC ISOlat1. */
	public static final char _uuml='\u00FC';
	/** <samp>&yacute;</samp> &amp;yacute; = &amp;#253; -- latin small letter y with acute, U+00FD ISOlat1. */
	public static final char _yacute='\u00FD';
	/** <samp>&thorn;</samp> &amp;thorn; = &amp;#254; -- latin small letter thorn, U+00FE ISOlat1. */
	public static final char _thorn='\u00FE';
	/** <samp>&yuml;</samp> &amp;yuml; = &amp;#255; -- latin small letter y with diaeresis, U+00FF ISOlat1. */
	public static final char _yuml='\u00FF';
	/** <samp>&fnof;</samp> &amp;fnof; = &amp;#402; -- latin small letter f with hook = function = florin, U+0192 ISOtech. */
	public static final char _fnof='\u0192';
	/** <samp>&Alpha;</samp> &amp;Alpha; = &amp;#913; -- greek capital letter alpha, U+0391. */
	public static final char _Alpha='\u0391';
	/** <samp>&Beta;</samp> &amp;Beta; = &amp;#914; -- greek capital letter beta, U+0392. */
	public static final char _Beta='\u0392';
	/** <samp>&Gamma;</samp> &amp;Gamma; = &amp;#915; -- greek capital letter gamma, U+0393 ISOgrk3. */
	public static final char _Gamma='\u0393';
	/** <samp>&Delta;</samp> &amp;Delta; = &amp;#916; -- greek capital letter delta, U+0394 ISOgrk3. */
	public static final char _Delta='\u0394';
	/** <samp>&Epsilon;</samp> &amp;Epsilon; = &amp;#917; -- greek capital letter epsilon, U+0395. */
	public static final char _Epsilon='\u0395';
	/** <samp>&Zeta;</samp> &amp;Zeta; = &amp;#918; -- greek capital letter zeta, U+0396. */
	public static final char _Zeta='\u0396';
	/** <samp>&Eta;</samp> &amp;Eta; = &amp;#919; -- greek capital letter eta, U+0397. */
	public static final char _Eta='\u0397';
	/** <samp>&Theta;</samp> &amp;Theta; = &amp;#920; -- greek capital letter theta, U+0398 ISOgrk3. */
	public static final char _Theta='\u0398';
	/** <samp>&Iota;</samp> &amp;Iota; = &amp;#921; -- greek capital letter iota, U+0399. */
	public static final char _Iota='\u0399';
	/** <samp>&Kappa;</samp> &amp;Kappa; = &amp;#922; -- greek capital letter kappa, U+039A. */
	public static final char _Kappa='\u039A';
	/** <samp>&Lambda;</samp> &amp;Lambda; = &amp;#923; -- greek capital letter lambda, U+039B ISOgrk3. */
	public static final char _Lambda='\u039B';
	/** <samp>&Mu;</samp> &amp;Mu; = &amp;#924; -- greek capital letter mu, U+039C. */
	public static final char _Mu='\u039C';
	/** <samp>&Nu;</samp> &amp;Nu; = &amp;#925; -- greek capital letter nu, U+039D. */
	public static final char _Nu='\u039D';
	/** <samp>&Xi;</samp> &amp;Xi; = &amp;#926; -- greek capital letter xi, U+039E ISOgrk3. */
	public static final char _Xi='\u039E';
	/** <samp>&Omicron;</samp> &amp;Omicron; = &amp;#927; -- greek capital letter omicron, U+039F. */
	public static final char _Omicron='\u039F';
	/** <samp>&Pi;</samp> &amp;Pi; = &amp;#928; -- greek capital letter pi, U+03A0 ISOgrk3. */
	public static final char _Pi='\u03A0';
	/** <samp>&Rho;</samp> &amp;Rho; = &amp;#929; -- greek capital letter rho, U+03A1. */
	public static final char _Rho='\u03A1';
	/** <samp>&Sigma;</samp> &amp;Sigma; = &amp;#931; -- greek capital letter sigma, U+03A3 ISOgrk3. */
	public static final char _Sigma='\u03A3';
	/** <samp>&Tau;</samp> &amp;Tau; = &amp;#932; -- greek capital letter tau, U+03A4. */
	public static final char _Tau='\u03A4';
	/** <samp>&Upsilon;</samp> &amp;Upsilon; = &amp;#933; -- greek capital letter upsilon, U+03A5 ISOgrk3. */
	public static final char _Upsilon='\u03A5';
	/** <samp>&Phi;</samp> &amp;Phi; = &amp;#934; -- greek capital letter phi, U+03A6 ISOgrk3. */
	public static final char _Phi='\u03A6';
	/** <samp>&Chi;</samp> &amp;Chi; = &amp;#935; -- greek capital letter chi, U+03A7. */
	public static final char _Chi='\u03A7';
	/** <samp>&Psi;</samp> &amp;Psi; = &amp;#936; -- greek capital letter psi, U+03A8 ISOgrk3. */
	public static final char _Psi='\u03A8';
	/** <samp>&Omega;</samp> &amp;Omega; = &amp;#937; -- greek capital letter omega, U+03A9 ISOgrk3. */
	public static final char _Omega='\u03A9';
	/** <samp>&alpha;</samp> &amp;alpha; = &amp;#945; -- greek small letter alpha, U+03B1 ISOgrk3. */
	public static final char _alpha='\u03B1';
	/** <samp>&beta;</samp> &amp;beta; = &amp;#946; -- greek small letter beta, U+03B2 ISOgrk3. */
	public static final char _beta='\u03B2';
	/** <samp>&gamma;</samp> &amp;gamma; = &amp;#947; -- greek small letter gamma, U+03B3 ISOgrk3. */
	public static final char _gamma='\u03B3';
	/** <samp>&delta;</samp> &amp;delta; = &amp;#948; -- greek small letter delta, U+03B4 ISOgrk3. */
	public static final char _delta='\u03B4';
	/** <samp>&epsilon;</samp> &amp;epsilon; = &amp;#949; -- greek small letter epsilon, U+03B5 ISOgrk3. */
	public static final char _epsilon='\u03B5';
	/** <samp>&zeta;</samp> &amp;zeta; = &amp;#950; -- greek small letter zeta, U+03B6 ISOgrk3. */
	public static final char _zeta='\u03B6';
	/** <samp>&eta;</samp> &amp;eta; = &amp;#951; -- greek small letter eta, U+03B7 ISOgrk3. */
	public static final char _eta='\u03B7';
	/** <samp>&theta;</samp> &amp;theta; = &amp;#952; -- greek small letter theta, U+03B8 ISOgrk3. */
	public static final char _theta='\u03B8';
	/** <samp>&iota;</samp> &amp;iota; = &amp;#953; -- greek small letter iota, U+03B9 ISOgrk3. */
	public static final char _iota='\u03B9';
	/** <samp>&kappa;</samp> &amp;kappa; = &amp;#954; -- greek small letter kappa, U+03BA ISOgrk3. */
	public static final char _kappa='\u03BA';
	/** <samp>&lambda;</samp> &amp;lambda; = &amp;#955; -- greek small letter lambda, U+03BB ISOgrk3. */
	public static final char _lambda='\u03BB';
	/** <samp>&mu;</samp> &amp;mu; = &amp;#956; -- greek small letter mu, U+03BC ISOgrk3. */
	public static final char _mu='\u03BC';
	/** <samp>&nu;</samp> &amp;nu; = &amp;#957; -- greek small letter nu, U+03BD ISOgrk3. */
	public static final char _nu='\u03BD';
	/** <samp>&xi;</samp> &amp;xi; = &amp;#958; -- greek small letter xi, U+03BE ISOgrk3. */
	public static final char _xi='\u03BE';
	/** <samp>&omicron;</samp> &amp;omicron; = &amp;#959; -- greek small letter omicron, U+03BF NEW. */
	public static final char _omicron='\u03BF';
	/** <samp>&pi;</samp> &amp;pi; = &amp;#960; -- greek small letter pi, U+03C0 ISOgrk3. */
	public static final char _pi='\u03C0';
	/** <samp>&rho;</samp> &amp;rho; = &amp;#961; -- greek small letter rho, U+03C1 ISOgrk3. */
	public static final char _rho='\u03C1';
	/** <samp>&sigmaf;</samp> &amp;sigmaf; = &amp;#962; -- greek small letter final sigma, U+03C2 ISOgrk3. */
	public static final char _sigmaf='\u03C2';
	/** <samp>&sigma;</samp> &amp;sigma; = &amp;#963; -- greek small letter sigma, U+03C3 ISOgrk3. */
	public static final char _sigma='\u03C3';
	/** <samp>&tau;</samp> &amp;tau; = &amp;#964; -- greek small letter tau, U+03C4 ISOgrk3. */
	public static final char _tau='\u03C4';
	/** <samp>&upsilon;</samp> &amp;upsilon; = &amp;#965; -- greek small letter upsilon, U+03C5 ISOgrk3. */
	public static final char _upsilon='\u03C5';
	/** <samp>&phi;</samp> &amp;phi; = &amp;#966; -- greek small letter phi, U+03C6 ISOgrk3. */
	public static final char _phi='\u03C6';
	/** <samp>&chi;</samp> &amp;chi; = &amp;#967; -- greek small letter chi, U+03C7 ISOgrk3. */
	public static final char _chi='\u03C7';
	/** <samp>&psi;</samp> &amp;psi; = &amp;#968; -- greek small letter psi, U+03C8 ISOgrk3. */
	public static final char _psi='\u03C8';
	/** <samp>&omega;</samp> &amp;omega; = &amp;#969; -- greek small letter omega, U+03C9 ISOgrk3. */
	public static final char _omega='\u03C9';
	/** <samp>&thetasym;</samp> &amp;thetasym; = &amp;#977; -- greek small letter theta symbol, U+03D1 NEW. */
	public static final char _thetasym='\u03D1';
	/** <samp>&upsih;</samp> &amp;upsih; = &amp;#978; -- greek upsilon with hook symbol, U+03D2 NEW. */
	public static final char _upsih='\u03D2';
	/** <samp>&piv;</samp> &amp;piv; = &amp;#982; -- greek pi symbol, U+03D6 ISOgrk3. */
	public static final char _piv='\u03D6';
	/** <samp>&bull;</samp> &amp;bull; = &amp;#8226; -- bullet = black small circle, U+2022 ISOpub<br />(see <a href="#_bull">comments</a>).<p>bullet is NOT the same as bullet operator, U+2219</p> */
	public static final char _bull='\u2022';
	/** <samp>&hellip;</samp> &amp;hellip; = &amp;#8230; -- horizontal ellipsis = three dot leader, U+2026 ISOpub. */
	public static final char _hellip='\u2026';
	/** <samp>&prime;</samp> &amp;prime; = &amp;#8242; -- prime = minutes = feet, U+2032 ISOtech. */
	public static final char _prime='\u2032';
	/** <samp>&Prime;</samp> &amp;Prime; = &amp;#8243; -- double prime = seconds = inches, U+2033 ISOtech. */
	public static final char _Prime='\u2033';
	/** <samp>&oline;</samp> &amp;oline; = &amp;#8254; -- overline = spacing overscore, U+203E NEW. */
	public static final char _oline='\u203E';
	/** <samp>&frasl;</samp> &amp;frasl; = &amp;#8260; -- fraction slash, U+2044 NEW. */
	public static final char _frasl='\u2044';
	/** <samp>&weierp;</samp> &amp;weierp; = &amp;#8472; -- script capital P = power set = Weierstrass p, U+2118 ISOamso. */
	public static final char _weierp='\u2118';
	/** <samp>&image;</samp> &amp;image; = &amp;#8465; -- black-letter capital I = imaginary part, U+2111 ISOamso. */
	public static final char _image='\u2111';
	/** <samp>&real;</samp> &amp;real; = &amp;#8476; -- black-letter capital R = real part symbol, U+211C ISOamso. */
	public static final char _real='\u211C';
	/** <samp>&trade;</samp> &amp;trade; = &amp;#8482; -- trade mark sign, U+2122 ISOnum. */
	public static final char _trade='\u2122';
	/** <samp>&alefsym;</samp> &amp;alefsym; = &amp;#8501; -- alef symbol = first transfinite cardinal, U+2135 NEW<br />(see <a href="#_alefsym">comments</a>).<p>alef symbol is NOT the same as hebrew letter alef, U+05D0 although the same glyph could be used to depict both characters</p> */
	public static final char _alefsym='\u2135';
	/** <samp>&larr;</samp> &amp;larr; = &amp;#8592; -- leftwards arrow, U+2190 ISOnum. */
	public static final char _larr='\u2190';
	/** <samp>&uarr;</samp> &amp;uarr; = &amp;#8593; -- upwards arrow, U+2191 ISOnum. */
	public static final char _uarr='\u2191';
	/** <samp>&rarr;</samp> &amp;rarr; = &amp;#8594; -- rightwards arrow, U+2192 ISOnum. */
	public static final char _rarr='\u2192';
	/** <samp>&darr;</samp> &amp;darr; = &amp;#8595; -- downwards arrow, U+2193 ISOnum. */
	public static final char _darr='\u2193';
	/** <samp>&harr;</samp> &amp;harr; = &amp;#8596; -- left right arrow, U+2194 ISOamsa. */
	public static final char _harr='\u2194';
	/** <samp>&crarr;</samp> &amp;crarr; = &amp;#8629; -- downwards arrow with corner leftwards = carriage return, U+21B5 NEW. */
	public static final char _crarr='\u21B5';
	/** <samp>&lArr;</samp> &amp;lArr; = &amp;#8656; -- leftwards double arrow, U+21D0 ISOtech<br />(see <a href="#_lArr">comments</a>).<p>ISO 10646 does not say that lArr is the same as the 'is implied by' arrow but also does not have any other character for that function. So &#63; lArr can be used for 'is implied by' as ISOtech suggests</p> */
	public static final char _lArr='\u21D0';
	/** <samp>&uArr;</samp> &amp;uArr; = &amp;#8657; -- upwards double arrow, U+21D1 ISOamsa. */
	public static final char _uArr='\u21D1';
	/** <samp>&rArr;</samp> &amp;rArr; = &amp;#8658; -- rightwards double arrow, U+21D2 ISOtech<br />(see <a href="#_rArr">comments</a>).<p>ISO 10646 does not say this is the 'implies' character but does not have another character with this function so &#63; rArr can be used for 'implies' as ISOtech suggests</p> */
	public static final char _rArr='\u21D2';
	/** <samp>&dArr;</samp> &amp;dArr; = &amp;#8659; -- downwards double arrow, U+21D3 ISOamsa. */
	public static final char _dArr='\u21D3';
	/** <samp>&hArr;</samp> &amp;hArr; = &amp;#8660; -- left right double arrow, U+21D4 ISOamsa. */
	public static final char _hArr='\u21D4';
	/** <samp>&forall;</samp> &amp;forall; = &amp;#8704; -- for all, U+2200 ISOtech. */
	public static final char _forall='\u2200';
	/** <samp>&part;</samp> &amp;part; = &amp;#8706; -- partial differential, U+2202 ISOtech. */
	public static final char _part='\u2202';
	/** <samp>&exist;</samp> &amp;exist; = &amp;#8707; -- there exists, U+2203 ISOtech. */
	public static final char _exist='\u2203';
	/** <samp>&empty;</samp> &amp;empty; = &amp;#8709; -- empty set = null set = diameter, U+2205 ISOamso. */
	public static final char _empty='\u2205';
	/** <samp>&nabla;</samp> &amp;nabla; = &amp;#8711; -- nabla = backward difference, U+2207 ISOtech. */
	public static final char _nabla='\u2207';
	/** <samp>&isin;</samp> &amp;isin; = &amp;#8712; -- element of, U+2208 ISOtech. */
	public static final char _isin='\u2208';
	/** <samp>&notin;</samp> &amp;notin; = &amp;#8713; -- not an element of, U+2209 ISOtech. */
	public static final char _notin='\u2209';
	/** <samp>&ni;</samp> &amp;ni; = &amp;#8715; -- contains as member, U+220B ISOtech<br />(see <a href="#_ni">comments</a>).<p>should there be a more memorable name than 'ni'&#63;</p> */
	public static final char _ni='\u220B';
	/** <samp>&prod;</samp> &amp;prod; = &amp;#8719; -- n-ary product = product sign, U+220F ISOamsb<br />(see <a href="#_prod">comments</a>).<p>prod is NOT the same character as U+03A0 'greek capital letter pi' though the same glyph might be used for both</p> */
	public static final char _prod='\u220F';
	/** <samp>&sum;</samp> &amp;sum; = &amp;#8721; -- n-ary summation, U+2211 ISOamsb<br />(see <a href="#_sum">comments</a>).<p>sum is NOT the same character as U+03A3 'greek capital letter sigma' though the same glyph might be used for both</p> */
	public static final char _sum='\u2211';
	/** <samp>&minus;</samp> &amp;minus; = &amp;#8722; -- minus sign, U+2212 ISOtech. */
	public static final char _minus='\u2212';
	/** <samp>&lowast;</samp> &amp;lowast; = &amp;#8727; -- asterisk operator, U+2217 ISOtech. */
	public static final char _lowast='\u2217';
	/** <samp>&radic;</samp> &amp;radic; = &amp;#8730; -- square root = radical sign, U+221A ISOtech. */
	public static final char _radic='\u221A';
	/** <samp>&prop;</samp> &amp;prop; = &amp;#8733; -- proportional to, U+221D ISOtech. */
	public static final char _prop='\u221D';
	/** <samp>&infin;</samp> &amp;infin; = &amp;#8734; -- infinity, U+221E ISOtech. */
	public static final char _infin='\u221E';
	/** <samp>&ang;</samp> &amp;ang; = &amp;#8736; -- angle, U+2220 ISOamso. */
	public static final char _ang='\u2220';
	/** <samp>&and;</samp> &amp;and; = &amp;#8743; -- logical and = wedge, U+2227 ISOtech. */
	public static final char _and='\u2227';
	/** <samp>&or;</samp> &amp;or; = &amp;#8744; -- logical or = vee, U+2228 ISOtech. */
	public static final char _or='\u2228';
	/** <samp>&cap;</samp> &amp;cap; = &amp;#8745; -- intersection = cap, U+2229 ISOtech. */
	public static final char _cap='\u2229';
	/** <samp>&cup;</samp> &amp;cup; = &amp;#8746; -- union = cup, U+222A ISOtech. */
	public static final char _cup='\u222A';
	/** <samp>&int;</samp> &amp;int; = &amp;#8747; -- integral, U+222B ISOtech. */
	public static final char _int='\u222B';
	/** <samp>&there4;</samp> &amp;there4; = &amp;#8756; -- therefore, U+2234 ISOtech. */
	public static final char _there4='\u2234';
	/** <samp>&sim;</samp> &amp;sim; = &amp;#8764; -- tilde operator = varies with = similar to, U+223C ISOtech<br />(see <a href="#_sim">comments</a>).<p>tilde operator is NOT the same character as the tilde, U+007E, although the same glyph might be used to represent both</p> */
	public static final char _sim='\u223C';
	/** <samp>&cong;</samp> &amp;cong; = &amp;#8773; -- approximately equal to, U+2245 ISOtech. */
	public static final char _cong='\u2245';
	/** <samp>&asymp;</samp> &amp;asymp; = &amp;#8776; -- almost equal to = asymptotic to, U+2248 ISOamsr. */
	public static final char _asymp='\u2248';
	/** <samp>&ne;</samp> &amp;ne; = &amp;#8800; -- not equal to, U+2260 ISOtech. */
	public static final char _ne='\u2260';
	/** <samp>&equiv;</samp> &amp;equiv; = &amp;#8801; -- identical to, U+2261 ISOtech. */
	public static final char _equiv='\u2261';
	/** <samp>&le;</samp> &amp;le; = &amp;#8804; -- less-than or equal to, U+2264 ISOtech. */
	public static final char _le='\u2264';
	/** <samp>&ge;</samp> &amp;ge; = &amp;#8805; -- greater-than or equal to, U+2265 ISOtech. */
	public static final char _ge='\u2265';
	/** <samp>&sub;</samp> &amp;sub; = &amp;#8834; -- subset of, U+2282 ISOtech. */
	public static final char _sub='\u2282';
	/** <samp>&sup;</samp> &amp;sup; = &amp;#8835; -- superset of, U+2283 ISOtech<br />(see <a href="#_sup">comments</a>).<p>note that nsup, 'not a superset of, U+2283' is not covered by the Symbol font encoding and is not included. Should it be, for symmetry&#63; It is in ISOamsn</p> */ 
	public static final char _sup='\u2283';
	/** <samp>&nsub;</samp> &amp;nsub; = &amp;#8836; -- not a subset of, U+2284 ISOamsn. */
	public static final char _nsub='\u2284';
	/** <samp>&sube;</samp> &amp;sube; = &amp;#8838; -- subset of or equal to, U+2286 ISOtech. */
	public static final char _sube='\u2286';
	/** <samp>&supe;</samp> &amp;supe; = &amp;#8839; -- superset of or equal to, U+2287 ISOtech. */
	public static final char _supe='\u2287';
	/** <samp>&oplus;</samp> &amp;oplus; = &amp;#8853; -- circled plus = direct sum, U+2295 ISOamsb. */
	public static final char _oplus='\u2295';
	/** <samp>&otimes;</samp> &amp;otimes; = &amp;#8855; -- circled times = vector product, U+2297 ISOamsb. */
	public static final char _otimes='\u2297';
	/** <samp>&perp;</samp> &amp;perp; = &amp;#8869; -- up tack = orthogonal to = perpendicular, U+22A5 ISOtech. */
	public static final char _perp='\u22A5';
	/** <samp>&sdot;</samp> &amp;sdot; = &amp;#8901; -- dot operator, U+22C5 ISOamsb<br />(see <a href="#_sdot">comments</a>).<p>dot operator is NOT the same character as U+00B7 middle dot</p> */
	public static final char _sdot='\u22C5';
	/** <samp>&lceil;</samp> &amp;lceil; = &amp;#8968; -- left ceiling = APL upstile, U+2308 ISOamsc. */
	public static final char _lceil='\u2308';
	/** <samp>&rceil;</samp> &amp;rceil; = &amp;#8969; -- right ceiling, U+2309 ISOamsc. */
	public static final char _rceil='\u2309';
	/** <samp>&lfloor;</samp> &amp;lfloor; = &amp;#8970; -- left floor = APL downstile, U+230A ISOamsc. */
	public static final char _lfloor='\u230A';
	/** <samp>&rfloor;</samp> &amp;rfloor; = &amp;#8971; -- right floor, U+230B ISOamsc. */
	public static final char _rfloor='\u230B';
	/** <samp>&lang;</samp> &amp;lang; = &amp;#9001; -- left-pointing angle bracket = bra, U+2329 ISOtech<br />(see <a href="#_lang">comments</a>).<p>lang is NOT the same character as U+003C 'less than' or U+2039 'single left-pointing angle quotation mark'</p> */
	public static final char _lang='\u2329';
	/** <samp>&rang;</samp> &amp;rang; = &amp;#9002; -- right-pointing angle bracket = ket, U+232A ISOtech<br />(see <a href="#_rang">comments</a>).<p>rang is NOT the same character as U+003E 'greater than' or U+203A 'single right-pointing angle quotation mark'</p> */
	public static final char _rang='\u232A';
	/** <samp>&loz;</samp> &amp;loz; = &amp;#9674; -- lozenge, U+25CA ISOpub. */
	public static final char _loz='\u25CA';
	/** <samp>&spades;</samp> &amp;spades; = &amp;#9824; -- black spade suit, U+2660 ISOpub<br />(see <a href="#_spades">comments</a>).<p>black here seems to mean filled as opposed to hollow</p> */
	public static final char _spades='\u2660';
	/** <samp>&clubs;</samp> &amp;clubs; = &amp;#9827; -- black club suit = shamrock, U+2663 ISOpub. */
	public static final char _clubs='\u2663';
	/** <samp>&hearts;</samp> &amp;hearts; = &amp;#9829; -- black heart suit = valentine, U+2665 ISOpub. */
	public static final char _hearts='\u2665';
	/** <samp>&diams;</samp> &amp;diams; = &amp;#9830; -- black diamond suit, U+2666 ISOpub. */
	public static final char _diams='\u2666';
	/** <samp>&quot;</samp> &amp;quot; = &amp;#34; -- quotation mark = APL quote, U+0022 ISOnum. */
	public static final char _quot='\u0022';
	/** <samp>&amp;</samp> &amp;amp; = &amp;#38; -- ampersand, U+0026 ISOnum. */
	public static final char _amp='\u0026';
	/** <samp>&lt;</samp> &amp;lt; = &amp;#60; -- less-than sign, U+003C ISOnum. */
	public static final char _lt='\u003C';
	/** <samp>&gt;</samp> &amp;gt; = &amp;#62; -- greater-than sign, U+003E ISOnum. */
	public static final char _gt='\u003E';
	/** <samp>&OElig;</samp> &amp;OElig; = &amp;#338; -- latin capital ligature OE, U+0152 ISOlat2. */
	public static final char _OElig='\u0152';
	/** <samp>&oelig;</samp> &amp;oelig; = &amp;#339; -- latin small ligature oe, U+0153 ISOlat2<br />(see <a href="#_oelig">comments</a>).<p>ligature is a misnomer, this is a separate character in some languages</p> */
	public static final char _oelig='\u0153';
	/** <samp>&Scaron;</samp> &amp;Scaron; = &amp;#352; -- latin capital letter S with caron, U+0160 ISOlat2. */
	public static final char _Scaron='\u0160';
	/** <samp>&scaron;</samp> &amp;scaron; = &amp;#353; -- latin small letter s with caron, U+0161 ISOlat2. */
	public static final char _scaron='\u0161';
	/** <samp>&Yuml;</samp> &amp;Yuml; = &amp;#376; -- latin capital letter Y with diaeresis, U+0178 ISOlat2. */
	public static final char _Yuml='\u0178';
	/** <samp>&circ;</samp> &amp;circ; = &amp;#710; -- modifier letter circumflex accent, U+02C6 ISOpub. */
	public static final char _circ='\u02C6';
	/** <samp>&tilde;</samp> &amp;tilde; = &amp;#732; -- small tilde, U+02DC ISOdia. */
	public static final char _tilde='\u02DC';
	/** <samp>&ensp;</samp> &amp;ensp; = &amp;#8194; -- en space, U+2002 ISOpub. */
	public static final char _ensp='\u2002';
	/** <samp>&emsp;</samp> &amp;emsp; = &amp;#8195; -- em space, U+2003 ISOpub. */
	public static final char _emsp='\u2003';
	/** <samp>&thinsp;</samp> &amp;thinsp; = &amp;#8201; -- thin space, U+2009 ISOpub. */
	public static final char _thinsp='\u2009';
	/** <samp>&zwnj;</samp> &amp;zwnj; = &amp;#8204; -- zero width non-joiner, U+200C NEW RFC 2070. */
	public static final char _zwnj='\u200C';
	/** <samp>&zwj;</samp> &amp;zwj; = &amp;#8205; -- zero width joiner, U+200D NEW RFC 2070. */
	public static final char _zwj='\u200D';
	/** <samp>&lrm;</samp> &amp;lrm; = &amp;#8206; -- left-to-right mark, U+200E NEW RFC 2070. */
	public static final char _lrm='\u200E';
	/** <samp>&rlm;</samp> &amp;rlm; = &amp;#8207; -- right-to-left mark, U+200F NEW RFC 2070. */
	public static final char _rlm='\u200F';
	/** <samp>&ndash;</samp> &amp;ndash; = &amp;#8211; -- en dash, U+2013 ISOpub. */
	public static final char _ndash='\u2013';
	/** <samp>&mdash;</samp> &amp;mdash; = &amp;#8212; -- em dash, U+2014 ISOpub. */
	public static final char _mdash='\u2014';
	/** <samp>&lsquo;</samp> &amp;lsquo; = &amp;#8216; -- left single quotation mark, U+2018 ISOnum. */
	public static final char _lsquo='\u2018';
	/** <samp>&rsquo;</samp> &amp;rsquo; = &amp;#8217; -- right single quotation mark, U+2019 ISOnum. */
	public static final char _rsquo='\u2019';
	/** <samp>&sbquo;</samp> &amp;sbquo; = &amp;#8218; -- single low-9 quotation mark, U+201A NEW. */
	public static final char _sbquo='\u201A';
	/** <samp>&ldquo;</samp> &amp;ldquo; = &amp;#8220; -- left double quotation mark, U+201C ISOnum. */
	public static final char _ldquo='\u201C';
	/** <samp>&rdquo;</samp> &amp;rdquo; = &amp;#8221; -- right double quotation mark, U+201D ISOnum. */
	public static final char _rdquo='\u201D';
	/** <samp>&bdquo;</samp> &amp;bdquo; = &amp;#8222; -- double low-9 quotation mark, U+201E NEW. */
	public static final char _bdquo='\u201E';
	/** <samp>&dagger;</samp> &amp;dagger; = &amp;#8224; -- dagger, U+2020 ISOpub. */
	public static final char _dagger='\u2020';
	/** <samp>&Dagger;</samp> &amp;Dagger; = &amp;#8225; -- double dagger, U+2021 ISOpub. */
	public static final char _Dagger='\u2021';
	/** <samp>&permil;</samp> &amp;permil; = &amp;#8240; -- per mille sign, U+2030 ISOtech. */
	public static final char _permil='\u2030';
	/** <samp>&lsaquo;</samp> &amp;lsaquo; = &amp;#8249; -- single left-pointing angle quotation mark, U+2039 ISO proposed<br />(see <a href="#_lsaquo">comments</a>).<p>lsaquo is proposed but not yet ISO standardized</p> */
	public static final char _lsaquo='\u2039';
	/** <samp>&rsaquo;</samp> &amp;rsaquo; = &amp;#8250; -- single right-pointing angle quotation mark, U+203A ISO proposed<br />(see <a href="#_rsaquo">comments</a>).<p>rsaquo is proposed but not yet ISO standardized</p> */
	public static final char _rsaquo='\u203A';
	/** <samp>&euro;</samp> &amp;euro; = &amp;#8364; -- euro sign, U+20AC NEW. */
	public static final char _euro='\u20AC';
	/** <samp>&apos;</samp> &amp;apos; = &amp;#39; -- apostrophe = APL quote, U+0027 ISOnum<br />(see <a href="#_apos">comments</a>).<p>apos is only defined for use in XHTML (see the <a target="_blank" href="http://www.w3.org/TR/xhtml1/dtds.html#a_dtd_Special_characters">XHTML Special Characters Entity Set</a>), but not in HTML.</p> */
	public static final char _apos='\'';

	private static Map NAME_TO_CODE_POINT_MAP=new HashMap(260);
	private static IntStringHashMap CODE_POINT_TO_NAME_MAP;

	private static int MAX_NAME_LENGTH=0;
	
	static {
		NAME_TO_CODE_POINT_MAP.put("nbsp",new Integer(_nbsp));
		NAME_TO_CODE_POINT_MAP.put("iexcl",new Integer(_iexcl));
		NAME_TO_CODE_POINT_MAP.put("cent",new Integer(_cent));
		NAME_TO_CODE_POINT_MAP.put("pound",new Integer(_pound));
		NAME_TO_CODE_POINT_MAP.put("curren",new Integer(_curren));
		NAME_TO_CODE_POINT_MAP.put("yen",new Integer(_yen));
		NAME_TO_CODE_POINT_MAP.put("brvbar",new Integer(_brvbar));
		NAME_TO_CODE_POINT_MAP.put("sect",new Integer(_sect));
		NAME_TO_CODE_POINT_MAP.put("uml",new Integer(_uml));
		NAME_TO_CODE_POINT_MAP.put("copy",new Integer(_copy));
		NAME_TO_CODE_POINT_MAP.put("ordf",new Integer(_ordf));
		NAME_TO_CODE_POINT_MAP.put("laquo",new Integer(_laquo));
		NAME_TO_CODE_POINT_MAP.put("not",new Integer(_not));
		NAME_TO_CODE_POINT_MAP.put("shy",new Integer(_shy));
		NAME_TO_CODE_POINT_MAP.put("reg",new Integer(_reg));
		NAME_TO_CODE_POINT_MAP.put("macr",new Integer(_macr));
		NAME_TO_CODE_POINT_MAP.put("deg",new Integer(_deg));
		NAME_TO_CODE_POINT_MAP.put("plusmn",new Integer(_plusmn));
		NAME_TO_CODE_POINT_MAP.put("sup2",new Integer(_sup2));
		NAME_TO_CODE_POINT_MAP.put("sup3",new Integer(_sup3));
		NAME_TO_CODE_POINT_MAP.put("acute",new Integer(_acute));
		NAME_TO_CODE_POINT_MAP.put("micro",new Integer(_micro));
		NAME_TO_CODE_POINT_MAP.put("para",new Integer(_para));
		NAME_TO_CODE_POINT_MAP.put("middot",new Integer(_middot));
		NAME_TO_CODE_POINT_MAP.put("cedil",new Integer(_cedil));
		NAME_TO_CODE_POINT_MAP.put("sup1",new Integer(_sup1));
		NAME_TO_CODE_POINT_MAP.put("ordm",new Integer(_ordm));
		NAME_TO_CODE_POINT_MAP.put("raquo",new Integer(_raquo));
		NAME_TO_CODE_POINT_MAP.put("frac14",new Integer(_frac14));
		NAME_TO_CODE_POINT_MAP.put("frac12",new Integer(_frac12));
		NAME_TO_CODE_POINT_MAP.put("frac34",new Integer(_frac34));
		NAME_TO_CODE_POINT_MAP.put("iquest",new Integer(_iquest));
		NAME_TO_CODE_POINT_MAP.put("Agrave",new Integer(_Agrave));
		NAME_TO_CODE_POINT_MAP.put("Aacute",new Integer(_Aacute));
		NAME_TO_CODE_POINT_MAP.put("Acirc",new Integer(_Acirc));
		NAME_TO_CODE_POINT_MAP.put("Atilde",new Integer(_Atilde));
		NAME_TO_CODE_POINT_MAP.put("Auml",new Integer(_Auml));
		NAME_TO_CODE_POINT_MAP.put("Aring",new Integer(_Aring));
		NAME_TO_CODE_POINT_MAP.put("AElig",new Integer(_AElig));
		NAME_TO_CODE_POINT_MAP.put("Ccedil",new Integer(_Ccedil));
		NAME_TO_CODE_POINT_MAP.put("Egrave",new Integer(_Egrave));
		NAME_TO_CODE_POINT_MAP.put("Eacute",new Integer(_Eacute));
		NAME_TO_CODE_POINT_MAP.put("Ecirc",new Integer(_Ecirc));
		NAME_TO_CODE_POINT_MAP.put("Euml",new Integer(_Euml));
		NAME_TO_CODE_POINT_MAP.put("Igrave",new Integer(_Igrave));
		NAME_TO_CODE_POINT_MAP.put("Iacute",new Integer(_Iacute));
		NAME_TO_CODE_POINT_MAP.put("Icirc",new Integer(_Icirc));
		NAME_TO_CODE_POINT_MAP.put("Iuml",new Integer(_Iuml));
		NAME_TO_CODE_POINT_MAP.put("ETH",new Integer(_ETH));
		NAME_TO_CODE_POINT_MAP.put("Ntilde",new Integer(_Ntilde));
		NAME_TO_CODE_POINT_MAP.put("Ograve",new Integer(_Ograve));
		NAME_TO_CODE_POINT_MAP.put("Oacute",new Integer(_Oacute));
		NAME_TO_CODE_POINT_MAP.put("Ocirc",new Integer(_Ocirc));
		NAME_TO_CODE_POINT_MAP.put("Otilde",new Integer(_Otilde));
		NAME_TO_CODE_POINT_MAP.put("Ouml",new Integer(_Ouml));
		NAME_TO_CODE_POINT_MAP.put("times",new Integer(_times));
		NAME_TO_CODE_POINT_MAP.put("Oslash",new Integer(_Oslash));
		NAME_TO_CODE_POINT_MAP.put("Ugrave",new Integer(_Ugrave));
		NAME_TO_CODE_POINT_MAP.put("Uacute",new Integer(_Uacute));
		NAME_TO_CODE_POINT_MAP.put("Ucirc",new Integer(_Ucirc));
		NAME_TO_CODE_POINT_MAP.put("Uuml",new Integer(_Uuml));
		NAME_TO_CODE_POINT_MAP.put("Yacute",new Integer(_Yacute));
		NAME_TO_CODE_POINT_MAP.put("THORN",new Integer(_THORN));
		NAME_TO_CODE_POINT_MAP.put("szlig",new Integer(_szlig));
		NAME_TO_CODE_POINT_MAP.put("agrave",new Integer(_agrave));
		NAME_TO_CODE_POINT_MAP.put("aacute",new Integer(_aacute));
		NAME_TO_CODE_POINT_MAP.put("acirc",new Integer(_acirc));
		NAME_TO_CODE_POINT_MAP.put("atilde",new Integer(_atilde));
		NAME_TO_CODE_POINT_MAP.put("auml",new Integer(_auml));
		NAME_TO_CODE_POINT_MAP.put("aring",new Integer(_aring));
		NAME_TO_CODE_POINT_MAP.put("aelig",new Integer(_aelig));
		NAME_TO_CODE_POINT_MAP.put("ccedil",new Integer(_ccedil));
		NAME_TO_CODE_POINT_MAP.put("egrave",new Integer(_egrave));
		NAME_TO_CODE_POINT_MAP.put("eacute",new Integer(_eacute));
		NAME_TO_CODE_POINT_MAP.put("ecirc",new Integer(_ecirc));
		NAME_TO_CODE_POINT_MAP.put("euml",new Integer(_euml));
		NAME_TO_CODE_POINT_MAP.put("igrave",new Integer(_igrave));
		NAME_TO_CODE_POINT_MAP.put("iacute",new Integer(_iacute));
		NAME_TO_CODE_POINT_MAP.put("icirc",new Integer(_icirc));
		NAME_TO_CODE_POINT_MAP.put("iuml",new Integer(_iuml));
		NAME_TO_CODE_POINT_MAP.put("eth",new Integer(_eth));
		NAME_TO_CODE_POINT_MAP.put("ntilde",new Integer(_ntilde));
		NAME_TO_CODE_POINT_MAP.put("ograve",new Integer(_ograve));
		NAME_TO_CODE_POINT_MAP.put("oacute",new Integer(_oacute));
		NAME_TO_CODE_POINT_MAP.put("ocirc",new Integer(_ocirc));
		NAME_TO_CODE_POINT_MAP.put("otilde",new Integer(_otilde));
		NAME_TO_CODE_POINT_MAP.put("ouml",new Integer(_ouml));
		NAME_TO_CODE_POINT_MAP.put("divide",new Integer(_divide));
		NAME_TO_CODE_POINT_MAP.put("oslash",new Integer(_oslash));
		NAME_TO_CODE_POINT_MAP.put("ugrave",new Integer(_ugrave));
		NAME_TO_CODE_POINT_MAP.put("uacute",new Integer(_uacute));
		NAME_TO_CODE_POINT_MAP.put("ucirc",new Integer(_ucirc));
		NAME_TO_CODE_POINT_MAP.put("uuml",new Integer(_uuml));
		NAME_TO_CODE_POINT_MAP.put("yacute",new Integer(_yacute));
		NAME_TO_CODE_POINT_MAP.put("thorn",new Integer(_thorn));
		NAME_TO_CODE_POINT_MAP.put("yuml",new Integer(_yuml));
		NAME_TO_CODE_POINT_MAP.put("fnof",new Integer(_fnof));
		NAME_TO_CODE_POINT_MAP.put("Alpha",new Integer(_Alpha));
		NAME_TO_CODE_POINT_MAP.put("Beta",new Integer(_Beta));
		NAME_TO_CODE_POINT_MAP.put("Gamma",new Integer(_Gamma));
		NAME_TO_CODE_POINT_MAP.put("Delta",new Integer(_Delta));
		NAME_TO_CODE_POINT_MAP.put("Epsilon",new Integer(_Epsilon));
		NAME_TO_CODE_POINT_MAP.put("Zeta",new Integer(_Zeta));
		NAME_TO_CODE_POINT_MAP.put("Eta",new Integer(_Eta));
		NAME_TO_CODE_POINT_MAP.put("Theta",new Integer(_Theta));
		NAME_TO_CODE_POINT_MAP.put("Iota",new Integer(_Iota));
		NAME_TO_CODE_POINT_MAP.put("Kappa",new Integer(_Kappa));
		NAME_TO_CODE_POINT_MAP.put("Lambda",new Integer(_Lambda));
		NAME_TO_CODE_POINT_MAP.put("Mu",new Integer(_Mu));
		NAME_TO_CODE_POINT_MAP.put("Nu",new Integer(_Nu));
		NAME_TO_CODE_POINT_MAP.put("Xi",new Integer(_Xi));
		NAME_TO_CODE_POINT_MAP.put("Omicron",new Integer(_Omicron));
		NAME_TO_CODE_POINT_MAP.put("Pi",new Integer(_Pi));
		NAME_TO_CODE_POINT_MAP.put("Rho",new Integer(_Rho));
		NAME_TO_CODE_POINT_MAP.put("Sigma",new Integer(_Sigma));
		NAME_TO_CODE_POINT_MAP.put("Tau",new Integer(_Tau));
		NAME_TO_CODE_POINT_MAP.put("Upsilon",new Integer(_Upsilon));
		NAME_TO_CODE_POINT_MAP.put("Phi",new Integer(_Phi));
		NAME_TO_CODE_POINT_MAP.put("Chi",new Integer(_Chi));
		NAME_TO_CODE_POINT_MAP.put("Psi",new Integer(_Psi));
		NAME_TO_CODE_POINT_MAP.put("Omega",new Integer(_Omega));
		NAME_TO_CODE_POINT_MAP.put("alpha",new Integer(_alpha));
		NAME_TO_CODE_POINT_MAP.put("beta",new Integer(_beta));
		NAME_TO_CODE_POINT_MAP.put("gamma",new Integer(_gamma));
		NAME_TO_CODE_POINT_MAP.put("delta",new Integer(_delta));
		NAME_TO_CODE_POINT_MAP.put("epsilon",new Integer(_epsilon));
		NAME_TO_CODE_POINT_MAP.put("zeta",new Integer(_zeta));
		NAME_TO_CODE_POINT_MAP.put("eta",new Integer(_eta));
		NAME_TO_CODE_POINT_MAP.put("theta",new Integer(_theta));
		NAME_TO_CODE_POINT_MAP.put("iota",new Integer(_iota));
		NAME_TO_CODE_POINT_MAP.put("kappa",new Integer(_kappa));
		NAME_TO_CODE_POINT_MAP.put("lambda",new Integer(_lambda));
		NAME_TO_CODE_POINT_MAP.put("mu",new Integer(_mu));
		NAME_TO_CODE_POINT_MAP.put("nu",new Integer(_nu));
		NAME_TO_CODE_POINT_MAP.put("xi",new Integer(_xi));
		NAME_TO_CODE_POINT_MAP.put("omicron",new Integer(_omicron));
		NAME_TO_CODE_POINT_MAP.put("pi",new Integer(_pi));
		NAME_TO_CODE_POINT_MAP.put("rho",new Integer(_rho));
		NAME_TO_CODE_POINT_MAP.put("sigmaf",new Integer(_sigmaf));
		NAME_TO_CODE_POINT_MAP.put("sigma",new Integer(_sigma));
		NAME_TO_CODE_POINT_MAP.put("tau",new Integer(_tau));
		NAME_TO_CODE_POINT_MAP.put("upsilon",new Integer(_upsilon));
		NAME_TO_CODE_POINT_MAP.put("phi",new Integer(_phi));
		NAME_TO_CODE_POINT_MAP.put("chi",new Integer(_chi));
		NAME_TO_CODE_POINT_MAP.put("psi",new Integer(_psi));
		NAME_TO_CODE_POINT_MAP.put("omega",new Integer(_omega));
		NAME_TO_CODE_POINT_MAP.put("thetasym",new Integer(_thetasym));
		NAME_TO_CODE_POINT_MAP.put("upsih",new Integer(_upsih));
		NAME_TO_CODE_POINT_MAP.put("piv",new Integer(_piv));
		NAME_TO_CODE_POINT_MAP.put("bull",new Integer(_bull));
		NAME_TO_CODE_POINT_MAP.put("hellip",new Integer(_hellip));
		NAME_TO_CODE_POINT_MAP.put("prime",new Integer(_prime));
		NAME_TO_CODE_POINT_MAP.put("Prime",new Integer(_Prime));
		NAME_TO_CODE_POINT_MAP.put("oline",new Integer(_oline));
		NAME_TO_CODE_POINT_MAP.put("frasl",new Integer(_frasl));
		NAME_TO_CODE_POINT_MAP.put("weierp",new Integer(_weierp));
		NAME_TO_CODE_POINT_MAP.put("image",new Integer(_image));
		NAME_TO_CODE_POINT_MAP.put("real",new Integer(_real));
		NAME_TO_CODE_POINT_MAP.put("trade",new Integer(_trade));
		NAME_TO_CODE_POINT_MAP.put("alefsym",new Integer(_alefsym));
		NAME_TO_CODE_POINT_MAP.put("larr",new Integer(_larr));
		NAME_TO_CODE_POINT_MAP.put("uarr",new Integer(_uarr));
		NAME_TO_CODE_POINT_MAP.put("rarr",new Integer(_rarr));
		NAME_TO_CODE_POINT_MAP.put("darr",new Integer(_darr));
		NAME_TO_CODE_POINT_MAP.put("harr",new Integer(_harr));
		NAME_TO_CODE_POINT_MAP.put("crarr",new Integer(_crarr));
		NAME_TO_CODE_POINT_MAP.put("lArr",new Integer(_lArr));
		NAME_TO_CODE_POINT_MAP.put("uArr",new Integer(_uArr));
		NAME_TO_CODE_POINT_MAP.put("rArr",new Integer(_rArr));
		NAME_TO_CODE_POINT_MAP.put("dArr",new Integer(_dArr));
		NAME_TO_CODE_POINT_MAP.put("hArr",new Integer(_hArr));
		NAME_TO_CODE_POINT_MAP.put("forall",new Integer(_forall));
		NAME_TO_CODE_POINT_MAP.put("part",new Integer(_part));
		NAME_TO_CODE_POINT_MAP.put("exist",new Integer(_exist));
		NAME_TO_CODE_POINT_MAP.put("empty",new Integer(_empty));
		NAME_TO_CODE_POINT_MAP.put("nabla",new Integer(_nabla));
		NAME_TO_CODE_POINT_MAP.put("isin",new Integer(_isin));
		NAME_TO_CODE_POINT_MAP.put("notin",new Integer(_notin));
		NAME_TO_CODE_POINT_MAP.put("ni",new Integer(_ni));
		NAME_TO_CODE_POINT_MAP.put("prod",new Integer(_prod));
		NAME_TO_CODE_POINT_MAP.put("sum",new Integer(_sum));
		NAME_TO_CODE_POINT_MAP.put("minus",new Integer(_minus));
		NAME_TO_CODE_POINT_MAP.put("lowast",new Integer(_lowast));
		NAME_TO_CODE_POINT_MAP.put("radic",new Integer(_radic));
		NAME_TO_CODE_POINT_MAP.put("prop",new Integer(_prop));
		NAME_TO_CODE_POINT_MAP.put("infin",new Integer(_infin));
		NAME_TO_CODE_POINT_MAP.put("ang",new Integer(_ang));
		NAME_TO_CODE_POINT_MAP.put("and",new Integer(_and));
		NAME_TO_CODE_POINT_MAP.put("or",new Integer(_or));
		NAME_TO_CODE_POINT_MAP.put("cap",new Integer(_cap));
		NAME_TO_CODE_POINT_MAP.put("cup",new Integer(_cup));
		NAME_TO_CODE_POINT_MAP.put("int",new Integer(_int));
		NAME_TO_CODE_POINT_MAP.put("there4",new Integer(_there4));
		NAME_TO_CODE_POINT_MAP.put("sim",new Integer(_sim));
		NAME_TO_CODE_POINT_MAP.put("cong",new Integer(_cong));
		NAME_TO_CODE_POINT_MAP.put("asymp",new Integer(_asymp));
		NAME_TO_CODE_POINT_MAP.put("ne",new Integer(_ne));
		NAME_TO_CODE_POINT_MAP.put("equiv",new Integer(_equiv));
		NAME_TO_CODE_POINT_MAP.put("le",new Integer(_le));
		NAME_TO_CODE_POINT_MAP.put("ge",new Integer(_ge));
		NAME_TO_CODE_POINT_MAP.put("sub",new Integer(_sub));
		NAME_TO_CODE_POINT_MAP.put("sup",new Integer(_sup));
		NAME_TO_CODE_POINT_MAP.put("nsub",new Integer(_nsub));
		NAME_TO_CODE_POINT_MAP.put("sube",new Integer(_sube));
		NAME_TO_CODE_POINT_MAP.put("supe",new Integer(_supe));
		NAME_TO_CODE_POINT_MAP.put("oplus",new Integer(_oplus));
		NAME_TO_CODE_POINT_MAP.put("otimes",new Integer(_otimes));
		NAME_TO_CODE_POINT_MAP.put("perp",new Integer(_perp));
		NAME_TO_CODE_POINT_MAP.put("sdot",new Integer(_sdot));
		NAME_TO_CODE_POINT_MAP.put("lceil",new Integer(_lceil));
		NAME_TO_CODE_POINT_MAP.put("rceil",new Integer(_rceil));
		NAME_TO_CODE_POINT_MAP.put("lfloor",new Integer(_lfloor));
		NAME_TO_CODE_POINT_MAP.put("rfloor",new Integer(_rfloor));
		NAME_TO_CODE_POINT_MAP.put("lang",new Integer(_lang));
		NAME_TO_CODE_POINT_MAP.put("rang",new Integer(_rang));
		NAME_TO_CODE_POINT_MAP.put("loz",new Integer(_loz));
		NAME_TO_CODE_POINT_MAP.put("spades",new Integer(_spades));
		NAME_TO_CODE_POINT_MAP.put("clubs",new Integer(_clubs));
		NAME_TO_CODE_POINT_MAP.put("hearts",new Integer(_hearts));
		NAME_TO_CODE_POINT_MAP.put("diams",new Integer(_diams));
		NAME_TO_CODE_POINT_MAP.put("quot",new Integer(_quot));
		NAME_TO_CODE_POINT_MAP.put("amp",new Integer(_amp));
		NAME_TO_CODE_POINT_MAP.put("lt",new Integer(_lt));
		NAME_TO_CODE_POINT_MAP.put("gt",new Integer(_gt));
		NAME_TO_CODE_POINT_MAP.put("OElig",new Integer(_OElig));
		NAME_TO_CODE_POINT_MAP.put("oelig",new Integer(_oelig));
		NAME_TO_CODE_POINT_MAP.put("Scaron",new Integer(_Scaron));
		NAME_TO_CODE_POINT_MAP.put("scaron",new Integer(_scaron));
		NAME_TO_CODE_POINT_MAP.put("Yuml",new Integer(_Yuml));
		NAME_TO_CODE_POINT_MAP.put("circ",new Integer(_circ));
		NAME_TO_CODE_POINT_MAP.put("tilde",new Integer(_tilde));
		NAME_TO_CODE_POINT_MAP.put("ensp",new Integer(_ensp));
		NAME_TO_CODE_POINT_MAP.put("emsp",new Integer(_emsp));
		NAME_TO_CODE_POINT_MAP.put("thinsp",new Integer(_thinsp));
		NAME_TO_CODE_POINT_MAP.put("zwnj",new Integer(_zwnj));
		NAME_TO_CODE_POINT_MAP.put("zwj",new Integer(_zwj));
		NAME_TO_CODE_POINT_MAP.put("lrm",new Integer(_lrm));
		NAME_TO_CODE_POINT_MAP.put("rlm",new Integer(_rlm));
		NAME_TO_CODE_POINT_MAP.put("ndash",new Integer(_ndash));
		NAME_TO_CODE_POINT_MAP.put("mdash",new Integer(_mdash));
		NAME_TO_CODE_POINT_MAP.put("lsquo",new Integer(_lsquo));
		NAME_TO_CODE_POINT_MAP.put("rsquo",new Integer(_rsquo));
		NAME_TO_CODE_POINT_MAP.put("sbquo",new Integer(_sbquo));
		NAME_TO_CODE_POINT_MAP.put("ldquo",new Integer(_ldquo));
		NAME_TO_CODE_POINT_MAP.put("rdquo",new Integer(_rdquo));
		NAME_TO_CODE_POINT_MAP.put("bdquo",new Integer(_bdquo));
		NAME_TO_CODE_POINT_MAP.put("dagger",new Integer(_dagger));
		NAME_TO_CODE_POINT_MAP.put("Dagger",new Integer(_Dagger));
		NAME_TO_CODE_POINT_MAP.put("permil",new Integer(_permil));
		NAME_TO_CODE_POINT_MAP.put("lsaquo",new Integer(_lsaquo));
		NAME_TO_CODE_POINT_MAP.put("rsaquo",new Integer(_rsaquo));
		NAME_TO_CODE_POINT_MAP.put("euro",new Integer(_euro));
		NAME_TO_CODE_POINT_MAP.put("apos",new Integer(_apos));
		
		CODE_POINT_TO_NAME_MAP=new IntStringHashMap(NAME_TO_CODE_POINT_MAP.size());
		for (Iterator i=NAME_TO_CODE_POINT_MAP.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry=(Map.Entry)i.next();
			String name=(String)entry.getKey();
			if (MAX_NAME_LENGTH<name.length()) MAX_NAME_LENGTH=name.length();
			CODE_POINT_TO_NAME_MAP.put(((Integer)entry.getValue()).intValue(),name);
		}
	}

	private CharacterEntityReference(Source source, int begin, int end, int codePoint) {
		super(source,begin,end,codePoint);
		name=getName(codePoint);
	}
	
	/**
	 * Returns the name of this character entity reference.
	 * <p>
	 * <dl>
	 *  <dt><b>Example:</b></dt>
	 *  <dd><code>((CharacterEntityReference)CharacterReference.parse("&amp;gt;")).getName()</code> returns "<code>gt</code>"</dd>
	 * </dl>
	 * @return  the name of this character entity reference.
	 * @see  #getName(int codePoint)
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the character entity reference name of the specified character.
	 * <p>
	 * Since all character entity references represent Unicode <a target="_blank" href="http://www.unicode.org/glossary/#bmp_code_point">BMP</a> code points,
	 * the functionality of this method is identical to that of {@link #getName(int codePoint)}. 
	 * <p>
	 * <dl>
	 *  <dt><b>Example:</b></dt>
	 *  <dd><code>CharacterEntityReference.getName('>')</code> returns "<code>gt</code>"</dd>
	 * </dl>
	 * @return  the character entity reference name of the specified character, or <code>null</code> if none exists.
	 */
	public static String getName(char ch) {
		return getName((int)ch);
	}

	/**
	 * Returns the character entity reference name of the specified Unicode code point.
	 * <p>
	 * Since all character entity references represent Unicode <a target="_blank" href="http://www.unicode.org/glossary/#bmp_code_point">BMP</a> code points,
	 * the functionality of this method is identical to that of {@link #getName(char ch)}. 
	 * <p>
	 * <dl>
	 *  <dt><b>Example:</b></dt>
	 *  <dd><code>CharacterEntityReference.getName(62)</code> returns "<code>gt</code>"</dd>
	 * </dl>
	 * @return  the character entity reference name of the specified Unicode code point, or <code>null</code> if none exists.
	 */
	public static String getName(int codePoint) {
		return CODE_POINT_TO_NAME_MAP.get(codePoint);
	}

	/**
	 * Returns the Unicode code point of the specified character entity reference name.
	 * <p>
 	 * If the string does not represent a valid character entity reference name, this method returns {@link #INVALID_CODE_POINT INVALID_CODE_POINT}.
	 * <p>
	 * Although character entity reference names are case sensitive, and in some cases differ from other entity references only by their case,
	 * some browsers will also recognise them in a case-insensitive way.
	 * For this reason, all decoding methods in this library will recognise character entity reference names even if they are in the wrong case.
	 * <p>
	 * <dl>
	 *  <dt><b>Example:</b></dt>
	 *  <dd><code>CharacterEntityReference.getCodePointFromName("gt")</code> returns <code>62</code></dd>
	 * </dl>
	 * @return  the Unicode code point of the specified character entity reference name, or {@link #INVALID_CODE_POINT INVALID_CODE_POINT} if the string does not represent a valid character entity reference name.
	 */
	public static int getCodePointFromName(String name) {
		Integer codePoint=(Integer)NAME_TO_CODE_POINT_MAP.get(name);
		if (codePoint==null) {
			// Most browsers will recognise character entity references even if they have the wrong case, so check for this as well:
			String lowerCaseName=name.toLowerCase();
			if (lowerCaseName!=name) codePoint=(Integer)NAME_TO_CODE_POINT_MAP.get(lowerCaseName);
		}
		return (codePoint!=null) ? codePoint.intValue() : INVALID_CODE_POINT;
	}
	
	/**
	 * Returns the correct encoded form of this character entity reference.
	 * <p>
	 * Note that the returned string is not necessarily the same as the original source text used to create this object.
	 * This library will recognise certain invalid forms of character references, as detailed in the {@link #decode(String) decode(String encodedString)} method.
	 * <p>
	 * To retrieve the original source text, use the {@link #getSourceText() getSourceText()} method instead.
	 * <p>
	 * <dl>
	 *  <dt><b>Example:</b></dt>
	 *   <dd><code>CharacterReference.parse("&amp;GT").getCharacterReferenceString()</code> returns "<code>&amp;gt;</code>"</dd>
	 * </dl>
	 * 
	 * @return  the correct encoded form of this character entity reference.
	 * @see  CharacterReference#getCharacterReferenceString(int codePoint)
	 */
	public String getCharacterReferenceString() {
		return getCharacterReferenceString(name);
	}

	/**
	 * Returns the character entity reference encoded form of the specified Unicode code point.
	 * <p>
	 * If the specified code point does not have an equivalent character entity reference, this method returns <code>null</code>.
	 * To get either the entity or numeric reference encoded form, use the {@link CharacterReference#getCharacterReferenceString(int codePoint)} method instead.
	 * <p>
	 * <dl>
	 *  <dt><b>Examples:</b></dt>
	 *   <dd><code>CharacterEntityReference.getCharacterReferenceString(62)</code> returns "<code>&amp;gt;</code>"</dd>
	 *   <dd><code>CharacterEntityReference.getCharacterReferenceString(9786)</code> returns <code>null</code></dd>
	 * </dl>
	 * 
	 * @return  the character entity reference encoded form of the specified Unicode code point, or <code>null</code> if none exists.
	 * @see  CharacterReference#getCharacterReferenceString(int codePoint)
	 */
	public static String getCharacterReferenceString(int codePoint) {
		if (codePoint>Character.MAX_VALUE) return null;
		String name=getName(codePoint);
		return name!=null ? getCharacterReferenceString(name) : null;
	}

	/**
	 * Returns a <code>Map</code> of character entity reference names (<code>String</code>) to code points (<code>Integer</code>).
	 * @return  a <code>Map</code> of character entity reference names to code points.
	 **/
	public static Map getNameToCodePointMap() {
		return NAME_TO_CODE_POINT_MAP;
	}
	
	private static String getCharacterReferenceString(String name) {
		return appendCharacterReferenceString(new StringBuffer(),name).toString();
	}

	static final StringBuffer appendCharacterReferenceString(StringBuffer sb, String name) {
		return sb.append('&').append(name).append(';');
	}
	
	static CharacterReference construct(Source source, int begin) {
		// only called from CharacterReference.construct(), so we can assume that first character is '&'
		String sourceText=source.text;
		String name;
		int nameBegin=begin+1;
		int maxNameEnd=nameBegin+MAX_NAME_LENGTH;
		int maxSourcePos=sourceText.length()-1;
		int end;
		int x=nameBegin;
		boolean invalidTermination=false;
		while (true) {
			char ch=sourceText.charAt(x);
			if (ch==';') {
				end=x+1;
				name=sourceText.substring(nameBegin,x);
				break;
			}
			if (!isValidNameCharacter(ch)) {
				// At this point, ch is determined to be an invalid character, meaning the source document is not valid HTML.
				invalidTermination=true;
			} else if (x==maxSourcePos) {
				// At this point, we have a valid name character but are at the last position in the source text without the terminating semicolon.
				// treat this the same as hitting an invalid name character.
				invalidTermination=true;
				x++; // include this character in the name
			}
			if (invalidTermination) {
				// In this situation we are free to either reject the character entity reference outright, or try to resolve it anyway as some browsers do.
				// IE will reject "uncommon" references in this situation (even if followed by whitespace), but accept "common" ones, even if they have no semicolon and are followed by more alphabetic characters. (eg "&lta" resolves to "<a").
				// We will allow any invalid character to terminate the character entity reference. 
				end=x;
				name=sourceText.substring(nameBegin,x);
				break;
			}
			if (++x>maxNameEnd) return null;
		}
		int codePoint=getCodePointFromName(name);
		return (codePoint==INVALID_CODE_POINT) ? null : new CharacterEntityReference(source,begin,end,codePoint);
	}
	
	private static final boolean isValidNameCharacter(char ch) {
		return ch>='A' && ch<='z' && (ch<='Z' || ch>='a');
	}
	
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append('"');
		appendCharacterReferenceString(sb,name);
		sb.append("\" ");
		appendUnicodeText(sb,codePoint);
		sb.append(' ').append(super.toString());
		return sb.toString();
	}
}

