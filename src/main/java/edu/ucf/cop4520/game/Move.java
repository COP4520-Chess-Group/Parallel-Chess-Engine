package edu.ucf.cop4520.game;

import edu.ucf.cop4520.game.pieces.Pawn;
import edu.ucf.cop4520.game.pieces.Piece;

public class Move {
    /**
     * This is the change in the piece's position
     * vertically along a column
     */
    private final int deltaFile;

    /**
     * This is the change in a piece's position
     * horizontally along a row
     */
    private final int deltaRank;

    private final Piece piece;
    private final boolean isCheck;
    private final boolean isCheckmate;
    private final boolean isCapture;
    private final CastleType castle;
    private final Piece promotion;

    private Move(Builder builder) {
        this.deltaFile = builder.deltaFile;
        this.deltaRank = builder.deltaRank;
        this.piece = builder.piece;
        this.isCheck = builder.isCheck;
        this.isCheckmate = builder.isCheckmate;
        this.isCapture = builder.isCapture;
        this.castle = builder.castle;
        this.promotion = builder.promotion;
    }

    public int getDeltaFile() {
        return this.deltaFile;
    }

    public int getDeltaRank() {
        return this.deltaRank;
    }

    public Piece getPiece() {
        return piece;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public boolean isCapture() {
        return isCapture;
    }

    public CastleType isCastle() {
        return castle;
    }

    @Override
    public String toString() {
        String string = this.piece.toString() + ('a' + this.piece.getFile() + this.deltaFile) + (this.piece.getRank() + this.deltaRank + 1);
        switch(this.castle) {
            case KINGSIDE:
                string = "O-O";
                break;
            case QUEENSIDE:
                string = "O-O-O";
                break;
        }
        if(this.isCapture) {
            if(!(this.piece instanceof Pawn)) {
                string = this.piece.toString() + "x" + ('a' + this.piece.getFile() + this.deltaFile) + (this.piece.getRank() + this.deltaRank + 1);
            } else {
                string = ('a' + this.piece.getFile()) + "x" + ('a' + this.piece.getFile() + this.deltaFile) + (this.piece.getRank() + this.deltaRank + 1);
            }
        }
        if(this.promotion != null)
            string += "=" + this.promotion.toString();
        if(this.isCheckmate) {
            return string + "#";
        } else if(this.isCheck){
            return string + "+";
        }
        return string;
    }

    public static class Builder {
        private int deltaFile;
        private int deltaRank;
        private Piece piece;
        private boolean isCheck = false;
        private boolean isCheckmate = false;
        private boolean isCapture = false;
        private CastleType castle = CastleType.NONE;
        private Piece promotion = null;

        public Builder(Piece piece, int deltaFile, int deltaRank) {
            this.piece = piece;
            this.deltaFile = deltaFile;
            this.deltaRank = deltaRank;
        }

        public Builder isCheck(boolean check) {
            this.isCheck = check;
            return this;
        }

        public Builder isCheckmate(boolean checkmate) {
            this.isCheckmate = checkmate;
            return this;
        }

        public Builder isCapture(boolean capture) {
            this.isCapture = capture;
            return this;
        }

        public Builder castle(CastleType castle) {
            this.castle = castle;
            return this;
        }

        public Move build() {
            return new Move(this);
        }
    }

    public enum CastleType {
        NONE, QUEENSIDE, KINGSIDE;
    }
}
