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
package org.simbrain.network;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.simbrain.coupling.Coupling;
import org.simbrain.coupling.CouplingMenuItem;
import org.simbrain.coupling.MotorCoupling;
import org.simbrain.coupling.SensoryCoupling;
import org.simbrain.gauge.GaugeFrame;
import org.simbrain.network.dialog.network.BackpropDialog;
import org.simbrain.network.dialog.network.BackpropTrainingDialog;
import org.simbrain.network.dialog.network.CompetitiveDialog;
import org.simbrain.network.dialog.network.CustomNetworkDialog;
import org.simbrain.network.dialog.network.HopfieldDialog;
import org.simbrain.network.dialog.network.NetworkDialog;
import org.simbrain.network.dialog.network.WTADialog;
import org.simbrain.network.dialog.neuron.NeuronDialog;
import org.simbrain.network.dialog.synapse.SynapseDialog;
import org.simbrain.network.pnodes.PNodeNeuron;
import org.simbrain.network.pnodes.PNodeSubNetwork;
import org.simbrain.network.pnodes.PNodeText;
import org.simbrain.network.pnodes.PNodeWeight;
import org.simbrain.resource.ResourceManager;
import org.simbrain.util.Utils;
import org.simbrain.util.XComparator;
import org.simbrain.util.YComparator;
import org.simnet.interfaces.Network;
import org.simnet.interfaces.Neuron;
import org.simnet.interfaces.Synapse;
import org.simnet.networks.Backprop;
import org.simnet.networks.Competitive;
import org.simnet.networks.ContainerNetwork;
import org.simnet.networks.ContinuousHopfield;
import org.simnet.networks.DiscreteHopfield;
import org.simnet.networks.Hopfield;
import org.simnet.networks.WinnerTakeAll;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PPanEventHandler;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * <b>NetworkPanel</b> is the main GUI view for the neural network model. It
 * handles the construction, modification, and analysis of
 * {@link org.simbrain.simnet} neural networks.
 */
public class NetworkPanel extends PCanvas implements ActionListener, PropertyChangeListener {

    /** The neural-network object. */
    protected ContainerNetwork network = new ContainerNetwork();
    /** Selected objects. */
    private ArrayList selection = new ArrayList();
    /** List of PNodes. */
    private ArrayList nodeList = new ArrayList();
    /** Interaction mode: worlds effects network only. */
    public static final int WORLD_TO_NET = 0;
    /** Interaction mode: network effects worlds only. */
    public static final int NET_TO_WORLD = 1;
    /** Interaction mode: worlds effect network and vice-versa. */
    public static final int BOTH_WAYS = 2;
    /** Interaction mode: worlds and networks are decoupled. */
    public static final int NEITHER_WAY = 3;
    /** Current interaction mode. */
    private int interactionMode = DEFAULT_INTERACTION_MODE;
    /** Build mode (connnect neurons or not). */
    private boolean buildToggle = true;
    /** Show input labels or not. */
    private boolean inOutMode = false;
    /** Automatically center network in frame or not. */
    private boolean isAutoZoom = true;
    /** Last state of autozoom. */
    private boolean prevAutoZoom = isAutoZoom;
    /** Show subnetwork outlines or not. */
    private boolean outlineSubnetwork = true;
    
    // Mode Constants
    public static final int SELECTION = 1;
    public static final int PAN = 2;
    public static final int ZOOMIN = 3;
    public static final int ZOOMOUT = 4;
    public static final int BUILD = 5;
    public static final int DELETE = 6;
    public static final int TEMP_SELECTION = 7;

    /** Current mode. */
    private int mode;
    /** Previous mode. */
    private int previousMode;
    /** Field separator. */
    public static final String FS = System.getProperty("file.separator");
    /** Backpropoagation directory. */
    private String backropDirectory = "." + FS + "simulations" + FS
            + "networks";
    /** Parent frame. */
    protected NetworkFrame parent;
    /** Thread which runs network via "play" button. */
    private NetworkThread theThread;
    /** Reference to serialization class. */
    private NetworkSerializer theSerializer;
    /** How much to nudge obejcts per key click. */
    private double nudgeAmount = 2;
    /** Tracks number of pasts that have occurred with same object; used to correctly position objects. */
    private double numberOfPastes = 0;
    /** Use when activating netpanel functions from a thread. */
    private boolean update_completed = false;

    /** Background color of network panel. */
    private Color backgroundColor = new Color(NetworkPreferences
            .getBackgroundColor());
    /** Color of all lines in network panel. */
    private Color lineColor = new Color(NetworkPreferences.getLineColor());
    /** Color of "active" neurons, with positive values. */
    private float hotColor = NetworkPreferences.getHotColor();
    /** Color of "inhibited" neurons, with negative values. */
    private float coolColor = NetworkPreferences.getCoolColor();
    /** Color of "excitatory" weights, with positive values. */
    private Color excitatoryColor = new Color(NetworkPreferences.getExcitatoryColor());
    /** Color of "inhibitory" weights, with negative values. */
    private Color inhibitoryColor = new Color(NetworkPreferences.getInhibitoryColor());
    /** Color of lasso. */
    private Color lassoColor = new Color(NetworkPreferences.getLassoColor());
    /** Color of selection boxes. */
    private Color selectionColor = new Color(NetworkPreferences
            .getSelectionColor());

    public static final int DEFAULT_INTERACTION_MODE = BOTH_WAYS;

    // Piccolo stuff
    private PPanEventHandler panEventHandler;

    private PZoomEventHandler zoomEventHandler;

    protected MouseEventHandler mouseEventHandler;

    protected KeyEventHandler keyEventHandler;

    // JComponents
    private JToolBar topTools = new JToolBar();

    private JToolBar buildTools = new JToolBar();

    private JToolBar iterationBar = new JToolBar();

    private JPanel bottomPanel = new JPanel();

    private JLabel timeLabel = new JLabel("0");

    private JButton clearBtn = new JButton(ResourceManager
            .getImageIcon("Eraser.gif"));

    private JButton randBtn = new JButton(ResourceManager
            .getImageIcon("Rand.gif"));

    private JButton playBtn = new JButton(ResourceManager
            .getImageIcon("Play.gif"));

    protected JButton stepBtn = new JButton(ResourceManager
            .getImageIcon("Step.gif"));

    private JButton interactionBtn = new JButton(ResourceManager
            .getImageIcon("BothWays.gif"));

    private JButton buildBtn = new JButton(ResourceManager
            .getImageIcon("Build.gif"));

    private JButton panBtn = new JButton(ResourceManager
            .getImageIcon("Pan.gif"));

    private JButton arrowBtn = new JButton(ResourceManager
            .getImageIcon("Arrow.gif"));

    private JButton refreshBtn = new JButton(ResourceManager
            .getImageIcon("Refresh.gif"));

    private JButton zoomInBtn = new JButton(ResourceManager
            .getImageIcon("ZoomIn.gif"));

    private JButton zoomOutBtn = new JButton(ResourceManager
            .getImageIcon("ZoomOut.gif"));

    private JButton gaugeBtn = new JButton(ResourceManager
            .getImageIcon("Gauge.gif"));

    private JButton newNodeBtn = new JButton(ResourceManager
            .getImageIcon("New.gif"));

    private JButton dltBtn = new JButton(ResourceManager
            .getImageIcon("Delete.gif"));

    private JLabel timeTypeLabel = new JLabel();

    public NetworkPanel() {
    }

    /**
     * Constructs a new network panel
     * 
     * @param owner
     *            Reference to Simulation frame
     */
    public NetworkPanel(NetworkFrame owner) {
        this.parent = owner;
        this.setPreferredSize(new Dimension(400, 200));
        init();
    }

    /**
     * Called after objects are read in from xml files
     */
    public void initCastor() {
        network.init();

        Iterator i = nodeList.iterator();

        while (i.hasNext()) {
            Object o = i.next();

            if (!(o instanceof PNodeSubNetwork)) {
                this.getLayer().addChild((PNode) o);

                ScreenElement se = (ScreenElement) o;
                se.initCastor(this);
            }
        }

        // Have to initialize subnets after other objects have been added,
        // since it makes reference to neurons
        Iterator j = nodeList.iterator();

        while (j.hasNext()) {
            Object o = j.next();

            if (o instanceof PNodeSubNetwork) {
                this.getLayer().addChild((PNode) o);

                ScreenElement se = (ScreenElement) o;
                se.initCastor(this);
            }
        }

        resetGauges();
    }

