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

package com.eressea.gamebinding.eressea;

import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.eressea.GameData;
import com.eressea.UnitID;
import com.eressea.completion.OrderParser;
import com.eressea.rules.BuildingType;
import com.eressea.rules.CastleType;
import com.eressea.rules.ItemCategory;
import com.eressea.rules.ItemType;
import com.eressea.util.Direction;
import com.eressea.util.IDBaseConverter;
import com.eressea.util.OrderToken;
import com.eressea.util.OrderTokenizer;
import com.eressea.util.Translations;
import com.eressea.util.logging.Logger;

/**
 * A class for reading Eressea orders and checking their syntactical correctness. A
 * <tt>OrderParser</tt> object can register a <tt>OrderCompleter</tt> object. In such a case the
 * <tt>OrderParser</tt> will call the corresponding methods of the <tt>OrderCompleter</tt> if it
 * encounters an incomplete order.
 */
public class EresseaOrderParser implements OrderParser {
	private static final Logger log = Logger.getInstance(EresseaOrderParser.class);

	// this is not entirely true with dynamic bases but it probably doesn't really hurt
	private static final int MAX_UID = 1679615;
	private String errMsg = null;
	private TokenBucket tokenBucket = null;
	private Iterator tokens = null;
	private EresseaOrderCompleter completer = null;
	private GameData data = null;

	/**
	 * Creates a new <tt>EresseaOrderParser</tt> object.
	 *
	 * @param data TODO: DOCUMENT ME!
	 */
	public EresseaOrderParser(GameData data) {
		this(data, null);
	}

	/**
	 * Creates a new <tt>EresseaOrderParser</tt> object and registers the specified
	 * <tt>OrderCompleter</tt> object. This constructor should be used only by the
	 * <tt>OrderCompleter</tt> class itself.
	 *
	 * @param data TODO: DOCUMENT ME!
	 * @param cc TODO: DOCUMENT ME!
	 */
	public EresseaOrderParser(GameData data, EresseaOrderCompleter cc) {
		tokenBucket = new TokenBucket();
		completer = cc;
		this.data = data;
	}

	/**
	 * Returns the tokens read by the parser.
	 *
	 * @return all <tt>OrderToken</tt> object produced by the underlying <tt>OrderTokenizer</tt> by
	 * 		   reading a order.
	 */
	public List getTokens() {
		return tokenBucket;
	}

	/**
	 * Returns the error messages produced by the last invocation of the <tt>read(Reader in)</tt>
	 * method.
	 *
	 * @return an error message if the last <tt>read</tt> returned <tt>false</tt>, <tt>null</tt>
	 * 		   else.
	 */
	public String getErrorMessage() {
		return errMsg;
	}

	/**
	 * Parses one line of text from the specified stream by tokenizing it and checking the syntax.
	 *
	 * @param in the stream to read the order from.
	 *
	 * @return <tt>true</tt> if the syntax of the order read is valid, <tt>false</tt> else.
	 */
	public boolean read(Reader in) {
		errMsg = null;
		tokenBucket.read(in);
		tokenBucket.mergeTempTokens(data.base);
		tokens = tokenBucket.iterator();

		boolean retVal = true;

		while(tokens.hasNext() && retVal) {
			OrderToken token = (OrderToken) tokens.next();

			if(token.ttype != OrderToken.TT_COMMENT) {
				retVal = readOrder(token);
			}
		}

		return retVal;
	}

