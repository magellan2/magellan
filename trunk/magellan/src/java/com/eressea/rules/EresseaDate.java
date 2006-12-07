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

package com.eressea.rules;

import java.util.Map;

import com.eressea.demo.Client;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Translations;

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
												   "Blütenregen", "Mond_der_milden_Winde",
												   "Sonnenfeuer"
											   };

	// long
	private static final String week_long[] = {
												  "Erste_Woche_des_Monats_",
												  "Zweite_Woche_des_Monats_",
												  "Dritte_Woche_des_Monats_"
											  };
	private static final String year_long = "_im_Jahre_";
	private static final String age_long = "_des_zweiten_Zeitalters.";

	// long alt
	private static final String week_long_alt[] = {
													  "Anfang_des_Monats_", "Mitte_des_Monats_",
													  "Ende_des_Monats_"
												  };
	private static final String year_long_alt = "_im_Jahre_";
	private static final String age_long_alt = "_des_zweiten_Zeitalters.";

	// long alt2
	private static final String week_long_alt2[] = {
													   "Erste_Woche_des_Monats_",
													   "Zweite_Woche_des_Monats_",
													   "Letzte_Woche_des_Monats_"
												   };
	private static final String year_long_alt2 = "_im_Jahre_";
	private static final String age_long_alt2 = "_des_zweiten_Zeitalters.";

	// phrase
	private static final String begin_phrase = "Wir_schreiben_";
	private static final String week_phrase[] = {
													"die_erste_Woche_des_Monats_",
													"die_zweite_Woche_des_Monats_",
													"die_dritte_Woche_des_Monats_"
												};
	private static final String year_phrase = "_im_Jahre_";
	private static final String age_phrase = "_des_zweiten_Zeitalters.";

	// alternative phrase
	private static final String begin_phrase_alt = "Wir_haben_";
	private static final String week_phrase_alt[] = {
														"den_Anfang_des_Monats_",
														"die_Mitte_des_Monats_",
														"das_Ende_des_Monats_"
													};
	private static final String year_phrase_alt = "_im_Jahre_";
	private static final String age_phrase_alt = "_des_zweiten_Zeitalters.";

	// alternative phrase 2
	private static final String begin_phrase_alt2 = "Wir_schreiben_";
	private static final String week_phrase_alt2[] = {
														 "die_erste_Woche_des_Monats_",
														 "die_zweite_Woche_des_Monats_",
														 "die_letzte_Woche_des_Monats_"
													 };
	private static final String year_phrase_alt2 = "_im_Jahre_";
	private static final String age_phrase_alt2 = "_des_zweiten_Zeitalters.";

	// seasons
	private static final String seasonPhrases[] = {
													"Es_ist_Sommer", "Es_ist_Herbst",
													  "Es_ist_Herbst", "Es_ist_Winter",
													  "Es_ist_Winter", "Es_ist_Winter",
													  "Es_ist_Frühling", "Es_ist_Frühling",
													  "Es_ist_Sommer"
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
				String strMonth = getString(months_new[(iDate2 / 3) % 9]);
				int iYear = (iDate2 / 27) + 1;

				strDate = iWeek + " " + getString("._Woche_") +" " + strMonth  + iYear;
			}

			break;

			case Date.TYPE_LONG: {
				int iWeek = iDate2 % 3;
				String strMonth = getString(months_new[(iDate2 / 3) % 9]);
				int iYear = (iDate2 / 27) + 1;

				switch(((int) (java.lang.Math.random() * 3)) % 3) {
				default:
				case 0:
					strDate = getString(week_long[iWeek]) + " " + strMonth + " " + getString(year_long) +  " " + iYear + " " + getString(age_long);

					break;

				case 1:
					strDate = getString(week_long_alt[iWeek]) + " " + strMonth + " " + getString(year_long_alt)  +  " " + iYear + " " +
						getString(age_long_alt);

					break;

				case 2:
					strDate = getString(week_long_alt2[iWeek]) + " " + strMonth + " " + getString(year_long_alt2) +  " " + iYear + " " +
						getString(age_long_alt2);

					break;
				}
			}

			break;

			case TYPE_PHRASE: {
				int iWeek = iDate2 % 3;
				String strMonth = getString(months_new[(iDate2 / 3) % 9]);
				int iYear = (iDate2 / 27) + 1;

				switch(((int) (java.lang.Math.random() * 3)) % 3) {
				default:
				case 0:
					strDate = getString(begin_phrase) + " " + getString(week_phrase[iWeek]) + " " + strMonth + " " + getString(year_phrase)  +  " " + iYear + " " +
						getString(age_phrase);

					break;

				case 1:
					strDate = getString(begin_phrase_alt) + " " + getString(week_phrase_alt[iWeek]) + " " + strMonth + " " +
						getString(year_phrase_alt) +  " " + iYear + " " + getString(age_phrase_alt);

					break;

				case 2:
					strDate = getString(begin_phrase_alt2) + " " + getString(week_phrase_alt2[iWeek]) + " " + strMonth + " " +
						getString(year_phrase_alt2)  +  " " + iYear + " " + getString(age_phrase_alt2);

					break;
				}
			}

			break;

			case TYPE_PHRASE_AND_SEASON: {
				int iWeek = iDate2 % 3;
				String strMonth = getString(months_new[(iDate2 / 3) % 9]);
				String season = " " + getString(seasonPhrases[(iDate2 / 3) % 9]);
				int iYear = (iDate2 / 27) + 1;

				switch(((int) (java.lang.Math.random() * 3)) % 3) {
				default:
				case 0:
					strDate = getString(begin_phrase) + " " + getString(week_phrase[iWeek]) + " " + strMonth + " " + getString(year_phrase) +  " " + iYear + " " +
						getString(age_phrase) + season;

					break;

				case 1:
					strDate = getString(begin_phrase_alt) + " " + getString(week_phrase_alt[iWeek]) + " " + strMonth + " " +
						getString(year_phrase_alt) +  " " + iYear + " " + getString(age_phrase_alt) + " " + season;

					break;

				case 2:
					strDate = getString(begin_phrase_alt2) + " " + getString(week_phrase_alt2[iWeek]) + " " + strMonth + " " +
						getString(year_phrase_alt2)  +  " " + iYear + " " + getString(age_phrase_alt2) + season;

					break;
				}
			}

			break;
			}
		}
		strDate = strDate.replaceAll("  ", " ");
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
	
    // /////////////////////////////
    // INTERNATIONALIZATION Code //
    // /////////////////////////////

    /**
     * Returns a translation from the translation table for the specified key.
     * 
     * @param key
     *            TODO: DOCUMENT ME!
     * 
     * @return TODO: DOCUMENT ME!
     */
    protected String getString(String key) {
        // this will indirectly evaluate getDefaultTranslation!
        return Translations.getTranslation(EresseaDate.class, key);
    }

    // pavkovic 2003.01.28: this is a Map of the default Translations mapped to
    // this class
    // it is called by reflection (we could force the implementation of an
    // interface,
    // this way it is more flexible.)
    // Pls use this mechanism, so the translation files can be created
    // automagically
    // by inspecting all classes.
    private static Map defaultTranslations;

    /**
     * TODO: DOCUMENT ME!
     * 
     * @return TODO: DOCUMENT ME!
     */
    public static synchronized Map getDefaultTranslations() {
        if (defaultTranslations == null) {
            defaultTranslations = CollectionFactory.createHashtable();
            defaultTranslations.put("Feldsegen", "harvest moon");
            defaultTranslations.put("Nebeltage", "impenetrable fog");
            defaultTranslations.put("Sturmmond", "storm moon");
            defaultTranslations.put("Herdfeuer", "hearth fire");
            defaultTranslations.put("Eiswind", "icewind");
            defaultTranslations.put("Schneebann", "snowbane");
            defaultTranslations.put("Blütenregen", "flowerrain");
            defaultTranslations.put("Mond_der_milden_Winde", "mild winds");
            defaultTranslations.put("Sonnenfeuer", "sunfire");
            defaultTranslations.put("Erste_Woche_des_Monats_", "first week of month");
            defaultTranslations.put("Zweite_Woche_des_Monats_", "second week of month");
            defaultTranslations.put("Dritte_Woche_des_Monats_", "third week of month");
            defaultTranslations.put("_im_Jahre_", "in year ");
            defaultTranslations.put("_des_zweiten_Zeitalters.", "of the second epoch.");
            defaultTranslations.put("Anfang_des_Monats_", "Start of month");
            defaultTranslations.put("Mitte_des_Monats_", "Middle of month");
            defaultTranslations.put("Ende_des_Monats_", "End of month");
            defaultTranslations.put("Wir_schreiben_", "We write");
            defaultTranslations.put("die_erste_Woche_des_Monats_", "the first week of month");
            defaultTranslations.put("die_zweite_Woche_des_Monats_", "the second week of month");
            defaultTranslations.put("die_dritte_Woche_des_Monats_", "the third week of month");
            defaultTranslations.put("die_letzte_Woche_des_Monats_", "the last week of month");
            defaultTranslations.put("den_Anfang_des_Monats_", "the beginning of month");
            defaultTranslations.put("die_Mitte_des_Monats_", "the middle of month");
            defaultTranslations.put("das_Ende_des_Monats_", "the ending of month");
            defaultTranslations.put("Wir_haben_", "We have");
            defaultTranslations.put("Es_ist_Sommer", "It is summer");
            defaultTranslations.put("Es_ist_Herbst", "It is autumn");
            defaultTranslations.put("Es_ist_Winter", "It is winter");
            defaultTranslations.put("Es_ist_Frühling", "It is spring");
            defaultTranslations.put("._Woche_", ". week");
        }

        return defaultTranslations;
    }
	
	
}
