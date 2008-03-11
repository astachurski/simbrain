
package org.simbrain.network.actions;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.simbrain.network.NetworkPanel;
import org.simbrain.network.nodes.NeuronNode;
import org.simbrain.network.nodes.SynapseNode;
import org.simbrain.resource.ResourceManager;

/**
 * Randomize screen elements action.
 *
 * TODO: rename to RandomizeScreenElementsAction?
 */
public final class RandomizeObjectsAction
    extends AbstractAction {

    /** Network panel. */
    private final NetworkPanel networkPanel;


    /**
     * Create a new randomize screen elements action with the
     * specified network panel.
     *
     * @param networkPanel network panel, must not be null
     */
    public RandomizeObjectsAction(final NetworkPanel networkPanel) {
        super("Randomize selection");

        if (networkPanel == null) {
            throw new IllegalArgumentException("networkPanel must not be null");
        }

        this.networkPanel = networkPanel;
        putValue(SMALL_ICON, ResourceManager.getImageIcon("Rand.gif"));
        putValue(SHORT_DESCRIPTION, "Radomize selected weights and nodes (r)");

        networkPanel.getInputMap().put(KeyStroke.getKeyStroke('r'), this);
        networkPanel.getActionMap().put(this, this);
    }


    /** @see AbstractAction */
    public void actionPerformed(final ActionEvent event) {
        for (Iterator i = networkPanel.getSelectedNeurons().iterator(); i.hasNext();) {
            NeuronNode node = (NeuronNode) i.next();
            node.getNeuron().randomize();
            node.update();
        }
        for (Iterator i = networkPanel.getSelectedSynapses().iterator(); i.hasNext();) {
            SynapseNode node = (SynapseNode) i.next();
            node.getSynapse().randomize();
            node.updateColor();
            node.updateDiameter();
        }
   }
}