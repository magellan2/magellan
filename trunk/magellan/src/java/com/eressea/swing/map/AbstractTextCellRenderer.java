/*
 *  Copyright (C) 2000-2003 Roger Butenuth, Andreas Gampe,
 *                          Stefan Goetz, Sebastian Pappert,
 *                          Klaas Prause, Enno Rehling,
 *                          Sebastian Tusk, Ulrich Kuester,
 *                          Ilja Pavkovic
 *
 * This file is part of the Eressea Java Code Base, see the
 * file LICENSING for the licensing information applying to
 * this file.
 *
 * $Id$
 */

package com.eressea.swing.map;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;

import java.util.Properties;

import com.eressea.Coordinate;
import com.eressea.GameData;
import com.eressea.Region;

/**
 * Abstract base class for text renderers. Several possibilities to change the
 * output. New feature: Alignment.
 *
 * @author Andreas
 * @version 1.0
 */
public abstract class AbstractTextCellRenderer extends HexCellRenderer {
	protected Color		  fontColor		  = Color.black;
	protected Font		  unscaledFont    = null;
	protected Font		  font			  = null;
	protected FontMetrics fontMetrics     = null;
	protected int		  minimumFontSize = 10;
	protected int		  fontHeight	  = 0;
	protected boolean     isScalingFont   = false;
	protected int		  hAlign		  = CENTER;
	protected boolean     shortenStrings  = false;
	protected String	  singleString[]  = new String[1];

	/** TODO: DOCUMENT ME! */
	public static final int LEFT = 0;

	/** TODO: DOCUMENT ME! */
	public static final int CENTER = 1;

	/** TODO: DOCUMENT ME! */
	public static final int RIGHT = 2;

	/**
	 * Creates new AbstractTextCellRenderer
	 *
	 * @param geo TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	protected AbstractTextCellRenderer(CellGeometry geo, Properties settings) {
		super(geo, settings);
	}

	protected Color getFontColor() {
		return fontColor;
	}

	protected void setFontColor(Color col) {
		fontColor = col;
	}

	protected Font getFont() {
		return unscaledFont;
	}

	protected void setFont(Font f) {
		unscaledFont = f;
		setFont(1f);
	}

	protected void setFont(float scaleFactor) {
		font = unscaledFont.deriveFont(Math.max(unscaledFont.getSize() * scaleFactor,
												(float) minimumFontSize));

		// using deprecated getFontMetrics() to avoid Java2D methods
		fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(this.font);
		fontHeight  = this.fontMetrics.getHeight();
	}

	protected boolean isScalingFont() {
		return isScalingFont;
	}

	protected void setScalingFont(boolean b) {
		isScalingFont = b;
	}

	protected int getMinimumFontSize() {
		return minimumFontSize;
	}

	protected void setMinimumFontSize(int m) {
		minimumFontSize = m;
	}

	protected int getHAlign() {
		return hAlign;
	}

	protected void setHAlign(int i) {
		hAlign = i;
	}

	protected FontMetrics getFontMetrics() {
		if(fontMetrics == null) {
			fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(new Font("TimesRoman",
																			  Font.PLAIN,
																			  10));

			//fontMetrics = graphics.getFontMetrics(new Font("TimesRoman", Font.PLAIN, 10));
		}

		return fontMetrics;
	}

	protected boolean isShortenStrings() {
		return shortenStrings;
	}

	protected void setShortenStrings(boolean b) {
		shortenStrings = b;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPlaneIndex() {
		return Mapper.PLANE_TEXT;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 * @param rect TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public abstract String getSingleString(Region r, Rectangle rect);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 * @param rect TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public abstract String[] getText(Region r, Rectangle rect);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param data TODO: DOCUMENT ME!
	 * @param g TODO: DOCUMENT ME!
	 * @param offset TODO: DOCUMENT ME!
	 */
	public void init(GameData data, Graphics g, Point offset) {
		super.init(data, g, offset);

		if(isScalingFont) {
			setFont(cellGeo.getScaleFactor());
		} else {
			setFont(1.0f);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param obj TODO: DOCUMENT ME!
	 * @param active TODO: DOCUMENT ME!
	 * @param selected TODO: DOCUMENT ME!
	 */
	public void render(Object obj, boolean active, boolean selected) {
		if(obj instanceof Region) {
			Region     r    = (Region) obj;
			Coordinate c    = r.getCoordinate();
			Rectangle  rect = cellGeo.getCellRect(c.x, c.y);

			String     display[] = getText(r, rect);

			if((display == null) || (display.length == 0)) {
				singleString[0] = getSingleString(r, rect);

				if((singleString[0] == null) || singleString[0].equals("")) {
					return;
				}

				display = singleString;
			}

			shortenStrings(display, rect.width);

			graphics.setFont(font);
			graphics.setColor(fontColor);

			int height  = (int) (fontHeight * 0.8);
			int middleX = (rect.x + (rect.width / 2)) - offset.x;
			int middleY = (rect.y + (rect.height / 2) + 1) - offset.y;
			int upperY  = middleY;

			if(display.length == 1) {
				upperY += (height / 4);
			} else {
				upperY -= (((display.length - 1) * height) / 4);
			}

			int maxWidth = -1;

			switch(hAlign) {
			// left
			case 0:

				for(int i = 0; i < display.length; i++) {
					if(fontMetrics.stringWidth(display[i]) > maxWidth) {
						maxWidth = fontMetrics.stringWidth(display[i]);
					}
				}

				int leftX = middleX - (maxWidth / 2);

				for(int i = 0; i < display.length; i++) {
					graphics.drawString(display[i], leftX, upperY +
										(i * height));
				}

				break;

			// center
			case 1:

				for(int i = 0; i < display.length; i++) {
					int l = fontMetrics.stringWidth(display[i]);
					graphics.drawString(display[i], middleX - (l / 2),
										upperY + (i * height));
				}

				break;

			// right
			case 2:

				for(int i = 0; i < display.length; i++) {
					if(fontMetrics.stringWidth(display[i]) > maxWidth) {
						maxWidth = fontMetrics.stringWidth(display[i]);
					}
				}

				int rightX = middleX + (maxWidth / 2);

				for(int i = 0; i < display.length; i++) {
					graphics.drawString(display[i],
										rightX -
										fontMetrics.stringWidth(display[i]),
										upperY + (i * height));
				}

				break;
			}
		}
	}

	protected void shortenStrings(String str[], int maxWidth) {
		if(shortenStrings && (str != null) && (str.length > 0)) {
			for(int i = 0; i < str.length; i++) {
				if(str[i] != null) {
					if(fontMetrics.stringWidth(str[i]) > maxWidth) {
						char  name[]    = str[i].toCharArray();
						int   nameLen   = name.length;
						int   nameWidth = 0;

						for(; nameLen > 0; nameLen--) {
							nameWidth = fontMetrics.charsWidth(name, 0, nameLen);

							if(nameWidth < (maxWidth * 0.9)) {
								if(nameLen < name.length) {
									name[nameLen - 1] = '.';
									name[nameLen]     = '.';
									nameLen++;
								}

								break;
							}
						}

						str[i] = new String(name, 0, nameLen);
					}
				}
			}
		}
	}
}
