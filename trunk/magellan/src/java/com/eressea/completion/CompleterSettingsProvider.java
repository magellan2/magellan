package com.eressea.completion;

import java.util.List;

public interface CompleterSettingsProvider {

	/** 
	 * Delivers a list of completions for self defined completions.
	 */

	public List getSelfDefinedCompletions();

	/** 
	 * Returns true iff LimitMakeCompletion is turned on
	 */
	public boolean getLimitMakeCompletion();
}
