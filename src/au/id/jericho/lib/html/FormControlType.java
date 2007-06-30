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
 * Represents one of the HTML <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.2.1">control types</a> in a <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html">form</a>
 * which have the potential to be <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#successful-controls">successful</a>.
 * This means that they can contribute name/value pairs to the <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#form-data-set">form data set</a> when the form is <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#submit-format">submitted</a>.
 * <p>
 * Each type of control has a certain behaviour in regards to the name/value pairs submitted.
 * This class defines that behaviour, so that the meaning of the name/value pairs submitted from an arbitrary HTML page can be determined.
 *
 * @see FormField
 * @see FormFields
 */
public final class FormControlType {
	private String formControlTypeId;
	private String tagName;
	private boolean allowsMultipleValues;
	private boolean predefinedValue;

	private static HashMap map=new HashMap();
	private static HashSet tagNames=new HashSet();

	/**
	 * <code>&lt;<a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.4.1">input</a> type="text" name="FieldName" value="DefaultValue" /&gt;</code>.
	 * <p>
	 * <code>{@link #getFormControlTypeId()} = "text"</code><br />
	 * <code>{@link #getTagName()} = "input"</code><br />
	 * <code>{@link #allowsMultipleValues()} = true</code><br />
	 * <code>{@link #isPredefinedValue()} = false</code><br />
	 * <code>{@link #isSubmit()} = false</code><br />
	 * <code>{@link #getAdditionalSubmitNames(String) getAdditionalSubmitNames("<i>name</i>")} = null</code><br />
	 */
	public static final FormControlType TEXT=construct(new FormControlType("text","input",true,false));

	/**
	 * <code>&lt;<a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.4.1">input</a> type="password" name="FieldName" value="DefaultValue" /&gt;</code>.
	 * <p>
	 * <code>{@link #getFormControlTypeId()} = "password"</code><br />
	 * <code>{@link #getTagName()} = "input"</code><br />
	 * <code>{@link #allowsMultipleValues()} = true</code><br />
	 * <code>{@link #isPredefinedValue()} = false</code><br />
	 * <code>{@link #isSubmit()} = false</code><br />
	 * <code>{@link #getAdditionalSubmitNames(String) getAdditionalSubmitNames("<i>name</i>")} = null</code><br />
	 */
	public static final FormControlType PASSWORD=construct(new FormControlType("password","input",true,false));

	/**
	 * <code>&lt;<a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.4.1">input</a> type="hidden" name="FieldName" value="PredefinedValue" /&gt;</code>.
	 * <p>
	 * <code>{@link #getFormControlTypeId()} = "hidden"</code><br />
	 * <code>{@link #getTagName()} = "input"</code><br />
	 * <code>{@link #allowsMultipleValues()} = true</code><br />
	 * <code>{@link #isPredefinedValue()} = false</code><br />
	 * <code>{@link #isSubmit()} = false</code><br />
	 * <code>{@link #getAdditionalSubmitNames(String) getAdditionalSubmitNames("<i>name</i>")} = null</code><br />
	 * <p>
	 * Note that {@link #isPredefinedValue()} returns false for this control type because the value of hidden fields is usually set via server or client side scripting.
	 */
	public static final FormControlType HIDDEN=construct(new FormControlType("hidden","input",true,false));

	/**
	 * <code>&lt;<a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.7">textarea</a> name="FieldName"&gt;Default Value&lt;/textarea&gt;</code>.
	 * <p>
	 * <code>{@link #getFormControlTypeId()} = "textarea"</code><br />
	 * <code>{@link #getTagName()} = "textarea"</code><br />
	 * <code>{@link #allowsMultipleValues()} = true</code><br />
	 * <code>{@link #isPredefinedValue()} = false</code><br />
	 * <code>{@link #isSubmit()} = false</code><br />
	 * <code>{@link #getAdditionalSubmitNames(String) getAdditionalSubmitNames("<i>name</i>")} = null</code><br />
	 */
	public static final FormControlType TEXTAREA=construct(new FormControlType("textarea","textarea",true,false));

	/**
	 * <code>&lt;<a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.4.1">input</a> type="checkbox" name="FieldName" value="PredefinedValue" /&gt;</code>.
	 * <p>
	 * <code>{@link #getFormControlTypeId()} = "checkbox"</code><br />
	 * <code>{@link #getTagName()} = "input"</code><br />
	 * <code>{@link #allowsMultipleValues()} = true</code><br />
	 * <code>{@link #isPredefinedValue()} = true</code><br />
	 * <code>{@link #isSubmit()} = false</code><br />
	 * <code>{@link #getAdditionalSubmitNames(String) getAdditionalSubmitNames("<i>name</i>")} = null</code><br />
	 */
	public static final FormControlType CHECKBOX=construct(new FormControlType("checkbox","input",true,true));

