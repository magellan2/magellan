// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.util.replacers;

import java.lang.reflect.Method;

import com.eressea.Region;
/**
 *
 * @author  unknown
 * @version 
 */
public class RegionMethodReplacer extends AbstractRegionReplacer {
	
	public final static int MODE_ALL=0;
	public final static int MODE_NON_NEGATIVE=1;
	public final static int MODE_POSITIVE=2;
	
	protected Method method;
	protected int mode;
	
	public RegionMethodReplacer(String method,int mode) {
		try{
			this.method=Class.forName("com.eressea.Region").getMethod(method,null);
		}catch(Exception exc) {throw new RuntimeException("Error retrieving region method "+method);}
		this.mode=mode;
	}

	public Object getRegionReplacement(Region r) {
		try{
			Object o=method.invoke(r,null);
			if (o!=null) {
				if (!(o instanceof Number))
					return o;
				Number n=(Number)o;
				switch(mode) {
					case MODE_ALL: return o;
					case MODE_NON_NEGATIVE: if (n.doubleValue()>=0) return o;break;
					case MODE_POSITIVE: if(n.doubleValue()>0) return o;break;
					default: break;
				}
			}
		}catch(Exception exc) {}
		return null;
	}
}
