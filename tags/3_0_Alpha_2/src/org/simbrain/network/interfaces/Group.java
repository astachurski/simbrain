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
package org.simbrain.network.interfaces;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <b>Group</b>: a logical group of neurons and synapses (and perhaps other
 * items later). In some cases this is useful for custom updating. In other
 * cases it is useful simply as a logical grouping of nodes (e.g. to represent
 * the "layers" of a feedforward network) Its gui representation is
 * {@link org.simbrain.network.gui.nodes.GroupNode}.
 *
 * Possibly add a flag to have the group visible or not.
 */
public abstract class Group {

    /** Reference to the network this group is a part of. */
    private RootNetwork parent;

    /** Set of neurons. */
    private List<Neuron> neuronList = new ArrayList<Neuron>();

    /** Set of synapses. */
    private Set<Synapse> synapseList = new HashSet<Synapse>();

    /** Whether this Group should be active or not. */
    private boolean isOn = true;

    /** Name of this group. */
    private String id;

    /** Name of this group. */
    private String label;

    /**
     * Construct a model group with a reference to its root network.
     *
     * @param net reference to root network.
     */
    public Group(final RootNetwork net) {
        parent = net;
    }

    /**
     * True if the group contains the specified neuron.
     *
     * @param n neuron to check for.
     * @return true if the group contains this neuron, false otherwise
     */
    public boolean containsNeuron(final Neuron n) {
        return neuronList.contains(n);
    }

    /**
     * Turn the group on or off.  When off, the group update function
     * should not be called.
     */
    public void toggleOnOff() {
        if (isOn) {
            isOn = false;
        } else {
            isOn = true;
        }
    }

    /**
     * @return whether the group is "on" or not.
     */
    public boolean isOn() {
        return isOn;
    }

    /**
     * True if this group has no neurons, synapses, or networks.
     *
     * @return whether the group is empty or not.
     */
    public boolean isEmpty() {
        boolean neuronsGone = neuronList.isEmpty();
        boolean synapsesGone = synapseList.isEmpty();
        return (neuronsGone && synapsesGone);
    }

    /**
     * Returns the number of neurons and synapses in this group.
     *
     * @return the number of neurons and synapses in this group.
     */
    public int getElementCount() {
        return neuronList.size() + synapseList.size();
    }

    /**
     * Returns a debug string.
     *
     * @return the debug string.
     */
    public String debugString() {
        String ret =  new String();
        ret += ("Group with " + this.getNeuronList().size() + " neuron(s),");
        ret += (" " + this.getSynapseList().size() + " synapse(s).");
        return ret;
    }

    @Override
    public String toString() {
        if (label != null) {
            return label;
        } else if (id != null){
            return id;
        } else {
            return super.toString();
        }
    }

    /**
     * Add neuron.
     *
     * @param neuron neuron to add
     */
    public void addNeuron(Neuron neuron) {
        neuronList.add(neuron);
    }

    /**
     * Add synapse.
     *
     * @param synapse synapse to add
     */
    public void addSynapse(Synapse synapse) {
        synapseList.add(synapse);
    }

    /**
     * Delete a neuron.
     *
     * @param toDelete neuron to delete
     */
    public void deleteNeuron(Neuron toDelete) {
        neuronList.remove(toDelete);
        parent.fireGroupChanged(this, this);
    }

    /**
     * Delete a synapse.
     *
     * @param toDelete synapse to delete
     */
    public void deleteSynapse(Synapse toDelete) {
        synapseList.remove(toDelete);
        parent.fireGroupChanged(this, this);
    }

    /**
     * @return a list of neurons
     */
    public List<Neuron> getNeuronList() {
        return neuronList;
    }

    /**
     * @return a list of weights
     */
    public List<Synapse> getSynapseList() {
        return new ArrayList<Synapse>(synapseList);
    }

    /**
     * Update group.  Override for special updating.
     */
    public void update() {
        updateAllNeurons();
        updateAllSynapses();
    }

    /**
     * Update all neurons.
     */
    public void updateAllNeurons() {
        for (Neuron neuron : neuronList) {
            neuron.update();
        }
    }

    /**
     * Update all synapses.
     */
    public void updateAllSynapses() {
        for (Synapse synapse : synapseList) {
            synapse.update();
        }
    }

    /**
     * @return the parent
     */
    public RootNetwork getParent() {
        return parent;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
        parent.fireGroupParametersChanged(this);
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(RootNetwork parent) {
        this.parent = parent;
    }

}