// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.swing.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

import com.eressea.Coordinate;
import com.eressea.Region;

/**
 * Abstract base class for renderers that want to paint regions as a colored
 * polygon.
 */
public abstract class AbstractRegionShapeCellRenderer extends HexCellRenderer {
	
	protected AbstractRegionShapeCellRenderer(CellGeometry geo, java.util.Properties settings) {
		super(geo,settings);
	}
	
	protected abstract Color getSingleColor(Region r);
	protected abstract Color[] getColor(Region r);
	
	public void render(Object obj, boolean active, boolean selected) {
		if (obj instanceof Region) {
			Region r = (Region)obj;
			Coordinate c = (Coordinate)r.getID();
			
			Point pos = cellGeo.getCellPosition(c.x, c.y);
			pos.translate(-offset.x, -offset.y);
			
			Polygon p = cellGeo.getScaledPolygon();
			p = new Polygon(p.xpoints, p.ypoints, p.npoints);
			p.translate(pos.x, pos.y);
			
			paintRegion(graphics,p,r);
		}
	}
	
	/**
	 * This renderer is for regions only and returns Mapper.PLANE_REGION.
	 * @return the plane index for this renderer
	 */
	public int getPlaneIndex() {
		return Mapper.PLANE_REGION;
	}
	
	/**
	 * Paints the specified region.
	 */
	protected void paintRegion(Graphics g, Polygon p, Region r) {
		Color colors[] = getColor(r);
		if (colors == null || colors.length < 2) {
			Color color = null;
			if (colors != null && colors.length > 0) {
				color = colors[0];
			} else {
				color = getSingleColor(r);
			}
			try{
				g.setColor(color);
				g.fillPolygon(p);
			} catch(Exception exc) {}
		} else {
			Rectangle bounds=p.getBounds();
			int i,j,imax=bounds.x+bounds.width,jmax=bounds.y+bounds.height;;
			for(i=bounds.x;i<imax;i++) {
				j=bounds.y;
				do{
					if (p.contains(i,j))
						break;
					j++;
				}while(j<jmax);
				g.setColor(colors[(int)((i-bounds.x)*colors.length/bounds.width)]);
				g.drawLine(i,j,i,j+(bounds.height-2*(j-bounds.y)));
			}
		}
		
		if (p.getBounds().width>3) {
			g.setColor(Color.black);
			g.drawPolygon(p);
		}
	}
}
