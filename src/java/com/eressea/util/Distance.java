/*
 *  Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe,
 *                          Stefan Goetz, Sebastian Pappert,
 *                          Klaas Prause, Enno Rehling,
 *                          Sebastian Tusk, Ulrich Kuester,
 *                          Ilja Pavkovic
 *
 * This file is part of the Eressea Java Code Base, see the
 * file LICENSING for the licensing information applying to
 * this file.
 *
 */

package com.eressea.util;

class Distance {
	static void usage() {
		System.out.println("usage: Distance x1 y1 x2 y2");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param x1 TODO: DOCUMENT ME!
	 * @param y1 TODO: DOCUMENT ME!
	 * @param x2 TODO: DOCUMENT ME!
	 * @param y2 TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static int distance(int x1, int y1, int x2, int y2) {
		/* Contributed by Hubert Mackenberg. Thanks.
		 * x und y Abstand zwischen x1 und x2 berechnen
		 */
		int dx = x1 - x2;
		int dy = y1 - y2;

		/* Bei negativem dy am Ursprung spiegeln, das veraendert
		 * den Abstand nicht
		 */
		if(dy < 0) {
			dy = -dy;
			dx = -dx;
		}

		/*
		 * dy ist jetzt >=0, fuer dx sind 3 Faelle zu untescheiden
		 */
		if(dx >= 0) {
			return dx + dy;
		} else if(-dx >= dy) {
			return -dx;
		} else {
			return dy;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param args TODO: DOCUMENT ME!
	 */
	public static void main(String args[]) {
		if(args.length != 4) {
			usage();
		} else {
			int x1 = Integer.parseInt(args[0]);
			int y1 = Integer.parseInt(args[1]);
			int x2 = Integer.parseInt(args[2]);
			int y2 = Integer.parseInt(args[3]);
			System.out.println(x1 + "," + y1 + " -> " + x2 + "," + y2 + ": " +
							   distance(x1, y1, x2, y2));
		}
	}
}
