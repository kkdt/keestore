/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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

public class VaultConsoleBuilder implements ApplicationListener<VaultCryptoInitialized> {
    private static final Logger logger = Logger.getLogger(VaultConsoleBuilder.class);
    private static final String title = "keestore: Vault";
    
    @Override
    public void onApplicationEvent(VaultCryptoInitialized event) {
        VaultCrypto crypto = event.getCrypto();
        if(crypto == null) {
            logger.error("Invalid configuration: No Vault Crypto found");
            System.exit(1);
        }
        
        JFrame window = new JFrame(title);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setIconImage(getIcon().getImage());
        window.setLayout(new FlowLayout(FlowLayout.LEFT));
        window.setJMenuBar(createMenuBar());
        
        // create contents
        ContentPanel contents = new ContentPanel()
            .layoutContents(c -> {
                c.setLayout(new BorderLayout());
                c.add(new Vaults(window).withVaultCrypto(crypto), BorderLayout.CENTER);
            }).layoutComponents();
        
        // default UI look and feel
        JFrame.setDefaultLookAndFeelDecorated(false);
        Util.setLookAndFeelDefaults();
        SwingUtilities.updateComponentTreeUI(window);
        // exception handler
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(window));
        
    }
    
    ImageIcon getIcon() {
        URL iconURL = getClass().getResource("/keestore/vault/img/laptop.icns");
        ImageIcon icon = new ImageIcon(iconURL);
        return icon;
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        JMenuItem exit = new JMenuItem("Exit");
        exit.setMnemonic(KeyEvent.VK_E);
        exit.setToolTipText("Exit application");
        exit.addActionListener(e -> {
            System.exit(0);
        });
        file.add(exit);
        JMenu help = Util.createHelpMenu(VaultConsoleWindow.class, "vault");
        
        menubar.add(file);
        menubar.add(help);
        return menubar;
    }

}
