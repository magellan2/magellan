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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Map;

import javax.swing.BoxLayout;
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
		jPanel = new JPanel();
		jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.Y_AXIS));
        
		
		
		magellanImage = new JLabel();
		jTextArea1 = new JTextArea();

		setModal(true);
		setTitle(getString("window.title"));

		
        
        URL url = ResourcePathClassLoader.getResourceStatically("images/about/magellan.gif");
        Icon icon = null;

        if(url != null) {
            Image image = getToolkit().createImage(url);
            icon = new ImageIcon(image);
            magellanImage.setIcon(icon);
        }
		magellanImage.setText("");
		magellanImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        jPanel.add(magellanImage);
        String text = getString("infotext") + getVersionString();


		jTextArea1.setWrapStyleWord(true);
		jTextArea1.setLineWrap(true);
		jTextArea1.setEditable(false);
		jTextArea1.setText(text);
		JScrollPane scrollPane = new JScrollPane(jTextArea1);
		scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		scrollPane.setPreferredSize(new Dimension(400,400));
		jPanel.add(scrollPane);

		// OK Button
		btn_OK = new JButton(getString("btn.close.caption"));
		btn_OK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					quit();
				}
			});
		btn_OK.setAlignmentX(Component.CENTER_ALIGNMENT);
        jPanel.add(btn_OK);

		getContentPane().add(jPanel);
        
		pack();
        
	}

    private String getVersionString() {
    	String versionInfo = VersionInfo.getVersion();
    	if (versionInfo==null){
    		versionInfo = "not available";
    	}
        return "Magellan " +
        getString("infotext.version") +": "+ versionInfo +"\n";
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
                                    "at http://magellan-client.sourceforge.net ."+
                                    "Check out that site if you need a new version, "+
                                    "support, or if you want to contribute to Magellan.\n\n"+
                                    "Credits:\nRoger Butenuth, Enno Rehling, Stefan G\u00f6tz,"+
                                    "Klaas Prause, Sebastian Tusk, Andreas Gampe, Roland Behme, "+
                                    "Michael Schmidt, Henning Zahn, Oliver Hertel, Guenter "+
                                    "Grossberger, S�ren Bendig, Marc Geerligs, Matthias M�ller, "+
                                    "Ulrich K�ster, Jake Hofer, Ilja Pavkovic...\n"+
                                    "(drop us a note if somebody is missing here).\n\n"+
                                    "Last but not least we want to mention Ferdinand Magellan, "+
                                    "daring explorer and first circumnavigator of the globe of "+
                                    "a time long ago \n(http://www.mariner.org/educationalad/ageofex/magellan.php)\n\n"+
                                    "This product includes software developed by the Apache Software "+
                                    "Foundation (http://www.apache.org/).\n\n");
		}

		return defaultTranslations;
	}
}
