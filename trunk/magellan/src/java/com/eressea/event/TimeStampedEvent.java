/*
 * TimeStampedEvent.java
 *
 * Created on 18. Februar 2002, 13:52
 */

package com.eressea.event;

/**
 *
 * @author  Andreas
 * @version 
 */
public abstract class TimeStampedEvent extends java.util.EventObject {
	
	private long timestamp;
	
	/** Creates new TimeStampedEvent */
	public TimeStampedEvent(Object source) {
		super(source);
		timestamp=System.currentTimeMillis();
	}
		
	public long getTimestamp() {
		return timestamp;
	}
}