	/**
	 * <code>&lt;<a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.4.1">input</a> type="radio" name="FieldName" value="PredefinedValue" /&gt;</code>.
	 * <p>
	 * <code>{@link #getFormControlTypeId()} = "radio"</code><br />
	 * <code>{@link #getTagName()} = "input"</code><br />
	 * <code>{@link #allowsMultipleValues()} = false</code><br />
	 * <code>{@link #isPredefinedValue()} = true</code><br />
	 * <code>{@link #isSubmit()} = false</code><br />
	 * <code>{@link #getAdditionalSubmitNames(String) getAdditionalSubmitNames("<i>name</i>")} = null</code><br />
	 */
	public static final FormControlType RADIO=construct(new FormControlType("radio","input",false,true));

	/**
	 * <code>&lt;<a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.4.1">input</a> type="file" name="FieldName" value="DefaultFileName" /&gt;</code>.
	 * <p>
	 * <code>{@link #getFormControlTypeId()} = "file"</code><br />
	 * <code>{@link #getTagName()} = "input"</code><br />
	 * <code>{@link #allowsMultipleValues()} = true</code><br />
	 * <code>{@link #isPredefinedValue()} = false</code><br />
	 * <code>{@link #isSubmit()} = false</code><br />
	 * <code>{@link #getAdditionalSubmitNames(String) getAdditionalSubmitNames("<i>name</i>")} = null</code><br />
	 */
	public static final FormControlType FILE=construct(new FormControlType("file","input",true,false));

	/**
	 * <code>&lt;<a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.5">button</a> type="submit" name="FieldName" value="PredefinedValue" /&gt;</code>.
	 * <p>
	 * <code>{@link #getFormControlTypeId()} = "button"</code><br />
	 * <code>{@link #getTagName()} = "button"</code><br />
	 * <code>{@link #allowsMultipleValues()} = false</code><br />
	 * <code>{@link #isPredefinedValue()} = true</code><br />
	 * <code>{@link #isSubmit()} = true</code><br />
	 * <code>{@link #getAdditionalSubmitNames(String) getAdditionalSubmitNames("<i>name</i>")} = null</code><br />
	 */
	public static final FormControlType BUTTON=construct(new FormControlType("button","button",false,true));

	/**
	 * <code>&lt;<a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.4.1">input</a> type="submit" name="FieldName" value="PredefinedValue" /&gt;</code>.
	 * <p>
	 * <code>{@link #getFormControlTypeId()} = "submit"</code><br />
	 * <code>{@link #getTagName()} = "input"</code><br />
	 * <code>{@link #allowsMultipleValues()} = false</code><br />
	 * <code>{@link #isPredefinedValue()} = true</code><br />
	 * <code>{@link #isSubmit()} = true</code><br />
	 * <code>{@link #getAdditionalSubmitNames(String) getAdditionalSubmitNames("<i>name</i>")} = null</code><br />
	 */
	public static final FormControlType SUBMIT=construct(new FormControlType("submit","input",false,true));

	/**
	 * <code>&lt;<a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.4.1">input</a> type="image" name="FieldName" src="ImageURL" value="PredefinedValue" /&gt;</code>.
	 * <p>
	 * <code>{@link #getFormControlTypeId()} = "image"</code><br />
	 * <code>{@link #getTagName()} = "input"</code><br />
	 * <code>{@link #allowsMultipleValues()} = false</code><br />
	 * <code>{@link #isPredefinedValue()} = true</code><br />
	 * <code>{@link #isSubmit()} = true</code><br />
	 * <code>{@link #getAdditionalSubmitNames(String) getAdditionalSubmitNames("<i>name</i>")} = {"<i>name</i>.x","<i>name</i>.y"}</code><br />
	 */
	public static final FormControlType IMAGE=construct(new FormControlType("image","input",false,true));

	/**
	 * <code>&lt;<a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.6">select</a> name="FieldName"&gt; &lt;option value="PredefinedValue"&gt;Display Text&lt;/option&gt; &lt;/select&gt;</code>.
	 * <p>
	 * <code>{@link #getFormControlTypeId()} = "select_single"</code><br />
	 * <code>{@link #getTagName()} = "select"</code><br />
	 * <code>{@link #allowsMultipleValues()} = false</code><br />
	 * <code>{@link #isPredefinedValue()} = true</code><br />
	 * <code>{@link #isSubmit()} = false</code><br />
	 * <code>{@link #getAdditionalSubmitNames(String) getAdditionalSubmitNames("<i>name</i>")} = null</code><br />
	 */
	public static final FormControlType SELECT_SINGLE=construct(new FormControlType("select_single","select",false,true));