	private boolean readOrder(OrderToken t) {
		boolean retVal = false;

		if(t.ttype == OrderToken.TT_PERSIST) {
			retVal = readAt(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_WORK))) {
			retVal = readWork(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_ATTACK))) {
			retVal = readAttack(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_BANNER))) {
			retVal = readBanner(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_CLAIM))) {
			retVal = readBeanspruche(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_PROMOTION))) {
			retVal = readBefoerderung(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_STEAL))) {
			retVal = readBeklaue(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SIEGE))) {
			retVal = readBelagere(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_NAME))) {
			retVal = readBenenne(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_USE))) {
			retVal = readBenutze(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_DESCRIBE))) {
			retVal = readBeschreibe(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_ENTER))) {
			retVal = readBetrete(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_GUARD))) {
			retVal = readBewache(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_MESSAGE))) {
			retVal = readBotschaft(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_DEFAULT))) {
			retVal = readDefault(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_EMAIL))) {
			retVal = readEmail(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_END))) {
			retVal = readEnde(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_RIDE))) {
			retVal = readFahre(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_FOLLOW))) {
			retVal = readFolge(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_RESEARCH))) {
			retVal = readForsche(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_GIVE))) {
			retVal = readGib(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_GROUP))) {
			retVal = readGruppe(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_HELP))) {
			retVal = readHelfe(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_COMBAT))) {
			retVal = readKaempfe(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_COMBATSPELL))) {
			retVal = readKampfzauber(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_BUY))) {
			retVal = readKaufe(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_CONTACT))) {
			retVal = readKontaktiere(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_TEACH))) {
			retVal = readLehre(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_LEARN))) {
			retVal = readLerne(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SUPPLY))) {
			retVal = readGib(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_LOCALE))) {
			retVal = readLocale(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_MAKE))) {
			retVal = readMache(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_MOVE))) {
			retVal = readNach(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_NEXT))) {
			retVal = readFinalKeyword(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_NUMBER))) {
			retVal = readNummer(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_OPTION))) {
			retVal = readOption(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_FACTION))) {
			retVal = readPartei(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_PASSWORD))) {
			retVal = readPasswort(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_PLANT))) {
			retVal = readPflanzen(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_PIRACY))) {
			retVal = readPiraterie(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_PREFIX))) {
			retVal = readPraefix(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_REGION))) {
			retVal = readRegion(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_RECRUIT))) {
			retVal = readRekrutiere(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_RESERVE))) {
			retVal = readReserviere(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_ROUTE))) {
			retVal = readRoute(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SORT))) {
			retVal = readSortiere(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SPY))) {
			retVal = readSpioniere(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_QUIT))) {
			retVal = readStirb(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_HIDE))) {
			retVal = readTarne(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_CARRY))) {
			retVal = readTransportiere(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_TAX))) {
			retVal = readTreibe(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_ENTERTAIN))) {
			retVal = readUnterhalte(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_ORIGIN))) {
			retVal = readUrsprung(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_FORGET))) {
			retVal = readVergesse(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SELL))) {
			retVal = readVerkaufe(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_LEAVE))) {
			retVal = readFinalKeyword(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_CAST))) {
			retVal = readZaubere(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SHOW))) {
			retVal = readZeige(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_DESTROY))) {
			retVal = readZerstoere(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_GROW))) {
			retVal = readZuechte(t);
		} else {
			if(completer != null) {
				completer.cmplt();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	//************* AT
	private boolean readAt(OrderToken token) {
		OrderToken t = (OrderToken) tokens.next();

		return readOrder(t);
	}

	//************* WORK (ARBEITE)
	private boolean readWork(OrderToken token) {
		token.ttype = OrderToken.TT_KEYWORD;

		return checkNextFinal();
	}

	//************* ATTACK (ATTACKIERE)
	private boolean readAttack(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readAttackUID(t);
		} else {
			if(completer != null) {
				completer.cmpltAttack();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readAttackUID(OrderToken token) {
		token.ttype = OrderToken.TT_ID;

		return checkNextFinal();
	}

	//************* BANNER
	private boolean readBanner(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isQuoted(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			unexpected(t);
		}

		return retVal;
	}

	//************* BEF�RDERUNG
	private boolean readBefoerderung(OrderToken token) {
		token.ttype = OrderToken.TT_KEYWORD;
		
		return checkNextFinal();
	}

	//************* BEKLAUE
	private boolean readBeklaue(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readBeklaueUID(t);
		} else {
			if(completer != null) {
				completer.cmpltBeklaue();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readBeklaueUID(OrderToken token) {
		token.ttype = OrderToken.TT_ID;

		return checkNextFinal();
	}

	//************* BELAGERE
	private boolean readBelagere(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readBelagereBID(t);
		} else {
			if(completer != null) {
				completer.cmpltBelagere();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readBelagereBID(OrderToken token) {
		token.ttype = OrderToken.TT_ID;

		return checkNextFinal();
	}

	//************* BENENNE
	private boolean readBenenne(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_CASTLE))) {
			retVal = readBenenneBeschreibeTarget(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_UNIT))) {
			retVal = readBenenneBeschreibeTarget(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_FACTION))) {
			retVal = readBenenneBeschreibeTarget(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_REGION))) {
			retVal = readBenenneBeschreibeTarget(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SHIP))) {
			retVal = readBenenneBeschreibeTarget(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_FOREIGN))) {
			retVal = readBenenneFremdes(t);
		} else {
			if(completer != null) {
				completer.cmpltBenenne();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readBenenneBeschreibeTarget(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isString(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			unexpected(t);
		}

		return retVal;
	}

	private boolean readBenenneFremdes(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_UNIT))) {
			retVal = readBenenneFremdeEinheit(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_CASTLE))) {
			retVal = readBenenneFremdesGebaeude(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_FACTION))) {
			retVal = readBenenneFremdePartei(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SHIP))) {
			retVal = readBenenneFremdesSchiff(t);
		} else {
			if(completer != null) {
				completer.cmpltBenenneFremdes();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readBenenneFremdeEinheit(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText())) {
			retVal = readBenenneFremdesTargetID(t);
		} else {
			if(completer != null) {
				completer.cmpltBenenneFremdeEinheit();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readBenenneFremdesGebaeude(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText())) {
			retVal = readBenenneFremdesTargetID(t);
		} else {
			if(completer != null) {
				completer.cmpltBenenneFremdesGebaeude();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readBenenneFremdePartei(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText())) {
			retVal = readBenenneFremdesTargetID(t);
		} else {
			if(completer != null) {
				completer.cmpltBenenneFremdePartei();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readBenenneFremdesSchiff(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText())) {
			retVal = readBenenneFremdesTargetID(t);
		} else {
			if(completer != null) {
				completer.cmpltBenenneFremdesSchiff();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readBenenneFremdesTargetID(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_ID;

		OrderToken t = (OrderToken) tokens.next();

		if(isString(t.getText())) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltBenenneFremdesTargetID();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* BENUTZE
	private boolean readBenutze(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isString(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltBenutze();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* BESCHREIBE
	private boolean readBeschreibe(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_CASTLE))) {
			retVal = readBenenneBeschreibeTarget(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_UNIT))) {
			retVal = readBenenneBeschreibeTarget(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_PRIVATE))) {
			retVal = readBenenneBeschreibeTarget(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_REGION))) {
			retVal = readBenenneBeschreibeTarget(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SHIP))) {
			retVal = readBenenneBeschreibeTarget(t);
		} else {
			if(completer != null) {
				completer.cmpltBeschreibe();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* BETRETE
	private boolean readBetrete(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_CASTLE))) {
			retVal = readBetreteBurg(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SHIP))) {
			retVal = readBetreteSchiff(t);
		} else {
			if(completer != null) {
				completer.cmpltBetrete();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readBetreteBurg(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readBetreteBurgBID(t);
		} else {
			if(completer != null) {
				completer.cmpltBetreteBurg();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readBetreteBurgBID(OrderToken token) {
		token.ttype = OrderToken.TT_ID;

		return checkNextFinal();
	}

	private boolean readBetreteSchiff(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readBetreteSchiffSID(t);
		} else {
			if(completer != null) {
				completer.cmpltBetreteSchiff();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readBetreteSchiffSID(OrderToken token) {
		token.ttype = OrderToken.TT_ID;

		return checkNextFinal();
	}

	//************* BEWACHE
	private boolean readBewache(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_NOT))) {
			retVal = readBewacheNicht(t);
		} else {
			retVal = checkFinal(t);
		}

		return retVal;
	}

	private boolean readBewacheNicht(OrderToken token) {
		token.ttype = OrderToken.TT_KEYWORD;

		return checkNextFinal();
	}

	//************* BOTSCHAFT
	private boolean readBotschaft(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		// FIX
		if(t.equalsToken("AN")) {
			retVal = readBotschaft(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_UNIT))) {
			retVal = readBotschaftEinheit(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_FACTION))) {
			retVal = readBotschaftPartei(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_REGION))) {
			retVal = readBotschaftRegion(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_CASTLE))) {
			retVal = readBotschaftGebaeude(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SHIP))) {
			retVal = readBotschaftSchiff(t);
		} else {
			if(completer != null) {
				completer.cmpltBotschaft();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readBotschaftEinheit(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readBotschaftEinheitUID(t);
		} else {
			if(completer != null) {
				completer.cmpltBotschaftEinheit();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readBotschaftEinheitUID(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_ID;

		OrderToken t = (OrderToken) tokens.next();

		if(isQuoted(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			unexpected(t);
		}

		return retVal;
	}

	private boolean readBotschaftPartei(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readBotschaftParteiFID(t);
		} else {
			if(completer != null) {
				completer.cmpltBotschaftPartei();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readBotschaftParteiFID(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_ID;

		OrderToken t = (OrderToken) tokens.next();

		if(isQuoted(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			unexpected(t);
		}

		return retVal;
	}

	private boolean readBotschaftRegion(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isQuoted(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			unexpected(t);
		}

		return retVal;
	}

	private boolean readBotschaftGebaeude(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readBotschaftGebaeudeID(t);
		} else {
			if(completer != null) {
				completer.cmpltBotschaftGebaeude();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readBotschaftGebaeudeID(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_ID;

		OrderToken t = (OrderToken) tokens.next();

		if(isQuoted(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltBotschaftGebaeudeID();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readBotschaftSchiff(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readBotschaftSchiffID(t);
		} else {
			if(completer != null) {
				completer.cmpltBotschaftSchiff();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readBotschaftSchiffID(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_ID;

		OrderToken t = (OrderToken) tokens.next();

		if(isQuoted(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltBotschaftSchiffID();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* DEFAULT
	private boolean readDefault(OrderToken token) {
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.ttype == OrderToken.TT_EOC) {
			if(completer != null) {
				completer.cmpltDefault();
			}

			return false;
		} else {
			return readOrder(t);
		}
	}

	//************* EMAIL
	private boolean readEmail(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isEmailAddress(t.getText()) == true) {
			retVal = readEmailAddress(t);
		} else {
			unexpected(t);
		}

		return retVal;
	}

	private boolean readEmailAddress(OrderToken token) {
		token.ttype = OrderToken.TT_STRING;

		return checkNextFinal();
	}

	//************* ENDE
	private boolean readEnde(OrderToken token) {
		token.ttype = OrderToken.TT_KEYWORD;

		return checkNextFinal();
	}

	//************* FAHRE
	private boolean readFahre(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readFahreUID(t);
		} else {
			if(completer != null) {
				completer.cmpltFahre();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readFahreUID(OrderToken token) {
		token.ttype = OrderToken.TT_ID;

		return checkNextFinal();
	}

	//************* FOLGE
	private boolean readFolge(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_UNIT)) == true) {
			retVal = readFolgeEinheit(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SHIP)) == true) {
			retVal = readFolgeSchiff(t);
		} else {
			if(completer != null) {
				completer.cmpltFolge();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readFolgeEinheit(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readFinalID(t);
		} else {
			if(completer != null) {
				completer.cmpltFolgeEinheit();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readFolgeSchiff(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readFinalID(t);
		} else {
			if(completer != null) {
				completer.cmpltFolgeSchiff();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* BEANSPRUCHE (Fiete)
	private boolean readBeanspruche(OrderToken token){
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if (isNumeric(t.getText())){
			retVal = readBeansprucheAmount(t);
		} else if (isString(t.getText())) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltBeanspruche();
			}
			unexpected(t);
		}
		return retVal;
	}
	
	private boolean readBeansprucheAmount(OrderToken token){
		boolean retVal = false;
		token.ttype = OrderToken.TT_NUMBER;

		OrderToken t = (OrderToken) tokens.next();

		if (isString(t.getText())){
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltBeanspruche();
			}
			unexpected(t);
		}
		return retVal;
	}
	
	
	//************* FORSCHE
	private boolean readForsche(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_HERBS))) {
			retVal = readFinalKeyword(t);
		} else {
			if(completer != null) {
				completer.cmpltForsche();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* GIB
	private boolean readGib(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readGibUID(t);
		} else {
			if(completer != null) {
				completer.cmpltGib();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readGibUID(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_ID;

		UnitID uid = UnitID.createUnitID(token.getText(),data.base);
		OrderToken t = (OrderToken) tokens.next();

		if(isNumeric(t.getText()) == true) {
			retVal = readGibUIDAmount(t, uid, Integer.parseInt(t.getText()), true);
		} else if (t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_EACH))){
			retVal = readGibJe(t, uid);
		}else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_ALL))) {
			retVal = readGibUIDAlles(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_UNIT)) ||
					  t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_CONTROL)) ||
					  t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_HERBS))) {
			retVal = readFinalKeyword(t);
		} else if(isString(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltGibUID();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readGibJe(OrderToken token, UnitID uid) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if (isNumeric(t.getText()) == true) {
			retVal = readGibUIDAmount(t, uid, Integer.parseInt(t.getText()), false); // GIB JE PERSONS is illegal
		} else
		// // GIVE bla JE ALL ... does not make sense
		// if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_ALL))) {
		// retVal = readGibUIDAlles(t);
		// } else
		if (isString(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			if (completer != null) {
				completer.cmpltGibJe();
			}

			unexpected(t);
		}

		return retVal;
	}
	
	/**
	 * For multiple-line-completion like the creation of give-orders for the resources of an item
	 * in OrderCompleter.cmpltGibUIDAmount it is necessary to save the unit's id and the amount to
	 * be given. This is done by:
	 *
	 * @param token TODO: DOCUMENT ME!
	 * @param uid the unit's id
	 * @param i the amount
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private boolean readGibUIDAmount(OrderToken token, UnitID uid, int i, boolean persons) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_NUMBER;

		OrderToken t = (OrderToken) tokens.next();

		if(isString(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltGibUIDAmount(uid, i, persons);
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readGibUIDAlles(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isString(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltGibUIDAlles();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	//************* GRUPPE
	private boolean readGruppe(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isString(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltGruppe();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* HELFE
	private boolean readHelfe(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readHelfeFID(t);
		} else {
			if(completer != null) {
				completer.cmpltHelfe();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readHelfeFID(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_ID;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_ALL)) ||
			   t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_GUARD)) ||
			   t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_GIVE)) ||
			   t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_COMBAT)) ||
			   t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SILVER)) ||
			   t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_FACTIONSTEALTH))) {
			retVal = readHelfeFIDModifier(t);
		} else {
			if(completer != null) {
				completer.cmpltHelfeFID();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readHelfeFIDModifier(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_NOT))) {
			retVal = readHelfeFIDModifierNicht(t, token.getText());
		} else {
			if(completer != null) {
				completer.cmpltHelfeFIDModifier();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	private boolean readHelfeFIDModifierNicht(OrderToken token, String modifier) {
		token.ttype = OrderToken.TT_KEYWORD;

		return checkNextFinal();
	}

	//************* KAEMPFE
	private boolean readKaempfe(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_AGGRESSIVE))) {
			retVal = readFinalKeyword(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_REAR))) {
			retVal = readFinalKeyword(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_DEFENSIVE))) {
			retVal = readFinalKeyword(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_NOT))) {
			retVal = readFinalKeyword(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_FLEE))) {
			retVal = readFinalKeyword(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_HELP_COMBAT))) {
			retVal = readKaempfeHelfe(t);
		} else {
			if(completer != null) {
				completer.cmpltKaempfe();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	private boolean readKaempfeHelfe(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_NOT))) {
			retVal = readFinalKeyword(t);
		} else {
			if(completer != null) {
				completer.cmpltKaempfeHelfe();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	//************* KAMPFZAUBER
	private boolean readKampfzauber(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_LEVEL))) {
			retVal = readKampfzauberStufe(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_NOT))) {
			retVal = readFinalKeyword(t);
		} else if(isString(t.getText())) {
			retVal = readKampfzauberSpell(t);
		} else {
			if(completer != null) {
				completer.cmpltKampfzauber();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readKampfzauberStufe(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isNumeric(t.getText())) {
			t.ttype = OrderToken.TT_NUMBER;
			t = (OrderToken) tokens.next();

			if(isString(t.getText())) {
				retVal = readFinalString(t);
			} else {
				if(completer != null) {
					completer.cmpltKampfzauberStufe();
				}

				unexpected(t);
			}
		}

		return retVal;
	}

	private boolean readKampfzauberSpell(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_STRING;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(EresseaConstants.O_NOT)) {
			retVal = readFinalKeyword(t);
		} else {
			if(completer != null) {
				completer.cmpltKampfzauberSpell();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* KAUFE
	private boolean readKaufe(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isNumeric(t.getText()) == true) {
			retVal = readKaufeAmount(t);
		} else {
			if(completer != null) {
				completer.cmpltKaufe();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readKaufeAmount(OrderToken token) {
		boolean retVal = false;
		ItemType type = null;
		ItemCategory luxuryCategory = (data != null)
									  ? data.rules.getItemCategory(EresseaConstants.C_LUXURIES) : null;
		token.ttype = OrderToken.TT_NUMBER;

		OrderToken t = (OrderToken) tokens.next();

		// 
		if((data.rules != null) && ((type = data.rules.getItemType(t.getText())) != null) &&
			   (luxuryCategory != null) && luxuryCategory.equals(type.getCategory())) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltKaufeAmount();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* KONTAKTIERE
	private boolean readKontaktiere(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readKontaktiereUID(t);
		} else {
			if(completer != null) {
				completer.cmpltKontaktiere();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readKontaktiereUID(OrderToken token) {
		token.ttype = OrderToken.TT_ID;

		return checkNextFinal();
	}

	//************* LEHRE
	private boolean readLehre(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readLehreUID(t);
		} else {
			if(completer != null) {
				completer.cmpltLehre();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readLehreUID(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_ID;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readLehreUID(t);
		} else {
			if(completer != null) {
				completer.cmpltLehre();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	//************* LERNE
	private boolean readLerne(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if((data.rules != null) && (data.rules.getSkillType(t.getText()) != null)) {
			t.ttype = OrderToken.TT_STRING;
			t = (OrderToken) tokens.next();

			if(isNumeric(t.getText()) == true) {
				retVal = readFinalNumber(t);
			} else {
				retVal = checkFinal(t);
			}
		} else {
			if(completer != null) {
				completer.cmpltLerne();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* LOCALE
	private boolean readLocale(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isString(t.getText())) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltLocale();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* MACHE
	private boolean readMache(OrderToken token) {
		boolean retVal = false;
		BuildingType type = null;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isNumeric(t.getText()) == true) {
			retVal = readMacheAmount(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_TEMP))) {
			retVal = readMacheTemp(t);
		} else if(isTempID(t.getText()) == true) {
			retVal = readMacheTempID(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_CASTLE))) {
			retVal = readMacheBurg(t);
		} else if((data.rules != null) && ((type = data.rules.getBuildingType(t.getText())) != null) &&
					  (!(type instanceof CastleType) ||
					  t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_CASTLE)))) {
			retVal = readMacheBuilding(t);
		} else if((data.rules != null) && (data.rules.getShipType(t.getText()) != null)) {
			retVal = readMacheShip(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SHIP))) {
			retVal = readMacheSchiff(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_ROAD))) {
			retVal = readMacheStrasse(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SEED))) {
			retVal = readFinalKeyword(t);
		} else if(t.ttype == OrderToken.TT_EOC) {
			if(completer != null) {
				completer.cmpltMache();
			}

			retVal = false;
		} else {
			retVal = readMacheAnything(t);
		}

		return retVal;
	}

	private boolean readMacheAmount(OrderToken token) {
		boolean retVal = false;
		BuildingType type = null;
		token.ttype = OrderToken.TT_NUMBER;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_CASTLE))) {
			retVal = readMacheBurg(t);
		} else if((data.rules != null) && ((type = data.rules.getBuildingType(t.getText())) != null) &&
					  !(type instanceof CastleType)) {
			retVal = readMacheBuilding(t);
		} else if((data.rules != null) && (data.rules.getShipType(t.getText()) != null)) {
			retVal = readMacheShip(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SHIP))) {
			retVal = readMacheSchiff(t);
		} else {
			if(completer != null) {
				completer.cmpltMacheAmount();
			}

			retVal = readMacheAnything(t);
		}

		return retVal;
	}

	private boolean readMacheTemp(OrderToken token) {
		token.ttype = OrderToken.TT_ID;

		OrderToken t = (OrderToken) tokens.next();

		if(completer != null) {
			completer.cmpltMacheTemp();
		}

		unexpected(t);

		return false; // there can't follow an id, else it would have been merged with TEMP
	}

	private boolean readMacheTempID(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_ID;

		OrderToken t = (OrderToken) tokens.next();

		if(isString(t.getText())) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltMacheTempID();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	private boolean readMacheBurg(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText())) {
			retVal = readFinalID(t);
		} else {
			if(completer != null) {
				completer.cmpltMacheBurg();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	private boolean readMacheBuilding(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_STRING;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readFinalID(t);
		} else {
			if(completer != null) {
				completer.cmpltMacheBuilding(token.getText());
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	private boolean readMacheShip(OrderToken token) {
		token.ttype = OrderToken.TT_STRING;

		return checkNextFinal();
	}

	private boolean readMacheSchiff(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_STRING;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readFinalID(t);
		} else {
			if(completer != null) {
				completer.cmpltMacheSchiff();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	private boolean readMacheStrasse(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_STRING;

		OrderToken t = (OrderToken) tokens.next();

		if(Direction.toInt(t.getText()) != Direction.DIR_INVALID) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltMacheStrasse();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readMacheAnything(OrderToken token) {
		boolean retVal = true;

		if((token.ttype != OrderToken.TT_EOC) && (token.ttype != OrderToken.TT_COMMENT)) {
			token.ttype = OrderToken.TT_STRING;
			retVal = checkNextFinal();
		}

		return retVal;
	}

	//************* NACH
	private boolean readNach(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(Direction.toInt(t.getText()) != Direction.DIR_INVALID) {
			retVal = readNachDirection(t);
		} else {
			if(completer != null) {
				completer.cmpltNach();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readNachDirection(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(Direction.toInt(t.getText()) != Direction.DIR_INVALID) {
			retVal = readNachDirection(t);
		} else {
			if(completer != null) {
				completer.cmpltNach();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	//************* NUMMER
	private boolean readNummer(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_UNIT)) == true) {
			retVal = readNummerEinheit(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SHIP)) == true) {
			retVal = readNummerSchiff(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_FACTION)) == true) {
			retVal = readNummerPartei(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_CASTLE)) == true) {
			retVal = readNummerBurg(t);
		} else {
			if(completer != null) {
				completer.cmpltNummer();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readNummerEinheit(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readFinalID(t);
		} else if(t.ttype == OrderToken.TT_EOC) {
			retVal = true;
		} else {
			unexpected(t);
		}

		return retVal;
	}

	private boolean readNummerPartei(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readFinalID(t);
		} else if(t.ttype == OrderToken.TT_EOC) {
			retVal = true;
		} else {
			unexpected(t);
		}

		return retVal;
	}

	private boolean readNummerSchiff(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readFinalID(t);
		} else if(t.ttype == OrderToken.TT_EOC) {
			retVal = true;
		} else {
			unexpected(t);
		}

		return retVal;
	}

	private boolean readNummerBurg(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readFinalID(t);
		} else if(t.ttype == OrderToken.TT_EOC) {
			retVal = true;
		} else {
			unexpected(t);
		}

		return retVal;
	}

	//************* OPTION
	private boolean readOption(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_ADDRESSES)) ||
			   t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_REPORT)) ||
			   t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_BZIP2)) ||
			   t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_COMPUTER)) ||
			   t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_ITEMPOOL)) ||
			   t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SILVERPOOL)) ||
			   t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_STATISTICS)) ||
			   t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_ZIPPED)) ||
			   t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SCORE)) ||
			   t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_TEMPLATE))) {
			retVal = readOptionOption(t);
		} else {
			if(completer != null) {
				completer.cmpltOption();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readOptionOption(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_NOT))) {
			retVal = readFinalKeyword(t);
		} else {
			if(completer != null) {
				completer.cmpltOptionOption();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	//************* PARTEI
	private boolean readPartei(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readParteiFID(t);
		} else {
			unexpected(t);
		}

		return retVal;
	}

	private boolean readParteiFID(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_ID;

		OrderToken t = (OrderToken) tokens.next();

		if(isQuoted(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			unexpected(t);
		}

		return retVal;
	}

	//************* PASSWORT
	private boolean readPasswort(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.ttype == OrderToken.TT_EOC) {
			retVal = true;
		} else if(isQuoted(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			unexpected(t);
		}

		return retVal;
	}

	//************* PFLANZEN
	private boolean readPflanzen(OrderToken token) {
		token.ttype = OrderToken.TT_KEYWORD;

		return checkFinal((OrderToken) tokens.next());
	}

	//************* PIRATERIE
	private boolean readPiraterie(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readPiraterieFID(t);
		} else {
			if(completer != null) {
				completer.cmpltPiraterie();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readPiraterieFID(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_ID;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readPiraterieFID(t);
		} else {
			if(completer != null) {
				completer.cmpltPiraterieFID();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	//************* PRAEFIX
	private boolean readPraefix(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isQuoted(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltPraefix();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	//************* REGION
	private boolean readRegion(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isRID(t.getText()) == true) {
			retVal = readFinalID(t);
		} else {
			unexpected(t);
		}

		return retVal;
	}

	//************* REKRUTIERE
	private boolean readRekrutiere(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isNumeric(t.getText()) == true) {
			retVal = readFinalNumber(t);
		} else {
			unexpected(t);
		}

		return retVal;
	}

	//************* RESERVIERE
	private boolean readReserviere(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();
		if(isNumeric(t.getText()) == true) {
			retVal = readReserviereAmount(t);
		} else if (t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_EACH))){
			retVal = readReserviereJe(t);
		} else {
			if(completer != null) {
				completer.cmpltReserviere();
			}

			unexpected(t);
		}

		return retVal;
	}
	
	private boolean readReserviereJe(OrderToken token){
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;
		
		OrderToken t = (OrderToken) tokens.next();
		
		if(isNumeric(t.getText()) == true) {
			retVal = readReserviereAmount(t);
		} else {
			if(completer != null) {
				completer.cmpltReserviereJe();
			}

			retVal = checkFinal(t);
		}

		return retVal;

	}

	private boolean readReserviereAmount(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_NUMBER;

		OrderToken t = (OrderToken) tokens.next();

		if(isString(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltReserviereAmount();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* ROUTE
	private boolean readRoute(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(Direction.toInt(t.getText()) != Direction.DIR_INVALID) {
			retVal = readRouteDirection(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_PAUSE))) {
			retVal = readRouteDirection(t);
		} else {
			if(completer != null) {
				completer.cmpltRoute();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readRouteDirection(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(Direction.toInt(t.getText()) != Direction.DIR_INVALID) {
			retVal = readRouteDirection(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_PAUSE))) {
			retVal = readRouteDirection(t);
		} else {
			if(completer != null) {
				completer.cmpltRoute();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	//************* SORTIERE
	private boolean readSortiere(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		// FIX
		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_BEFORE))) {
			retVal = readSortiereVor(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_AFTER))) {
			retVal = readSortiereHinter(t);
		} else {
			if(completer != null) {
				completer.cmpltSortiere();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readSortiereVor(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText())) {
			retVal = readFinalID(t);
		} else {
			if(completer != null) {
				completer.cmpltSortiereVor();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readSortiereHinter(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText())) {
			retVal = readFinalID(t);
		} else {
			if(completer != null) {
				completer.cmpltSortiereHinter();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* SPIONIERE
	private boolean readSpioniere(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readFinalID(t);
		} else {
			if(completer != null) {
				completer.cmpltSpioniere();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* STIRB
	private boolean readStirb(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isQuoted(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltStirb();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* TARNE
	private boolean readTarne(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isNumeric(t.getText()) == true) {
			retVal = readFinalNumber(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_FACTION))) {
			retVal = readTarnePartei(t);
		} else if((data.rules != null) && (data.rules.getRace(t.getText()) != null)) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltTarne();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	private boolean readTarnePartei(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_NOT))) {
			retVal = readFinalKeyword(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_NUMBER))) {
			retVal = readTarneParteiNummer(t);
		} else {
			if(completer != null) {
				completer.cmpltTarnePartei();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	private boolean readTarneParteiNummer(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText())) {
			retVal = readFinalID(t);
		} else {
			if(completer != null) {
				completer.cmpltTarneParteiNummer();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* TRANSPORTIERE
	private boolean readTransportiere(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isID(t.getText()) == true) {
			retVal = readFinalID(t);
		} else {
			if(completer != null) {
				completer.cmpltTransportiere();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* TREIBE
	private boolean readTreibe(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isNumeric(t.getText()) == true) {
			retVal = readFinalNumber(t);
		} else {
			retVal = checkFinal(t);
		}

		return retVal;
	}

	//************* UNTERHALTE
	private boolean readUnterhalte(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isNumeric(t.getText()) == true) {
			retVal = readFinalNumber(t);
		} else {
			retVal = checkFinal(t);
		}

		return retVal;
	}

	//************* URSPRUNG
	private boolean readUrsprung(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isNumeric(t.getText(), 10, Integer.MIN_VALUE, Integer.MAX_VALUE) == true) {
			retVal = readUrsprungX(t);
		} else {
			unexpected(t);
		}

		return retVal;
	}

	private boolean readUrsprungX(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_ID;

		OrderToken t = (OrderToken) tokens.next();

		if(isNumeric(t.getText(), 10, Integer.MIN_VALUE, Integer.MAX_VALUE) == true) {
			retVal = readFinalID(t);
		} else {
			unexpected(t);
		}

		return retVal;
	}

	//************* VERGESSE
	private boolean readVergesse(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if((data.rules != null) && (data.rules.getSkillType(t.getText()) != null)) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltVergesse();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* VERKAUFE
	private boolean readVerkaufe(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isNumeric(t.getText()) == true) {
			retVal = readVerkaufeAmount(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_ALL))) {
			retVal = readVerkaufeAlles(t);
		} else {
			if(completer != null) {
				completer.cmpltVerkaufe();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readVerkaufeAmount(OrderToken token) {
		boolean retVal = false;
		ItemType type = null;
		ItemCategory luxuryCategory = (data.rules != null)
									  ? data.rules.getItemCategory(EresseaConstants.C_LUXURIES) : null;
		token.ttype = OrderToken.TT_NUMBER;

		OrderToken t = (OrderToken) tokens.next();

		if((data.rules != null) && ((type = data.rules.getItemType(t.getText())) != null) &&
			   (luxuryCategory != null) && type.getCategory().equals(luxuryCategory)) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltVerkaufeAmount();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readVerkaufeAlles(OrderToken token) {
		boolean retVal = false;
		ItemType type = null;
		ItemCategory luxuryCategory = (data.rules != null)
									  ? data.rules.getItemCategory(EresseaConstants.C_LUXURIES) : null;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if((data.rules != null) && ((type = data.rules.getItemType(t.getText())) != null) && (type != null) &&
			   (luxuryCategory != null) && luxuryCategory.equals(type.getCategory())) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltVerkaufeAlles();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* ZAUBERE
	private boolean readZaubere(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_REGION))) {
			retVal = readZaubereRegion(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_LEVEL))) {
			retVal = readZaubereStufe(t);
		} else if(isString(t.getText())) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltZaubere();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readZaubereRegion(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isNumeric(t.getText(), 10, Integer.MIN_VALUE, Integer.MAX_VALUE)) {
			retVal = readZaubereRegionCoor(t);
		} else {
			if(completer != null) {
				completer.cmpltZaubereRegion();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readZaubereRegionCoor(OrderToken token) {
		boolean retVal = false;

		// x-coordinate
		token.ttype = OrderToken.TT_NUMBER;

		OrderToken t = (OrderToken) tokens.next();

		if(isNumeric(t.getText(), 10, Integer.MIN_VALUE, Integer.MAX_VALUE)) {
			// y-coordinate
			t.ttype = OrderToken.TT_NUMBER;
			t = (OrderToken) tokens.next();

			if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_LEVEL))) {
				retVal = readZaubereRegionStufe(t);
			} else if(isString(t.getText())) {
				retVal = readFinalString(t);
			} else {
				if(completer != null) {
					completer.cmpltZaubereRegionCoor();
				}

				unexpected(t);
			}
		}

		return retVal;
	}

	private boolean readZaubereStufe(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isNumeric(t.getText())) {
			t.ttype = OrderToken.TT_NUMBER;
			t = (OrderToken) tokens.next();

			if(isString(t.getText())) {
				retVal = readFinalString(t);
			} else {
				if(completer != null) {
					completer.cmpltZaubereStufe();
				}

				unexpected(t);
			}
		}

		return retVal;
	}

	private boolean readZaubereRegionStufe(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isNumeric(t.getText())) {
			t.ttype = OrderToken.TT_NUMBER;
			t = (OrderToken) tokens.next();

			if(isString(t.getText())) {
				retVal = readFinalString(t);
			} else {
				if(completer != null) {
					completer.cmpltZaubereRegionStufe();
				}

				unexpected(t);
			}
		}

		return retVal;
	}

	//************* ZEIGE
	private boolean readZeige(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_ALL))) {
			retVal = readZeigeAlle(t);
		} else if(isString(t.getText()) == true) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltZeige();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readZeigeAlle(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_POTIONS)) ||
			   t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_SPELLS))) {
			retVal = readFinalKeyword(t);
		} else {
			if(completer != null) {
				completer.cmpltZeigeAlle();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* ZERSTOERE
	private boolean readZerstoere(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(isNumeric(t.getText())) {
			retVal = readZerstoereAmount(t);
		} else if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_ROAD))) {
			retVal = readZerstoereStrasse(t);
		} else {
			if(completer != null) {
				completer.cmpltZerstoere();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	private boolean readZerstoereAmount(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_NUMBER;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_ROAD))) {
			retVal = readZerstoereStrasse(t);
		} else {
			if(completer != null) {
				completer.cmpltZerstoere();
			}

			retVal = checkFinal(t);
		}

		return retVal;
	}

	private boolean readZerstoereStrasse(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_STRING;

		OrderToken t = (OrderToken) tokens.next();

		if(Direction.toInt(t.getText()) != Direction.DIR_INVALID) {
			retVal = readFinalString(t);
		} else {
			if(completer != null) {
				completer.cmpltZerstoereStrasse();
			}

			unexpected(t);
		}

		return retVal;
	}

	//************* ZUECHTE
	private boolean readZuechte(OrderToken token) {
		boolean retVal = false;
		token.ttype = OrderToken.TT_KEYWORD;

		OrderToken t = (OrderToken) tokens.next();

		if(t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_HERBS)) ||
			   t.equalsToken(Translations.getOrderTranslation(EresseaConstants.O_HORSES))) {
			retVal = readFinalKeyword(t);
		} else {
			if(completer != null) {
				completer.cmpltZuechte();
			}

			unexpected(t);
		}

		return retVal;
	}

	private boolean readFinalKeyword(OrderToken token) {
		token.ttype = OrderToken.TT_KEYWORD;

		return checkNextFinal();
	}

	private boolean readFinalString(OrderToken token) {
		token.ttype = OrderToken.TT_STRING;

		return checkNextFinal();
	}

	private boolean readFinalID(OrderToken token) {
		token.ttype = OrderToken.TT_ID;

		return checkNextFinal();
	}

	private boolean readFinalNumber(OrderToken token) {
		token.ttype = OrderToken.TT_NUMBER;

		return checkNextFinal();
	}

	/**
	 * Checks whether the next token is the end of line or a comment, i.e. the indicating a valid
	 * end of the order. Reports an unexpected token if that is not the case.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private boolean checkNextFinal() {
		if(tokens.hasNext()) {
			OrderToken t = (OrderToken) tokens.next();

			return checkFinal(t);
		} else {
			errMsg = "Missing token";

			return false;
		}
	}

	/**
	 * Checks whether the token t is the end of line or a comment, i.e. the indicating a valid end
	 * of the order. Reports an unexpected token if that is not the case.
	 *
	 * @param t TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private boolean checkFinal(OrderToken t) {
		boolean retVal = ((t.ttype == OrderToken.TT_EOC) || (t.ttype == OrderToken.TT_COMMENT));

		if(retVal == false) {
			unexpected(t);
		}

		return retVal;
	}

	private void unexpected(OrderToken t) {
		errMsg = "Unexpected token " + t.toString();
	}

	private boolean isNumeric(String txt, int radix, int min, int max) {
		boolean retVal = false;

		try {
			int i = Integer.parseInt(txt, radix);
			retVal = ((i >= min) && (i <= max));
		} catch(NumberFormatException e) {
		}

		return retVal;
	}

	private boolean isNumeric(String txt) {
		return isNumeric(txt, 10, 0, Integer.MAX_VALUE);
	}

	private boolean isID(String txt) {
		boolean retVal = isNumeric(txt, data.base, 0, MAX_UID);

		if(retVal == false) {
			retVal = isTempID(txt);
		}

		return retVal;
	}

	private boolean isTempID(String txt) {
		boolean retVal = false;
		int blankPos = txt.indexOf(" ");

		if(blankPos == -1) {
			blankPos = txt.indexOf("\t");
		}

		if(blankPos > -1) {
			String temp = txt.substring(0, blankPos);
			String nr = txt.substring(blankPos + 1);
			retVal = (temp.equalsIgnoreCase("TEMP"));
			retVal = retVal && isNumeric(nr, data.base, 0, MAX_UID);
		}

		return retVal;
	}

	private boolean isRID(String txt) {
		boolean retVal = false;
		int firstCommaPos = txt.indexOf(",");
		int secondCommaPos = txt.lastIndexOf(",");

		if(firstCommaPos > -1) {
			if(secondCommaPos > firstCommaPos) {
				try {
					Integer.parseInt(txt.substring(0, firstCommaPos));
					Integer.parseInt(txt.substring(firstCommaPos + 1, secondCommaPos));
					Integer.parseInt(txt.substring(secondCommaPos + 1, txt.length()));
					retVal = true;
				} catch(NumberFormatException e) {
					log.warn("OrderEditor.getColor()", e);
				}
			} else {
				try {
					Integer.parseInt(txt.substring(0, firstCommaPos));
					Integer.parseInt(txt.substring(firstCommaPos + 1, txt.length()));
					retVal = true;
				} catch(NumberFormatException e) {
					log.warn("OrderEditor.getColor()", e);
				}
			}
		}

		return retVal;
	}

	private boolean isQuoted(String txt) {
		return (txt.startsWith("\"") && txt.endsWith("\""));
	}

	private boolean isString(String txt) {
		boolean retVal = isQuoted(txt);

		if((retVal == false) && (txt.length() > 0)) {
			retVal = true;

			for(int i = 0; i < txt.length(); i++) {
				char c = txt.charAt(i);

				if(!(((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z')) || (c == '�') ||
					   (c == '�') || (c == '�') || (c == '�') || (c == '�') || (c == '�') ||
					   (c == '~') || (c == '"') || (c == '�'))) {
					retVal = false;

					break;
				}
			}
		}

		return retVal;
	}

	private boolean isEmailAddress(String txt) {
		boolean retVal = true;
		int atIndex = txt.indexOf("@");

		if((atIndex > -1) && (atIndex == txt.lastIndexOf("@"))) {
			for(int i = 0; i < txt.length(); i++) {
				char c = txt.charAt(i);

				if(!(((c >= '0') && (c <= '9')) || ((c >= 'A') && (c <= 'Z')) ||
					   ((c >= 'a') && (c <= 'z')) || (c == '-') || (c == '_') || (c == '.') ||
					   (c == '@'))) {
					retVal = false;

					break;
				}
			}
		} else {
			retVal = false;
		}

		return retVal;
	}
}


/**
 * A class for collecting and preprocessing order tokens
 */
class TokenBucket extends Vector {
	private static final int MAX_TEMP_NR = 1679615; // = (36 ^ 4) - 1;

	/**
	 * Creates a new TokenBucket object.
	 */
	public TokenBucket() {
	}

	/**
	 * Creates a new TokenBucket object.
	 *
	 * @param in TODO: DOCUMENT ME!
	 */
	public TokenBucket(Reader in) {
		read(in);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean add(Object o) {
		boolean retVal = false;

		if(o instanceof OrderToken) {
			retVal = true;
			super.add(o);
		}

		return retVal;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param in TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int read(Reader in) {
		OrderTokenizer tokenizer = new OrderTokenizer(in);
		OrderToken token = null;
		clear();

		do {
			token = tokenizer.getNextToken();
			add(token);
		} while(token.ttype != OrderToken.TT_EOC);

		return size();
	}

	/**
	 * Merges two tokens if the first one contains the string TEMP the second one contains an id.
	 *
	 * @return the number of remaining tokens.
	 */
	public int mergeTempTokens(int base) {
		if(size() > 1) {
			for(int i = 0; i < (size() - 1); i++) {
				OrderToken tempToken = tokenAt(i);
				String tempText = tempToken.getText();

				if(tempText.equalsIgnoreCase("TEMP")) {
					try {
						OrderToken nrToken = tokenAt(i + 1);
						String nrText = nrToken.getText();
						int nr = IDBaseConverter.parse(nrText,base);

						if((nr >= 0) && (nr <= MAX_TEMP_NR)) {
							tempToken.setText("TEMP " + nrText);

							if((tempToken.getEnd() > -1) && (nrToken.getEnd() > -1)) {
								tempToken.setEnd(nrToken.getEnd());
							}

							remove(i + 1);
						}
					} catch(NumberFormatException e) {
					}
				}
			}
		}

		return size();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param index TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public OrderToken tokenAt(int index) {
		OrderToken retVal = null;

		if(index < size()) {
			retVal = (OrderToken) elementAt(index);
		}

		return retVal;
	}
}
