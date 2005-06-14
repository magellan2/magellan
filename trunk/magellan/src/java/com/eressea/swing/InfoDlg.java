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

package com.eressea.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.eressea.resource.ResourcePathClassLoader;
import com.eressea.util.CollectionFactory;
import com.eressea.util.VersionInfo;

/**
 *
 */
public class InfoDlg extends InternationalizedDialog {
	private JPanel jPanel;
	private JButton btn_OK;
	private JLabel magellanImage;
	private JTextArea jTextArea1;

	/**
	 * Creates a new InfoDlg object.
	 *
	 * @param parent modally stucked frame.
	 */
	public InfoDlg(JFrame parent) {
		super(parent, true);
		initComponents();

		// center
		this.setLocation((getToolkit().getScreenSize().width - this.getWidth()) / 2,
						 (getToolkit().getScreenSize().height - this.getHeight()) / 2);
	}

	private void initComponents() {
		jPanel = new JPanel(new BorderLayout());
        JPanel jPanelNORTH = new JPanel(new BorderLayout());
        JPanel jPanelSOUTH = new JPanel(new FlowLayout());
        
		btn_OK = new JButton();
		magellanImage = new JLabel();
		jTextArea1 = new JTextArea();

		setModal(true);
		setTitle(getString("window.title"));

		btn_OK.setText(getString("btn.close.caption"));
		btn_OK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					quit();
				}
			});
        
        jPanelSOUTH.add(btn_OK);
        jPanel.add(jPanelSOUTH,BorderLayout.SOUTH);
        
        URL url = ResourcePathClassLoader.getResourceStatically("images/about/magellan.gif");
        Icon icon = null;

        if(url != null) {
            Image image = getToolkit().createImage(url);
            icon = new ImageIcon(image);
            magellanImage.setIcon(icon);
        }
		magellanImage.setPreferredSize(new java.awt.Dimension(400, 200));
		magellanImage.setMinimumSize(new java.awt.Dimension(400, 200));
		magellanImage.setText("");
		//magellanImage.setBackground(new Color(213, 169, 131));
		magellanImage.setMaximumSize(new java.awt.Dimension(400, 200));
        
        jPanel.add(jPanelNORTH,BorderLayout.NORTH);
        jPanelNORTH.add(magellanImage, BorderLayout.WEST);

		String text = getString("infotext");


		jTextArea1.setWrapStyleWord(true);

		jTextArea1.setLineWrap(true);
		jTextArea1.setEditable(false);
		jTextArea1.setText(text);

		jPanelNORTH.add(new JScrollPane(jTextArea1), BorderLayout.CENTER);

		getContentPane().add(jPanel);
        

        pack();
	}

    private String getString() {
        return "Magellan\n"+
        getString("infotext.version") +" "+ VersionInfo.getVersion()+"\n";
    }
    
	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static synchronized Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("window.title", "About Magellan");

			defaultTranslations.put("btn.close.caption", "Close");
            defaultTranslations.put("infotext.version", "Version");

			defaultTranslations.put("infotext",
									"You can find the official Magellan homepage "+
                                    "at http://eressea.upb.de/magellan/. "+
                                    "Check out that site if you need a new version, "+
                                    "support, or if you want to contribute to Magellan.\n\n"+
                                    "Credits:\nRoger Butenuth, Enno Rehling, Stefan G\u00f6tz,"+
                                    "Klaas Prause, Sebastian Tusk, Andreas Gampe, Roland Behme, "+
                                    "Michael Schmidt, Henning Zahn, Oliver Hertel, Guenter "+
                                    "Grossberger, Sören Bendig, Marc Geerligs, Matthias Müller, "+
                                    "Ulrich Küster, Jake Hofer, Ilja Pavkovic...\n"+
                                    "(drop us a note if somebody is missing here).\n\n"+
                                    "Last but not least we want to mention Ferdinand Magellan, "+
                                    "daring explorer and first circumnavigator of the globe of "+
                                    "a time long ago (http://www.mariner.org/age/magellan.html)\n\n"+
                                    "This product includes software developed by the Apache Software "+
                                    "Foundation (http://www.apache.org/).\n\n");
		}

		return defaultTranslations;
	}
}
