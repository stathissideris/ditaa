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
 * Represents all of the {@link FormField} objects within a given {@link Segment}.
 * <p>
 * FormFields objects are created by calling the {@link Segment#findFormFields()} method.
 * <p>
 * The following example gets the FormFields for the form named "MyForm" in the document <code>htmlString</code>:
 * <pre>
 *  Element myForm=null;
 *  List formElements=new Source(htmlString).findAllElements("form");
 *  for (Iterator i=formElements.iterator(); i.hasNext();) {
 *    Element form=(Element)i.next();
 *    Attributes formAttributes=form.getStartTag().getAttributes();
 *    if (formAttributes!=null) {
 *      if ("MyForm".equals(formAttributes.get("name"))) {
 *      myForm=form;
 *      break;
 *    }
 *  }
 *  FormFields formFields=myForm.findFormFields();
 * </pre>
 * @see FormField
 */
public final class FormFields {
	private HashMap map=new HashMap();
	private ArrayList list=new ArrayList();

	private FormFields() {}

	/** only called from Segment class */
	static FormFields construct(Segment segment) {
		FormFields formFields=new FormFields();
		formFields.loadInputControls(segment);
		formFields.loadTextAreaControls(segment);
		formFields.loadButtonControls(segment);
		formFields.loadSelectControls(segment);
		Collections.sort(formFields.list,FormField.COMPARATOR);
		return formFields;
	}

	/**
	 * Returns the number of FormField objects in the Segment.
	 * @return  the number of FormField objects in the Segment.
	 */
	public int getCount() {
		return list.size();
	}

	/**
	 * Returns the number of FormField objects in the Segment.
	 * <p>
	 * This is equivalent to calling <code>getCount()</code>, and is only provided for consistency with the <code>java.util.Collection</code> interface.
	 * @return  the number of FormField objects in the Segment.
	 * @see  #getCount()
	 */
	public int size() {
		return getCount();
	}

	/**
	 * Returns the FormField with the specified name (case insensitive).
	 *
	 * @param  name  the name of the FormField to get.
	 * @return  the FormField with the specified name; null if no FormField with the specified name exists.
	 */
	public FormField get(String name) {
		return (FormField)map.get(name.toLowerCase());
	}

	/**
	 * Returns an iterator over the {@link FormField} objects in the list.
	 * @return  an iterator over the {@link FormField} objects in the list.
	 */
	public Iterator iterator() {
		return list.iterator();
	}

	/**
	 * Merges the specified FormFields into this FormFields collection.
	 * This is useful if a full list of possible FormFields is required from multiple {@link Source} documents.
	 * <p>
	 * If FormField objects with the same name appear in both collections, the resulting FormField will have the following properties:
	 * <ul>
	 * <li>{@link FormField#allowsMultipleValues() allowsMultipleValues} : <code>true</code> if either FormField allows multiple values</li>
	 * <li>{@link FormField#getPredefinedValues() getPredefinedValues} : the union of predefined values in both FormField objects</li>
	 * <li>{@link FormField#getUserValueCount() getUserValueCount} : the maximum user value count from both FormField objects</li>
	 * </ul>
	 * <p>
	 * NOTE: Some underlying data structures may end up being shared between both FormFields collections.
	 */
	public void merge(FormFields formFields) {
		for (Iterator i=formFields.iterator(); i.hasNext();) {
			FormField formField=(FormField)i.next();
			String name=formField.getName();
			FormField existingFormField=get(name);
			if (existingFormField==null)
				add(formField);
			else
				existingFormField.merge(formField);
		}
		Collections.sort(list,FormField.COMPARATOR);
	}

	/**
	 * Returns a string representation of this object useful for debugging purposes.
	 * @return  a string representation of this object useful for debugging purposes.
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer();
		for (Iterator i=iterator(); i.hasNext();) {
			sb.append(i.next());
		}
		return sb.toString();
	}

	private void add(FormField formField) {
		map.put(formField.getName().toLowerCase(),formField);
		list.add(formField);
	}

	private void loadInputControls(Segment segment) {
		for (Iterator i=segment.findAllStartTags(Tag.INPUT).iterator(); i.hasNext();) {
			StartTag startTag=(StartTag)i.next();
			Attributes attributes=startTag.getAttributes();
			Attribute nameAttribute=attributes.get("name");
			if (nameAttribute==null) continue;
			FormControlType formControlType=startTag.getFormControlType();
			if (formControlType==null) continue;
			String predefinedValue=null;
			if (formControlType.isPredefinedValue()) {
				Attribute valueAttribute=attributes.get("value");
				if (valueAttribute!=null) predefinedValue=valueAttribute.getValue();  // only treat as a predefined value if it actually has a value.
			}
			String name=nameAttribute.getValue();
			registerField(name,startTag.begin,predefinedValue,formControlType);
			String[] additionalSubmitNames=formControlType.getAdditionalSubmitNames(name);
			if (additionalSubmitNames!=null) {
				for (int j=0; j<additionalSubmitNames.length; j++) {
					registerUserValueField(additionalSubmitNames[j],startTag.begin);
				}
			}
		}
	}

	private void loadTextAreaControls(Segment segment) {
		for (Iterator i=segment.findAllStartTags(Tag.TEXTAREA).iterator(); i.hasNext();) {
			StartTag startTag=(StartTag)i.next();
			Attributes attributes=startTag.getAttributes();
			Attribute nameAttribute=attributes.get("name");
			if (nameAttribute==null) continue;
			String name=nameAttribute.getValue();
			registerUserValueField(name,startTag.begin);
		}
	}

	private void loadButtonControls(Segment segment) {
		for (Iterator i=segment.findAllStartTags(Tag.BUTTON).iterator(); i.hasNext();) {
			StartTag startTag=(StartTag)i.next();
			FormControlType formControlType=startTag.getFormControlType();
			if (formControlType==null) continue;
			Attributes attributes=startTag.getAttributes();
			Attribute nameAttribute=attributes.get("name");
			if (nameAttribute==null) continue;
			String name=nameAttribute.getValue();
			String predefinedValue=null;
			Attribute valueAttribute=attributes.get("value");
			if (valueAttribute!=null) predefinedValue=valueAttribute.getValue();  // only treat as a predefined value if it actually has a value.
			registerField(name,startTag.begin,predefinedValue,formControlType);
		}
	}

	private void loadSelectControls(Segment segment) {
		List selectElements=segment.findAllElements(Tag.SELECT);
		if (selectElements.isEmpty()) return;
		List optionTags=segment.findAllStartTags(Tag.OPTION);
		if (optionTags.isEmpty()) return;
		Iterator selectIterator=selectElements.iterator();
		Element selectElement=(Element)selectIterator.next();
		Element lastSelectElement=null;
		String name=null;
		FormControlType formControlType=null;
		for (Iterator optionIterator=optionTags.iterator(); optionIterator.hasNext();) {
			StartTag optionTag=(StartTag)optionIterator.next();
			// find select element containing this option:
			while (optionTag.begin>selectElement.end) {
				if (!selectIterator.hasNext()) return;
				selectElement=(Element)selectIterator.next();
			}
			if (selectElement!=lastSelectElement) {
				if (optionTag.begin<selectElement.begin) continue;  // option tag before current select element - ignore it
				StartTag selectTag=selectElement.getStartTag();
				formControlType=selectTag.getFormControlType();
				if (formControlType==null) throw new RuntimeException("Internal Error: FormControlType not recognised for select tag "+selectTag);
				Attribute nameAttribute=selectTag.getAttributes().get("name");
				if (nameAttribute==null) {
					// select element has no name - skip to the next one
					if (!selectIterator.hasNext()) return;
					selectElement=(Element)selectIterator.next();
					lastSelectElement=null;
					continue;
				}
				name=nameAttribute.getValue();
				if (name==null) continue;
			}
			lastSelectElement=selectElement;
			String value;
			Attribute valueAttribute=optionTag.getAttributes().get("value");
			if (valueAttribute!=null) {
				value=valueAttribute.getValue();
			} else {
				Segment valueSegment=optionTag.getFollowingTextSegment();
				value=valueSegment.getSourceTextNoWhitespace();
				if (value.length()==0) continue; // no value attribute and no content - ignore this option
			}
			registerPredefinedValueField(name,selectElement.begin,value,formControlType);
		}
	}

	private void registerField(String name, int position, String predefinedValue, FormControlType formControlType) {
		if (predefinedValue==null)
			registerUserValueField(name, position);
		else
			registerPredefinedValueField(name, position, predefinedValue, formControlType);
	}

	private void registerUserValueField(String name, int position) {
		FormField formField=get(name);
		if (formField==null) {
			add(formField=new FormField(name,position,null));
		} else {
			formField.setMultipleValues();
			formField.setLowerPosition(position);
		}
		formField.incrementUserValueCount();
	}

	private void registerPredefinedValueField(String name, int position, String predefinedValue, FormControlType formControlType) {
		FormField formField=get(name);
		if (formField==null) {
			add(formField=new FormField(name,position,formControlType));
		} else {
			formField.setMultipleValues(formControlType);
			formField.setLowerPosition(position);
		}
		formField.addPredefinedValue(predefinedValue);
	}
}
