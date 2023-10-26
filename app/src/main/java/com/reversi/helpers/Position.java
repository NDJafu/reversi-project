package com.reversi.helpers;

public class Position {
    public int row;
    public int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public int hashCode() {
        return 8 * row + col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position) {
            Position other = (Position) obj;
            return this.row == other.row && this.col == other.col;
        }

        return false;
    }

}
