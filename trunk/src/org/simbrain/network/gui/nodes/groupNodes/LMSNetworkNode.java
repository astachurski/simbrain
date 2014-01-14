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
package org.simbrain.network.gui.nodes.groupNodes;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.simbrain.network.gui.NetworkPanel;
import org.simbrain.network.gui.dialogs.network.LMSEditorDialog;
import org.simbrain.network.gui.trainer.IterativeTrainingPanel;
import org.simbrain.network.gui.trainer.LMSOfflineTrainingPanel;
import org.simbrain.network.gui.trainer.TrainerGuiActions;
import org.simbrain.network.subnetworks.LMSNetwork;
import org.simbrain.network.trainers.LMSIterative;
import org.simbrain.network.trainers.LMSOffline;
import org.simbrain.network.trainers.Trainable;
import org.simbrain.resource.ResourceManager;
import org.simbrain.util.StandardDialog;
import org.simbrain.util.genericframe.GenericFrame;
import org.simbrain.util.math.NumericMatrix;

/**
 * PNode representation of a group of a LMS network.
 *
 * @author jyoshimi
 */
public class LMSNetworkNode extends SubnetworkNode {

    /**
     * Create a layered network.
     *
     * @param networkPanel parent panel
     * @param group the layered network
     */
    public LMSNetworkNode(NetworkPanel networkPanel, LMSNetwork group) {
        super(networkPanel, group);
        setContextMenu();
    }

    /**
     * Sets custom menu.
     */
    private void setContextMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.add(editGroup);
        menu.add(renameGroup);
        menu.add(removeGroup);
        menu.addSeparator();
        JMenu dataActions = new JMenu("View / Edit Data");
        final LMSNetwork network = (LMSNetwork) getSubnetwork();
        dataActions.add(TrainerGuiActions.getEditCombinedDataAction(
                getNetworkPanel(), network));
        dataActions.addSeparator();
        dataActions.add(TrainerGuiActions.getEditDataAction(getNetworkPanel(),
                network.getInputNeurons(), network.getTrainingSet()
                        .getInputDataMatrix(), "Input"));
        dataActions.add(TrainerGuiActions.getEditDataAction(getNetworkPanel(),
                network.getOutputNeurons(), network.getTrainingSet()
                        .getTargetDataMatrix(), "Target"));
        menu.add(dataActions);

        setContextMenu(menu);
    }

    @Override
    protected StandardDialog getPropertyDialog() {
        return new LMSEditorDialog(this.getNetworkPanel(),
                (LMSNetwork) getSubnetwork());
    }

    /**
     * Action to train LMS Iteratively.  No longer used.
     */
    Action trainIterativelyAction = new AbstractAction() {

        // Initialize
        {
            putValue(SMALL_ICON, ResourceManager.getImageIcon("Trainer.png"));
            putValue(NAME, "Train iteratively...");
            putValue(SHORT_DESCRIPTION, "Train iteratively...");
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            LMSNetwork network = (LMSNetwork) getSubnetwork();
            IterativeTrainingPanel trainingPanel = new IterativeTrainingPanel(
                    getNetworkPanel(), new LMSIterative(network));
            GenericFrame frame = getNetworkPanel().displayPanel(trainingPanel,
                    "Trainer");
            trainingPanel.setFrame(frame);
        }
    };

    /**
     * Action to train LMS Offline. No longer used.
     */
    Action trainOfflineAction = new AbstractAction() {

        // Initialize
        {
            putValue(SMALL_ICON, ResourceManager.getImageIcon("Trainer.png"));
            putValue(NAME, "Train offline...");
            putValue(SHORT_DESCRIPTION, "Train offline...");
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            LMSNetwork network = (LMSNetwork) getSubnetwork();
            LMSOfflineTrainingPanel trainingPanel = new LMSOfflineTrainingPanel(
                    getNetworkPanel(), new LMSOffline(network));
            GenericFrame frame = getNetworkPanel().displayPanel(trainingPanel,
                    "Trainer");
            trainingPanel.setFrame(frame);
        }
    };

}
