
package org.simbrain.network.nodes;

import java.awt.Color;

import java.awt.geom.Rectangle2D;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

import org.simbrain.network.NetworkPreferences;

import edu.umd.cs.piccolo.PNode;

import edu.umd.cs.piccolox.handles.PHandle;

import edu.umd.cs.piccolox.util.PNodeLocator;

/**
 * Selection handle.
 *
 * <p>Usage:
 * <pre>
 * PNode node = ...;
 * SelectionHandle.addSelectionHandleTo(node)
 * </pre>
 * and
 * <pre>
 * PNode node = ...;
 * SelectionHandle.removeSelectionHandleFrom(node)
 * </pre>
 * </p>
 *
 * @see #addSelectionHandleTo(PNode)
 * @see #removeSelectionHandleFrom(PNode)
 */
public final class SelectionHandle
    extends PHandle {

    /** Extend factor. */
    private static final double EXTEND_FACTOR = 0.075d;

    /** Color of selection boxes. */
    private static Color selectionColor = new Color(NetworkPreferences.getSelectionColor());


    /**
     * Create a new selection handle.
     *
     * @param locator locator
     */
    private SelectionHandle(final PNodeLocator locator) {

        super(locator);

        reset();
        setPickable(false);

        PNode parentNode = locator.getNode();
        parentNode.addChild(this);

        setPaint(null);
        setStrokePaint(selectionColor);

        double x = 0.0d - (parentNode.getWidth() * EXTEND_FACTOR);
        double y = 0.0d - (parentNode.getHeight() * EXTEND_FACTOR);
        double width = parentNode.getWidth() + 2 * (parentNode.getWidth() * EXTEND_FACTOR);
        double height = parentNode.getHeight() + 2 * (parentNode.getHeight() * EXTEND_FACTOR);

        Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);

        append(rect, false);
    }


    /**
     * Return true if the specified node has a selection handle
     * as a child.
     *
     * @param node node
     * @return true if the specified node has a selection handle
     *    as a child
     */
    private static boolean hasSelectionHandle(final PNode node) {

        for (Iterator i = node.getChildrenIterator(); i.hasNext(); ) {
            PNode n = (PNode) i.next();

            if (n instanceof SelectionHandle) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a selection handle to the specified node, if one does not
     * exist already.
     *
     * @param node node to add the selection handle to, must not be null
     */
    public static void addSelectionHandleTo(final PNode node) {

        if (node == null) {
            throw new IllegalArgumentException("node must not be null");
        }

        if (hasSelectionHandle(node)) {
            return;
        }

        PNodeLocator nodeLocator = new PNodeLocator(node);
        SelectionHandle selectionHandle = new SelectionHandle(nodeLocator);
    }

    /**
     * Remove the selection handle(s) from the specified node, if any exist.
     *
     * @param node node to remove the selection handle(s) from, must not be null
     */
    public static void removeSelectionHandleFrom(final PNode node) {

        if (node == null) {
            throw new IllegalArgumentException("node must not be null");
        }

        Collection handlesToRemove = new ArrayList();

        for (Iterator i = node.getChildrenIterator(); i.hasNext(); ) {
            PNode n = (PNode) i.next();

            if (n instanceof SelectionHandle) {
                handlesToRemove.add(n);
            }
        }
        node.removeChildren(handlesToRemove);
    }


    /**
     * @return Returns the selectionColor.
     */
    public static Color getSelectionColor() {
        return selectionColor;
    }


    /**
     * @param selectionColor The selectionColor to set.
     */
    public static void setSelectionColor(final Color selectionColor) {
        SelectionHandle.selectionColor = selectionColor;
    }
}