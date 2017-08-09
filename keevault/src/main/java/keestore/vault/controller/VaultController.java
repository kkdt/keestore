/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.controller;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;

import org.springframework.util.Assert;

import keestore.access.Kee;
import keestore.access.KeeAccess;
import keestore.vault.Util;
import keestore.vault.VaultEditor;
import keestore.vault.model.Vault;
import keestore.vault.ui.VaultEditDialog;
import keestore.vault.ui.table.KeeTable;

/**
 * The main controller for an implementation of {@code KeeAccess}.
 * 
 * @author thinh ho
 *
 */
public class VaultController implements KeeAccess {
    private final KeeAccess access;

    /**
     * Must instantiate with a non-null {@code KeeAccess}.
     * 
     * @param access
     */
    public VaultController(KeeAccess access) {
        this.access = access;
        Assert.notNull(access, "Invalid instantiation object");
    }

    @Override
    public void addKee(Kee item) {
        if (item != null) {
            access.addKee(item);
        }
    }

    @Override
    public void deleteKee(String id) {
        access.deleteKee(id);
    }

    @Override
    public void deleteKee(Kee item) {
        access.deleteKee(item);
    }
    
    @Override
    public Kee getKee(String id) {
        return access.getKee(id);
    }
    
    /**
     * Create the appropriate vault in the underlying model.
     * 
     * @param id
     * @return
     */
    public Vault createVault(String id) {
        return new Vault(id);
    }
    
    /**
     * Saving the vault captured from the viewer - performs a delete/add from the
     * specified table.
     * 
     * @param viewer the form.
     * @param table the table that also needs to be updated.
     */
    public static void saveKee(VaultEditor viewer, KeeTable table) {
        Kee updated = viewer.getOriginalVault();
        table.deleteKee(updated.getId());
        table.addKee(updated);
    }
    
    /**
     * Editing always involve a table (i.e. the user selecting an entry from the
     * table).
     * 
     * @param parent
     * @param table
     */
    public void editSelected(JFrame parent, KeeTable table) {
        Util.withEventQueue(() -> {
            if(table.getSelectedKee() != null) {
                Kee item = table.getSelectedKee();
                VaultEditDialog view = new VaultEditDialog(parent, "Vault: " + item.getName(), item, this);
                view.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        if (e.getSource() instanceof VaultEditor) {
                            saveKee((VaultEditor)e.getSource(), table);
                        }
                    }
                });
                view.setVisible(true);
            }
        });
    }
    
    /**
     * Handles the Save action as an {@code ActionListener} that can be attached
     * to a button.
     * 
     * @param vaultViewer the form.
     * @param itemController the controller containing the updated item(s).
     * @return
     */
    public ActionListener actionListener(final VaultEditor vaultViewer, 
        final VaultItemController itemController) 
    {
        return e -> {
            switch(e.getActionCommand()) {
            case "Save":
                Kee original = vaultViewer.getOriginalVault();
                original.clear();
                original.setName(vaultViewer.getVaultName());
                itemController.getVaultItems().forEach(i -> {
                    original.put(i.getKey(), i.getValue());
                });
                break;
            }
        };
    }

    @Override
    public void clear() {
        access.clear();
    }
    
    public void updateAll(List<Kee> all) {
        if(all != null && all.size() > 0) {
            clear();
            all.forEach(a -> access.addKee(a));
        }
    }

}