    public void init() {
        this.setBackground(new Color(NetworkPreferences.getBackgroundColor()));
        theSerializer = new NetworkSerializer(this);
        clearBtn.addActionListener(this);
        randBtn.addActionListener(this);
        playBtn.addActionListener(this);
        stepBtn.addActionListener(this);
        buildBtn.addActionListener(this);
        interactionBtn.addActionListener(this);
        panBtn.addActionListener(this);
        arrowBtn.addActionListener(this);
        refreshBtn.addActionListener(this);
        zoomInBtn.addActionListener(this);
        zoomOutBtn.addActionListener(this);
        gaugeBtn.addActionListener(this);
        newNodeBtn.addActionListener(this);
        dltBtn.addActionListener(this);

        clearBtn.setToolTipText("Set selected nodes to 0");
        randBtn.setToolTipText("Randomize selected nodes and weights");
        playBtn.setToolTipText("Iterate network update algorithm");
        stepBtn.setToolTipText("Step network update algorithm");
        buildBtn.setToolTipText("Build mode (B)");
        interactionBtn
                .setToolTipText("Determine how network and world interact");
        panBtn.setToolTipText("Pan and right-drag-zoom mode (H)");
        arrowBtn.setToolTipText("Selection mode (V)");
        zoomInBtn.setToolTipText("Zoom in mode (Z)");
        zoomOutBtn.setToolTipText("Zoom out mode (Z)");
        gaugeBtn.setToolTipText("Add gauge to simulation");
        newNodeBtn.setToolTipText("Add new node");
        dltBtn.setToolTipText("Delete selected node");
        timeTypeLabel.setToolTipText("Reset iterations");

        topTools.add(zoomInBtn);
        topTools.add(zoomOutBtn);
        topTools.addSeparator();
        topTools.addSeparator();
        topTools.add(arrowBtn);
        topTools.add(panBtn);
        topTools.add(buildBtn);
        topTools.addSeparator();
        topTools.addSeparator();
        topTools.add(playBtn);
        topTools.add(stepBtn);
        topTools.addSeparator();
        topTools.addSeparator();
        topTools.add(randBtn);
        topTools.add(clearBtn);
        topTools.addSeparator();
        topTools.addSeparator();
        topTools.add(gaugeBtn);
        topTools.add(interactionBtn);

        buildTools.add(newNodeBtn);
        buildTools.add(dltBtn);

        this.setLayout(new BorderLayout());
        add("North", topTools);

        timeLabel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        iterationBar.add(timeLabel);
        iterationBar.add(timeTypeLabel);
        updateTimeType();

        bottomPanel.add(buildTools);
        bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(iterationBar);
        add("South", bottomPanel);

        if (buildToggle == false) {
            bottomPanel.setVisible(false);
        }

        this.mode = SELECTION;
        panEventHandler = this.getPanEventHandler();
        this.removeInputEventListener(this.getPanEventHandler());
        zoomEventHandler = this.getZoomEventHandler();
        this.removeInputEventListener(this.getZoomEventHandler());

        // Create and register event handlers
        mouseEventHandler = new MouseEventHandler(this, getLayer());
        keyEventHandler = new KeyEventHandler(this);
        addInputEventListener(mouseEventHandler);
        addInputEventListener(keyEventHandler);
        getRoot().getDefaultInputManager().setKeyboardFocus(keyEventHandler);
    }

    public ArrayList getNodeList() {
        return nodeList;
    }

    public ArrayList getSelection() {
        return selection;
    }

    public ContainerNetwork getNetwork() {
        return this.network;
    }

    public void save() {
        if (this.getCurrentFile() == null) {
            theSerializer.showSaveFileDialog();
        } else {
            theSerializer.writeNet(this.getCurrentFile());
        }

        parent.setChangedSinceLastSave(false);
    }

    /**
     * Forwards setNames accidentially invoked here
     */
    public void setName(String theTitle) {
        this.getParentFrame().setName(theTitle);
    }

    public void saveAs() {
        theSerializer.showSaveFileDialog();
    }

    public void open(File theFile) {
        theSerializer.readNetwork(theFile);
    }

    public void open() {
        theSerializer.showOpenFileDialog();
    }

    public File getCurrentFile() {
        return theSerializer.getCurrentFile();
    }

    public boolean getInOutMode() {
        return inOutMode;
    }

    public void setInOutMode(boolean b) {
        inOutMode = b;
    }

    /**
     * Returns a reference to the Simulation frame. Used to provide access to
     * Simulation level methods.
     * 
     * @return reference to the Simulation frame
     */
    public NetworkFrame getParentFrame() {
        return parent;
    }

    /**
     * Returns a refrence to the network selection event handler
     * 
     * @return reference to network handler
     */
    public MouseEventHandler getHandle() {
        return mouseEventHandler;
    }

