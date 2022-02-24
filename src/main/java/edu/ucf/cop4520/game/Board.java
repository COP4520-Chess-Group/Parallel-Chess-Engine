package edu.ucf.cop4520.game;

import edu.ucf.cop4520.game.pieces.*;

public class Board {
    public static final String startingPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private Piece[][] board = new Piece[8][8];
    private String castlingRights;
    private Piece.Color toMove;
    private String enPassant;
    private int halfMoveClock;
    private int fullMoveClock;

    public Board(String fen) {
        int characterBeginIndex = 0;
        for(int i = 7; i >= 0; i--) {
            String row;
            if(fen.indexOf('/') == -1)
                row = fen.substring(0, fen.indexOf(' '));
            else
                row = fen.substring(0, fen.indexOf('/'));
            int j = 0;
            int charBuffer = 0;
            while(j < 8) {
                char ch = row.charAt(charBuffer);
                if(Character.isDigit(ch))
                    j += Integer.parseInt(ch + "");
                else {
                    Piece.Color color = Character.isUpperCase(ch) ? Piece.Color.LIGHT : Piece.Color.DARK;
                    switch(Character.toLowerCase(ch)) {
                        case 'r':
                            board[i][j] = new Rook(color);
                            j++;
                            break;
                        case 'n':
                            board[i][j] = new Knight(color);
                            j++;
                            break;
                        case 'b':
                            board[i][j] = new Bishop(color);
                            j++;
                            break;
                        case 'q':
                            board[i][j] = new Queen(color);
                            j++;
                            break;
                        case 'k':
                            board[i][j] = new King(color);
                            j++;
                            break;
                        case 'p':
                            board[i][j] = new Pawn(color);
                            j++;
                            break;
                    }
                }
                charBuffer++;
            }
            characterBeginIndex = fen.indexOf('/') + 1;
            fen = fen.substring(characterBeginIndex);
        }
        fen = fen.substring(fen.indexOf(' ') + 1);
        toMove = fen.charAt(0) == 'w' ? Piece.Color.LIGHT : Piece.Color.DARK;
        characterBeginIndex = fen.indexOf(' ') + 1;
        fen = fen.substring(characterBeginIndex);
        castlingRights = fen.substring(0, fen.indexOf(' '));
        characterBeginIndex = fen.indexOf(' ') + 1;
        fen = fen.substring(characterBeginIndex);
        enPassant = fen.substring(0, fen.indexOf(' '));
        characterBeginIndex = fen.indexOf(' ') + 1;
        fen = fen.substring(characterBeginIndex);
        halfMoveClock = Integer.parseInt(fen.substring(0, fen.indexOf(' ')));
        fullMoveClock = Integer.parseInt(fen.substring(fen.indexOf(' ') + 1));
    }

    public Piece[][] getBoard() {
        return board;
    }

    public Board copyOf() {
        return new Board(this.toString());
    }

    public Board move(Move move) {

        return this;
    }

    /**
     * @return FEN for the current board
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int rank = 7; rank >= 0; rank--) {
            int blanks = 0;
            for(int file = 0; file < 8; file++) {
                if(board[rank][file] == null)
                    blanks++;
                else {
                    if(blanks != 0) {
                        sb.append(blanks);
                        blanks = 0;
                    }
                    String piece;
                    if(board[rank][file] instanceof Pawn)
                        piece = "P";
                    else
                        piece = board[rank][file].toString();

                    piece = board[rank][file].getColor() == Piece.Color.DARK ?
                            piece.toLowerCase() : piece;
                    sb.append(piece);
                }
            }
            if(blanks != 0)
                sb.append(blanks);
            if(rank != 0) {
                sb.append("/");
            }
        }
        sb.append(String.format(" %c %s %s %d %d",
                toMove == Piece.Color.LIGHT ? 'w' : 'b',
                this.castlingRights,
                this.enPassant,
                this.halfMoveClock,
                this.fullMoveClock));
        return sb.toString();
    }

}
