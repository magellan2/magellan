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
 */

package com.eressea.rules;

/**
 * DOCUMENT ME!
 *
 * @author Sebastian
 * @version
 */
public class EresseaDate extends Date {
	private static final String months_old[] = {
												   "Januar", "Februar", "März", "April", "Mai",
												   "Juni", "Juli", "August", "September", "Oktober",
												   "November", "Dezember"
											   };
	private static final String months_new[] = {
												   "Feldsegen", "Nebeltage", "Sturmmond",
												   "Herdfeuer", "Eiswind", "Schneebann",
												   "Blütenregen", "Mond der milden Winde",
												   "Sonnenfeuer"
											   };

	// long
	private static final String week_long[] = {
												  "Erste Woche des Monats ",
												  "Zweite Woche des Monats ",
												  "Dritte Woche des Monats "
											  };
	private static final String year_long = " im Jahre ";
	private static final String age_long = " des zweiten Zeitalters";

	// long alt
	private static final String week_long_alt[] = {
													  "Anfang des Monats ", "Mitte des Monats ",
													  "Ende des Monats "
												  };
	private static final String year_long_alt = " im Jahr ";
	private static final String age_long_alt = " des zweiten Zeitalters";

	// long alt2
	private static final String week_long_alt2[] = {
													   "Erste Woche des Monats ",
													   "Zweite Woche des Monats ",
													   "Letzte Woche des Monats "
												   };
	private static final String year_long_alt2 = " im Jahre ";
	private static final String age_long_alt2 = " des zweiten Zeitalters";

	// phrase
	private static final String begin_phrase = "Wir schreiben ";
	private static final String week_phrase[] = {
													"die erste Woche des Monats ",
													"die zweite Woche des Monats ",
													"die dritte Woche des Monats "
												};
	private static final String year_phrase = " im Jahre ";
	private static final String age_phrase = " des zweiten Zeitalters.";

	// alternative phrase
	private static final String begin_phrase_alt = "Wir haben ";
	private static final String week_phrase_alt[] = {
														"den Anfang des Monats ",
														"die Mitte des Monats ",
														"das Ende des Monats "
													};
	private static final String year_phrase_alt = " im Jahr ";
	private static final String age_phrase_alt = " des zweiten Zeitalters.";

	// alternative phrase 2
	private static final String begin_phrase_alt2 = "Wir schreiben ";
	private static final String week_phrase_alt2[] = {
														 "die erste Woche des Monats ",
														 "die zweite Woche des Monats ",
														 "die letzte Woche des Monats "
													 };
	private static final String year_phrase_alt2 = " im Jahre ";
	private static final String age_phrase_alt2 = " des zweiten Zeitalters.";

	// seasons
	private static final String seasonPhrases[] = {
													  "Es ist Sommer", "Es ist Herbst",
													  "Es ist Herbst", "Es ist Winter",
													  "Es ist Winter", "Es ist Winter",
													  "Es ist Frühling", "Es ist Frühling",
													  "Es ist Sommer"
												  };

	// private static int epochsBeginAt[] = {0, 1, 184};
	private int epoch = 1;

