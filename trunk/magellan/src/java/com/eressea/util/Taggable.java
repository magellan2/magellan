/*
 * Taggable.java
 *
 * Created on 6. Juni 2002, 18:44
 */

package com.eressea.util;

import java.util.Map;

/**
 *
 * @author  Andreas
 * @version 
 */
public interface Taggable {

	public boolean hasTags();
	
	public boolean containsTag(String tag);
	
	public String putTag(String tag, String value);
	public String getTag(String tag);
	public String removeTag(String tag);
	
	public void deleteAllTags();
	
	public Map getTagMap();
}

