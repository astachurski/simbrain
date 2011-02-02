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
package org.simbrain.network.gui.actions.connection;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;

import org.simbrain.network.connections.*;
import org.simbrain.network.gui.NetworkPanel;

/**
 * Connect neurons action.  Connects a set of source neurons to a set of target neurons.
 */
public final class ConnectNeuronsAction
    extends AbstractAction {

    /** Network panel. */
    private final NetworkPanel networkPanel;

    /** Source neuron. */
    private ArrayList sourceNeurons;

    /** Target neuron. */
    private ArrayList targetNeurons;

    /**
     * Create a new connect neurons action.  Connects a set of source neurons to a set of target neurons.
     *
     * @param networkPanel network panel, must not be null
     * @param sourceNeurons NeuronNodes to connect from
     * @param targetNeurons NeuronNodes to connect to
     */
    public ConnectNeuronsAction(final NetworkPanel networkPanel,
                                final ArrayList sourceNeurons,
                                final ArrayList targetNeurons) {

        if (networkPanel == null) {
            throw new IllegalArgumentException("networkPanel must not be null");
        }

        this.networkPanel = networkPanel;
        this.sourceNeurons = sourceNeurons;
        this.targetNeurons = targetNeurons;

        putValue(NAME, "Connect using \"" + ConnectNeurons.connectionType + "\"");

    }


    /** @see AbstractAction */
    public void actionPerformed(final ActionEvent event) {
        if (sourceNeurons.isEmpty() || targetNeurons.isEmpty()) {
            return;
        }
        ConnectNeurons connection = ConnectNeurons.connectionType;
        connection.connectNeurons(networkPanel.getRootNetwork(), sourceNeurons, targetNeurons);
    }
}