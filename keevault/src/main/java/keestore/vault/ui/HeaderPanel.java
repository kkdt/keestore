/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import keestore.vault.Util;
import keestore.vault.crypto.VaultCrypto;

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
    private JLabel vaultLbl;
    private JLabel secretKeyLbl;
    private JLabel soureLbl;
    private JLabel applicationIdLbl;
    private JLabel pubPriLbl;
    private JPanel controls;

    public HeaderPanel() {
        initComponents();
        layoutComponents();
    }
    
    public void setVaultCrypto(VaultCrypto crypto) {
        Util.withEventQueue(() -> {
            secretKeyLbl.setText(crypto.getSecretKey());
            soureLbl.setText(crypto.getSource().getPath());
            applicationIdLbl.setText(crypto.getSeed());
            pubPriLbl.setText(crypto.hasPublicPrivateKeys() ? "Valid" : "N/A");
            vaultLbl.setText(crypto.getVault().getAbsolutePath());
            validate();
        });
    }

    public void setTime(Date date) {
        Util.withEventQueue(() -> {
            timeFld.setText(formatDateTime(date));
        });
    }
    
    public void withAction(String action, ActionListener l) {
        JButton b = new JButton(action);
        b.addActionListener(l);
        controls.add(b);
        Util.withEventQueue(() -> {
            controls.repaint();
        });
    }

    private void layoutComponents() {
        int x = 0, y = 0;
        JPanel panel0 = new JPanel(new GridBagLayout());
        panel0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Registration"));
        GridBagConstraints c0 = new GridBagConstraints();
        {
            c0.anchor = GridBagConstraints.LINE_START;
            c0.fill = GridBagConstraints.HORIZONTAL;
            c0.gridx = x++;
            c0.gridy = y;
            c0.gridwidth = 1;
            c0.ipadx = 3;
            c0.ipady = 3;
            panel0.add(new JLabel("Source "), c0);
        }
        {
            c0.anchor = GridBagConstraints.LINE_START;
            c0.fill = GridBagConstraints.HORIZONTAL;
            c0.gridx = x++;
            c0.gridy = y;
            c0.gridwidth = 1;
            c0.ipadx = 3;
            c0.ipady = 3;
            panel0.add(soureLbl, c0);
        }
        y++;
        x = 0;
        {
            c0.anchor = GridBagConstraints.LINE_START;
            c0.fill = GridBagConstraints.HORIZONTAL;
            c0.gridx = x++;
            c0.gridy = y;
            c0.gridwidth = 1;
            c0.ipadx = 3;
            c0.ipady = 3;
            panel0.add(new JLabel("Vault "), c0);
        }
        {
            c0.anchor = GridBagConstraints.LINE_START;
            c0.fill = GridBagConstraints.HORIZONTAL;
            c0.gridx = x++;
            c0.gridy = y;
            c0.gridwidth = 1;
            c0.ipadx = 3;
            c0.ipady = 3;
            panel0.add(vaultLbl, c0);
        }
        y++;
        x = 0;
        {
            c0.anchor = GridBagConstraints.LINE_START;
            c0.fill = GridBagConstraints.HORIZONTAL;
            c0.gridx = x++;
            c0.gridy = y;
            c0.gridwidth = 1;
            c0.ipadx = 3;
            c0.ipady = 3;
            panel0.add(new JLabel("Secret Key "), c0);
        }
        {
            c0.anchor = GridBagConstraints.LINE_START;
            c0.fill = GridBagConstraints.HORIZONTAL;
            c0.gridx = x++;
            c0.gridy = y;
            c0.gridwidth = 1;
            c0.ipadx = 3;
            c0.ipady = 3;
            panel0.add(secretKeyLbl, c0);
        }
        y++;
        x = 0;
        {
            c0.anchor = GridBagConstraints.LINE_START;
            c0.fill = GridBagConstraints.HORIZONTAL;
            c0.gridx = x++;
            c0.gridy = y;
            c0.gridwidth = 1;
            c0.ipadx = 3;
            c0.ipady = 3;
            panel0.add(new JLabel("Pub/Pri Keys "), c0);
        }
        {
            c0.anchor = GridBagConstraints.LINE_START;
            c0.fill = GridBagConstraints.HORIZONTAL;
            c0.gridx = x++;
            c0.gridy = y;
            c0.gridwidth = 1;
            c0.ipadx = 3;
            c0.ipady = 3;
            panel0.add(pubPriLbl, c0);
        }
        y++;
        x = 0;
        {
            c0.anchor = GridBagConstraints.LINE_START;
            c0.fill = GridBagConstraints.HORIZONTAL;
            c0.gridx = x++;
            c0.gridy = y;
            c0.gridwidth = 1;
            c0.ipadx = 3;
            c0.ipady = 3;
            panel0.add(new JLabel("Application ID "), c0);
        }
        {
            c0.anchor = GridBagConstraints.LINE_START;
            c0.fill = GridBagConstraints.HORIZONTAL;
            c0.gridx = x++;
            c0.gridy = y;
            c0.gridwidth = 1;
            c0.ipadx = 3;
            c0.ipady = 3;
            panel0.add(applicationIdLbl, c0);
        }
        
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        {
            c.anchor = GridBagConstraints.PAGE_START;
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 2;
            add(timeFld, c);
        }
        {
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 1;
            add(panel0, c);
        }
        {
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 1;
            c.gridwidth = 1;
            c.fill = GridBagConstraints.BOTH;
            add(controls, c);
        }
    }

    private void initComponents() {
        dateformat = new SimpleDateFormat(defaultDateTimeFormat);
        timeFld = new JLabel("");
        timeFld.setText(formatDateTime(new Date()));
        secretKeyLbl = new JLabel("");
        soureLbl = new JLabel("");
        applicationIdLbl = new JLabel("");
        pubPriLbl = new JLabel("");
        vaultLbl = new JLabel("");
        
        controls = new JPanel();
        BoxLayout layout = new BoxLayout(controls, BoxLayout.Y_AXIS);
        controls.setLayout(layout);
        controls.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Crypto Controls"));
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
