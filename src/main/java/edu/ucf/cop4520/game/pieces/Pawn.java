package edu.ucf.cop4520.game.pieces;

import edu.ucf.cop4520.game.Move;

public class Pawn extends Piece {
    private boolean firstMove = true;

    public Pawn(Color color) {
        super(color);
    }

    public void setFirstMove(boolean moved) {
        this.firstMove = moved;
    }

    public boolean hasMoved() {
        return this.firstMove;
    }
}
