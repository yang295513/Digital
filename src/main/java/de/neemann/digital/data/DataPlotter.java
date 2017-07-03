package de.neemann.digital.data;

import de.neemann.digital.draw.graphics.Graphic;
import de.neemann.digital.draw.graphics.Orientation;
import de.neemann.digital.draw.graphics.Style;
import de.neemann.digital.draw.graphics.Vector;
import de.neemann.digital.draw.shapes.Drawable;

/**
 * The dataSet stores the collected DataSamples.
 * Every DataSample contains the values of al signals at a given time.
 *
 * @author hneemann
 */
public class DataPlotter implements Drawable {
    private final ValueTable data;
    private final int maxTextLength;
    private double size = SIZE;

    /**
     * Creates a simple dummy DataSet used for creating the DataShape
     */
    public DataPlotter() {
        this(new ValueTable("A", "B", "C")
                .add(new Value[]{new Value(0), new Value(0), new Value(0)})
                .add(new Value[]{new Value(0), new Value(1), new Value(0)})
                .add(new Value[]{new Value(0), new Value(1), new Value(0)})
        );
    }

    /**
     * Creates a new instance
     *
     * @param data the signals used to collect DataSamples
     */
    public DataPlotter(ValueTable data) {
        this.data = data;
        int tl = 0;
        for (int i = 0; i < data.getColumns(); i++) {
            String text = data.getColumnName(i);
            int w = text.length();
            if (w > tl) tl = w;
        }
        maxTextLength = tl;
    }

    private static final int BORDER = 10;
    private static final int SIZE = 25;
    private static final int SEP2 = 5;
    private static final int SEP = SEP2 * 2;

    /**
     * Fits the data in the visible area
     *
     * @param width width of the frame
     */
    public void fitInside(int width) {
        size = ((double) (width - getTextBorder())) / data.getRows();
    }

    /**
     * Apply a scaling factor
     *
     * @param f the factor
     */
    public void scale(double f) {
        size *= f;
        if (size < Style.NORMAL.getThickness()) size = Style.NORMAL.getThickness();
        if (size > SIZE * 4) size = SIZE * 4;
    }

    @Override
    synchronized public void drawTo(Graphic g, Style highLight) {
        int x = getTextBorder();

        int yOffs = SIZE / 2;
        int y = BORDER;
        int signals = data.getColumns();
        for (int i = 0; i < signals; i++) {
            String text = data.getColumnName(i);
            g.drawText(new Vector(x - 2, y + yOffs), new Vector(x + 1, y + yOffs), text, Orientation.RIGHTCENTER, Style.NORMAL);
            g.drawLine(new Vector(x, y - SEP2), new Vector(x + (int) (size * data.getRows()), y - SEP2), Style.DASH);
            y += SIZE + SEP;
        }
        g.drawLine(new Vector(x, y - SEP2), new Vector(x + (int) (size * data.getRows()), y - SEP2), Style.DASH);


        int[] lastRy = new int[signals];
        boolean first = true;
        double pos = 0;
        for (Value[] s : data) {
            int xx = (int) (pos + x);
            g.drawLine(new Vector(xx, BORDER - SEP2), new Vector(xx, (SIZE + SEP) * signals + BORDER - SEP2), Style.DASH);
            y = BORDER;
            for (int i = 0; i < signals; i++) {

                long width = data.getMax(i);
                if (width == 0) width = 1;
                int ry = (int) (SIZE - (SIZE * s[i].getValue()) / width);
                g.drawLine(new Vector(xx, y + ry), new Vector((int) (xx + size), y + ry), Style.NORMAL);
                if (!first && ry != lastRy[i])
                    g.drawLine(new Vector(xx, y + lastRy[i]), new Vector(xx, y + ry), Style.NORMAL);

                lastRy[i] = ry;
                y += SIZE + SEP;
            }
            first = false;
            pos += size;
        }
        g.drawLine(new Vector(x, BORDER - SEP2), new Vector(x, (SIZE + SEP) * signals + BORDER - SEP2), Style.DASH);
    }

    private int getTextBorder() {
        return maxTextLength * Style.NORMAL.getFontSize() / 2 + BORDER + SEP;
    }

    /**
     * @return the preferred width of the graphical representation
     */
    public int getGraphicWidth() {
        return getTextBorder() + data.getRows() * SIZE;
    }

    /**
     * @return the preferred height of the graphical representation
     */
    public int getGraphicHeight() {
        return data.getColumns() * (SIZE + SEP) + 2 * BORDER;
    }

    /**
     * @return the current width of the graphical representation
     */
    public int getCurrentGraphicWidth() {
        return getTextBorder() + (int) (data.getRows() * size);
    }

}