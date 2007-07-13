/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005,2007 The Authors.  See http://www.simbrain.net/credits
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.simbrain.world.dataworld;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.JXTable;
import org.simbrain.util.StandardDialog;
import org.simbrain.workspace.Consumer;
import org.simbrain.workspace.Coupling;
import org.simbrain.workspace.CouplingMenuItem;
import org.simbrain.workspace.Workspace;
import org.simnet.interfaces.Neuron;

/**
 * <b>DataWorld</b> creates a table and then adds it to the viewport.
 *
 * @author rbartley
 */
public class DataWorld extends JPanel implements MouseListener, KeyListener, ActionListener {

    /** Edit buttons boolean. */
    public static boolean editButtons = false;

    /** Table model. */
    private TableModel model = new TableModel(this);

    /** Data table. */
    private JXTable table = new JXTable(model);

    /** Parent frame that calls world. */
    private DataWorldComponent parentFrame;

    /** Upper bound. */
    private int upperBound = 1;

    /** Lower bound. */
    private int lowerBound = 0;

    /** Iteration mode. */
    private boolean iterationMode = false;

    /** Use last column for iteration. */
    private boolean columnIteration = false;

    /** Name. */
    private String name;

    /** Point selected. */
    private Point selectedPoint;

    /** Inserts a new row. */
    private JMenuItem addRow = new JMenuItem("Insert row");

    /** Inserts a new column. */
    private JMenuItem addCol = new JMenuItem("Insert column");

    /** Removes a row. */
    private JMenuItem remRow = new JMenuItem("Delete row");

    /** Removes a column. */
    private JMenuItem remCol = new JMenuItem("Delete column");

    /** Local variable used for iterating. */
    private int thisRowCount = 0;

    /** Local variable used for iterating. */
    private int currentRowCounter = 0;

    /**
     * Creates a new instance of the data world.
     *
     * @param ws World frame to create a new data world within
     */
    public DataWorld(final DataWorldComponent ws) {
        super(new BorderLayout());
        setParentFrame(ws);

        addRow.addActionListener(parentFrame);
        addRow.setActionCommand("addRowHere");
        addCol.addActionListener(parentFrame);
        addCol.setActionCommand("addColHere");
        remRow.addActionListener(parentFrame);
        remRow.setActionCommand("remRowHere");
        remCol.addActionListener(parentFrame);
        remCol.setActionCommand("remColHere");

        add("Center", table);
        init();
    }

    /**
     * Add listeners.
     */
    private void init() {

        table.addKeyListener(this);
        table.addMouseListener(this);
        table.setColumnSelectionAllowed(true);
        table.setRolloverEnabled(true);
        table.setRowSelectionAllowed(true);

    }

    /**
     * Resets the model.
     *
     * @param data Data to reset
     */
    public void resetModel(final String[][] data) {
        model = new TableModel(data);
        table.setModel(model);
        init();
        repaint();
        parentFrame.pack();
        model.setCurrentRow(0);
        currentRowCounter = 0;
    }

    /**
     * Responds to mouse clicked event.
     *
     * @param e Mouse event
     */
    public void mouseClicked(final MouseEvent e) {
    }

    /**
     * Responds to mouse pressed event.
     *
     * @param e Mouse event
     */
    public void mousePressed(final MouseEvent e) {
        selectedPoint = e.getPoint();
   //     model.setCurrentRow(table.getSelectedRow());

        // TODO: should use isPopupTrigger, see e.g. ContextMenuEventHandler
        boolean isRightClick = (e.isControlDown() || (e.getButton() == 3));
        if (isRightClick) {
            JPopupMenu menu = buildPopupMenu();
            menu.show(this, (int) selectedPoint.getX(), (int) selectedPoint.getY());
        }
    }

    /**
     * Responds to mouse released event.
     *
     * @param e Mouse event
     */
    public void mouseReleased(final MouseEvent e) {
    }

    /**
     * Responds to mouse entered events.
     *
     * @param e Mouse event
     */
    public void mouseEntered(final MouseEvent e) {
    }

    /**
     * Responds to mouse exited event.
     *
     * @param e Mouse event
     */
    public void mouseExited(final MouseEvent e) {
    }

    /**
     * @return The pop up menu to be built.
     */
    public JPopupMenu buildPopupMenu() {
        JPopupMenu ret = new JPopupMenu();

        ret.add(addRow);

        if (this.getTable().columnAtPoint(selectedPoint) != 0) {
            ret.add(addCol);
        }

        ret.add(remRow);

        if (this.getTable().columnAtPoint(selectedPoint) != 0) {
            ret.add(remCol);
        }

        ret.addSeparator();
        ret.add(Workspace.getInstance().getProducerListMenu(this));

        return ret;
    }

    /**
     * @return World type.
     */
    public String getType() {
        return "DataWorld";
    }

    /**
     * @return Name of data world.
     */
    public String getName() {
        return this.getParentFrame().getTitle();
    }

    /**
     * @return Returns the parentFrame.
     */
    public DataWorldComponent getParentFrame() {
        return parentFrame;
    }

    /**
     * @param parentFrame The parentFrame to set.
     */
    public void setParentFrame(final DataWorldComponent parentFrame) {
        this.parentFrame = parentFrame;
    }

    /**
     * @return Returns the table.
     */
    public JTable getTable() {
        return table;
    }

    /**
     * Randomizes the values.
     *
     */
    public void randomize() {
        if (upperBound <= lowerBound) {
            displayRandomizeDialog();
        }

        for (int i = 0; i < table.getColumnCount(); i++) {
            for (int j = 0; j < table.getRowCount(); j++) {
                table.setValueAt(randomInteger(), j, i);
            }
        }
    }

