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

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.simbrain.workspace.Consumer;
import org.simbrain.workspace.Coupling;
import org.simbrain.workspace.Producer;
import org.simbrain.workspace.WorkspaceComponent;
import org.simbrain.workspace.WorkspaceComponentListener;

/**
 * <b>DataWorldComponent</b> is a "spreadsheet world" used to send rows of raw data to input nodes.
 */
public class DataWorldComponent extends WorkspaceComponent<WorkspaceComponentListener> {
    
    /** The stastic logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(DataWorldComponent.class);

    /** Table model. */
    private final DataModel<Double> dataModel = new DataModel<Double>(this);

    /**
     * Returns the data model for this component.
     * 
     * @return The data model for this component.
     */
    DataModel<Double> getDataModel() {
        return dataModel;
    }

    /**
     * This method is the default constructor.
     */
    public DataWorldComponent(String name) {
        super(name);
    }

    @SuppressWarnings("unchecked")
    void wireCouplings(Collection<? extends Producer> producers) {
        /* Handle Coupling wire-up */
        LOGGER.debug("wiring " + producers.size() + " producers");

       Iterator<? extends Producer> producerIterator = producers.iterator();

        for (Consumer consumer : getConsumers()) {
            if (producerIterator.hasNext()) {
                Coupling<?> coupling = new Coupling(producerIterator.next().getDefaultProducingAttribute(), consumer.getDefaultConsumingAttribute());
                getWorkspace().addCoupling(coupling);
            }
        }
    }

    /**
     * Read a world from a world-xml file.
     *
     * @param theFile the xml file containing world information
     */
    public void open(final File theFile) {
        // TODO implement
    }

    /**
     * Returns a properly initialized xstream object.
     * @return the XStream object
     */
//    private XStream getXStream() {
//        XStream xstream = new XStream(new DomDriver());
//        xstream.omitField(TableModel.class, "consumers");
//        xstream.omitField(TableModel.class, "producers");
//        xstream.omitField(TableModel.class, "couplingList");
//        xstream.omitField(TableModel.class, "model");
//        return xstream;
//    }

    /**
     * Save a specified file.
     *
     * @param worldFile File to save world
     */
    public void save(final File worldFile) {
     // TODO implement
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileExtension() {
       return "xml";
    }

//    @Override
//    public void setCurrentDirectory(final String currentDirectory) {
//        super.setCurrentDirectory(currentDirectory);
//        DataWorldPreferences.setCurrentDirectory(currentDirectory);
//    }
//
//    @Override
//    public String getCurrentDirectory() {
//        return DataWorldPreferences.getCurrentDirectory();
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends Consumer> getConsumers() {
        return dataModel.getConsumers();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends Producer> getProducers() {
        return dataModel.getProducers();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
    }
}