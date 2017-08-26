/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Various utility methods to support the UI.
 * 
 * @author thinh ho
 *
 */
public class Util {
    /***
     * Helper that executes the specified function within the Event Queue
     * thread.
     * 
     * @param r
     */
    public static void withEventQueue(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }
    
    public static ImageIcon getIcon() {
        URL iconURL = Util.class.getResource("/keestore/vault/img/laptop.icns");
        ImageIcon icon = new ImageIcon(iconURL);
        return icon;
    }
    
    public static ActionListener infoActionListener(String type) {
        return e -> {
            StringBuilder buffer = new StringBuilder();
            switch(type) {
            case "vault":
                buffer.append("A 'vault' contains key-value items that you can enrypt.");
                buffer.append("\nUse this screen to create one or more vaults. Click 'Encrypt' to save.");
                break;
            case "vaultItem":
                buffer.append("A key-value item can have any meaning and is user-specified.");
                buffer.append("\nThis pair will be encrypted. Click 'Edit' to allow table editing, ");
                buffer.append("\nand click 'Save' to save the data. When in Edit mode, mouse-over");
                buffer.append("\ncells to view contents.");
                break;
            }
            JOptionPane.showMessageDialog(null, buffer.toString(),"Info",JOptionPane.INFORMATION_MESSAGE, getIcon());
        };
    }
    
    public static JMenu createHelpMenu(Class<?> origin, String infoType) {
        JMenu help = new JMenu("Help");
        help.setMnemonic(KeyEvent.VK_H);
        JMenuItem about = new JMenuItem("About");
        about.setMnemonic(KeyEvent.VK_A);
        about.setToolTipText("About");
        about.addActionListener(Util.aboutActionListener(origin));
        JMenuItem info = new JMenuItem("Info");
        info.setMnemonic(KeyEvent.VK_I);;
        info.setToolTipText("Information");
        info.addActionListener(Util.infoActionListener(infoType));
        help.add(info);
        help.addSeparator();
        help.add(about);
        return help;
    }
    
    public static ActionListener aboutActionListener(final Class<?> type) {
        return e -> {
            Package p = type.getPackage();
            String version = p.getImplementationVersion();
            String spec = p.getSpecificationVersion();
            String vendor = p.getImplementationVendor();
            StringBuilder buffer = new StringBuilder(p.getImplementationTitle());
            buffer.append("\nVersion: " + version);
            buffer.append("\nSpecification: " + p.getSpecificationTitle() + " " + spec);
            buffer.append("\nAuthor: " + vendor);
            JOptionPane.showMessageDialog(null, buffer.toString(),"About",JOptionPane.INFORMATION_MESSAGE, getIcon());
        };
    }

    /**
     * Default look and feel for the UI (online source somewhere).
     */
    public static void setLookAndFeelDefaults() {
        Font defaultFont = new Font("Arial", Font.PLAIN, 9);
        UIManager.put("defaultFont", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("ToggleButton.font", defaultFont);
        UIManager.put("RadioButton.font", defaultFont);
        UIManager.put("CheckBox.font", defaultFont);
        UIManager.put("ColorChooser.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("List.font", defaultFont);
        UIManager.put("MenuBar.font", defaultFont);
        UIManager.put("MenuItem.font", defaultFont);
        UIManager.put("RadioButtonMenuItem.font", defaultFont);
        UIManager.put("CheckBoxMenuItem.font", defaultFont);
        UIManager.put("Menu.font", defaultFont);
        UIManager.put("PopupMenu.font", defaultFont);
        UIManager.put("OptionPane.font", defaultFont);
        UIManager.put("Panel.font", defaultFont);
        UIManager.put("ProgressBar.font", defaultFont);
        UIManager.put("ScrollPane.font", defaultFont);
        UIManager.put("Viewport.font", defaultFont);
        UIManager.put("TabbedPane.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("TableHeader.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("PasswordField.font", defaultFont);
        UIManager.put("TextArea.font", defaultFont);
        UIManager.put("TextPane.font", defaultFont);
        UIManager.put("EditorPane.font", defaultFont);
        UIManager.put("TitledBorder.font", defaultFont);
        UIManager.put("ToolBar.font", defaultFont);
        UIManager.put("ToolTip.font", defaultFont);
        UIManager.put("Tree.font", defaultFont);
        UIManager.put("Table.alternateRowColor", new Color(240, 240, 240));
    }
}
