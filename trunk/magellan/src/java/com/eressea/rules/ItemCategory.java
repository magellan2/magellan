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
public class ItemCategory extends Category {
	
	/** Creates new ItemCategory */
	public ItemCategory(ID id) {
		super(id);
	}
	
	public ItemCategory(ID id, Category parent) {
		super(id, parent);
	}
		
	public boolean isInstance(Object o) {
		if (o instanceof ItemType) {
			ItemType it = (ItemType)o;
			if (it.getCategory() != null) {
				return it.getCategory().isDescendant(this);
			}
		}
		return false;
	}	
}
