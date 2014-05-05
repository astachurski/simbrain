package org.simbrain.plot.histogram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.statistics.HistogramBin;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.statistics.SimpleHistogramDataset;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

/**
 * A modification of the JFreeChart class HistogramDataset that allows data to
 * be overridden. The main change is the addition of the method overwrriteSeries
 * I would have extended HistogramDataset but needed access to the internal
 * list.
 *
 * @see HistogramDataset
 * @see SimpleHistogramDataset
 *
 * @author Jeff Yoshimi
 */
public class OverwritableHistogramDataset extends AbstractIntervalXYDataset
implements IntervalXYDataset, Cloneable, PublicCloneable, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -6341668077370231153L;

    private List<SeriesStruct> dataMap = new ArrayList<SeriesStruct>();

    /** The histogram type. */
    private HistogramType type;

    /**
     * Creates a new (empty) dataset with a default type of
     * {@link HistogramType}.FREQUENCY.
     */
    public OverwritableHistogramDataset() {
        this.type = HistogramType.FREQUENCY;
    }

    /**
     * Returns the histogram type.
     *
     * @return The type (never <code>null</code>).
     */
    public HistogramType getType() {
        return this.type;
    }

    /**
     * Sets the histogram type and sends a {@link DatasetChangeEvent} to all
     * registered listeners.
     *
     * @param type the type (<code>null</code> not permitted).
     */
    public void setType(HistogramType type) {
        if (type == null) {
            throw new IllegalArgumentException("Null 'type' argument");
        }
        this.type = type;
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    /**
     * Add new values to an existing series. Overwrites the old data.
     * The value set will be sorted after this method completes.
     *
     * @param key the series key (<code>null</code> not permitted).
     * @param values the raw observations.
     * @param bins the number of bins (must be at least 1).
     * @param minimum the lower bound of the bin range.
     * @param maximum the upper bound of the bin range.
     */
    public void overwriteSeries(int index, Comparable key, Number[] values,
            int bins) {

        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        if (values == null) {
            throw new IllegalArgumentException("Null 'values' argument.");
        } else if (bins < 1) {
            throw new IllegalArgumentException(
                    "The 'bins' value must be at least 1.");
        }

        // If the series does not already exist create the series.
        if (index >= dataMap.size()) {
            addSeries(key, values, bins);
        } else {
            addSeriesAtIndex(index, key, values, bins);
            this.fireDatasetChanged();
        }

    }

    /**
     * Adds a series to the dataset. Any data value less than minimum will be
     * assigned to the first bin, and any data value greater than maximum will
     * be assigned to the last bin. Values falling on the boundary of adjacent
     * bins will be assigned to the higher indexed bin.
     * 
     * The values array passed to this method will be sorted upon completion. 
     *
     * @param key the series key (<code>null</code> not permitted).
     * @param values the raw observations.
     * @param bins the number of bins (must be at least 1).
     * @param minimum the lower bound of the bin range.
     * @param maximum the upper bound of the bin range.
     */
    public void addSeries(Comparable key, Number[] values, int bins) {
        addSeriesAtIndex(dataMap.size(), key, values, bins);
    }

    private void addSeriesAtIndex(int series, Comparable key, Number[] values,
            int bins) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        if (values == null) {
            throw new IllegalArgumentException("Null 'values' argument.");
        } else if (bins < 1) {
            throw new IllegalArgumentException(
                    "The 'bins' value must be at least 1.");
        }

        HistogramBin [] histBins = new HistogramBin[0];
        double binWidth = 0;
        if (values.length != 0) {
            Arrays.sort(values);

            binWidth = (values[values.length - 1].doubleValue()
                    - values[0].doubleValue()) / bins;

            histBins = new HistogramBin[bins];

            int index = 0;
            HistogramBin bin;
            double endVal = 0;
            double startVal = values[0].doubleValue();
            for (int i = 0; i < bins; i++) {
                if (index < values.length) {
                    endVal = startVal + binWidth;
                    bin = new HistogramBin(startVal, endVal);
                    while(index < values.length
                            && values[index].doubleValue() <= endVal) {
                        bin.incrementCount();
                        index++;
                    }
                    startVal = endVal;
                } else {
                    bin = new HistogramBin(0, 0); //Empty bin
                }
                histBins[i] = bin;
            }
        } 

        SeriesStruct packet = new SeriesStruct(key, histBins,
                values.length, binWidth);

        if (dataMap.size() - 1 < series) {
            dataMap.add(packet);
        } else {
            dataMap.set(series, packet);
        }
    }

    /**
     * Returns the bins for a series.
     *
     * @param series the series index (in the range <code>0</code> to
     *            <code>getSeriesCount() - 1</code>).
     *
     * @return A list of bins.
     *
     * @throws IndexOutOfBoundsException if <code>series</code> is outside the
     *             specified range.
     */
    List<HistogramBin> getBins(int series) {
        return Arrays.asList(dataMap.get(series).bins);
    }

    /**
     * Returns the total number of observations for a series.
     *
     * @param series the series index.
     *
     * @return The total.
     */
    private int getTotal(int series) {
        return dataMap.get(series).length;
    }

    /**
     * Returns the bin width for a series.
     *
     * @param series the series index (zero based).
     *
     * @return The bin width.
     */
    private double getBinWidth(int series) {
        return dataMap.get(series).binWidth;
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return The series count.
     */
    public int getSeriesCount() {
        return this.dataMap.size();
    }

    /**
     * Returns the key for a series.
     *
     * @param series the series index (in the range <code>0</code> to
     *            <code>getSeriesCount() - 1</code>).
     *
     * @return The series key.
     *
     * @throws IndexOutOfBoundsException if <code>series</code> is outside the
     *             specified range.
     */
    public Comparable getSeriesKey(int series) {
        return dataMap.get(series).key;
    }

    /**
     * Returns the number of data items for a series.
     *
     * @param series the series index (in the range <code>0</code> to
     *            <code>getSeriesCount() - 1</code>).
     *
     * @return The item count.
     *
     * @throws IndexOutOfBoundsException if <code>series</code> is outside the
     *             specified range.
     */
    public int getItemCount(int series) {
        return getBins(series).size();
    }

    /**
     * Returns the X value for a bin. This value won't be used for plotting
     * histograms, since the renderer will ignore it. But other renderers can
     * use it (for example, you could use the dataset to create a line chart).
     *
     * @param series the series index (in the range <code>0</code> to
     *            <code>getSeriesCount() - 1</code>).
     * @param item the item index (zero based).
     *
     * @return The start value.
     *
     * @throws IndexOutOfBoundsException if <code>series</code> is outside the
     *             specified range.
     */
    public Number getX(int series, int item) {
        List bins = getBins(series);
        HistogramBin bin = (HistogramBin) bins.get(item);
        double x = (bin.getStartBoundary() + bin.getEndBoundary()) / 2.0;
        return new Double(x);
    }

    /**
     * Returns the y-value for a bin (calculated to take into account the
     * histogram type).
     *
     * @param series the series index (in the range <code>0</code> to
     *            <code>getSeriesCount() - 1</code>).
     * @param item the item index (zero based).
     *
     * @return The y-value.
     *
     * @throws IndexOutOfBoundsException if <code>series</code> is outside the
     *             specified range.
     */
    public Number getY(int series, int item) {
        List bins = getBins(series);
        HistogramBin bin = (HistogramBin) bins.get(item);
        double total = getTotal(series);
        double binWidth = getBinWidth(series);

        if (this.type == HistogramType.FREQUENCY) {
            return new Double(bin.getCount());
        } else if (this.type == HistogramType.RELATIVE_FREQUENCY) {
            return new Double(bin.getCount() / total);
        } else if (this.type == HistogramType.SCALE_AREA_TO_1) {
            return new Double(bin.getCount() / (binWidth * total));
        } else { // pretty sure this shouldn't ever happen
            throw new IllegalStateException();
        }
    }

    /**
     * Returns the start value for a bin.
     *
     * @param series the series index (in the range <code>0</code> to
     *            <code>getSeriesCount() - 1</code>).
     * @param item the item index (zero based).
     *
     * @return The start value.
     *
     * @throws IndexOutOfBoundsException if <code>series</code> is outside the
     *             specified range.
     */
    public Number getStartX(int series, int item) {
        List bins = getBins(series);
        HistogramBin bin = (HistogramBin) bins.get(item);
        return new Double(bin.getStartBoundary());
    }

    /**
     * Returns the end value for a bin.
     *
     * @param series the series index (in the range <code>0</code> to
     *            <code>getSeriesCount() - 1</code>).
     * @param item the item index (zero based).
     *
     * @return The end value.
     *
     * @throws IndexOutOfBoundsException if <code>series</code> is outside the
     *             specified range.
     */
    public Number getEndX(int series, int item) {
        List bins = getBins(series);
        HistogramBin bin = (HistogramBin) bins.get(item);
        return new Double(bin.getEndBoundary());
    }

    /**
     * Returns the start y-value for a bin (which is the same as the y-value,
     * this method exists only to support the general form of the
     * {@link IntervalXYDataset} interface).
     *
     * @param series the series index (in the range <code>0</code> to
     *            <code>getSeriesCount() - 1</code>).
     * @param item the item index (zero based).
     *
     * @return The y-value.
     *
     * @throws IndexOutOfBoundsException if <code>series</code> is outside the
     *             specified range.
     */
    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    /**
     * Returns the end y-value for a bin (which is the same as the y-value, this
     * method exists only to support the general form of the
     * {@link IntervalXYDataset} interface).
     *
     * @param series the series index (in the range <code>0</code> to
     *            <code>getSeriesCount() - 1</code>).
     * @param item the item index (zero based).
     *
     * @return The Y value.
     *
     * @throws IndexOutOfBoundsException if <code>series</code> is outside the
     *             specified range.
     */
    public Number getEndY(int series, int item) {
        return getY(series, item);
    }

    /**
     * Tests this dataset for equality with an arbitrary object.
     *
     * @param obj the object to test against (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj.getClass().equals(this.getClass()))) {
            return false;
        }
        OverwritableHistogramDataset that = (OverwritableHistogramDataset) obj;
        if (!ObjectUtilities.equal(this.type, that.type)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.dataMap, that.dataMap)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        final int p1 = 13;
        final int p2 = 89;
        return p1 * this.type.hashCode() + p2 * this.dataMap.hashCode();
    }

    /**
     * Returns a clone of the dataset.
     *
     * @return A clone of the dataset.
     *
     * @throws CloneNotSupportedException if the object cannot be cloned.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Mimics the "struct" construct in C/C++. In this case used to store and
     * represent values about a data series for a histogram. 
     * 
     * @author zach
     *
     */
    private class SeriesStruct {

        public final Comparable key;

        public final HistogramBin [] bins;

        public final int length;

        public final double binWidth;

        public SeriesStruct(final Comparable key, final HistogramBin [] bins,
                final int length, final double binWidth) {
            this.key = key;
            this.bins = bins;
            this.length = length;
            this.binWidth = binWidth;
        }
    }

}