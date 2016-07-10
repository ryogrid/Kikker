package jp.ryo.informationPump.server.debug.profile;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Arrays;

public class CompactGridLayout extends GridLayout {

    public CompactGridLayout() {
        super();
    }

    public CompactGridLayout(int rows, int cols) {
        super(rows, cols);
    }

    public CompactGridLayout(int rows, int cols, int hgap, int vgap) {
        super(rows, cols, hgap, vgap);
    }

    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int ncomponents = parent.getComponentCount();
            int nrows = getRows();
            int ncols = getColumns();

            if (nrows > 0) {
                ncols = (ncomponents + nrows - 1) / nrows;
            } else {
                nrows = (ncomponents + ncols - 1) / ncols;
            }
            
            int widths[] = new int[ncols];
            Arrays.fill(widths, 0);
            
            int height = 0;
            
            for (int row = 0; row < nrows ; row++) {
                int h = 0;
                for (int column = 0; column < ncols ; column++) {
                    int i = row * ncols + column;
                    if (i >= parent.getComponentCount()) {
                        continue;
                    }

                    Component comp = parent.getComponent(i);
                    Dimension d = comp.getPreferredSize();
                    
                    if (h < d.height) {
                        h = d.height;
                    }
                    
                    if (widths[column] < d.width) {
                        widths[column] = d.width;
                    }
                }
                
                height += h;
            }
            
            int width = 0;
            for (int column = 0 ; column < ncols ; column++) {
                width += widths[column];
            }
            
            return new Dimension(insets.left + insets.right + width + (ncols-1)*getVgap(), 
                                 insets.top + insets.bottom + height + (nrows-1)*getHgap());
        }
    }
    
    public Dimension minimumLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int ncomponents = parent.getComponentCount();
            int nrows = getRows();
            int ncols = getColumns();
            
            if (nrows > 0) {
                ncols = (ncomponents + nrows - 1) / nrows;
            } else {
                nrows = (ncomponents + ncols - 1) / ncols;
            }
            
            int widths[] = new int[ncols];
            Arrays.fill(widths, 0);
            
            int height = 0;
            
            for (int row = 0; row < nrows ; row++) {
                int h = 0;
                for (int column = 0; column < ncols ; column++) {
                    int i = row * ncols + column;
                    if (i >= parent.getComponentCount()) {
                        continue;
                    }

                    Component comp = parent.getComponent(i);
                    Dimension d = comp.getMinimumSize();
                    
                    if (h < d.height) {
                        h = d.height;
                    }
                    
                    if (widths[column] < d.width) {
                        widths[column] = d.width;
                    }
                }
                
                height += h;
            }
            
            int width = 0;
            for (int column = 0 ; column < ncols ; column++) {
                width += widths[column];
            }
            
            return new Dimension(insets.left + insets.right + width + (ncols-1)*getVgap(), 
                                 insets.top + insets.bottom + height + (nrows-1)*getHgap());
        }
    }
    
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            Dimension parentSize = parent.getSize();
            int ncomponents = parent.getComponentCount();
            int nrows = getRows();
            int ncols = getColumns();
            boolean ltr = parent.getComponentOrientation().isLeftToRight();
            
            if (nrows > 0) {
                ncols = (ncomponents + nrows - 1) / nrows;
            } else {
                nrows = (ncomponents + ncols - 1) / ncols;
            }
            
            int widths[] = new int[ncols];
            Arrays.fill(widths, 0);
            
            int heights[] = new int[nrows];
            Arrays.fill(heights, 0);
            
            for (int row = 0; row < nrows ; row++) {
                for (int column = 0; column < ncols ; column++) {
                    int i = row * ncols + column;
                    if (i >= parent.getComponentCount()) {
                        continue;
                    }
                    
                    Component comp = parent.getComponent(i);
                    Dimension d = comp.getPreferredSize();
                    
                    if (heights[row] < d.height) {
                        heights[row] = d.height;
                    }
                    
                    if (widths[column] < d.width) {
                        widths[column] = d.width;
                    }
                }
            }
            
            if (ltr) {
                int y = insets.top;
                for (int row = 0; row < nrows ; row++) {
                    int x = insets.left;
                    for (int column = 0; column < ncols ; column++) {
                        int i = row * ncols + column;
                        if (i >= parent.getComponentCount()) {
                            continue;
                        }

                        Component comp = parent.getComponent(i);
                        Dimension compSize = comp.getPreferredSize();

                        if (column == ncols - 1 
                            && x + widths[column] < parentSize.width - insets.right) {

                            if (compSize.height < heights[row]) {
                                comp.setBounds(x, y,
                                               parentSize.width - x  - insets.right, compSize.height);
                            } else {
                                comp.setBounds(x, y,
                                               parentSize.width - x  - insets.right, heights[row]);
                            }
                        } else {
                            if (compSize.height < heights[row]) {
                                comp.setBounds(x, y, widths[column], compSize.height);
                            } else {
                                comp.setBounds(x, y, widths[column], heights[row]);
                            }
                            x += (widths[column] + getVgap());
                        }
                    }
                    
                    y += (heights[row] + getHgap());
                }
            } else {
                int y = insets.top;
                for (int row = 0; row < nrows ; row++) {
                    int x = parentSize.width - insets.right;
                    for (int column = 0; column < ncols ; column++) {
                        int i = row * ncols + column;
                        if (i >= parent.getComponentCount()) {
                            continue;
                        }

                        Component comp = parent.getComponent(i);
                        Dimension compSize = comp.getPreferredSize();
                        
                        if (column == ncols - 1 && x - widths[column] >insets.left) {
                            if (compSize.height < heights[row]) {
                                comp.setBounds(insets.left, y, widths[column], compSize.height);
                            } else {
                                comp.setBounds(insets.left, y, widths[column], heights[row]);
                            }
                        } else {
                            if (compSize.height < heights[row]) {
                                comp.setBounds(x - widths[column], y, widths[column], compSize.height);
                            } else {
                                comp.setBounds(x - widths[column], y, widths[column], heights[row]);
                            }
                            x -= (widths[column] + getVgap());
                        }
                    }
                    
                    y += (heights[row] + getHgap());
                }
            }
        }
    }
}