    /**
     * Registers the neural network which this PCanvas represents
     * 
     * @param network
     *            reference to the neural network object
     */
    public void setNetwork(ContainerNetwork network) {
        this.network = network;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Component#getPreferredSize()
     */
    public Dimension getPreferredSize() {
        return (new Dimension(400, 400));
    }

    public void actionPerformed(ActionEvent e) {
        // Handle pop-up menu events
        Object o = e.getSource();

        if (o instanceof JMenuItem) {
            JMenuItem m = (JMenuItem) o;

            String st = m.getActionCommand();

            // Sensory and Motor Couplings
            if (m instanceof CouplingMenuItem) {
                CouplingMenuItem cmi = (CouplingMenuItem) m;
                Coupling coupling = cmi.getCoupling();

                if (coupling instanceof MotorCoupling) {
                    ((MotorCoupling) coupling)
                            .setNeuron(((PNodeNeuron) mouseEventHandler
                                    .getCurrentNode()));
                    ((PNodeNeuron) mouseEventHandler.getCurrentNode())
                            .setMotorCoupling((MotorCoupling) coupling);
                } else if (coupling instanceof SensoryCoupling) {
                    ((SensoryCoupling) coupling)
                            .setNeuron(((PNodeNeuron) mouseEventHandler
                                    .getCurrentNode()));
                    ((PNodeNeuron) mouseEventHandler.getCurrentNode())
                            .setSensoryCoupling((SensoryCoupling) coupling);
                }

                this.getParentFrame().setChangedSinceLastSave(true);
            }

            // Gauge events
            if (st.startsWith("Gauge:")) {
                // I use the label's text since it is the gauge's name
                GaugeFrame gauge = getParentFrame().getWorkspace().getGauge(
                        m.getText());

                if (gauge != null) {
                    gauge.getGaugedVars().setVariables(this.getSelection());
                    gauge.getGaugedVars().setNetworkName(this.getName());
                    gauge.getGaugePanel().update();
                }
            }

            if (st.equals("Not output")) {
                ((PNodeNeuron) mouseEventHandler.getCurrentNode())
                        .setOutput(false);
                this.getParentFrame().setChangedSinceLastSave(true);
                renderObjects();
            } else if (st.equals("Not input")) {
                ((PNodeNeuron) mouseEventHandler.getCurrentNode())
                        .setInput(false);
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("connect")) {
                connectSelected();
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("delete")) {
                deleteSelection();
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("cut")) {
                mouseEventHandler.cutToClipboard();
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("copy")) {
                mouseEventHandler.copyToClipboard();
            } else if (st.equals("paste")) {
                paste();
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("setNeuronProps")
                    || (st.equals("setSynapseProps"))) {
                showPrefsDialog(mouseEventHandler.getCurrentNode());
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("horizontal")) {
                alignHorizontal();
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("vertical")) {
                alignVertical();
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("spacingHorizontal")) {
                spacingHorizontal();
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("spacingVertical")) {
                spacingVertical();
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("setGeneralNetProps")) {
                showNetworkPrefs();
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("newNeuron")) {
                addNeuron();
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("winnerTakeAllNetwork")) {
                showWTADialog();
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("hopfieldNetwork")) {
                showHopfieldDialog();
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("backpropNetwork")) {
                showBackpropDialog();
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("competitiveNetwork")) {
                showCompetitiveDialog();
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("customNetwork")) {
                showCustomNetworkDialog();
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("trainBackpropNetwork")) {
                Network net = ((PNodeNeuron) mouseEventHandler.getCurrentNode())
                        .getNeuron().getParentNetwork().getNetworkParent();

                if (net != null) {
                    showBackpropTraining((Backprop) net);
                }

                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("randomizeNetwork")) {
                Network net = ((PNodeNeuron) mouseEventHandler.getCurrentNode())
                        .getNeuron().getParentNetwork().getNetworkParent();

                if (net != null) {
                    if (net instanceof Backprop) {
                        ((Backprop) net).randomize();
                    }
                }

                net = ((PNodeNeuron) mouseEventHandler.getCurrentNode())
                        .getNeuron().getParentNetwork();

                if (net != null) {
                    if (net instanceof Hopfield) {
                        ((Hopfield) net).randomizeWeights();
                    }
                }

                renderObjects();
                this.getParentFrame().setChangedSinceLastSave(true);
            } else if (st.equals("trainHopfieldNetwork")) {
                Network net = ((PNodeNeuron) mouseEventHandler.getCurrentNode())
                        .getNeuron().getParentNetwork();

                if (net != null) {
                    ((Hopfield) net).train();
                    renderObjects();
                }

                this.getParentFrame().setChangedSinceLastSave(true);
            }

            return;
        }

        // Handle button events
        JButton btemp = (JButton) o;

        if (btemp == clearBtn) {
            clearSelection();
            this.getParentFrame().setChangedSinceLastSave(true);
        } else if (btemp == randBtn) {
            randomizeSelection();
            this.getParentFrame().setChangedSinceLastSave(true);
        } else if (btemp == stepBtn) {
            updateNetworkAndWorld();
            this.getParentFrame().setChangedSinceLastSave(true);
        } else if (btemp == gaugeBtn) {
            addGauge();
            this.getParentFrame().setChangedSinceLastSave(true);
        } else if (btemp == playBtn) {
            if (theThread == null) {
                theThread = new NetworkThread(this);
            }

            if (theThread.isRunning() == false) {
                playBtn.setIcon(ResourceManager.getImageIcon("Stop.gif"));
                playBtn
                        .setToolTipText("Stop iterating network update algorithm");
                startNetwork();
            } else {
                playBtn.setIcon(ResourceManager.getImageIcon("Play.gif"));
                playBtn
                        .setToolTipText("Start iterating network update algorithm");
                stopNetwork();
            }

            this.getParentFrame().setChangedSinceLastSave(true);
        } else if (btemp == buildBtn) {
            if (mode != BUILD) {
                setMode(BUILD);
            }
        } else if (btemp == newNodeBtn) {
            addNeuron();
            this.getParentFrame().setChangedSinceLastSave(true);
        } else if (btemp == dltBtn) {
            deleteSelection();
            this.getParentFrame().setChangedSinceLastSave(true);
        } else if (btemp == interactionBtn) {
            if (interactionMode == 3) {
                interactionMode = 0;
            } else {
                interactionMode++;
            }

            switch (interactionMode) {
            case WORLD_TO_NET:
                interactionBtn.setIcon(ResourceManager
                        .getImageIcon("WorldToNet.gif"));
                interactionBtn
                        .setToolTipText("World is sending stimuli to the network");

                break;

            case NET_TO_WORLD:
                interactionBtn.setIcon(ResourceManager
                        .getImageIcon("NetToWorld.gif"));
                interactionBtn
                        .setToolTipText("Network output is moving the creature");

                break;

            case BOTH_WAYS:
                interactionBtn.setIcon(ResourceManager
                        .getImageIcon("BothWays.gif"));
                interactionBtn
                        .setToolTipText("World and network are interacting");

                break;

            case NEITHER_WAY:
                interactionBtn.setIcon(ResourceManager
                        .getImageIcon("NeitherWay.gif"));
                interactionBtn
                        .setToolTipText("World and network are disconnected");

                break;
            }

            this.getParentFrame().setChangedSinceLastSave(true);
        } else if (btemp == panBtn) {
            if (mode != PAN) {
                setMode(PAN);
            }
        } else if (btemp == arrowBtn) {
            if (mode != SELECTION) {
                setMode(SELECTION);
            }
        } else if (btemp == zoomInBtn) {
            if (mode != ZOOMIN) {
                setMode(ZOOMIN);
            }
        } else if (btemp == zoomOutBtn) {
            if (mode != ZOOMOUT) {
                setMode(ZOOMOUT);
            }
        }
    }

    public void setInteractionMode(int i) {
        if ((interactionMode > 0) && (interactionMode < 4)) {
            interactionMode = i;
        }
    }

    public int getInteractionMode() {
        return interactionMode;
    }

    /**
     * "Run" the network
     */
    public void startNetwork() {
        if (theThread == null) {
            theThread = new NetworkThread(this);
        }

        theThread.setRunning(true);
        theThread.start();
    }

    /**
     * "Stop" the network
     */
    public void stopNetwork() {
        if (theThread == null) {
            return;
        }

        theThread.setRunning(false);
        theThread = null;
    }

    /**
     * Returns the on-screen neurons
     * 
     * @return a collection of PNodeNeurons
     */
    public ArrayList getPNodeNeurons() {
        ArrayList v = new ArrayList();
        Iterator i = nodeList.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeNeuron) {
                v.add(pn);
            }
        }

