package org.fhcrc.honeycomb.metapop.coordinate;

/**
 * Cartesian coordinates.
 *
 * Created on 10 Apr, 2013
 * @author Adam Waite
 * @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 *
 */
public class Coordinate {
    final int row;
    final int col;

    public Coordinate(final int row, final int col) {
        this.row = row;
        this.col = col;
    }

    public Coordinate(Coordinate coord) { 
        this(coord.getRow(), coord.getCol());
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    @Override
    public String toString() { 
        return new String("row: "+row+" col: "+col);
    }

    @Override
    public boolean equals(Object obj) { 
        if (this == obj) return true;
        if (!(obj instanceof Coordinate)) return false;

        Coordinate c = (Coordinate) obj;
        return c.getRow() == row && c.getCol() == col;
    }

    @Override
    public int hashCode() {
        return (Integer.toString(row) + Integer.toString(col)).hashCode();
    }
}
