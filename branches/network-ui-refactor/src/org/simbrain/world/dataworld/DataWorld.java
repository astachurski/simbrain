/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005 Jeff Yoshimi <www.jeffyoshimi.net>
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
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.simnet.coupling.CouplingMenuItem;
import org.simnet.coupling.MotorCoupling;
import org.simnet.coupling.SensoryCoupling;
import org.simnet.interfaces.NetworkEvent;
import org.simbrain.network.NetworkPanel;
import org.simbrain.util.StandardDialog;
import org.simbrain.world.Agent;
import org.simbrain.world.World;


/**
 * <b>DataWorld</b> creates a table and then adds it to the viewport.
 *
 * @author rbartley
 */
public class DataWorld extends World implements MouseListener, Agent, KeyListener {
    public static boolean editButtons = false;
    private TableModel model = new TableModel(this);
    private JTable table = new JTable(model);
    private DataWorldFrame parentFrame;

    private int upperBound = 0;
    private int lowerBound = 0;
    private int current_row = 1;
    private String name;
    private Point selectedPoint;
    private JMenuItem addRow = new JMenuItem("Insert row");
    private JMenuItem addCol = new JMenuItem("Insert column");
    private JMenuItem remRow = new JMenuItem("Delete row");
    private JMenuItem remCol = new JMenuItem("Delete column");
//    private JMenuItem changeName = new JMenuItem("Edit button text");

    public DataWorld(final DataWorldFrame ws) {
        super(new BorderLayout());
        setParentFrame(ws);
        table.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer(table.getDefaultRenderer(JButton.class)));
//        table.getColumnModel().getColumn(0).setCellEditor(new ButtonEditor(this));
        table.addMouseListener(this);
        this.add("Center", table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        addRow.addActionListener(parentFrame);
        addRow.setActionCommand("addRowHere");
        addCol.addActionListener(parentFrame);
        addCol.setActionCommand("addColHere");
        remRow.addActionListener(parentFrame);
        remRow.setActionCommand("remRowHere");
        remCol.addActionListener(parentFrame);
        remCol.setActionCommand("remColHere");
//        changeName.addActionListener(parentFrame);
//        changeName.setActionCommand("changeButtonName");

        table.addKeyListener(this);
    }

    public void resetModel(final String[][] data) {
        model = new TableModel(data);
        table.setModel(model);
        table.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer(table.getDefaultRenderer(JButton.class)));

