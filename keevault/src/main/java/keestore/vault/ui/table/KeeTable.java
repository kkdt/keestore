/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.ui.table;

import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import keestore.access.Kee;
import keestore.access.KeeAccess;

public class KeeTable extends JTable implements KeeAccess {
    private static final long serialVersionUID = 2130271474698758233L;

    private KeeTableModel model;
    private String keyColumn;
    private String valueColumn;

    /**
     * Uses the default table model column values.
     */
    public KeeTable() {
        initComponents();
    }

    /**
     * Specifies the key/value column display text for the table columns.
     * 
     * @param keyColumnName
     * @param valueColumnName
     */
    public KeeTable(String keyColumnName, String valueColumnName) {
        this.keyColumn = keyColumnName;
        this.valueColumn = valueColumnName;
        initComponents();
    }

    private void initComponents() {
        model = new KeeTableModel(keyColumn, valueColumn);
        setModel(model);
        setRowSelectionAllowed(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getColumnModel().getColumn(1).setPreferredWidth(300);
        setAutoCreateRowSorter(true);
    }

    /**
     * The currently selected {@code Kee} or null if nothing is selected.
     * 
     * @return
     */
    public Kee getSelectedKee() {
        Kee selected = null;
        int selectedRow = getSelectedRow();
        if (selectedRow >= 0) {
            int modelIndex = convertRowIndexToModel(selectedRow);
            selected = model.getAt(modelIndex);
        }
        return selected;
    }
    
    /**
     * All {@code Kee} models from the underlying table model.
     * 
     * @return
     */
    public List<Kee> getAll() {
        return model.getAll();
    }

    @Override
    public void addKee(Kee item) {
        model.addKee(item);
    }

    @Override
    public Kee getKee(String id) {
        return model.getKee(id);
    }

    @Override
    public void deleteKee(Kee item) {
        model.deleteKee(item);
    }

    @Override
    public void deleteKee(String id) {
        deleteKee(getKee(id));
    }

    @Override
    public void clear() {
        model.clear();
    }
}
