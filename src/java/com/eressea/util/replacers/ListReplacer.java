/*
 * ListReplacer.java
 *
 * Created on 19. Mai 2002, 11:39
 */

package com.eressea.util.replacers;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author  Andreas
 * @version
 */
public class ListReplacer implements Replacer {
	
	protected StringBuffer buffer;
	protected List list;
	protected String unknown;
	protected String evolved = null;
	protected static NumberFormat numberFormat;
	
	/** Creates new ListReplacer */
	public ListReplacer(List list, String unknown) {
		buffer = new StringBuffer();
		this.list = list;
		this.unknown = unknown;
		if (numberFormat == null) {
			try{
				numberFormat = NumberFormat.getInstance(com.eressea.util.Locales.getGUILocale());
			}catch(IllegalStateException ise) {
				numberFormat = NumberFormat.getInstance();
			}
			numberFormat.setMaximumFractionDigits(2);
			numberFormat.setMinimumFractionDigits(0);
		}
		if (list == null) {
			evolved = "";
		} else {
			Iterator it = list.iterator();
			boolean canEvolve = true;
			while(canEvolve && it.hasNext()) {
				canEvolve = !(it.next() instanceof Replacer);
			}
			if (canEvolve) {
				evolved = (String)getReplacement(null);
			}
		}
	}
	
	public String getDescription() {
		return "list replacer";
	}
	
	public Object getReplacement(Object o) {
		if (evolved != null) {
			return evolved;
		}
		//try{
			if (list == null) {
				return null;
			}
			buffer.setLength(0);
			Iterator it = list.iterator();
			while(it.hasNext()) {
				Object obj = it.next();
				if (obj instanceof Replacer) {
					obj = ((Replacer)obj).getReplacement(o);
				}
				if (obj == null) {
					buffer.append(unknown);
				} else {
					if (obj instanceof Number) {
						buffer.append(numberFormat.format(obj));
					} else {
						buffer.append(obj.toString());
					}
				}
			}
			String str = buffer.toString();
			buffer.setLength(0);
			return str;
		//}catch(Exception exc) {}
		//return null;
	}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		Iterator it = list.iterator();
		while(it.hasNext()) {
			buf.append(it.next());
		}
		if (evolved != null) {
			buf.append("(Evolved to: ");
			buf.append(evolved);
			buf.append(")");
		}
		return buf.toString();
	}
}