	/**
	 * <code>&lt;<a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.6">select</a> name="FieldName" multiple&gt; &lt;option value="PredefinedValue"&gt;Display Text&lt;/option&gt; &lt;/select&gt;</code>.
	 * <p>
	 * <code>{@link #getFormControlTypeId()} = "select_multiple"</code><br />
	 * <code>{@link #getTagName()} = "select"</code><br />
	 * <code>{@link #allowsMultipleValues()} = true</code><br />
	 * <code>{@link #isPredefinedValue()} = true</code><br />
	 * <code>{@link #isSubmit()} = false</code><br />
	 * <code>{@link #getAdditionalSubmitNames(String) getAdditionalSubmitNames("<i>name</i>")} = null</code><br />
	 */
	public static final FormControlType SELECT_MULTIPLE=construct(new FormControlType("select_multiple","select",true,true));

	private static FormControlType construct(FormControlType formControlType) {
		map.put(formControlType.formControlTypeId,formControlType);
		tagNames.add(formControlType.tagName);
		return formControlType;
	}

	private FormControlType(String formControlTypeId, String tagName, boolean allowsMultipleValues, boolean predefinedValue) {
		this.formControlTypeId=formControlTypeId;
		this.tagName=tagName;
		this.allowsMultipleValues=allowsMultipleValues;
		this.predefinedValue=predefinedValue;
	}

	/**
	 * Returns a string which identifies this form control type.
	 * <p>
	 * This is the same as the control type's static field name in lower case.
	 *
	 * @return  a string which identified this form control type.
	 */
	public String getFormControlTypeId() {
		return formControlTypeId;
	}

	/**
	 * Returns the {@linkplain StartTag#getName() name} of the tag that defines this form control type.
	 * @return  the name of the tag that defines this form control type.
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * Indicates whether more than one control of this type with the same <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#control-name">name</a> can be <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#successful-controls">successful</a>.
	 * @return  <code>true</code> if more than one control of this type with the same <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#control-name">name</a> can be <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#successful-controls">successful</a>, otherwise <code>false</code>.
	 */
	public boolean allowsMultipleValues() {
		return allowsMultipleValues;
	}

	/**
	 * Indicates whether the <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#current-value">value</a> submitted by this type of control is predefined in the HTML and typically not modified by the user or server/client scripts.
	 * @return  <code>true</code> if the <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#current-value">value</a> submitted by this type of control is predefined in the HTML and typically not modified by the user or server/client scripts, otherwise <code>false</code>.
	 */
	public boolean isPredefinedValue() {
		return predefinedValue;
	}

	/**
	 * Indicates whether this control type causes the form to be <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#submit-format">submitted</a>.
	 * @return  <code>true</code> if this control type causes the form to be <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#submit-format">submitted</a>, otherwise <code>false</code>.
	 */
	public boolean isSubmit() {
		return this==SUBMIT || this==BUTTON || this==IMAGE;
	}

	/**
	 * Returns an array containing the additional field names submitted if a control of this type with the specified <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#control-name">name</a> is <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#successful-controls">successful</a>.
	 * <p>
	 * This method returns <code>null</code> for all control types except {@link #IMAGE}.
	 * It relates to the extra <code><i>name</i>.x</code> and <code><i>name</i>.y</code> data submitted when a pointing device is used to activate an IMAGE control.
	 * @param  name  the <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#control-name">name</a> of a form control.
	 * @return  an array containing the additional field names submitted if a control of this type with the specified <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#control-name">name</a> is <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#successful-controls">successful</a>, or <code>null</code> if none.
	 */
	public String[] getAdditionalSubmitNames(String name) {
		if (this!=IMAGE) return null;
		String[] names=new String[2];
		names[0]=name+".x";
		names[1]=name+".y";
		return names;
	}

	/**
	 * Returns the {@link FormControlType} with the specified ID.
	 * @param  formControlTypeId  the ID of a form control type.
	 * @return  the {@link FormControlType} with the specified ID, or <code>null</code> if no such control exists.
	 */
	public static FormControlType get(String formControlTypeId) {
		return (FormControlType)map.get(formControlTypeId);
	}

	/**
	 * Indicates whether an HTML tag with the specified name is potentially a form <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.2">control</a>.
	 * @param  tagName  the name of an HTML tag.
	 * @return  <code>true</code> if an HTML tag with the specified name is potentially a form <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.2">control</a>, otherwise <code>false</code>.
	 */
	public static boolean isPotentialControl(String tagName) {
		return tagNames.contains(tagName);
	}

	/**
	 * Returns a string representation of this object useful for debugging purposes.
	 * @return  a string representation of this object useful for debugging purposes.
	 */
	public String toString() {
		return "formControlTypeId="+formControlTypeId+", tagName="+tagName+", allowsMultipleValues="+allowsMultipleValues+", predefinedValue="+predefinedValue;
	}
}