	/**
	 * Creates new EresseaDate
	 *
	 * @param iInitDate TODO: DOCUMENT ME!
	 */
	public EresseaDate(int iInitDate) {
		super(iInitDate);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getEpoch() {
		return this.epoch;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param newEpoch TODO: DOCUMENT ME!
	 */
	public void setEpoch(int newEpoch) {
		this.epoch = newEpoch;

		/* not such a bad idea, actually, but removed for vinyambar
		int round = getDate();

		if (newEpoch > 0 && newEpoch < epochsBeginAt.length) {
		    if (round < epochsBeginAt[newEpoch]) {
		        setDate(epochsBeginAt[newEpoch]);
		    } else if (newEpoch < epochsBeginAt.length - 1 && round > epochsBeginAt[newEpoch + 1]) {
		        setDate(epochsBeginAt[newEpoch + 1]);
		    }
		}*/
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param iDateType TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString(int iDateType) {
		String strDate = "";

		if(getEpoch() == 1) {
			// first age
			switch(iDateType) {
			default:
			case TYPE_SHORT:
				strDate = months_old[(iDate - 1) % 12] + " " + (((iDate - 1) / 12) + 1);

				break;

			case TYPE_LONG:
				strDate = months_old[(iDate - 1) % 12] + " des Jahres " + (((iDate - 1) / 12) + 1) +
						  " im ersten Zeitalter";

				break;

			case TYPE_PHRASE:
				strDate = "Wir schreiben den " + months_old[(iDate - 1) % 12] + " des Jahres " +
						  (((iDate - 1) / 12) + 1) + " im ersten Zeitalter.";

				break;
			}
		} else if(getEpoch() == 2) {
			// second age
			int iDate2 = iDate;

			if(iDate2 >= 184) {
				iDate2 -= 184;
			}

			switch(iDateType) {
			default:
			case Date.TYPE_SHORT: {
				int iWeek = (iDate2 % 3) + 1;
				String strMonth = months_new[(iDate2 / 3) % 9];
				int iYear = (iDate2 / 27) + 1;

				strDate = iWeek + ". Woche " + strMonth + " " + iYear;
			}

			break;

			case Date.TYPE_LONG: {
				int iWeek = iDate2 % 3;
				String strMonth = months_new[(iDate2 / 3) % 9];
				int iYear = (iDate2 / 27) + 1;

				switch(((int) (java.lang.Math.random() * 3)) % 3) {
				default:
				case 0:
					strDate = week_long[iWeek] + strMonth + year_long + iYear + age_long;

					break;

				case 1:
					strDate = week_long_alt[iWeek] + strMonth + year_long_alt + iYear +
							  age_long_alt;

					break;

				case 2:
					strDate = week_long_alt2[iWeek] + strMonth + year_long_alt2 + iYear +
							  age_long_alt2;

					break;
				}
			}

			break;

			case TYPE_PHRASE: {
				int iWeek = iDate2 % 3;
				String strMonth = months_new[(iDate2 / 3) % 9];
				int iYear = (iDate2 / 27) + 1;

				switch(((int) (java.lang.Math.random() * 3)) % 3) {
				default:
				case 0:
					strDate = begin_phrase + week_phrase[iWeek] + strMonth + year_phrase + iYear +
							  age_phrase;

					break;

				case 1:
					strDate = begin_phrase_alt + week_phrase_alt[iWeek] + strMonth +
							  year_phrase_alt + iYear + age_phrase_alt;

					break;

				case 2:
					strDate = begin_phrase_alt2 + week_phrase_alt2[iWeek] + strMonth +
							  year_phrase_alt2 + iYear + age_phrase_alt2;

					break;
				}
			}

			break;

			case TYPE_PHRASE_AND_SEASON: {
				int iWeek = iDate2 % 3;
				String strMonth = months_new[(iDate2 / 3) % 9];
				String season = " " + seasonPhrases[(iDate2 / 3) % 9];
				int iYear = (iDate2 / 27) + 1;

				switch(((int) (java.lang.Math.random() * 3)) % 3) {
				default:
				case 0:
					strDate = begin_phrase + week_phrase[iWeek] + strMonth + year_phrase + iYear +
							  age_phrase + season;

					break;

				case 1:
					strDate = begin_phrase_alt + week_phrase_alt[iWeek] + strMonth +
							  year_phrase_alt + iYear + age_phrase_alt + season;

					break;

				case 2:
					strDate = begin_phrase_alt2 + week_phrase_alt2[iWeek] + strMonth +
							  year_phrase_alt2 + iYear + age_phrase_alt2 + season;

					break;
				}
			}

			break;
			}
		}

		return strDate;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public com.eressea.ID copy() {
		return new EresseaDate(this.iDate);
	}
}
