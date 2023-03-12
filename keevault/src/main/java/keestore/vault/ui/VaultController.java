/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import keestore.vault.crypto.VaultCrypto;
import keestore.vault.model.Vault;
import keestore.vault.model.VaultAccess;
import keestore.vault.ui.table.VaultEntry;
import kkdt.generictable.GenericTableController;

public class VaultController implements ActionListener {
    private final JFrame reference;
    private final GenericTableController<VaultEntry> tableController;
    private final VaultAccess vaultAccess;
    
    public VaultController(JFrame reference, GenericTableController<VaultEntry> tableController, VaultAccess vaultAccess) {
        this.reference = reference;
        this.tableController = tableController;
        this.vaultAccess = vaultAccess;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()) {
        case "Create":
            handleCreate();
            break;
        case "Open":
            break;
        case "Delete":
            break;
        }
    }
    
    private void handleCreate() {
        String name = (String) JOptionPane.showInputDialog(reference, 
            "Enter vault name",
            "Create New Vault", 
            JOptionPane.PLAIN_MESSAGE, 
            null, null, "");
        
        if (name != null) {
            if (name.length() == 0) {
                JOptionPane.showMessageDialog(reference, 
                    "Vault description must be non-empty", "Error",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                Vault vault = new Vault(name);
                if(vaultAccess.getKee(vault.getId()) != null) {
                    throw new IllegalArgumentException("Duplicate vault " + vault.getId());
                }
                saveVault(vault);
            }
        }
    }
    
    public void saveVault(Vault v) {
        vaultAccess.addKee(v);
        addEntry(v);
    }
    
    public void addEntry(Vault v) {
        VaultEntry entry = new VaultEntry();
        entry.setId(v.getId());
        entry.setName(v.getName());
        tableController.addEntry(entry);
    }
    
    public void loadTable(VaultCrypto crypto) throws IOException {
        if(crypto != null) {
            List<Vault> values = crypto.loadVault();
            values.forEach(v -> {
                saveVault(v);
            });
        }
    }

}