        return v;
    }

    /**
     * Returns the on-screen neurons
     * 
     * @return a collection of PNodeNeurons
     */
    public ArrayList getPNodeSubnets() {
        ArrayList v = new ArrayList();
        Iterator i = nodeList.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeSubNetwork) {
                v.add(pn);
            }
        }

        return v;
    }

    /**
     * Paste contents of clipobard into this network
     */
    public void paste() {
        NetworkClipboard.paste(this);
        numberOfPastes++;
    }

    /**
     * Returns the on-screen syanpses
     * 
     * @return a collection of PNodeNeurons
     */
    public Collection getSynapseList() {
        Collection v = new Vector();
        Iterator i = nodeList.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeWeight) {
                v.add(pn);
            }
        }

        return v;
    }

    /**
     * Returns the selected PNodeNeurons
     * 
     * @return selected PNodeNeurons
     */
    public ArrayList getSelectedPNodeNeurons() {
        ArrayList ret = new ArrayList();
        Iterator i = selection.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeNeuron) {
                ret.add(pn);
            }
        }

        return ret;
    }

    /**
     * Returns the selected PNodeWeights
     * 
     * @return selected PNodeWeights
     */
    public ArrayList getSelectedPNodeWeights() {
        ArrayList ret = new ArrayList();
        Iterator i = selection.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeWeight) {
                ret.add(pn);
            }
        }

        return ret;
    }

    /**
     * Returns the current KeyEventHandler
     * 
     * @return KeyEventHandler current KeyEventHandler
     */
    public KeyEventHandler getKeyEventHandler() {
        return keyEventHandler;
    }

    /**
     * Returns the current KeyEventHandler
     * 
     * @return KeyEventHandler current KeyEventHandler
     */
    public void setKeyEventHandler(KeyEventHandler keh) {
        this.keyEventHandler = keh;
    }

    /**
     * Returns the on-screen neurons
     * 
     * @return selected neurons
     */
    public ArrayList getSelectedNeurons() {
        ArrayList neurons = new ArrayList();
        Iterator i = selection.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeNeuron) {
                neurons.add(((PNodeNeuron) pn).getNeuron());
            }
        }

        return neurons;
    }

    /**
     * Returns the on-screen weights
     * 
     * @return selecteed Weights
     */
    public ArrayList getSelectedWeights() {
        ArrayList v = new ArrayList();
        Iterator i = selection.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeWeight) {
                v.add(((PNodeWeight) pn).getWeight());
            }
        }

        return v;
    }

    /**
     * Toggle between pan and zoom mode
     * 
     * @param newmode
     *            mode to set cursor to
     */
    public void setMode(int newmode) {
        if (newmode != mode) {
            previousMode = mode;
            mode = newmode;

            if (newmode == PAN) {
                isAutoZoom = prevAutoZoom;
                this.addInputEventListener(this.panEventHandler);
                this.addInputEventListener(this.zoomEventHandler);
                this.removeInputEventListener(this.mouseEventHandler);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else if (newmode == ZOOMIN) {
                if (previousMode != ZOOMOUT) {
                    prevAutoZoom = isAutoZoom;
                }

                isAutoZoom = false;

                if (previousMode == PAN) {
                    this.removeInputEventListener(this.panEventHandler);
                    this.removeInputEventListener(this.zoomEventHandler);
                    this.addInputEventListener(this.mouseEventHandler);
                }

                setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                        ResourceManager.getImage("ZoomIn.gif"),
                        new Point(9, 9), "zoom_in"));
            } else if (newmode == ZOOMOUT) {
                if (previousMode != ZOOMIN) {
                    prevAutoZoom = isAutoZoom;
                }

                isAutoZoom = false;

                if (previousMode == PAN) {
                    this.removeInputEventListener(this.panEventHandler);
                    this.removeInputEventListener(this.zoomEventHandler);
                    this.addInputEventListener(this.mouseEventHandler);
                }

                setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                        ResourceManager.getImage("ZoomOut.gif"),
                        new Point(9, 9), "zoom_out"));
            } else if ((newmode == SELECTION) || (newmode == TEMP_SELECTION)) {
                isAutoZoom = prevAutoZoom;

                if (previousMode == PAN) {
                    this.removeInputEventListener(this.panEventHandler);
                    this.removeInputEventListener(this.zoomEventHandler);
                    this.addInputEventListener(this.mouseEventHandler);
                }

                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } else if (newmode == BUILD) {
                // TODO replace with build code
                isAutoZoom = prevAutoZoom;

                if (previousMode == PAN) {
                    this.removeInputEventListener(this.panEventHandler);
                    this.removeInputEventListener(this.zoomEventHandler);
                    this.addInputEventListener(this.mouseEventHandler);
                }

                setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                        ResourceManager.getImage("Build.gif"), new Point(0, 0),
                        "Build Cursor"));
            }

            // else if (newmode == DELETE) {
            // //TODO replace with break code
            // isAutoZoom = prevAutoZoom;
            // if (prevCursorMode == PAN) {
            // this.removeInputEventListener(this.panEventHandler);
            // this.removeInputEventListener(this.zoomEventHandler);
            // this.addInputEventListener(this.mouseEventHandler);
            // }
            // setCursor(Toolkit.getDefaultToolkit().createCustomCursor(ResourceManager.getImage("Delete.gif"),new
            // Point(0,0),"Break Cursor"));
            // }
        }
    }

    /**
     * Display units based on whether network is discrete or continous.
     */
    public void updateTimeType() {
        network.updateTimeType();
        timeTypeLabel.setText(" " + getNetwork().getTimeLabel());
    }

    /**
     * Update time label depending on the type of network
     */
    public void updateTimeLabel() {
        if (network.getTimeType() == Network.CONTINUOUS) {
            timeLabel.setText("" + Utils.round(network.getTime(), 6)); // Update
                                                                        // the
                                                                        // timeLabel
        } else {
            timeLabel.setText("" + (int) network.getTime()); // Update the
                                                                // timeLabel
        }
    }

    /**
     * Update the network, gauges, and world. This is where the main control
     * between components happens. Called by world component (on clicks), and
     * the network-thread.
     */
    public synchronized void updateNetwork() {
        // Get stimulus vector from world and update input nodes
        if ((interactionMode == WORLD_TO_NET) || (interactionMode == BOTH_WAYS)) {
            updateNetworkInputs();
        }

        this.network.update(); // Call Network's update function
        updateTimeLabel();
        renderObjects();

        // Send state-information to gauge(s)
        this.getParentFrame().getWorkspace().updateGauges();

        update_completed = true;

        // Clear input nodes
        if ((interactionMode == WORLD_TO_NET) || (interactionMode == BOTH_WAYS)) {
            clearNetworkInputs();
        }
    }

    /**
     * Update network then get output from the world object
     */
    public synchronized void updateNetworkAndWorld() {
        updateNetwork();

        // Update World
        if ((interactionMode == NET_TO_WORLD) || (interactionMode == BOTH_WAYS)) {
            updateWorld();
        }
    }

    /**
     * Go through each output node and send the associated output value to the
     * world component
     */
    public void updateWorld() {
        Iterator it = getOutputList().iterator();

        while (it.hasNext()) {
            PNodeNeuron n = (PNodeNeuron) it.next();

            if (n.getMotorCoupling().getAgent() != null) {
                n.getMotorCoupling().getAgent().setMotorCommand(
                        n.getMotorCoupling().getCommandArray(),
                        n.getNeuron().getActivation());
            }
        }
    }

    /**
     * Update input nodes of the network based on the state of the world
     */
    public void updateNetworkInputs() {
        Iterator it = getInputList().iterator();

        while (it.hasNext()) {
            PNodeNeuron n = (PNodeNeuron) it.next();

            if (n.getSensoryCoupling().getAgent() != null) {
                double val = n.getSensoryCoupling().getAgent().getStimulus(
                        n.getSensoryCoupling().getSensorArray());
                n.getNeuron().setInputValue(val);
            } else {
                n.getNeuron().setInputValue(0);
            }
        }
    }

    /**
     * Clears out input values of network nodes, which otherwise linger and
     * cause problems
     */
    public void clearNetworkInputs() {
        Iterator it = getInputList().iterator();

        while (it.hasNext()) {
            PNodeNeuron n = (PNodeNeuron) it.next();
            n.getNeuron().setInputValue(0);
        }
    }

    /**
     * Used by Network thread to ensure that an update cycle is complete before
     * updating again.
     */
    public boolean isUpdateCompleted() {
        return update_completed;
    }

    public void setUpdateCompleted(boolean b) {
        update_completed = b;
    }

    // ///////////////////////////////////////////////////
    // Neuron and weight deletion and addition methods //
    // ///////////////////////////////////////////////////

    /**
     * Delete all neurons, reset gauges
     */
    public void deleteAll() {
        selectAll();
        deleteSelection();
        renderObjects();
        resetGauges();
    }

    /**
     * Adds a simnet network
     * 
     * @param net
     *            the net to add
     * @param layout
     *            how to lay out the neurons in the network
     */
    public void addNetwork(Network net, String layout) {
        network.addNetwork(net);

        PNodeSubNetwork sn = new PNodeSubNetwork(net, this);
        sn.initSubnet(layout);
        nodeList.add(sn);
        getLayer().addChild(sn);
        renderObjects();
        repaint();
    }

    /**
     * Adds a PNode to the the network
     * 
     * @param theNode
     *            the node to add to the network
     * @param whether
     *            the newly added node should be the only selected node
     */
    public void addNode(PNode theNode, boolean select) {
        nodeList.add(theNode);
        ((ScreenElement) theNode).addToNetwork(this);
        this.getLayer().addChild(theNode);

        if (select == true) {
            unselectAll();
            select(theNode);
        }

        resetGauges();
        renderObjects();
    }

    /**
     * Delete a PNode from the NetworkPanel
     * 
     * @param node
     *            PNode to be deleted fromm network
     */
    public void deleteNode(PNode node) {
        /*
         * If the node is a child of a PNodeSubNetwork, check to see if it is
         * the last child of the subnetwork and if so, delete the subnetwork
         * node.
         * 
         * NOTE: This code could probably be put in the PNodeNeuron delete
         * method but I was not sure whether it would be the last node to be
         * deleted when a selection is made. It seemed that the code may have to
         * be duplicated in the PNodeLine and/or PNodeWeight classes as well so
         * I just put it in this one place for now.
         */
        if (node.getParent() instanceof PNodeSubNetwork) {
            PNodeSubNetwork sn = (PNodeSubNetwork) node.getParent();

            if (sn.getChildrenCount() == 1) {
                /*
                 * Last node inside the PNodeSubNetwork so make sure to clean up
                 * the PNodeSubNetwork node itself.
                 */
                network.deleteNetwork(sn.getSubnet());
                node.removeFromParent();
                sn.delete();
                sn.removeFromParent();
                nodeList.remove(sn);
            } else {
                ((ScreenElement) node).delete();
                node.removeFromParent();
            }
        } else {
            ((ScreenElement) node).delete();
            node.removeFromParent();
        }

        nodeList.remove(node);
        resetGauges(); // TODO: Check whether this is a monitored node, and
                        // reset gauge if it is.
    }

    /**
     * Add a new PNodeNeuron to the network, either at the last position clicked
     * on screen or to the right of the last selected neuron
     */
    protected void addNeuron() {
        if (getLastClicked() == null) {
            return;
        }

        PNodeNeuron theNode = new PNodeNeuron(getLastClicked(), this);
        theNode.initNewNeuron();
        addNode(theNode, true);
    }

    /**
     * Create a PNodeWeight connecting two PNodeNeurons
     * 
     * @param source
     *            source PNodeNeuron
     * @param target
     *            target PNodeNeuron
     * @param weight
     *            weight, to be associated with a PNodeWeight, connecting source
     *            and target
     */
    public void addWeight(PNodeNeuron source, PNodeNeuron target, Synapse weight) {
        weight.setSource(source.getNeuron());
        weight.setTarget(target.getNeuron());
        network.addWeight(weight);

        PNodeWeight theNode = new PNodeWeight(source, target, weight);
        addNode(theNode, false);
    }

    /**
     * Create a PNodeWeight connecting two PNodeNeurons
     * 
     * @param source
     *            source PNodeNeuron
     * @param target
     *            target PNodeNeuron
     */
    protected void addWeight(PNodeNeuron source, PNodeNeuron target) {
        PNodeWeight w = new PNodeWeight(source, target);
        network.addWeight(w.getWeight());
        addNode(w, false);
    }

    /**
     * @return true if the weight exists, false otherwise
     */
    private boolean checkWeight(Synapse w) {
        Iterator i = nodeList.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeWeight) {
                if (((PNodeWeight) pn).getWeight().equals(w)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Make sure all weights in the logical network are represented by
     * PNodeWeights in the Network Panel view. Handles cases where connections
     * are added directly to the network object
     */
    public void updateWeights() {
        // Update node_list with all weights
        ArrayList weights = (ArrayList) network.getWeightList();

        for (int i = 0; i < weights.size(); i++) {
            Synapse w = (Synapse) weights.get(i);

            if ((checkWeight(w) == false)) {
                addWeight(findPNodeNeuron(w.getSource()), findPNodeNeuron(w
                        .getTarget()), w);
            }
        }
    }

    /**
     * Connect all selected nodes to currently clicked node in netpanel
     */
    public void connectSelected() {
        PNode currentNode = mouseEventHandler.getCurrentNode();

        if ((currentNode != null) && (currentNode instanceof PNodeNeuron)) {
            for (int i = 0; i < selection.size(); i++) {
                PNode n = (PNode) selection.get(i);

                if (n instanceof PNodeNeuron) {
                    addWeight((PNodeNeuron) n, (PNodeNeuron) currentNode);
                }
            }

            currentNode.moveToFront();
        }
    }

    public void connectSelectedTo(PNodeNeuron target) {
        if (target != null) {
            for (int i = 0; i < selection.size(); i++) {
                PNode n = (PNode) selection.get(i);

                if (n instanceof PNodeNeuron) {
                    addWeight((PNodeNeuron) n, (PNodeNeuron) target);
                }
            }

            target.moveToFront();
        }
    }

    /**
     * Connect one set of neurons to another
     * 
     * @param source
     *            source pnodeneuron
     * @param target
     *            target pnodeneuron
     */
    public void connect(ArrayList source, ArrayList target) {
        for (int i = 0; i < source.size(); i++) {
            for (int j = 0; j < target.size(); j++) {
                PNodeNeuron src = (PNodeNeuron) source.get(i);
                PNodeNeuron tar = (PNodeNeuron) target.get(j);
                addWeight(src, tar);
            }
        }
    }

    /**
     * Find the PNodeNeuron associated with a logical Neuron
     * 
     * @param n
     *            refrence to the Neuron object to be assocaited with a
     *            PNodeNeuron
     * 
     * @return PNodeNeuron associated with the provided neuron object
     */
    public PNodeNeuron findPNodeNeuron(Neuron n) {
        Iterator i = nodeList.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeNeuron) {
                if (((PNodeNeuron) pn).getNeuron().equals(n)) {
                    return (PNodeNeuron) pn;
                }
            }
        }

        return null; // PNode not found
    }

    /**
     * Get pnode (weight or neuron) with this name
     */
    public PNode getPNode(String name) {
        Iterator i = nodeList.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeNeuron) {
                if (((PNodeNeuron) pn).getId().equals(name)) {
                    return pn;
                }
            }

            if (pn instanceof PNodeWeight) {
                if (((PNodeWeight) pn).getId().equals(name)) {
                    return pn;
                }
            }
        }

        return null; // PNode not found
    }

    public void addText(String text) {
        PNodeText theText = new PNodeText(text);
        theText.addToNetwork(this);
    }

    // ////////////////////////////////////
    // Node select and unselect methods //
    // ////////////////////////////////////

    /**
     * Select a PNode (Neuron or weight). Called by networkSelectionEvent
     * handler
     * 
     * @param node
     *            PNode to select
     */
    public void select(PNode node) {
        if (selection.contains(node)) {
            return;
        }

        if (node.getParent() instanceof PNodeWeight) {
            selection.add(node.getParent());
            SelectionHandle.addSelectionHandleTo(node.getParent(), this);

            return;
        }

        if (node instanceof ScreenElement) {
            if (((ScreenElement) node).isSelectable()) {
                selection.add(node);
                SelectionHandle.addSelectionHandleTo(node, this);
            }
        }
    }

    /**
     * Unselect a specific PNode
     * 
     * @param node
     *            the PNode to unselect
     */
    public void unselect(PNode node) {
        this.selection.remove(node);

        if (node.getParent() instanceof PNodeWeight) {
            SelectionHandle.removeSelectionHandleFrom(node.getParent());
        }

        SelectionHandle.removeSelectionHandleFrom(node);
    }

    /**
     * Select all PNodes (Neurons and Weights)
     */
    public void selectAll() {
        selection.clear();

        Iterator i = nodeList.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();
            select(pn);
        }

        renderObjects();
    }

    /**
     * Select all PNodeNeurons
     */
    public void selectNeurons() {
        selection.clear();

        Iterator i = nodeList.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeNeuron) {
                select(pn);
            }
        }

        renderObjects();
    }

    /**
     * Select all PNodeWeights
     */
    public void selectWeights() {
        selection.clear();

        Iterator i = nodeList.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeWeight) {
                select(pn);
            }
        }

        renderObjects();
    }

    /**
     * Determines if a node is already selected
     * 
     * @param node
     *            node to be checked
     * 
     * @return true if the input object is already selected; false otherwise
     */
    public boolean isSelected(PNode node) {
        if (node.getParent() instanceof PNodeWeight) {
            return selection.contains(node.getParent());
        }

        return selection.contains(node);
    }

    public void toggleSelection(PNode node) {
        if (isSelected(node)) {
            unselect(node);
        } else {
            select(node);
        }
    }

    public void toggleSelection(Collection items) {
        Iterator itemIt = items.iterator();

        while (itemIt.hasNext()) {
            PNode node = (PNode) itemIt.next();
            toggleSelection(node);
        }
    }

    /**
     * @param Collection
     *            Collection of items to be selected
     */
    public void select(Collection items) {
        Iterator itemIt = items.iterator();

        while (itemIt.hasNext()) {
            PNode node = (PNode) itemIt.next();
            select(node);
        }
    }

    /**
     * Unselect a collection of objects
     * 
     * @param items
     *            objects to be unselect
     */
    public void unselect(Collection items) {
        Iterator itemIt = items.iterator();

        while (itemIt.hasNext()) {
            PNode node = (PNode) itemIt.next();
            unselect(node);
        }
    }

    /**
     * Unselect all selected PNodes
     */
    public void unselectAll() {
        while (selection.size() > 0) {
            unselect((PNode) selection.get(selection.size() - 1));
        }

        renderObjects();
    }

    /**
     * Delete the currently selected PNodes (Neurons and Weights)
     */
    public void deleteSelection() {
        for (Iterator e = selection.iterator(); e.hasNext();) {
            PNode node = (PNode) e.next();
            deleteNode(node);
        }

        selection.clear();
        renderObjects();
    }

    /**
     * Get a reference to the currently selected node. Assumes that only one
     * PNode is selected.
     * 
     * @return Reference to a single selected node
     */
    public PNode getSingleSelection() {
        if (this.selection.size() != 1) {
            return null;
        }

        return (PNode) selection.get(0);
    }

    /**
     * Returns the x position (in global coords) of input node's bounds. It
     * simply gets the position of the input node, after that converts it to
     * Global coordinate system before returning the x value. The reason for
     * using getGlobalX() and getGlobalY() is that PNode.getX() and PNode.getY()
     * only returns position of PNode in local coordinate system. However,
     * sometimes the position of a node in global coordinate system (which in
     * this case is NetWorkPanel's system ) is needed for moving, calculating..
     * 
     * @param node
     *            PNode that has the x position to be returned.
     * 
     * @return x position (in global coords) of input node's bounds.
     */
    public static double getGlobalX(PNode node) {
        Point2D p = new Point2D.Double(node.getX(), node.getY());

        return node.localToGlobal(p).getX();
    }

    /**
     * Returns the y position (in global coords) of input node's bounds. It
     * simply get the position of the input node, afterthat converts it to
     * Global coordinate system before returning the y value. The reason for
     * using getGlobalX() and getGlobalY() is that PNode.getX() and PNode.getY()
     * only returns position of PNode in local coordinate system. However,
     * sometimes the position of a node in global coordinate system (which in
     * this case is NetWorkPanel's system ) is needed for moving, calculating..
     * 
     * @param node
     *            PNode that has the y position to be returned.
     * 
     * @return y position (in global coords) of input node's bounds.
     */
    public static double getGlobalY(PNode node) {
        Point2D p = new Point2D.Double(node.getX(), node.getY());

        return node.localToGlobal(p).getY();
    }

    /**
     * Return global coordinates of x centerpoint of a Neuron
     * 
     * @param node
     *            the PNodeNeuron whose centerpoint is desired
     * 
     * @return x coordinate (in global coordinates) of the PNodeNeuron
     */
    public static double getGlobalCenterX(PNodeNeuron node) {
        Point2D p = new Point2D.Double(node.getX() + PNodeNeuron.NEURON_HALF,
                node.getY() + PNodeNeuron.NEURON_HALF);

        return node.localToGlobal(p).getX();
    }

    /**
     * Return global coordinates of y centerpoint of a Neuron
     * 
     * @param node
     *            the PNodeNeuron whose centerpoint is desired
     * 
     * @return y coordinate (in global coordinates) of the PNodeNeuron
     */
    public static double getGlobalCenterY(PNodeNeuron node) {
        double y = node.getY() + PNodeNeuron.NEURON_HALF;

        // Compensate for size of input/output line.
        if (node.isInput()) {
            y += PNodeNeuron.ARROW_LINE;
        }

        if (node.isOutput()) {
            y -= PNodeNeuron.ARROW_LINE;
        }

        Point2D p = new Point2D.Double(node.getX() + PNodeNeuron.NEURON_HALF, y);

        return node.localToGlobal(p).getY();
    }

    /**
     * Set activation of selected neurons to zero
     */
    protected void clearSelection() {
        Iterator i = selection.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeNeuron) {
                ((PNodeNeuron) pn).getNeuron().setActivation(0);
            }
        }

        renderObjects();
    }

    public void clearAll() {
        Iterator i = nodeList.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeNeuron) {
                ((PNodeNeuron) pn).getNeuron().setActivation(0);
            }
        }

        renderObjects();
    }

    /**
     * Randomize selected neurons and weights
     */
    protected void randomizeSelection() {
        for (Iterator en = this.selection.iterator(); en.hasNext();) {
            ScreenElement se = (ScreenElement) en.next();
            se.randomize();
        }

        renderObjects();
    }

    /**
     * Increment selected objects (weights and neurons)
     */
    public void incrementSelectedObjects() {
        ArrayList v = getSelection();

        for (Iterator en = v.iterator(); en.hasNext();) {
            ScreenElement se = (ScreenElement) en.next();
            se.increment();
        }
    }

    /**
     * Decrement selected objects (weights and neurons)
     */
    public void decrementSelectedObjects() {
        ArrayList v = getSelection();

        for (Iterator en = v.iterator(); en.hasNext();) {
            ScreenElement se = (ScreenElement) en.next();
            se.decrement();
        }
    }

    /**
     * Show neuron or weight dialog. If multiple nodes are selected show all of
     * given type
     * 
     * @param theNode
     *            node for which to show dialog
     */
    public void showPrefsDialog(PNode theNode) {
        if (theNode instanceof PNodeNeuron) {
            showNeuronPrefs();
        } else if (theNode.getParent() instanceof PNodeWeight) {
            showWeightPrefs();
        }
    }

    /**
     * Show dialog for weight settings
     */
    public void showWeightPrefs() {
        ArrayList synapses = getSelectedPNodeWeights();

        if (synapses.size() == 0) {
            PNode p = mouseEventHandler.getCurrentNode();

            if (p instanceof PNodeWeight) {
                synapses.add(p);
            } else {
                return;
            }
        }

        SynapseDialog theDialog = new SynapseDialog(synapses);
        theDialog.pack();
        theDialog.setVisible(true);

        if (!theDialog.hasUserCancelled()) {
            theDialog.commmitChanges();
        }

        renderObjects();
    }

    /**
     * Show dialog for selected neurons
     */
    public void showNeuronPrefs() {
        ArrayList pnodes = getSelectedPNodeNeurons();

        // If no neurons are selected use the node that was clicked on
        if (pnodes.size() == 0) {
            PNode p = mouseEventHandler.getCurrentNode();

            if (p instanceof PNodeNeuron) {
                pnodes.add(p);
            } else {
                return;
            }
        }

        NeuronDialog theDialog = new NeuronDialog(pnodes);
        theDialog.pack();
        theDialog.setVisible(true);

        if (!theDialog.hasUserCancelled()) {
            theDialog.commitChanges();
        }

        renderObjects();
    }

    /**
     * Shows WTA dialog
     */
    public void showWTADialog() {
        WTADialog dialog = new WTADialog(this);
        dialog.pack();
        dialog.setVisible(true);

        if (!dialog.hasUserCancelled()) {
            WinnerTakeAll wta = new WinnerTakeAll(dialog.getNumUnits());
            this.addNetwork(wta, dialog.getCurrentLayout());
        }

        renderObjects();
    }

    /**
     * Shows hopfield dialog
     */
    public void showHopfieldDialog() {
        HopfieldDialog dialog = new HopfieldDialog();
        dialog.pack();
        dialog.setVisible(true);

        if (!dialog.hasUserCancelled()) {
            if (dialog.getType() == HopfieldDialog.DISCRETE) {
                DiscreteHopfield hop = new DiscreteHopfield(dialog
                        .getNumUnits());
                this.addNetwork(hop, dialog.getCurrentLayout());
            } else if (dialog.getType() == HopfieldDialog.CONTINUOUS) {
                ContinuousHopfield hop = new ContinuousHopfield(dialog
                        .getNumUnits());
                this.addNetwork(hop, dialog.getCurrentLayout());
            }
        }

        repaint();
    }

    /**
     * Shows backprop dialog
     */
    public void showBackpropDialog() {
        BackpropDialog dialog = new BackpropDialog(this);
        dialog.pack();
        dialog.setVisible(true);

        if (!dialog.hasUserCancelled()) {
            Backprop bp = new Backprop();
            bp.setN_inputs(dialog.getNumInputs());
            bp.setN_hidden(dialog.getNumHidden());
            bp.setN_outputs(dialog.getNumOutputs());
            bp.defaultInit();
            this.addNetwork(bp, "Layers");
        }

        renderObjects();
    }

    /**
     * Shows Layerd Nework Panel
     */
    public void showCustomNetworkDialog() {
        CustomNetworkDialog dialog = new CustomNetworkDialog();
        dialog.pack();
        dialog.setVisible(true);
        renderObjects();
    }

    public void showCompetitiveDialog() {
        CompetitiveDialog dialog = new CompetitiveDialog(this);
        dialog.pack();
        dialog.setVisible(true);

        if (!dialog.hasUserCancelled()) {
            Competitive comp_net = new Competitive(dialog.getNumberOfNeurons());
            this.addNetwork(comp_net, "Grid");
       }
       renderObjects();
    
    }

    /**
     * Aligns neurons horizontally
     */
    public void alignHorizontal() {
        Iterator i = getSelectedPNodeNeurons().iterator();
        double min = Double.MAX_VALUE;

        while (i.hasNext()) {
            PNodeNeuron node = (PNodeNeuron) i.next();
            PNodeNeuron n = (PNodeNeuron) node;

            if (n.getYpos() < min) {
                min = n.getYpos();
            }
        }

        i = getSelectedPNodeNeurons().iterator();

        while (i.hasNext()) {
            PNodeNeuron node = (PNodeNeuron) i.next();
            PNodeNeuron n = (PNodeNeuron) node;
            n.setYpos(min);
        }

        renderObjects();
    }

    /**
     * Aligns neurons vertically
     */
    public void alignVertical() {
        Iterator i = getSelectedPNodeNeurons().iterator();
        double min = Double.MAX_VALUE;

        while (i.hasNext()) {
            PNodeNeuron node = (PNodeNeuron) i.next();
            PNodeNeuron n = (PNodeNeuron) node;

            if (n.getXpos() < min) {
                min = n.getXpos();
            }
        }

        i = getSelectedPNodeNeurons().iterator();

        while (i.hasNext()) {
            PNodeNeuron node = (PNodeNeuron) i.next();
            PNodeNeuron n = (PNodeNeuron) node;
            n.setXpos(min);
        }

        renderObjects();
    }

    /**
     * Spaces neurons horizontally
     */
    public void spacingHorizontal() {
        if (getSelectedNeurons().size() <= 1) {
            return;
        }

        ArrayList sortedNeurons = getSelectedPNodeNeurons();
        java.util.Collections.sort(sortedNeurons, new XComparator());

        double min = ((PNodeNeuron) sortedNeurons.get(0)).getXpos();
        double max = ((PNodeNeuron) sortedNeurons.get(sortedNeurons.size() - 1))
                .getXpos();
        double space = (max - min) / (sortedNeurons.size() - 1);

        for (int j = 0; j < sortedNeurons.size(); j++) {
            PNodeNeuron n = (PNodeNeuron) sortedNeurons.get(j);
            n.setXpos(min + (space * j));
        }

        renderObjects();
    }

    /**
     * Spaces neurons vertically
     */
    public void spacingVertical() {
        if (getSelectedNeurons().size() <= 1) {
            return;
        }

        ArrayList sortedNeurons = getSelectedPNodeNeurons();
        java.util.Collections.sort(sortedNeurons, new YComparator());

        double min = ((PNodeNeuron) sortedNeurons.get(0)).getYpos();
        double max = ((PNodeNeuron) sortedNeurons.get(sortedNeurons.size() - 1))
                .getYpos();
        double space = (max - min) / (sortedNeurons.size() - 1);

        for (int j = 0; j < sortedNeurons.size(); j++) {
            PNodeNeuron n = (PNodeNeuron) sortedNeurons.get(j);
            n.setYpos(min + (space * j));
        }

        renderObjects();
    }

    /**
     * Shows dialog for backprop training
     * 
     * @param bp
     *            network to be trained
     */
    public void showBackpropTraining(Backprop bp) {
        BackpropTrainingDialog dialog = new BackpropTrainingDialog(this, bp);
        dialog.pack();
        dialog.setVisible(true);
        renderObjects();
    }

    /**
     * Shows network preferences dialog
     */
    public void showNetworkPrefs() {
        NetworkDialog dialog = new NetworkDialog(this);
        dialog.pack();
        dialog.setVisible(true);

        if (dialog.hasUserCancelled()) {
            dialog.returnToCurrentPrefs();
        } else {
            getNetwork().setPrecision(
                    Integer.parseInt(dialog.getPrecisionField().getText()));

            // getParentFrame().getWorkspace().getNetworkList().updateUsingIndent(dialog.isUsingIndent());
            // getParentFrame().getWorkspace().getNetworkList().updateNudge(dialog.getNudgeAmountField());
            dialog.setAsDefault();
        }

        renderObjects();
    }

    /**
     * Adds selected neurons to a new layer object
     */
    public void addLayer() {
        Iterator i = selection.iterator();
        Vector the_neurons = new Vector();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeNeuron) {
                the_neurons.add(((PNodeNeuron) pn).getNeuron());
            }
        }

        // network.addLayer((Collection) the_neurons);
    }

    // //////////////////////////
    // Gauge handling methods //
    // //////////////////////////

    /**
     * Adds a new gauge
     */
    public void addGauge() {
        this.getParentFrame().getWorkspace().addGauge();
        this.getParentFrame().getWorkspace().getLastGauge().getGaugedVars()
                .setVariables(getPNodeNeurons());
        this.getParentFrame().getWorkspace().getLastGauge().getGaugedVars()
                .setNetworkName(this.getName());
    }

    /**
     * Reset all gauges so that they gauge a default set of objects (currently
     * all neurons)
     */
    public void resetGauges() {
        ArrayList gaugeList = this.getParentFrame().getWorkspace().getGauges(
                this.getParentFrame());

        for (int i = 0; i < gaugeList.size(); i++) {
            GaugeFrame gauge = (GaugeFrame) gaugeList.get(i);
            gauge.getGaugedVars().setVariables(this.getPNodeNeurons());
            gauge.getGaugePanel().resetGauge();
        }
    }

    // ///////////////////////////
    // Other Graphics Methods //
    // ///////////////////////////
    public void repaint() {
        super.repaint();

        if ((network != null) && (nodeList != null) && (nodeList.size() > 1)
                && (mode != PAN) && (isAutoZoom == true)) {
            centerCamera();
        }
    }

    /**
     * Centers the neural network in the middle of the PCanvas
     */
    public void centerCamera() {
        PCamera cam = this.getCamera();
        PBounds pb = getLayer().getGlobalFullBounds();
        pb = new PBounds(pb.x - 40, pb.y - 40, pb.width + 80, pb.height + 80);
        cam.animateViewToCenterBounds(pb, true, 0);
    }

    /**
     * Nudge selected object
     * 
     * @param offset_x
     *            amount to nudge in the x direction
     * @param offset_y
     *            amount to nudge in the y direction
     */
    protected void nudge(int offset_x, int offset_y) {
        Iterator it = getSelection().iterator();

        while (it.hasNext()) {
            ScreenElement se = (ScreenElement) it.next();
            se.nudge(offset_x, offset_y, nudgeAmount);
        }

        renderObjects();
        repaint();
    }

    /**
     * Calls render methods of PNodeNeurons and PNodeWeights before painting
     */
    public synchronized void renderObjects() {
        Iterator i = nodeList.iterator();

        while (i.hasNext()) {
            ScreenElement se = (ScreenElement) i.next();
            se.renderNode();
        }
    }

    /**
     * Reset everything without deleting any nodes or weights. Clear the gauges.
     * Unselect all. Reset the time. Used when reading in a new network.
     */
    public void resetNetwork() {
        stopNetwork();
        getNodeList().clear();
        getLayer().removeAllChildren();
        unselectAll();
        network.setTime(0);
        updateTimeLabel();
        resetGauges();
    }

    /**
     * @return true if auto-zooming is on, false otherwise
     */
    public boolean isAutoZoom() {
        return isAutoZoom;
    }

    /**
     * @param b
     *            true if auto-zooming is on, false otherwise
     */
    public void setAutoZoom(boolean b) {
        isAutoZoom = b;
    }

    /**
     * @return true if auto-zooming is on, false otherwise
     */
    public boolean outlineSubnetwork() {
        return outlineSubnetwork;
    }

    /**
     * @param b
     *            true if subnetwork outlines should be displayed
     */
    public void setSubnetworkOutline(boolean b) {
        outlineSubnetwork = b;
    }

    /**
     * Print debug information to standard output
     */
    public void debug() {
        Iterator i = getSelection().iterator();

        if (i.hasNext()) {
            System.out.println("\n---------- Selected Neuron(s) Debug--------");

            while (i.hasNext()) {
                PNode n = (PNode) i.next();

                if (n instanceof PNodeNeuron) {
                    // ((PNodeNeuron)n).getNeuron().debug();
                    ((PNodeNeuron) n).debug();
                }
            }
        } else {
            System.out.println("---------- Network GUI Debug --------");
            System.out.println("" + nodeList.size() + " nodes.");
            System.out.println("" + selection.size() + " selected nodes.");

            System.out.println("\n---------- Neural Network Debug --------");
            getNetwork().debug();
        }
    }

    /**
     * Resets all PNodes to graphics values, which may have been changed by the
     * user
     */
    public void resetLineColors() {
        for (int i = 0; i < nodeList.size(); i++) {
            PNode node = (PNode) nodeList.get(i);

            if (node instanceof PNodeWeight) {
                ((PNodeWeight) node).resetLineColors();
            } else if (node instanceof PNodeNeuron) {
                ((PNodeNeuron) node).resetLineColors();
            }
        }
    }

    /**
     * @param node_list
     *            The node_list to set.
     */
    public void setNodeList(ArrayList node_list) {
        this.nodeList = node_list;
    }

    /**
     * @param selection
     *            The selection to set.
     */
    public void setSelection(ArrayList selection) {
        this.selection = selection;
    }

    /**
     * Forwards results of mouseHandler method
     * 
     * @return the last point clicked on screen
     */
    public Point2D getLastClicked() {
        return mouseEventHandler.getLastLeftClicked();
    }

    /**
     * @return Returns the theSerializer.
     */
    public NetworkSerializer getSerializer() {
        return theSerializer;
    }

    /**
     * @return Returns the cursorMode.
     */
    public int getMode() {
        return mode;
    }

    /**
     * @return Returns the nudgeAmount.
     */
    public double getNudgeAmount() {
        return nudgeAmount;
    }

    /**
     * @param nudgeAmount
     *            The nudgeAmount to set.
     */
    public void setNudgeAmount(double nudgeAmount) {
        this.nudgeAmount = nudgeAmount;
    }

    /**
     * Overrides JComponent getName, and adverts to the frame level, which is
     * where name information is kept.
     */
    public String getName() {
        return this.getParentFrame().getName();
    }

    /**
     * Returns a list of all couplings associated with neurons in this network
     */
    public ArrayList getCouplingList() {
        ArrayList ret = new ArrayList();
        Iterator i = nodeList.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeNeuron) {
                Coupling c = ((PNodeNeuron) pn).getSensoryCoupling();

                if (c != null) {
                    ret.add(c);
                }

                c = ((PNodeNeuron) pn).getMotorCoupling();

                if (c != null) {
                    ret.add(c);
                }
            }
        }

        return ret;
    }

    /**
     * @return Returns the inputList.
     */
    public ArrayList getInputList() {
        // TODO: This is an expensive routine given that it's called every time
        // the network is updated
        // but the alternative is maintaining a separate list of inputs which
        // programmers can
        // easily forget about. What to do...?
        ArrayList ret = new ArrayList();
        Iterator i = nodeList.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeNeuron) {
                if (((PNodeNeuron) pn).getSensoryCoupling() != null) {
                    ret.add(pn);
                }
            }
        }

        return ret;
    }

    /**
     * @return Returns the outputList.
     */
    public ArrayList getOutputList() {
        ArrayList ret = new ArrayList();
        Iterator i = nodeList.iterator();

        while (i.hasNext()) {
            PNode pn = (PNode) i.next();

            if (pn instanceof PNodeNeuron) {
                if (((PNodeNeuron) pn).getMotorCoupling() != null) {
                    ret.add(pn);
                }
            }
        }

        return ret;
    }

    public void propertyChange(PropertyChangeEvent arg0) {
        if (arg0.getPropertyName().equals("transform")) {
            this.getParentFrame().setChangedSinceLastSave(true);
        }
    }

    /**
     * @return Returns the theSerializer.
     */
    public NetworkSerializer getTheSerializer() {
        return theSerializer;
    }

    /**
     * @return Returns the mouseEventHandler.
     */
    public MouseEventHandler getMouseEventHandler() {
        return this.mouseEventHandler;
    }

    /**
     * @return Returns the backropDirectory.
     */
    public String getBackropDirectory() {
        return backropDirectory;
    }

    /**
     * @param backropDirectory
     *            The backropDirectory to set.
     */
    public void setBackropDirectory(String backropDirectory) {
        this.backropDirectory = backropDirectory;
    }

    /**
     * @return Returns the numberOfPastes.
     */
    public double getNumberOfPastes() {
        return numberOfPastes;
    }

    /**
     * @param numberOfPastes
     *            The numberOfPastes to set.
     */
    public void setNumberOfPastes(double numberOfPastes) {
        this.numberOfPastes = numberOfPastes;
    }

    /**
     * Persistent getter and setter for line color; to be used with Castor
     */
    public int getLineColorC() {
        return lineColor.getRGB();
    }

    public void setLineColorC(int color) {
        lineColor = new Color(color);
    }

    public void setBackgroundColorC(final int color) {
        backgroundColor = new Color(color);
        this.setBackground(backgroundColor);
        repaint();
    }

    public int getBackgroundColorC() {
        return backgroundColor.getRGB();
    }

    public int getExcitatoryColorC() {
        return excitatoryColor.getRGB();
    }

    public void setExcitatoryColor(final int excitatoryColor) {
        this.excitatoryColor = new Color(excitatoryColor);
    }

    public int getInhibitoryColorC() {
        return inhibitoryColor.getRGB();
    }

    public void setInhibitoryColorC(final int inhibitoryColor) {
        this.inhibitoryColor = new Color(inhibitoryColor);
    }

    public int getLassoColorC() {
        return lassoColor.getRGB();
    }

    public void setLassoColorC(final int lassoColor) {
        this.lassoColor = new Color(lassoColor);
    }

    public int getSelectionColorC() {
        return selectionColor.getRGB();
    }

    public void setSelectionColorC(final int selectionColor) {
        this.selectionColor = new Color(selectionColor);
    }

    // ////////////////////////////////////
    // Network color getters and setters//
    // ////////////////////////////////////

    /**
     * Set the background color, store it to user preferences, and repaint the
     * panel.
     * 
     * @param clr
     *            new background color for network panel
     */
    public void setBackgroundColor(final Color clr) {
        backgroundColor = clr;
        this.setBackground(backgroundColor);
        repaint();
    }

    /**
     * Get the background color.
     * 
     * @return the background color
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(final Color lineColor) {
        this.lineColor = lineColor;
    }

    public float getHotColor() {
        return hotColor;
    }

    public void setHotColor(final float hotColor) {
        this.hotColor = hotColor;
    }

    public float getCoolColor() {
        return coolColor;
    }

    public void setCoolColor(final float coolColor) {
        this.coolColor = coolColor;
    }

    public Color getExcitatoryColor() {
        return excitatoryColor;
    }

    public void setExcitatoryColor(final Color excitatoryColor) {
        this.excitatoryColor = excitatoryColor;
    }

    public Color getInhibitoryColor() {
        return inhibitoryColor;
    }

    public void setInhibitoryColor(final Color inhibitoryColor) {
        this.inhibitoryColor = inhibitoryColor;
    }

    public Color getLassoColor() {
        return lassoColor;
    }

    public void setLassoColor(final Color lassoColor) {
        this.lassoColor = lassoColor;
    }

    public Color getSelectionColor() {
        return selectionColor;
    }

    public void setSelectionColor(final Color selectionColor) {
        this.selectionColor = selectionColor;
    }
}
