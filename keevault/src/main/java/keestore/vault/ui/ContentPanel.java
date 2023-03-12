/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.ui;

import java.awt.BorderLayout;
import java.util.function.Consumer;

import javax.swing.JPanel;

/**
 * Generic content panel in Border Layout with 2 areas for building.
 * <ol>
 * <li>Contents: Guaranteed to be laid out in <code>BorderLayout.CENTER</code>.
 * <li>Controls: Guaranteed to be laid out in <code>BorderLayout.SOUTH</code>.
 * </ol>
 * 
 * @author thinh ho
 *
 */
public class ContentPanel extends JPanel {
    private static final long serialVersionUID = -1419281734023406158L;
    
    private final JPanel contents = new JPanel();
    private final JPanel controls = new JPanel();
    
    public ContentPanel layoutContents(Consumer<JPanel> c) {
        c.accept(contents);
        return this;
    }
    
    public ContentPanel layoutControls(Consumer<JPanel> c) {
        c.accept(controls);
        return this;
    }
    
    public ContentPanel layoutComponents() {
        setLayout(new BorderLayout());
        add(contents, BorderLayout.CENTER);
        add(controls, BorderLayout.SOUTH);
        return this;
    }
}
