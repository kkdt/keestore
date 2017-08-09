/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.function.BiConsumer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import keestore.vault.Util;
import keestore.vault.VaultItemEditor;
import keestore.vault.model.VaultItem;

public class VaultItemEditorDialog extends JDialog implements VaultItemEditor {
    private static final long serialVersionUID = -606137868735907379L;
    
    private JTextField key;
    private JTextField value;
    private JCheckBox encryptKey;
    private JCheckBox encryptValue;
    private JButton saveBtn;
    private JButton cancelBtn;
    private VaultItem originalItem;

    public VaultItemEditorDialog(Window parent, String title) {
        super(parent, title, Dialog.ModalityType.DOCUMENT_MODAL);
        setLocationRelativeTo(parent);
        initComponents();
        layoutComponents();
    }
    
    @Override
    public VaultItem getOriginalItem() {
        return originalItem;
    }

    @Override
    public void closeEditor() {
        Util.withEventQueue(() -> {
            this.setVisible(false);
            this.dispose();
        });
    }

    @Override
    public VaultItem getVaultItem() {
        VaultItem item = new VaultItem();
        item.setEncryptKey(encryptKey.isSelected());
        item.setEncryptValue(encryptValue.isSelected());
        item.setKey(key.getText());
        item.setValue(value.getText());
        return item;
    }
    
    public VaultItemEditorDialog withItem(VaultItem item) {
        this.originalItem = item;
        Util.withEventQueue(() -> {
            key.setText(item.getKey() != null ? item.getKey() : "");
            value.setText(item.getValue() != null ? item.getValue() : "");
            encryptKey.setSelected(item.isEncryptKey());
            encryptValue.setSelected(item.isEncryptValue());
        });
        return this;
    }

    public VaultItemEditorDialog withController(BiConsumer<String, VaultItemEditor> c) {
        saveBtn.addActionListener(e -> {
            c.accept(e.getActionCommand(), this);
        });
        cancelBtn.addActionListener(e -> {
            c.accept(e.getActionCommand(), this);
        });
        return this;
    }

    private void layoutComponents() {
        JPanel panel0 = new JPanel();
        panel0.add(saveBtn);
        panel0.add(cancelBtn);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        {
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            c.insets = new Insets(5, 5, 5, 5);
            panel.add(new JLabel("Key: "), c);
        }
        {
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 0;
            c.insets = new Insets(5, 5, 5, 5);
            panel.add(key, c);
        }
        {
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 0;
            c.insets = new Insets(5, 5, 5, 5);
            panel.add(encryptKey, c);
        }
        {
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 1;
            c.insets = new Insets(5, 5, 5, 5);
            panel.add(new JLabel("Value: "), c);
        }
        {
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 1;
            c.insets = new Insets(5, 5, 5, 5);
            panel.add(value, c);
        }
        {
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 1;
            c.insets = new Insets(5, 5, 5, 5);
            panel.add(encryptValue, c);
        }

        setLayout(new BorderLayout(5, 5));
        add(panel, BorderLayout.CENTER);
        add(panel0, BorderLayout.SOUTH);
        pack();
    }

    private void initComponents() {
        this.key = new JTextField("", 15);
        this.value = new JTextField("", 20);
        this.encryptKey = new JCheckBox("Encrypt");
        this.encryptValue = new JCheckBox("Encrypt");
        this.saveBtn = new JButton("Save");
        this.cancelBtn = new JButton("Cancel");
    }

}
