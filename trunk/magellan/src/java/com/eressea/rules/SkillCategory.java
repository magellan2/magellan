/*
 * ItemCategory.java
 *
 * Created on 9. März 2002, 20:39
 */

package com.eressea.rules;

import com.eressea.ID;

/**
 *
 * @author  Andreas
 * @version
 */
public class SkillCategory extends Category {
	
	/** Creates new ItemCategory */
	public SkillCategory(ID id) {
		super(id);
	}
	
	public SkillCategory(ID id, Category parent) {
		super(id, parent);
	}
		
	public boolean isInstance(Object o) {
		if (o instanceof SkillType) {
			SkillType st = (SkillType)o;
			if (st.getCategory() != null) {
				return st.getCategory().isDescendant(this);
			}
		}
		return false;
	}	
}
