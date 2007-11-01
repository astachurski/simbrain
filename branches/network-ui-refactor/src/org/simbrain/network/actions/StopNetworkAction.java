
package org.simbrain.network.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.simbrain.network.NetworkPanel;
import org.simbrain.resource.ResourceManager;
import org.simnet.NetworkThread;

/**
 * Stop network action.
 */
public final class StopNetworkAction
    extends AbstractAction {

    /** Network panel. */
    private final NetworkPanel networkPanel;


    /**
     * Create a new stop network action with the specified network panel.
     *
     * @param networkPanel network panel, must not be null
     */
    public StopNetworkAction(final NetworkPanel networkPanel) {

        if (networkPanel == null) {
            throw new IllegalArgumentException("networkPanel must not be null");
        }

        this.networkPanel = networkPanel;
        putValue(SMALL_ICON, ResourceManager.getImageIcon("Stop.gif"));
    }


    /** @see AbstractAction */
    public void actionPerformed(final ActionEvent event) {

        // TODO:
        // move to a method stopNetwork() or similar on NetworkPanel

        if (networkPanel.getNetwork().getNetworkThread() == null) {
            networkPanel.getNetwork().setNetworkThread(new NetworkThread(networkPanel.getNetwork()));
        }

        NetworkThread networkThread = networkPanel.getNetwork().getNetworkThread();

        networkThread.setRunning(false);
        networkPanel.getNetwork().setNetworkThread(null);
    }
}