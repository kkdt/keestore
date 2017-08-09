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

import org.apache.log4j.Logger;

import keestore.access.Kee;
import keestore.access.KeeAccess;
import keestore.vault.Util;

/**
 * Table data model for {@code Kee} object models.
 * 
 * @author thinh ho
 *
 */
public class KeeTableModel extends AbstractTableModel implements KeeAccess {
    private static final long serialVersionUID = -848690210505001216L;
    private static final Logger logger = Logger.getLogger(KeeTableModel.class);

    private List<Kee> data = new Vector<>();
    private final String[] columns = new String[2];

    /**
     * Default "key" and "value" will be the column model.
     */
    public KeeTableModel() {
        columns[0] = "Key";
        columns[1] = "Value";
    }

    /**
     * The column model will be the specified values.
     * 
     * @param idColumnDisplayValue
     * @param nameColumnDisplayValue
     */
    public KeeTableModel(String idColumnDisplayValue, String nameColumnDisplayValue) {
        columns[0] = idColumnDisplayValue != null && idColumnDisplayValue.length() > 0 
            ? idColumnDisplayValue : "Key";
        columns[1] = nameColumnDisplayValue != null && nameColumnDisplayValue.length() > 0 
            ? nameColumnDisplayValue : "Value";
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
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = null;
        Kee kee = data.get(rowIndex);
        switch (columnIndex) {
        case 0:
            value = kee.getId();
            break;
        case 1:
            value = kee.getName();
            break;
        }
        return value;
    }

    @Override
    public Kee getKee(String id) {
        return data.stream().filter(k -> {
            return k.getId().equals(id);
        }).findFirst().orElseGet(() -> {
            return null;
        });
    }

    @Override
    public void addKee(Kee item) {
        Util.withEventQueue(() -> {
            boolean added = data.add(item);
            if (added) {
                fireTableDataChanged();
            }
        });
    }

    @Override
    public void deleteKee(String id) {
        data.stream().filter(k -> {
            return k.getId().equals(id);
        }).findFirst().ifPresent(k -> {
            deleteKee(k);
        });
    }

    @Override
    public void deleteKee(Kee k) {
        if (k != null) {
            Util.withEventQueue(() -> {
                boolean removed = data.remove(k);
                if (removed) {
                    logger.info("Kee removed: " + k);
                    fireTableDataChanged();
                }
            });
        }
    }
    
    @Override
    public void clear() {
        Util.withEventQueue(() -> {
            data.clear();
            fireTableDataChanged();
        });
    }
    
    /**
     * The the model at the specified <b>modelIndex</b>.
     * 
     * @param modelIndex
     * @return
     */
    public Kee getAt(int modelIndex) {
        return data.get(modelIndex);
    }
    
    /**
     * A copy of the entire repository.
     * 
     * @return
     */
    public List<Kee> getAll() {
        List<Kee> copy = new ArrayList<>();
        synchronized(data) {
            copy.addAll(data);
        }
        return copy;
    }

}
