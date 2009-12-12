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
package org.simbrain.network.gui.dialogs.connect;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.simbrain.network.connections.Radial;
import org.simbrain.network.gui.dialogs.synapse.SynapseDialog;
import org.simbrain.network.interfaces.Synapse;


/**
 * <b>SparsePanel</b> creates a dialog for setting preferences of Sparse neuron connections.
 */
public class RadialPanel extends AbstractConnectionPanel {

    /** Excitatory Probability. */
    private JTextField tfExciteProbability = new JTextField();

    /** Excitatory Ratio. */
    private JTextField tfExciteRatio = new JTextField();

    /** Excitatory Radius. */
    private JTextField tfExciteRadius = new JTextField();

    /** Inhibitory Probability. */
    private JTextField tfInhibitProbability = new JTextField();

    /** Inhibitory Radius. */
    private JTextField tfInhibitRadius = new JTextField();

    /** Allow self connections check box. */
    private JCheckBox allowSelfConnect = new JCheckBox();

    /** Label showing current excitatory type of synapse. */
    private JLabel baseExcitatorySynapseLabel = new JLabel("");

    /** Label showing current inhibitory type of synapse. */
    private JLabel baseInhibitorySynapseLabel = new JLabel("");

    /**
     * This method is the default constructor.
     * @param connection type
     */
    public RadialPanel(final Radial connection) {
        super(connection);
        this.addItem("Excitatory/Inhibitory Ratio", tfExciteRatio);
        this.addItem("Excitatory Probability", tfExciteProbability);
        this.addItem("Excitatory Radius", tfExciteRadius);
        

        JButton setExcitatorySynapseType = new JButton("Set...");
        setExcitatorySynapseType.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ArrayList<Synapse> excitatoryList = new ArrayList<Synapse>();
                excitatoryList.add(Radial.getBaseExcitatorySynapse());
                SynapseDialog dialog = new SynapseDialog(excitatoryList);
                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
                Synapse excitatorySynapse = dialog.getSynapseList().get(0);
                Radial.setBaseExcitatorySynapse(excitatorySynapse);
                baseExcitatorySynapseLabel.setText(excitatorySynapse.getType());
            }

        });
        baseExcitatorySynapseLabel.setText(Radial.getBaseExcitatorySynapse().getType());
        this.addItem("Base Excitatory Synapse Type:", baseExcitatorySynapseLabel);
        this.addItem("Set Base Excitatory Synapse Type:", setExcitatorySynapseType);

        this.addItem("Inhibitory Probability", tfInhibitProbability);
        this.addItem("Inhibitory Radius", tfInhibitRadius);
        
        JButton setInhibitorySynapseType = new JButton("Set...");
        setInhibitorySynapseType.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ArrayList<Synapse> inhibitoryList = new ArrayList<Synapse>();
                inhibitoryList.add(Radial.getBaseInhibitorySynapse());
                SynapseDialog dialog = new SynapseDialog(inhibitoryList);
                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
                Synapse inhibitorySynapse = dialog.getSynapseList().get(0);
                Radial.setBaseInhibitorySynapse(inhibitorySynapse);
                baseInhibitorySynapseLabel.setText(inhibitorySynapse.getType());
            }
            
        });
        baseInhibitorySynapseLabel.setText(Radial.getBaseInhibitorySynapse().getType());
        this.addItem("Base Inhibitory Synapse Type:", baseInhibitorySynapseLabel);
        this.addItem("Set Inhibitory Base Synapse Type:", setInhibitorySynapseType);

        this.addItem("Allow Self Connections", allowSelfConnect);
    }

    /**
     * {@inheritDoc}
     */
    public void commitChanges() {
        Radial.setExcitatoryProbability(Double.parseDouble(tfExciteProbability.getText()));
        Radial.setExcitatoryRadius(Double.parseDouble(tfExciteRadius.getText()));
        Radial.setPercentExcitatory(Double.parseDouble(tfExciteRatio.getText()));
        Radial.setInhibitoryProbability(Double.parseDouble(tfInhibitProbability.getText()));
        Radial.setInhibitoryRadius(Double.parseDouble(tfInhibitRadius.getText()));
        Radial.setAllowSelfConnections(allowSelfConnect.isSelected());
    }

    
    /**
     * {@inheritDoc}
     */
    public void fillFieldValues() {
        tfExciteProbability.setText(Double.toString(Radial.getExcitatoryProbability()));
        tfInhibitProbability.setText(Double.toString(Radial.getInhibitoryProbability()));
        tfExciteRadius.setText(Double.toString(Radial.getExcitatoryRadius()));
        tfExciteRatio.setText(Double.toString(Radial.getPercentExcitatory()));
        tfInhibitProbability.setText(Double.toString(Radial.getInhibitoryProbability()));
        tfInhibitRadius.setText(Double.toString(Radial.getInhibitoryRadius()));
        allowSelfConnect.setSelected(Radial.isAllowSelfConnections());
    }

}