        parentFrame.resize();
    }

    /**
     * Sets the names of the buttons to the saved string array
     *
     * @param names
     */
    public void setButtonNames(final String[] names) {
        for (int i = 0; i < names.length; i++) {
            ((JButton) table.getValueAt(i, 0)).setText(names[i]);
        }

        this.columnResize();
    }

    /**
     * Retrieves the names of the buttons as a string array
     *
     * @return
     */
    public String[] getButtonNames() {
        String[] names = new String[table.getRowCount()];

        for (int i = 0; i < table.getRowCount(); i++) {
            names[i] = ((JButton) table.getValueAt(i, 0)).getText();
        }

        return names;
    }

    /**
     * Resizes the first column ("Send" button) on a name change, to fit the largest name.
     */
    public void columnResize() {
        int max = 0;
        int size;

        for (int i = 0; i < table.getRowCount(); i++) {
            size = ((JButton) table.getValueAt(i, 0)).getText().length();

            if (size > max) {
                max = size;
            }
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(50 + (max * 5));
    }

    public void mouseClicked(final MouseEvent e) {
        //This makes the buttons act like buttons instead of images
        Point point = e.getPoint();

        if ((table.columnAtPoint(point) == 0) && !((e.isControlDown() == true) || (e.getButton() == 3))) {
            current_row = table.rowAtPoint(point);
            this.fireWorldChanged();
        } else {
            return;
        }
    }

    public void mousePressed(final MouseEvent e) {
        selectedPoint = e.getPoint();

        if ((e.getButton() == MouseEvent.BUTTON3) || e.isControlDown()) {
            JPopupMenu menu = buildPopupMenu();
            menu.show(this, (int) selectedPoint.getX(), (int) selectedPoint.getY());
        }
    }

    public void mouseReleased(final MouseEvent e) {
    }

    public void mouseEntered(final MouseEvent e) {
    }

    public void mouseExited(final MouseEvent e) {
    }

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

//        if (this.getTable().columnAtPoint(selectedPoint) == 0) {
//            ret.add(changeName);
//        }

        return ret;
    }

    public String getType() {
        return "DataWorld";
    }

    /**
     * @return Returns the parentFrame.
     */
    public DataWorldFrame getParentFrame() {
        return parentFrame;
    }

    /**
     * @param parentFrame The parentFrame to set.
     */
    public void setParentFrame(final DataWorldFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    /**
     * @return Returns the table.
     */
    public JTable getTable() {
        return table;
    }

    /**
     * @param table The table to set.
     */
    public void setTable(final JTable table) {
        this.table = table;
    }

    /**
     * Dataworlds contain one agent, themselves
     *
     * @return Returns the agentList.
     */
    public ArrayList getAgentList() {
        ArrayList ret = new ArrayList();
        ret.add(this);

        return ret;
    }

    /**
     * Dataworlds are agents, hence this returns itself
     *
     * @return Returns the world this agent is associated with, itself
     */
    public World getParentWorld() {
        return this;
    }

    public void randomize() {
        if (upperBound <= lowerBound) {
            displayRandomizeDialog();
        }

        for (int i = 1; i < table.getColumnCount(); i++) {
            for (int j = 0; j < table.getRowCount(); j++) {
                table.setValueAt(randomInteger(), j, i);
            }
        }
    }

    public Double randomInteger() {
        if (upperBound >= lowerBound) {
            double drand = Math.random();
            drand = (drand * (upperBound - lowerBound)) + lowerBound;

            Double element = new Double(drand);

            return element;
        }

        return new Double(0);
    }

    public void displayRandomizeDialog() {
        StandardDialog rand = new StandardDialog(this.getParentFrame().getWorkspace(), "randomize Bounds");
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
     * Unused stub; data worlds don't receive commands
     */
    public void setMotorCommand(final String[] commandList, final double value) {
        int col = Integer.parseInt(commandList[0]);

        table.setValueAt(new Double(value), current_row, col);
    }

    /**
     * Unused stub; data worlds don't receive commands
     */
    public JMenu getMotorCommandMenu(final ActionListener al) {
        JMenu ret = new JMenu("" + this.getWorldName());

        for (int i = 1; i < table.getColumnCount(); i++) {
            CouplingMenuItem motorItem = new CouplingMenuItem("Column " + i,
                                                              new MotorCoupling(this, new String[] {"" + i }));
            motorItem.addActionListener(al);
            ret.add(motorItem);
        }

        return ret;
    }

    /**
     * Returns the value in the given column of the table uses the current row.
     */
    public double getStimulus(final String[] sensor_id) {
        int i = Integer.parseInt(sensor_id[0]) - 1;
        String snum = new String("" + table.getModel().getValueAt(current_row, i + 1));

        return Double.parseDouble(snum);
    }

    /**
     * Returns a menu with on id, "Column X" for each column.
     */
    public JMenu getSensorIdMenu(final ActionListener al) {
        JMenu ret = new JMenu("" + this.getWorldName());

        for (int i = 1; i < (table.getColumnCount()); i++) {
            CouplingMenuItem stimItem = new CouplingMenuItem("Column " + i,
                                                             new SensoryCoupling(this, new String[] {"" + i }));
            stimItem.addActionListener(al);
            ret.add(stimItem);
        }

        return ret;
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

    public int getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(final int lowerBound) {
        this.lowerBound = lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(final int upperBound) {
        this.upperBound = upperBound;
    }

    public int getCurrent_row() {
        return current_row;
    }

    public void setCurrent_row(final int current_row) {
        this.current_row = current_row;
    }

    public Point getSelectedPoint() {
        return selectedPoint;
    }

    public void setSelectedPoint(final Point selectedPoint) {
        this.selectedPoint = selectedPoint;
    }

    public void keyTyped(final KeyEvent arg0) {
        this.getParentFrame().setChangedSinceLastSave(true);
    }

    public void keyPressed(final KeyEvent arg0) {
    }

    public void keyReleased(final KeyEvent arg0) {
    }

}