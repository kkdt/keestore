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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import keestore.access.Kee;
import keestore.vault.Util;
import keestore.vault.VaultEditor;
import keestore.vault.controller.VaultController;
import keestore.vault.controller.VaultItemController;
import keestore.vault.model.VaultItem;
import keestore.vault.ui.table.VaultItemTable;

/**
 * A dialog for editing a {@code Vault} that allows for the following actions:
 * <ol>
 * <li>Add: Add a new vault item</li>
 * <li>Delete: Delete a vault item</li>
 * <li>Cancel: Discard all edits and close the editor</li>
 * <li>Edit/Save: Put the editor in 'edit' mode and save the model</li>
 * </ol>
 * 
 * @author thinh ho
 *
 */
public class VaultEditDialog extends JDialog implements VaultEditor {
    private static final long serialVersionUID = -2348362433621883193L;

    private final VaultController vaultController;
    private final VaultItemController itemController;
    private final Kee item;
    private JTextField nameFld;
    private VaultItemTable table;
    private JButton addBtn;
    private JButton deleteBtn;
    private JButton saveBtn;
    private JButton cancelBtn;

    public VaultEditDialog(Window parent, String title, Kee item, VaultController vaultController) {
        super(parent, title, Dialog.ModalityType.DOCUMENT_MODAL);
        this.itemController = new VaultItemController();
        this.vaultController = vaultController;
        this.item = item;
        initComponents();
        layoutComponents();
        initListeners();
        this.setLocationRelativeTo(null);
    }

    private void initComponents() {
        nameFld = new JTextField("", 15);
        table = new VaultItemTable();
        addBtn = new JButton("Add");
        deleteBtn = new JButton("Delete");
        saveBtn = new JButton("Edit");
        cancelBtn = new JButton("Cancel");

        // initially disabled
        enableTableControls(false);
        nameFld.setEnabled(false);
        
        // load initial item 
        if(item != null) {
            nameFld.setText(item.getName());
            item.toMap().entrySet().stream()
                .filter(e -> !e.getKey().equals(item.idKey()) && !e.getKey().equals(item.nameKey()))
                .map(e -> {
                    VaultItem model = new VaultItem();
                    model.setKey(e.getKey());
                    model.setValue((String)e.getValue());
                    model.setEncryptKey(true);
                    model.setEncryptValue(true);
                    return model;
                }).forEach(i -> {
                    table.addItem(i);
                    itemController.getVaultItems().add(i);
                });
        }
    }

    private void enableTableControls(boolean enable) {
        addBtn.setEnabled(enable);
        deleteBtn.setEnabled(enable);
    }

    private JPanel createLeftPanel() {
        JPanel panel0 = new JPanel(new GridBagLayout());
        GridBagConstraints c0 = new GridBagConstraints();
        {
            c0.anchor = GridBagConstraints.LINE_START;
            c0.fill = GridBagConstraints.HORIZONTAL;
            c0.gridx = 0;
            c0.gridy = 0;
            c0.insets = new Insets(5, 5, 5, 5);
            panel0.add(saveBtn, c0);
        }
        {
            c0.anchor = GridBagConstraints.LINE_START;
            c0.fill = GridBagConstraints.HORIZONTAL;
            c0.gridx = 1;
            c0.gridy = 0;
            c0.insets = new Insets(5, 5, 5, 5);
            panel0.add(cancelBtn, c0);
        }
        panel0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder()));

        JPanel panel1 = new JPanel(new GridBagLayout());
        GridBagConstraints c1 = new GridBagConstraints();
        {
            c1.anchor = GridBagConstraints.LINE_START;
            c1.fill = GridBagConstraints.HORIZONTAL;
            c1.gridx = 0;
            c1.gridy = 0;
            c1.insets = new Insets(5, 5, 5, 5);
            panel1.add(addBtn, c1);
        }
        {
            c1.anchor = GridBagConstraints.LINE_START;
            c1.fill = GridBagConstraints.HORIZONTAL;
            c1.gridx = 1;
            c1.gridy = 0;
            c1.insets = new Insets(5, 5, 5, 5);
            panel1.add(deleteBtn, c1);
        }
        panel1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Table Controls"));

        JPanel info = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        {
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            c.insets = new Insets(5, 5, 5, 5);
            info.add(new JLabel("Name: "), c);
        }
        {
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 0;
            c.insets = new Insets(5, 5, 5, 5);
            info.add(nameFld, c);
        }
        // separator
        {
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 2;
            c.insets = new Insets(5, 5, 5, 5);
            info.add(new JSeparator(JSeparator.HORIZONTAL), c);
        }
        // controls
        {
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 2;
            c.insets = new Insets(5, 5, 5, 5);
            info.add(panel1, c);
        }
        {
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 3;
            c.gridwidth = 2;
            c.insets = new Insets(5, 5, 5, 5);
            info.add(panel0, c);
        }
        return info;
    }

    private void layoutComponents() {
        // left panel
        JPanel panel0 = createLeftPanel();

        // table
        JScrollPane scrollpane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        JPanel tablepanel = new JPanel(new BorderLayout(10, 10));
        tablepanel.add(scrollpane, BorderLayout.CENTER);

        // main contents
        JPanel contents = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        {
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            contents.add(panel0, c);
        }
        {
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 0;
            contents.add(tablepanel, c);
        }

        // dialog
        getContentPane().add(contents);
        pack();
    }

    private void initListeners() {
        cancelBtn.addActionListener(e -> {
            VaultEditDialog.this.setVisible(false);
            VaultEditDialog.this.dispose();
        });

        saveBtn.addActionListener(e -> {
            if ("Edit".equals(e.getActionCommand())) {
                Util.withEventQueue(() -> {
                    saveBtn.setText("Save");
                    enableTableControls(true);
                    nameFld.setEnabled(true);
                    table.hideValues(false);
                });
            } else {
                Util.withEventQueue(() -> {
                    saveBtn.setText("Edit");
                    enableTableControls(false);
                    nameFld.setEnabled(false);
                    table.hideValues(true);
                });
            }
        });
        
        table.getModel().addTableModelListener(itemController.tableModelListener());
        table.addMouseListener(itemController.mouseListener(this));
        saveBtn.addActionListener(vaultController.actionListener(this, itemController));
        deleteBtn.addActionListener(itemController.tableActionListener(table));
        addBtn.addActionListener(e -> {
            Util.withEventQueue(() -> {
                final VaultItemEditorDialog editor = itemController.editVaultItem(this, "New Kee Item", null)
                    .withController(itemController.editorActionHandler(table));
                editor.setVisible(true);
            });
        });
    }

    @Override
    public Kee getOriginalVault() {
        return item;
    }

    @Override
    public String getVaultName() {
        return nameFld.getText();
    }

}
