/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.controller;

import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import keestore.vault.Util;
import keestore.vault.VaultItemEditor;
import keestore.vault.model.VaultItem;
import keestore.vault.ui.VaultItemEditorDialog;
import keestore.vault.ui.table.VaultItemTable;
import keestore.vault.ui.table.VaultItemTableModel;

/**
 * For each {@code Vault}, this controller keeps track of the item update(s).
 * 
 * @author thinh ho
 *
 */
public class VaultItemController {
    
    private List<VaultItem> items = new ArrayList<>();
    
    /**
     * The underlying model.
     * 
     * @return
     */
    public List<VaultItem> getVaultItems() {
        return items;
    }
    
    /**
     * This controller can be attached as a table model listener so that it will
     * synchronizes its data with the table model on updates.
     * 
     * @return
     */
    public TableModelListener tableModelListener() {
        return e -> {
            if(e.getSource() instanceof VaultItemTableModel) {
                VaultItemTableModel sourceModel = (VaultItemTableModel)e.getSource();
                switch(e.getType()) {
                case TableModelEvent.INSERT:
                    break;
                case TableModelEvent.DELETE:
                    break;
                case TableModelEvent.UPDATE:
                    // clear and add-all
                    items.clear();
                    items.addAll(sourceModel.getAll());
                    break;
                }
            }
        };
    }
    
    /**
     * Listens for mouse events on a {@code VaultItemTable}.
     * 
     * @param reference
     * @return
     */
    public MouseListener mouseListener(final Window reference) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() instanceof VaultItemTable) {
                    VaultItemTable t = (VaultItemTable) e.getSource();
                    if (t.getSelectedRowCount() == 1 && e.getClickCount() == 2) {
                        
                        Util.withEventQueue(() -> {
                            VaultItem selected = t.getSelectedItem();
                            final VaultItemEditorDialog editor = editVaultItem(reference, "Edit Vault Item", selected)
                                .withController(editorActionHandler(t));
                            editor.setVisible(true);
                        });
                    }
                }
            }
        };
    }
    
    /**
     * Create a dialog to add/edit a vault item.
     * 
     * @param reference the parent.
     * @param title the dialog title.
     * @param selected (optional) the initial value.
     * @return
     */
    public VaultItemEditorDialog editVaultItem(Window reference, String title, VaultItem selected) {
        final VaultItemEditorDialog viewer = new VaultItemEditorDialog(reference, title);
        if(selected != null) {
            viewer.withItem(selected);
        }
        return viewer;
    }
    
    /**
     * Handles the Delete action on the specified table as an {@code ActionListener}.
     * 
     * @param table
     * @return
     */
    public ActionListener tableActionListener(final VaultItemTable table) {
        return e -> {
            switch(e.getActionCommand()) {
            case "Delete":
                table.deleteSelectedItem();
                break;
            }
        };
    }
    
    /**
     * Handles the following actions: Save, Delete, Cancel, on the specified table.
     * 
     * @param table
     * @return the callback handler.
     */
    public BiConsumer<String, VaultItemEditor> editorActionHandler(final VaultItemTable table) {
        return (action, editor) -> {
            switch(action) {
            case "Save":
                VaultItem updated = editor.getVaultItem();
                String error = validateItem(editor).get();
                if(error.length() > 0) {
                    throw new IllegalArgumentException(error);
                }
                
                if(editor.getOriginalItem() != null) {
                    table.updateVaultItem(table.getSelectedRow(), updated);
                } else {
                    table.addItem(updated);
                }
                editor.closeEditor();
                break;
            case "Delete":
                table.deleteSelectedItem();
                break;
            case "Cancel":
                editor.closeEditor();
                break;
            }
        };
    }
    
    /**
     * Vault items cannot have the same key. 
     * 
     * @return
     */
    private Predicate<VaultItem> isDuplicateItem() {
        return i -> {
            return items.size() > 0 
                && items.stream().filter(existing -> i.getKey().equals(existing.getKey())).count() > 0;
        };
    }
    
    /**
     * Validation rules when adding/editing an item.
     * 
     * @param editor
     * @return
     */
    private Supplier<String> validateItem(final VaultItemEditor editor) {
        return () -> {
            VaultItem item = editor.getVaultItem();
            if(item.getKey() == null || item.getKey().length() == 0 
                || item.getValue() == null || item.getValue().length() == 0) 
            {
                return "Item must contain non-null key and value";
            }
            if((editor.getOriginalItem() == null || !editor.getOriginalItem().getKey().equals(item.getKey())) 
                && isDuplicateItem().test(item)) 
            {
                return "Cannot add/update " + item.getKey() +", duplicate key";
            }
            return "";
        };
    }
}
