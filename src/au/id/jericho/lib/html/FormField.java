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
 * Represents a <em>field</em> in an HTML <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html">form</a>,
 * a <em>field</em> being defined as the combination of all <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.2">controls</a>
 * in the form having the same <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#control-name">name</a>.
 * The properties of a FormField object describe how the values associated with a particular <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#control-name">name</a>
 * in a <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#form-data-set">form data set</a>
 * should be interpreted.
 * These properties include whether multiple values can be expected, the number of values which would typically
 * be set by the user, and a list of values which are predefined in the HTML.
 * This information allows the server to store and format the data in an appropriate way.
 * <p>
 * A form field which allows user values will normally consist of a single control whose {@link FormControlType#isPredefinedValue()} method
 * returns <code>false</code>, such as a {@link FormControlType#TEXT TEXT} control.
 * <p>
 * When a form field consists of more than one control, these controls will normally be all be of the same {@link FormControlType} which has predefined values, such as the {@link FormControlType#CHECKBOX CHECKBOX} control.
 * <p>
 * Form fields consisting of more than one control do not necessarily return multiple values.
 * A form field consisting of {@link FormControlType#CHECKBOX CHECKBOX} controls can return multiple values, whereas
 * a form field consisting of {@link FormControlType#CHECKBOX RADIO} controls will return at most one value.
 * <p>
 * The HTML designer can disregard convention and mix all types of controls with the same name in the same form,
 * or include multiple controls of the same name which do not have predefined values.  This library will attempt to make sense of such
 * situations, although it is up to the developer using the library to decide how to handle the received data.
 * <p>
 * FormField objects are usually created by calling the {@link Segment#findFormFields() findFormFields()} method on a form {@link Element} or a {@link Source} object.
 *
 * @see FormFields
 * @see FormControlType
 */
public final class FormField {
	private String name;
	private int userValueCount=0;
	private boolean allowsMultipleValues=false;
	private LinkedHashSet predefinedValues=null;
	private int position;

	private transient FormControlType firstEncounteredFormControlType=null;
	private transient boolean updateable=false;

	/** only used in FormFields class */
	static Comparator COMPARATOR=new PositionComparator();

	/** Constructor called from FormFields class. */
	FormField(String name, int position, FormControlType formControlType) {
		this.name=name;
		this.position=position;
		firstEncounteredFormControlType=formControlType;
		updateable=true;
	}

	/**
	 * Returns the <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#control-name">name</a> of the field.
	 * @return  the <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#control-name">name</a> of the field.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the number of values which would typically be set by the user, and are not included in the list of {@linkplain #getPredefinedValues() predefined values}.
	 * This should in most cases be either 0 or 1.
	 * The word "typically" is used because the use of scripts can cause <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#h-17.2.1">control types</a>
	 * which normally have predefined values to be set by the user, which is a condition which is beyond the scope of this library to test for.
	 * <p>
	 * A value of 0 indicates the field values will consist only of {@linkplain #getPredefinedValues() predefined values}.
	 * This is the case when the field consists of only
	 * {@link FormControlType#CHECKBOX CHECKBOX}, {@link FormControlType#RADIO RADIO}, {@link FormControlType#BUTTON BUTTON},
	 * {@link FormControlType#SUBMIT SUBMIT}, {@link FormControlType#IMAGE IMAGE}, {@link FormControlType#SELECT_SINGLE SELECT_SINGLE}
	 * and {@link FormControlType#SELECT_MULTIPLE SELECT_MULTIPLE} form control types.
	 * <p>
	 * A value of 1 indicates the field values will consist of at most 1 value set by the user.
	 * It is still possible to receive multiple values, but any others should consist only of {@linkplain #getPredefinedValues() predefined values},
	 * and this situation will only arise if the HTML designer has mixed different control types with the same name.
	 * <p>
	 * A value greater than 1 indicates that the HTML designer has included multiple controls of the same name which do not have predefined values.
	 *
	 * @return  the number of values which would typically be set by the user.
	 */
	public int getUserValueCount() {
		return userValueCount;
	}

	/**
	 * Indicates whether the field allows multiple values.
	 * <p>
	 * This will be true if the field consists of more than one control, and one of the following conditions is met:
	 * <ul>
	 * <li>Any one of the controls allows user values ({@link FormControlType#isPredefinedValue()}<code>==false</code>)
	 * <li>Any one of the controls allows multiple values ({@link FormControlType#allowsMultipleValues()}<code>==true</code>)
	 * <li>Not all of the controls are of the same type <super>*</super>
	 * </ul>
	 * <p>
	 * <super>*</super> Note that for the purposes of this comparison, all control types which cause the form to be submitted
	 * ({@link FormControlType#isSubmit()}<code>==true</code>) are considered to be the same type.
	 *
	 * @return  <code>true</code> if the field allows multiple values, otherwise <code>false</code>.
	 */
	public boolean allowsMultipleValues() {
		return allowsMultipleValues;
	}

	/**
	 * Returns a java.util.Collection containing the predefined values of all controls in this field.
	 * A control only has a predefined value if {@link FormControlType#isPredefinedValue()}<code>==true</code>.
	 * Its value is defined by its <a target="_blank" href="http://www.w3.org/TR/html4/interact/forms.html#initial-value">initial value</a>.
	 * <p>
	 * An interator over this collection will return the values in the order of appearance in the source.
	 *
	 * @return  a java.util.Collection containing the predefined values of all controls in this field, or <code>null</code> if none.
	 */
	public Collection getPredefinedValues() {
		return predefinedValues;
	}

	/**
	 * Returns a string representation of this object useful for debugging purposes.
	 * @return  a string representation of this object useful for debugging purposes.
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("Field: ").append(name).append(", UserValueCount=").append(userValueCount).append(", AllowsMultipleValues=").append(allowsMultipleValues);
		//sb.append(", fefct=").append(firstEncounteredFormControlType==null ? "null" : firstEncounteredFormControlType.getName());
		if (predefinedValues!=null) {
			for (Iterator i=predefinedValues.iterator(); i.hasNext();) {
				sb.append("\nPredefinedValue: ");
				sb.append(i.next());
			}
		}
		sb.append("\n\n");
		return sb.toString();
	}

	/** only called from FormFields class */
	void setLowerPosition(int position) {
		if (!updateable) throw new NoLongerUpdateableException();
		if (position<this.position) this.position=position;
	}

	/** only called from FormFields class */
	void incrementUserValueCount() {
		userValueCount++;
	}

	/** only called from FormFields class */
	void addPredefinedValue(String predefinedValue) {
		if (!updateable) throw new NoLongerUpdateableException();
		if (predefinedValues==null) predefinedValues=new LinkedHashSet();
		predefinedValues.add(predefinedValue);
	}

	/** only called from FormFields class */
	void setMultipleValues() {
		if (!updateable) throw new NoLongerUpdateableException();
		allowsMultipleValues=true;
	}

	/** only called from FormFields class */
	void setMultipleValues(FormControlType formControlType) {
		if (!updateable) throw new NoLongerUpdateableException();
		if (!allowsMultipleValues) allowsMultipleValues=calculateMultipleValues(formControlType);
	}

	private boolean calculateMultipleValues(FormControlType formControlType) {
		if (userValueCount>0 || formControlType.allowsMultipleValues() || firstEncounteredFormControlType.allowsMultipleValues()) return true;
		if (formControlType==firstEncounteredFormControlType) return false;
		if (formControlType.isSubmit() && firstEncounteredFormControlType.isSubmit()) return false;
		return true;
	}

	/** only called from FormFields class */
	void merge(FormField formField) {
		updateable=false;
		formField.updateable=false;
		if (formField.userValueCount>userValueCount) userValueCount=formField.userValueCount;
		allowsMultipleValues=allowsMultipleValues || formField.allowsMultipleValues;
		if (predefinedValues==null) {
			predefinedValues=formField.predefinedValues;
		} else if (formField.predefinedValues!=null) {
			for (Iterator i=formField.getPredefinedValues().iterator(); i.hasNext();)
				predefinedValues.add(i.next());
		}
	}

	private static class PositionComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			if (!(o1 instanceof FormField && o2 instanceof FormField)) throw new ClassCastException();
			FormField formField1=(FormField)o1;
			FormField formField2=(FormField)o2;
			if (formField1.position<formField2.position) return -1;
			if (formField1.position>formField2.position) return 1;
			return formField1.name.compareTo(formField2.name);
		}
	}

	private static class NoLongerUpdateableException extends RuntimeException {
		public NoLongerUpdateableException() {
			super("Internal Error: FormField objects are no longer updateable after merge or deserialisation");
		}
	}
}

