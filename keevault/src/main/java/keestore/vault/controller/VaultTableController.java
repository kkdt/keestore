/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;

import keestore.access.Kee;
import keestore.vault.model.Vault;
import keestore.vault.ui.table.KeeTable;
import keestore.vault.ui.table.KeeTableModel;

/**
 * A table controller for {@code KeeTable} and exposes functionalities via
 * AWT event handlers; mainly table, table input, and table model listeners. 
 * 
 * @author thinh ho
 *
 */
public class VaultTableController {
    private static final Logger logger = Logger.getLogger(VaultTableController.class);
    
    final private KeeTable table;
    
    /**
     * Must instantiate with a table to control.
     * 
     * @param table
     */
    public VaultTableController(KeeTable table) {
        this.table = table;
    }
    
    /**
     * Attach a table mouse listener that calls back to the specified controller
     * to take action. 
     * 
     * @param reference
     * @param controller
     * @return
     */
    public static MouseListener mouseListener(final JFrame reference, final VaultController controller) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() instanceof KeeTable) {
                    KeeTable t = (KeeTable) e.getSource();
                    if (t.getSelectedRowCount() == 1 && e.getClickCount() == 2) {
                        // TODO: handle this better
                        controller.editSelected(reference, t);
                    }
                }
            }
        };
    }
    
    /**
     * Handler for action commands on the underlying table.
     * <ol>
     * <li>Create</li>
     * <li>Delete</li>
     * <li>Open</li>
     * </ol>
     * 
     * @param reference
     * @param controller
     * @return
     */
    public ActionListener actionListener(final JFrame reference, final VaultController controller) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch(e.getActionCommand()) {
                case "Create":
                    handleCreate(reference, controller);
                    break;
                case "Open":
                    controller.editSelected(reference, table);
                    break;
                case "Delete":
                    handleDelete(table.getSelectedKee());
                    break;
                default:
                    logger.debug("Unrecognized action: " + e.getActionCommand());
                    break;
                }
            }
        };
    }
    
    /**
     * Any update to the table model will callback to this controller to update
     * the underlying model with table data.
     * 
     * @param controller
     * @return
     */
    public TableModelListener tableModelListener(final VaultController controller) {
        return e -> {
            if(e.getSource() instanceof KeeTableModel) {
                KeeTableModel sourceModel = (KeeTableModel)e.getSource();
                switch(e.getType()) {
                case TableModelEvent.INSERT:
                    break;
                case TableModelEvent.DELETE:
                    break;
                case TableModelEvent.UPDATE:
                    controller.updateAll(sourceModel.getAll());
                    break;
                }
            }
        };
    }
    
    /**
     * Delete will get the current selected item and delete it from the table
     * model, notifying any model listeners that the underlying model has changed.
     * 
     * @param selected
     */
    private void handleDelete(Kee selected) {
        if(selected != null) {
            table.deleteKee(selected);;
        }
    }
    
    /**
     * Create will essentially update the table model, notifying any model
     * listeners that the underlying model has changed. 
     * 
     * @param reference
     * @param controller
     */
    private void handleCreate(final JFrame reference, final VaultController controller) {
        String description = (String) JOptionPane.showInputDialog(reference, 
            "Enter vault description",
            "Create New Vault", 
            JOptionPane.PLAIN_MESSAGE, 
            null, null, "");
        
        if (description != null) {
            if (description.length() == 0) {
                JOptionPane.showMessageDialog(reference, 
                    "Vault description must be non-empty", "Error",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                Vault vault = controller.createVault(description);
                if(controller.getKee(vault.getId()) != null) {
                    throw new IllegalArgumentException("Duplicate vault " + vault.getId());
                }
                table.addKee(vault);
            }
        }
    }
    
}
