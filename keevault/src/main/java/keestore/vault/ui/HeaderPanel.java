/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import keestore.vault.Util;
import keestore.vault.VaultContext;

/**
 * Informational header panel in the main window.
 * 
 * @author thinh ho
 *
 */
class HeaderPanel extends JPanel {
    private static final long serialVersionUID = 7362297315973899167L;
    private static final Logger logger = Logger.getLogger(HeaderPanel.class);
    private static final String defaultDateTimeFormat = "MM/dd/yyyy HH:mm:ss z";

    private SimpleDateFormat dateformat;
    private JLabel timeFld;
    private JLabel registrationLlb;
    private JLabel applicationKeyLbl;

    public HeaderPanel() {
        initComponents();
        layoutComponents();
    }
    
    public void setVaultContext(VaultContext context) {
        Util.withEventQueue(() -> {
            registrationLlb.setText(context.getRegistrationFile().getPath());
            applicationKeyLbl.setText(context.getApplicationSecretKeyFile().getPath());
            validate();
        });
    }

    public void setTime(Date date) {
        Util.withEventQueue(() -> {
            timeFld.setText(formatDateTime(date));
        });
    }

    private void layoutComponents() {
        JPanel panel0 = new JPanel(new GridBagLayout());
        panel0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Registration"));
        GridBagConstraints c0 = new GridBagConstraints();
        {
            c0.anchor = GridBagConstraints.LINE_START;
            c0.fill = GridBagConstraints.HORIZONTAL;
            c0.gridx = 0;
            c0.gridy = 0;
            c0.gridwidth = 1;
            c0.ipadx = 5;
            c0.ipady = 5;
            panel0.add(new JLabel("User registration: "), c0);
        }
        {
            c0.anchor = GridBagConstraints.LINE_START;
            c0.fill = GridBagConstraints.HORIZONTAL;
            c0.gridx = 1;
            c0.gridy = 0;
            c0.gridwidth = 1;
            c0.ipadx = 5;
            c0.ipady = 5;
            panel0.add(registrationLlb, c0);
        }
        {
            c0.anchor = GridBagConstraints.LINE_START;
            c0.fill = GridBagConstraints.HORIZONTAL;
            c0.gridx = 0;
            c0.gridy = 1;
            c0.gridwidth = 1;
            c0.ipadx = 5;
            c0.ipady = 5;
            panel0.add(new JLabel("Application secret: "), c0);
        }
        {
            c0.anchor = GridBagConstraints.LINE_START;
            c0.fill = GridBagConstraints.HORIZONTAL;
            c0.gridx = 1;
            c0.gridy = 1;
            c0.gridwidth = 1;
            c0.ipadx = 5;
            c0.ipady = 5;
            panel0.add(applicationKeyLbl, c0);
        }
        
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        {
            c.anchor = GridBagConstraints.PAGE_START;
            c.gridx = 0;
            c.gridy = 0;
            add(timeFld, c);
        }
        {
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 1;
            add(panel0, c);
        }
    }

    private void initComponents() {
        dateformat = new SimpleDateFormat(defaultDateTimeFormat);
        timeFld = new JLabel("");
        timeFld.setText(formatDateTime(new Date()));
        registrationLlb = new JLabel("");
        applicationKeyLbl = new JLabel("");
    }

    private String formatDateTime(Date date) {
        String ret = "";
        try {
            ret = dateformat.format(date);
        } catch (Exception e) {
            ret = "N/A";
            logger.debug("Unable to format time " + date + ": " + e.getMessage(), e);
        }
        return ret;
    }
}
