/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import keestore.vault.Util;
import keestore.vault.controller.VaultController;
import keestore.vault.controller.VaultTableController;
import keestore.vault.crypto.VaultCrypto;
import keestore.vault.model.VaultAccess;
import keestore.vault.ui.table.KeeTable;

/**
 * <p>
 * The main panel containing a list of all {@code Vault} that are tracked/stored.
 * </p>
 * 
 * @author thinh ho
 *
 */
public class Vaults extends JPanel {
    private static final long serialVersionUID = -5581444246366196211L;
    private static final Logger logger = Logger.getLogger(Vaults.class);

    private final JFrame parent;
    private VaultCrypto crypto;
    private VaultController vaultController;
    private VaultTableController tableController;
    private HeaderPanel header;
    private KeeTable table;
    private Timer timer = new Timer("currentTime");
    private JButton createBtn;
    private JButton deleteBtn;
    private JButton openBtn;

    public Vaults(JFrame parent) {
        this.parent = parent;
        initComponents();
        layoutComponents();
    }
    
    void setVaultCrypto(VaultCrypto crypto) {
        this.crypto = crypto;
        this.tableController.setVaultCrypto(crypto);
    }

    public void init() {
        scheduleTimer();
        initListeners();
        header.setVaultCrypto(crypto);
        logger.info("Vaults initialized");
    }

    private void initComponents() {
        header = new HeaderPanel();
        table = new KeeTable(null, "Vault");
        this.vaultController = new VaultController(new VaultAccess());
        this.tableController  = new VaultTableController(table);
        table.getColumnModel().removeColumn(table.getColumnModel().getColumn(0));
        createBtn = new JButton("Create");
        deleteBtn = new JButton("Delete");
        deleteBtn.setEnabled(false);
        openBtn = new JButton("Open");
        openBtn.setEnabled(false);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(5, 5));

        // header
        JPanel panel0 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel0.add(header);

        // table
        JScrollPane scrollpane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        JPanel panel1 = new JPanel(new BorderLayout(10, 10));
        panel1.add(scrollpane, BorderLayout.CENTER);

        // controls
        JPanel panel2 = new JPanel();
        panel2.add(createBtn);
        panel2.add(openBtn);
        panel2.add(deleteBtn);

        add(panel0, BorderLayout.NORTH);
        add(panel1, BorderLayout.CENTER);
        add(panel2, BorderLayout.SOUTH);
    }

    /**
     * Sets up all the view-specific and datamodel-specific controller callbacks.
     */
    private void initListeners() {
        header.withAction("Encrypt", event -> {
            tableController.handleEncrypt(table);
        });
        
        Consumer<KeeTable> enableButtons = t -> {
            openBtn.setEnabled(t.getRowCount() > 0 && t.getSelectedRowCount() == 1);
            deleteBtn.setEnabled(t.getRowCount() > 0 && t.getSelectedRowCount() == 1);
        };

        // any time the table model changes, all table selections will be cleared
        // and controls reset
        table.getModel().addTableModelListener(e -> {
            Util.withEventQueue(() -> {
                table.clearSelection();
                enableButtons.accept(table);
            });
        });

        // enable controls when the user selects an item from the table
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() instanceof KeeTable) {
                    Util.withEventQueue(() -> {
                        enableButtons.accept((KeeTable) e.getSource());
                    });
                }
            }
        });
        
        table.getModel().addTableModelListener(tableController.tableModelListener(vaultController));
        table.addMouseListener(VaultTableController.mouseListener(parent, vaultController));
        createBtn.addActionListener(tableController.actionListener(parent, vaultController));
        deleteBtn.addActionListener(tableController.actionListener(parent, vaultController));
        openBtn.addActionListener(tableController.actionListener(parent, vaultController));
        
        try {
            tableController.loadTable(table);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load vaults: " + e.getMessage(), e);
        }
    }

    private void scheduleTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                header.setTime(new Date());
            }
        }, 0, 1000);
    }
}
