/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;

import keestore.vault.Util;
import keestore.vault.crypto.VaultCrypto;
import keestore.vault.crypto.VaultCryptoInitialized;

/**
 * The main application window that can be initialized within a Spring
 * Application Context and be notified of an {@code VaultCryptoInitialized}
 * event which will then, fully initialize the window.
 * 
 * @author thinh ho
 *
 */
public class VaultConsoleWindow extends JFrame implements ApplicationListener<VaultCryptoInitialized> {
    private static final long serialVersionUID = 6173573611734161069L;
    private static final Logger logger = Logger.getLogger(VaultConsoleWindow.class);
    private static final String title = "keestore: Vault";

    private VaultCrypto crypto;
    private Vaults vaults;
    private JMenuBar menubar;

    public VaultConsoleWindow() {
        super(title);
        initComponents();
        layoutComponents();
        setLocationRelativeTo(null);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        JMenuItem eMenuItem = new JMenuItem("Exit");
        eMenuItem.setMnemonic(KeyEvent.VK_E);
        eMenuItem.setToolTipText("Exit application");
        eMenuItem.addActionListener((ActionEvent event) -> {
            System.exit(0);
        });
        file.add(eMenuItem);
        menubar.add(file);
        return menubar;
    }

    private void initComponents() {
        // default UI look and feel
        JFrame.setDefaultLookAndFeelDecorated(false);
        Util.setLookAndFeelDefaults();
        SwingUtilities.updateComponentTreeUI(this);
        // contents
        menubar = createMenuBar();
        vaults = new Vaults(this);
    }

    private void layoutComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(getIcon().getImage());
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setJMenuBar(menubar);
        add(vaults);
        pack();
    }

    ImageIcon getIcon() {
        URL iconURL = getClass().getResource("/keestore/vault/img/laptop.icns");
        ImageIcon icon = new ImageIcon(iconURL);
        return icon;
    }

    /**
     * Expose for programmatic initialization.
     */
    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(VaultConsoleWindow.this));
        Util.withEventQueue(() -> {
            logger.info("Initializing main view");
            vaults.setVaultCrypto(this.crypto);
            vaults.init();
            pack();
            
            setResizable(false);
            setVisible(true);
        });
    }

    @Override
    public void onApplicationEvent(VaultCryptoInitialized event) {
        logger.info("Initializing display with vault conext: " 
            + event.getCrypto() + ", source: " + event.getSource());
        this.crypto = event.getCrypto();
        init();
    }
}