    /**
     * @return A random integer.
     */
    public Double randomInteger() {
        if (upperBound >= lowerBound) {
            double drand = Math.random();
            drand = (drand * (upperBound - lowerBound)) + lowerBound;

            Double element = new Double(drand);

            return element;
        }

        return new Double(0);
    }

    /**
     * Displays the randomize dialog.
     */
    public void displayRandomizeDialog() {
        StandardDialog rand = new StandardDialog(Workspace.getInstance(), "randomize Bounds");
        JPanel pane = new JPanel();
        JTextField lower = new JTextField();
        JTextField upper = new JTextField();
        lower.setText(Integer.toString(getLowerBound()));
        lower.setColumns(3);
        upper.setText(Integer.toString(getUpperBound()));
        upper.setColumns(3);
        pane.add(new JLabel("Lower Bound"));
        pane.add(lower);
        pane.add(new JLabel("Upper Bound"));
        pane.add(upper);

        rand.setContentPane(pane);

        rand.pack();

        rand.setLocationRelativeTo(getParentFrame());
        rand.setVisible(true);

        if (!rand.hasUserCancelled()) {
            setLowerBound(Integer.parseInt(lower.getText()));
            setUpperBound(Integer.parseInt(upper.getText()));
        }

        repaint();
    }

    /**
     * Increment a number of times equal to the last column.
     */
    public void incrementUsingLastColumn() {
        if (currentRowCounter < thisRowCount) {
            currentRowCounter++;
        } else {
            incrementCurrentRow();
            currentRowCounter = 0;
            thisRowCount = (int) Double.parseDouble(""
                    + table.getModel().getValueAt(model.getCurrentRow(),
                            table.getModel().getColumnCount() - 1));
        }
    }

    /**
     * Increment current row by 1.
     */
    public void incrementCurrentRow() {
        if (iterationMode) {
            table.setColumnSelectionAllowed(false);
            if (model.getCurrentRow() >= (table.getRowCount() - 1)) {
                model.setCurrentRow(0);
            } else {
                model.setCurrentRow(model.getCurrentRow() + 1);
            }
            table.setRowSelectionInterval(model.getCurrentRow(), model.getCurrentRow());
        } else {
            table.setColumnSelectionAllowed(true);
        }
    }

    /**
     * @return Returns the name.
     */
    public String getWorldName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setWorldName(final String name) {
        this.getParentFrame().setTitle(name);
        this.name = name;
    }

    /**
     * @return Returns the model.
     */
    public TableModel getModel() {
        return model;
    }

    /**
     * @param model The model to set.
     */
    public void setModel(final TableModel model) {
        this.model = model;
    }

    /**
     * @return The lower bound.
     */
    public int getLowerBound() {
        return lowerBound;
    }

    /**
     * Sets the lower bound value.
     *
     * @param lowerBound Value to set
     */
    public void setLowerBound(final int lowerBound) {
        this.lowerBound = lowerBound;
    }

    /**
     * @return The upper bound value.
     */
    public int getUpperBound() {
        return upperBound;
    }

    /**
     * Sets the upper bound value.
     *
     * @param upperBound Value to set
     */
    public void setUpperBound(final int upperBound) {
        this.upperBound = upperBound;
    }

    /**
     * @return The selected point.
     */
    public Point getSelectedPoint() {
        return selectedPoint;
    }

    /**
     * Sets the selected point.
     *
     * @param selectedPoint Valuet to set
     */
    public void setSelectedPoint(final Point selectedPoint) {
        this.selectedPoint = selectedPoint;
    }

    /**
     * Responds to key typed events.
     *
     * @param arg0 Key event
     */
    public void keyTyped(final KeyEvent arg0) {
        this.getParentFrame().setChangedSinceLastSave(true);
    }

    /**
     * Responds to key pressed events.
     *
     * @param arg0 Key event
     */
    public void keyPressed(final KeyEvent arg0) {
    }

    /**
     * Responds to key released events.
     *
     * @param arg0 Key event
     */
    public void keyReleased(final KeyEvent arg0) {
    }


    public void completedInputRound() {
        if (iterationMode) {
            if (columnIteration) {
                incrementUsingLastColumn();
            } else {
                incrementCurrentRow();
            }
        }
    }

    /**
     * @return Returns the iterationMode.
     */
    public boolean isIterationMode() {
        return iterationMode;
    }

    /**
     * @param iterationMode The iterationMode to set.
     */
    public void setIterationMode(final boolean iterationMode) {
        this.iterationMode = iterationMode;
    }

    /**
     * @return Returns the columnIteration.
     */
    public boolean getColumnIteration() {
        return columnIteration;
    }

    /**
     * @param columnIteration The columnIteration to set.
     */
    public void setColumnIteration(final boolean columnIteration) {
        this.columnIteration = columnIteration;
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() instanceof CouplingMenuItem) {
            CouplingMenuItem m = (CouplingMenuItem) event.getSource();
            Iterator producerIterator = m.getCouplingContainer().getProducers().iterator();
            for (Consumer consumer : this.getModel().getConsumers()) {
                if (producerIterator.hasNext()) {
                    Coupling coupling = new Coupling(((org.simbrain.workspace.Producer)producerIterator.next()).getDefaultProducingAttribute(), consumer.getDefaultConsumingAttribute());
                    this.getModel().getCouplings().add(coupling);
                }
            }
        }
    }
}