/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.ui.table;

import java.util.Enumeration;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import keestore.vault.Util;
import keestore.vault.model.VaultItem;

/**
 * A table that captures all vault item(s) for a single vault.
 * 
 * @author thinh ho
 *
 */
public class VaultItemTable extends JTable {
    private static final long serialVersionUID = -5402705888985049390L;
    
    private final VaultItemTableModel model;

    public VaultItemTable() {
        model = new VaultItemTableModel();
        setModel(model);
        setRowSelectionAllowed(true);
//        setColumnSelectionAllowed(true);
        setCellSelectionEnabled(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        configureColumnModel();
    }
    
    /**
     * Control whether or not the table should hide/show its data values.
     * 
     * @param hide
     */
    public void hideValues(boolean hide) {
        Enumeration<TableColumn> columnModel = getColumnModel().getColumns();
        while(columnModel.hasMoreElements()) {
            TableColumn column = columnModel.nextElement();
            ((VaultTableCellRenderer)column.getCellRenderer()).setHideValue(hide);
        }
        Util.withEventQueue(() -> repaint());
    }
    
    private void configureColumnModel() {
        getColumnModel().getColumn(1).setPreferredWidth(300);
        Enumeration<TableColumn> columnModel = getColumnModel().getColumns();
        while(columnModel.hasMoreElements()) {
            TableColumn column = columnModel.nextElement();
            column.setCellRenderer(new VaultTableCellRenderer());
        }
    }

    /**
     * Add an item to the table.
     * 
     * @param item
     */
    public void addItem(VaultItem item) {
        if(item != null) {
            model.add(item);
        }
    }

    /**
     * All {@code VaultItem} currently captured by this table.
     * 
     * @return
     */
    public List<VaultItem> getAll() {
        return model.getAll();
    }
    
    /**
     * The currently selected item, or null if the table is not selected.
     * 
     * @return
     */
    public VaultItem getSelectedItem() {
        VaultItem item = null;
        int rowIndex = getSelectedRow();
        if(rowIndex >= 0) {
            item = model.getItem(convertRowIndexToModel(rowIndex));
        }
        return item;
    }
    
    /**
     * Delete the currently selected item, if there is a single table selectdion.
     */
    public void deleteSelectedItem() {
        VaultItem selected = getSelectedItem();
        if(selected != null) {
            model.delete(selected.getKey());
        }
    }
    
    /**
     * Replace
     * 
     * @param rowIndex
     * @param update
     */
    public void updateVaultItem(int rowIndex, VaultItem update) {
        int modelIndex = convertRowIndexToModel(rowIndex);
        model.updateItem(modelIndex, update);
    }
}
