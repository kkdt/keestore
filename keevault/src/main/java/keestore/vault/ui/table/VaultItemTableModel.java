/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.ui.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import keestore.vault.Util;
import keestore.vault.model.VaultItem;

/**
 * Table model that holds a repository of {@code VaultItem}.
 * 
 * @author thinh ho
 *
 */
public class VaultItemTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -2186630406797152517L;

    private final String[] columns = new String[2];
    private final List<VaultItem> data = new Vector<>();

    /**
     * An item is always a key/value pair, indicated by the column headings.
     */
    public VaultItemTableModel() {
        columns[0] = "Key";
        columns[1] = "Value";
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = null;
        VaultItem entry = data.get(rowIndex);
        switch (columnIndex) {
        case 0:
            value = entry.getKey();
            break;
        case 1:
            value = entry.getValue();
            break;
        }
        return value;
    }
    
    /**
     * All {@code VaultItem} in the repository in their current state.
     * 
     * @return
     */
    public List<VaultItem> getAll() {
        List<VaultItem> all = new ArrayList<>();
        synchronized(data) {
            all.addAll(data);
        }
        return all;
    }

    void add(VaultItem item) {
        Util.withEventQueue(() -> {
            data.add(item);
            fireTableDataChanged();
        });
    }
    
    VaultItem getItem(int index) {
        return data.get(index);
    }
    
    void delete(String id) {
        Util.withEventQueue(() -> {
            boolean removed = false;
            synchronized(data) {
                removed = data.removeIf(i -> id.equals(i.getKey()));
            }
            if(removed) fireTableDataChanged();
        });
    }
    
    /**
     * Updating is a remove/add, removing the current item at the specified index
     * then adding the update to the end of the list.
     * 
     * @param modelIndex
     * @param update
     */
    void updateItem(int modelIndex, VaultItem update) {
        Util.withEventQueue(() -> {
            data.remove(modelIndex);
            data.add(update);
            fireTableDataChanged();
        });
    }
}
