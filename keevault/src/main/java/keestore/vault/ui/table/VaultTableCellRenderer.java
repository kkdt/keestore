/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.ui.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import keestore.crypto.Crypto;

/**
 * Table cell render that hides the actual value.
 * 
 * @author thinh ho
 *
 */
public class VaultTableCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 8845797570030428182L;
    private static final String defaultValue = "*******************";
    
    private boolean hideValue = true;
    
    public boolean isHideValue() {
        return hideValue;
    }

    public void setHideValue(boolean hideValue) {
        this.hideValue = hideValue;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) 
    {
        setValue(hideValue ? defaultValue : value);
        String _value = (String)value;
        setToolTipText(hideValue ? "Table must be in 'Edit' mode in order to view" 
            : new String(Crypto.decode(_value).get()));
        return this;
    }
}
