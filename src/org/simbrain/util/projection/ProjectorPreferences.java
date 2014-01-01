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
package org.simbrain.util.projection;

import java.awt.Color;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * <b>ProjectionPreferences</b> handles storage and retrieval of user
 * preferences for projections.
 */
public final class ProjectorPreferences {

    /** The default tolerance. */
    private static final double DEFAULT_TOLERANCE = 5;

    /** The default perturbation. */
    private static final double DEFAULT_PERTURBATION = .1;

    /** The default iterations between updates. */
    private static final int DEFAULT_ITERATIONS_BETWEEN_UPDATES = 10;

    /** The default epsilon. */
    private static final int DEFAULT_EPSILON = 3;

    /** User preferences. */
    private static final Preferences PROJECTION_PREFERENCES = Preferences
            .userRoot().node("/org/simbrain/util/projection");

    /** File system separator. */
    public static final String FS = System.getProperty("file.separator");

    /**
     * Save all user preferences.
     */
    public static void saveAll() {
        try {
            PROJECTION_PREFERENCES.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restores general preferences to default values.
     */
    public static void restoreGeneralDefaults() {
        setPerturbationAmount(getDefaultPerturbationAmount());
        setTolerance(getDefaultTolerance());
        setAddMethod(getDefaultAddMethod());
        setDefaultProjectionMethod(getDefaultDefaultProjector());
        setCurrentDirectory(getDefaultCurrentDirectory());
    }

    /**
     * Restores graphics preferences to default values.
     */
    public static void restoreGraphicsDefaults() {
        setBackgroundColor(getDefaultBackgroundColor());
        setHotColor(getDefaultHotColor());
        setDefaultColor(getDefaultDefaultColor());
        setColorDataPoints(getDefaultColorDataPoints());
        setShowError(getDefaultShowError());
        setShowStatusBar(getDefaultShowStatusBar());
        setPointSize(getDefaultPointSize());
        setIterationsBetweenUpdates(getDefaultIterationsBetweenUpdates());
    }

    /**
     * Restores coordinate preferences to default values.
     *
     */
    public static void restoreCoordinateDefaults() {
        setHiDim1(getDefaultHiDim1());
        setHiDim2(getDefaultHiDim2());
        setAutoFind(getDefaultAutoFind());
    }

    /**
     * Restores Sammon preferences to default values.
     */
    public static void restoreSammonDefaults() {
        setEpsilon(getDefaultEpsilon());
    }

    // ////////////////////////////////////////////////////////////////
    // Getters and setters for user preferences //
    // Note that default values for preferences are stored in the //
    // second argument of the getter method //
    // ////////////////////////////////////////////////////////////////
    /**
     * @param rgbColor Sets Background color.
     */
    public static void setBackgroundColor(final int rgbColor) {
        PROJECTION_PREFERENCES.putInt("BackgroundColor", rgbColor);
    }

    /**
     * @return Current background color.
     */
    public static int getBackgroundColor() {
        return PROJECTION_PREFERENCES.getInt("BackgroundColor",
                getDefaultBackgroundColor());
    }

    /**
     * @return Default background color.
     */
    public static int getDefaultBackgroundColor() {
        return Color.WHITE.getRGB();
    }

    /**
     * @param rgbColor Sets hot color.
     */
    public static void setHotColor(final int rgbColor) {
        PROJECTION_PREFERENCES.putInt("HotColor", rgbColor);
    }

    /**
     * @return Current hot color.
     */
    public static int getHotColor() {
        return PROJECTION_PREFERENCES.getInt("HotColor", getDefaultHotColor());
    }

    /**
     * @return Default hot color.
     */
    public static int getDefaultHotColor() {
        return Color.RED.getRGB();
    }

    /**
     * @param rgbColor Sets default color.
     */
    public static void setDefaultColor(final int rgbColor) {
        PROJECTION_PREFERENCES.putInt("DefaultColor", rgbColor);
    }

    /**
     * @return Default color.
     */
    public static int getDefaultColor() {
        return PROJECTION_PREFERENCES.getInt("DefaultColor",
                getDefaultDefaultColor());
    }

    /**
     * @return Default default color.
     */
    public static int getDefaultDefaultColor() {
        return Color.BLUE.getRGB();
    }

    /**
     * @param tolerance Sets tolerance.
     */
    public static void setTolerance(final double tolerance) {
        PROJECTION_PREFERENCES.putDouble("Tolerance", tolerance);
    }

    /**
     * @return Tolerance.
     */
    public static double getTolerance() {
        return PROJECTION_PREFERENCES.getDouble("Tolerance",
                getDefaultTolerance());
    }

    /**
     * @return Default tolerance
     */
    public static double getDefaultTolerance() {
        return DEFAULT_TOLERANCE;
    }

    /**
     * @param amount Sets pertubation amount.
     */
    public static void setPerturbationAmount(final double amount) {
        PROJECTION_PREFERENCES.putDouble("PerturbationAmount", amount);
    }

    /**
     * @return Current perturbation amount.
     */
    public static double getPerturbationAmount() {
        return PROJECTION_PREFERENCES.getDouble("PerturbationAmount",
                getDefaultPerturbationAmount());
    }

    /**
     * @return Default perturbation amount.
     */
    public static double getDefaultPerturbationAmount() {
        return DEFAULT_PERTURBATION;
    }

    /**
     * @param error Sets show error.
     */
    public static void setShowError(final boolean error) {
        PROJECTION_PREFERENCES.putBoolean("ShowError", error);
    }

    /**
     * @return Current show error value.
     */
    public static boolean getShowError() {
        return PROJECTION_PREFERENCES.getBoolean("ShowError",
                getDefaultShowError());
    }

    /**
     * @return Default show error value.
     */
    public static boolean getDefaultShowError() {
        return false;
    }

    /**
     * @param statusBar Sets show status bar.
     */
    public static void setShowStatusBar(final boolean statusBar) {
        PROJECTION_PREFERENCES.putBoolean("ShowStatusBar", statusBar);
    }

    /**
     * @return Current set status bar value.
     */
    public static boolean getShowStatusBar() {
        return PROJECTION_PREFERENCES.getBoolean("ShowStatusBar",
                getDefaultShowStatusBar());
    }

    /**
     * @return Default show status bar value.
     */
    public static boolean getDefaultShowStatusBar() {
        return true;
    }

    /**
     * @param dataPoints Sets color data points.
     */
    public static void setColorDataPoints(final boolean dataPoints) {
        PROJECTION_PREFERENCES.putBoolean("ColorDataPoints", dataPoints);
    }

    /**
     * @return Current color data points value.
     */
    public static boolean getColorDataPoints() {
        return PROJECTION_PREFERENCES.getBoolean("ColorDataPoints",
                getDefaultColorDataPoints());
    }

    /**
     * @return Default color data points value.
     */
    public static boolean getDefaultColorDataPoints() {
        return false;
    }

    /**
     * @param size Sets the size of data points.
     */
    public static void setPointSize(final double size) {
        PROJECTION_PREFERENCES.putDouble("PointSize", size);
    }

    /**
     * @return Current point size.
     */
    public static double getPointSize() {
        return PROJECTION_PREFERENCES.getDouble("PointSize",
                getDefaultPointSize());
    }

    /**
     * @return Default point size.
     */
    public static double getDefaultPointSize() {
        return 1;
    }

    /**
     * @param iterations Sets number of iterations between updates.
     */
    public static void setIterationsBetweenUpdates(final int iterations) {
        PROJECTION_PREFERENCES.putInt("IterationsBetweenUpdates", iterations);
    }

    /**
     * @return Current number of iterations between updates.
     */
    public static int getIterationsBetweenUpdates() {
        return PROJECTION_PREFERENCES.getInt("IterationsBetweenUptates",
                getDefaultIterationsBetweenUpdates());
    }

    /**
     * @return Default number of iterations between updates.
     */
    public static int getDefaultIterationsBetweenUpdates() {
        return DEFAULT_ITERATIONS_BETWEEN_UPDATES;
    }

    /**
     * @param epsilon Sets epsilon value.
     */
    public static void setEpsilon(final double epsilon) {
        PROJECTION_PREFERENCES.putDouble("Epsilon", epsilon);
    }

    /**
     * @return Current epsilon value.
     */
    public static double getEpsilon() {
        return PROJECTION_PREFERENCES.getDouble("Epsilon", getDefaultEpsilon());
    }

    /**
     * @return Default epsilon value.
     */
    public static double getDefaultEpsilon() {
        return DEFAULT_EPSILON;
    }

    /**
     * @param dim Sets high dimension 1 value.
     */
    public static void setHiDim1(final int dim) {
        PROJECTION_PREFERENCES.putInt("HiDim1", dim);
    }

    /**
     * @return Current high dimension 1 value.
     */
    public static int getHiDim1() {
        return PROJECTION_PREFERENCES.getInt("HiDim1", getDefaultHiDim1());
    }

    /**
     * @return Default high dimension 1 value.
     */
    public static int getDefaultHiDim1() {
        return 0;
    }

    /**
     * @param dim Sets high dimension 2 value.
     */
    public static void setHiDim2(final int dim) {
        PROJECTION_PREFERENCES.putInt("HiDim2", dim);
    }

    /**
     * @return Current high dimension 2 value.
     */
    public static int getHiDim2() {
        return PROJECTION_PREFERENCES.getInt("HiDim2", getDefaultHiDim2());
    }

    /**
     * @return Default high dimension 2 value.
     */
    public static int getDefaultHiDim2() {
        return 1;
    }

    /**
     * @param autoFind Sets auto find value.
     */
    public static void setAutoFind(final boolean autoFind) {
        PROJECTION_PREFERENCES.putBoolean("AutoFind", autoFind);
    }

    /**
     * @return Current auto find value.
     */
    public static boolean getAutoFind() {
        return PROJECTION_PREFERENCES.getBoolean("AutoFind",
                getDefaultAutoFind());
    }

    /**
     * @return Default auto find value.
     */
    public static boolean getDefaultAutoFind() {
        return true;
    }

    /**
     * @param addMethod Sets current add method value.
     */
    public static void setAddMethod(final String addMethod) {
        PROJECTION_PREFERENCES.put("AddMethod", addMethod);
    }

    /**
     * @return Current add method value.
     */
    public static String getAddMethod() {
        return PROJECTION_PREFERENCES.get("AddMethod", getDefaultAddMethod());
    }

    /**
     * @return Default add method value.
     */
    public static String getDefaultAddMethod() {
        return "Refresh";
    }

    /**
     * @param defaultProjector Sets default projector value.
     */
    public static void setDefaultProjectionMethod(final String defaultProjector) {
        PROJECTION_PREFERENCES.put("DefaultProjector", defaultProjector);
    }

    /**
     * @return Current default projector value.
     */
    public static String getDefaultProjectionMethod() {
        return PROJECTION_PREFERENCES.get("DefaultProjector",
                getDefaultDefaultProjector());
    }

    /**
     * @return Default default projector value.
     */
    public static String getDefaultDefaultProjector() {
        return "PCA";
    }

    /**
     * @param dir Sets current directory value.
     */
    public static void setCurrentDirectory(final String dir) {
        PROJECTION_PREFERENCES.put("CurrentDirectory", dir);
    }

    /**
     * @return Current directory value.
     */
    public static String getCurrentDirectory() {
        return PROJECTION_PREFERENCES.get("CurrentDirectory",
                getDefaultCurrentDirectory());
    }

    /**
     * @return Default current directory.
     *
     */
    public static String getDefaultCurrentDirectory() {
        return "." + FS + "simulations";
    }
}