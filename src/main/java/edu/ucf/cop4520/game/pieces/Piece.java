package edu.ucf.cop4520.game.pieces;

import edu.ucf.cop4520.game.Board;
import edu.ucf.cop4520.game.Move;

public abstract class Piece {
    protected final Color color;
    protected int file;
    protected int rank;

    public Piece(Color color) {
        this.color = color;
    }

    public void move(Move move) {
        this.file += move.getDeltaFile();
        this.rank += move.getDeltaRank();
    }

    public int getFile() {
        return this.file;
    }

    public int getRank() {
        return this.rank;
    }

    public Color getColor() {
        return this.color;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Piece) {
            Piece p = (Piece) o;
            return p.file == this.file && p.rank == this.rank && p.color == this.color;
        }
        return false;
    }

    public enum Color {
        LIGHT, DARK
    }
}
