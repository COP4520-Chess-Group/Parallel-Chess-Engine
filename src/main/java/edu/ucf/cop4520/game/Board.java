package edu.ucf.cop4520.game;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

import edu.ucf.cop4520.game.pieces.*;
import edu.ucf.cop4520.game.Move;

public class Board {
    public static final String startingPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private Piece[][] board = new Piece[8][8];
    private String castlingRights;
    private Piece.Color toMove;
    private String enPassant;
    private int halfMoveClock;
    private int fullMoveClock;
    private int kRank;
    private int kFile;

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

    public Piece.Color getToMove() {
        return toMove;
    }

    public Board move(Move move) {
        Piece piece = move.getPiece();
        board[piece.getFile()][piece.getRank()] = null;
        piece.move(move);
        board[piece.getFile()][piece.getRank()] = piece;
        return this;
    }

    public double evaluate() {
        double evaluation = 0;
        String fen = this.toString();
        String board = fen.substring(0, fen.indexOf(' '));
        double[] numberOfPieces = new double[6];
        // Get's the value of all the pieces on the board
        for(char c : board.toCharArray()) {
            switch (c) {
                case 'K':
                    numberOfPieces[0] += 200;
                    break;
                case 'Q':
                    numberOfPieces[1] += 9;
                    break;
                case 'R':
                    numberOfPieces[2] += 5;
                    break;
                case 'B':
                    numberOfPieces[3] += 3.5;
                    break;
                case 'N':
                    numberOfPieces[4] += 2.5;
                    break;
                case 'P':
                    numberOfPieces[5] += 1;
                    break;
                case 'k':
                    numberOfPieces[0] -= 200;
                    break;
                case 'q':
                    numberOfPieces[1] -= 9;
                    break;
                case 'r':
                    numberOfPieces[2] -= 5;
                    break;
                case 'b':
                    numberOfPieces[3] -= 3.5;
                    break;
                case 'n':
                    numberOfPieces[4] -= 2.5;
                    break;
                case 'p':
                    numberOfPieces[5] -= 1;
                    break;
                default:
                    continue;
            }
        }
        // Sum all the pieces
        for (double val : numberOfPieces) {
            evaluation += val;
        }
        // Give more value to being able to move
        evaluation += generateMoves().size() * .1 * (toMove == Piece.Color.LIGHT ? 1 : -1);
        evaluation += pawnEvaluation();
        return evaluation;
    }

    public double pawnEvaluation() {
        // Stores all the doubled pawns in each file
        int[] doubledArrayWhite = {-1, -1, -1, -1, -1, -1, -1, -1};
        int[] doubledArrayBlack = {-1, -1, -1, -1, -1, -1, -1, -1};

        // Finds all the doubled pawns
        for(int file = 0; file < 8; file++) {
            for(int rank = 0; rank < 8; rank++) {
                if(board[rank][file] instanceof Pawn) {
                    switch(board[rank][file].getColor()) {
                        case LIGHT:
                            doubledArrayWhite[file] += 1;
                            break;
                        case DARK:
                            doubledArrayBlack[file] += 1;
                            break;
                    }
                }
            }
            if(doubledArrayWhite[file] == -1) doubledArrayWhite[file] = 0;
            if(doubledArrayBlack[file] == -1) doubledArrayBlack[file] = 0;
        }

        //Sums all the doubled pawns
        int doubledPawnsWhite = Arrays.stream(doubledArrayWhite).sum();
        int doubledPawnsBlack = Arrays.stream(doubledArrayBlack).sum();

        // All the isolated pawns
        int isolatedPawnsWhite = 0;
        int isolatedPawnsBlack = 0;
        // Whether the file has a white or black pawn
        char[] fileHasPawn = {0x00, 0x00};
        char[] files = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80};
        for(int file = 0; file < 8; file++) {
            for(int rank = 0; rank < 8; rank++) {
                if(board[rank][file] instanceof Pawn) {
                    if(board[rank][file].getColor().equals(Piece.Color.LIGHT)) {
                        fileHasPawn[0] |= files[file];
                    } else if(board[rank][file].getColor().equals(Piece.Color.DARK)){
                        fileHasPawn[1] |= files[file];
                    }
                }
            }
        }

        char[] masks = {0x03, 0x07, 0x0E, 0x1C, 0x38, 0x70, 0xE0, 0xC0};
        for(int i = 0; i < 8; i++) {
            char check = (char) (masks[i] & fileHasPawn[0]);
            if(check == files[i]) isolatedPawnsWhite++;

            check = (char) (masks[i] & fileHasPawn[1]);
            if(check == files[i]) isolatedPawnsBlack++;
        }

        return -.5 * (doubledPawnsWhite-doubledPawnsBlack + isolatedPawnsWhite - isolatedPawnsBlack);
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

    // Returns true if board if player whose move it is is currently in check
    public boolean checked()
    {
        // Check for pawn attacks
        if (toMove == Piece.Color.LIGHT)
        {
            // Pawns attack from 'above' i.e. from higher ranks to lower ones
            if (kRank < 7 && kFile > 0 && board[kRank + 1][kFile - 1] instanceof Pawn &&
                board[kRank + 1][kFile - 1].getColor() != toMove)
            {
                return true;
            }
            else if (kRank < 7 && kFile < 7 && board[kRank + 1][kFile + 1] instanceof Pawn &&
                board[kRank + 1][kFile + 1].getColor() != toMove)
            {
                return true;
            }
        }
        else
        {
            // Pawns attack from 'below' i.e. from lower ranks to higher ones
            if (kRank > 0 && kFile > 0 && board[kRank - 1][kFile - 1] instanceof Pawn &&
                board[kRank + 1][kFile - 1].getColor() != toMove)
            {
                return true;
            }
            else if (kRank > 0 && kFile < 7 && board[kRank - 1][kFile + 1] instanceof Pawn &&
                board[kRank + 1][kFile + 1].getColor() != toMove)
            {
                return true;
            }
        }
        // Check for rook attacks (and queen straight attacks)
        // Check if rook is attacking king from below
        for (int i = kRank - 1; i >= 0; i--)
        {
            if ((board[i][kFile] instanceof Rook || board[i][kFile] instanceof Queen) &&
                board[i][kFile].getColor() != toMove)
            {
                return true;
            }
            else if (board[i][kFile] != null)
            {
                i = -1; // path is blocked
            }
        }
        // Check if rook is attacking king from above
        for (int i = kRank + 1; i < 8; i++)
        {
            if ((board[i][kFile] instanceof Rook || board[i][kFile] instanceof Queen) &&
                board[i][kFile].getColor() != toMove)
            {
                return true;
            }
            else if (board[i][kFile] != null)
            {
                i = 8; // path is blocked
            }
        }
        // Check if rook is attacking king from left
        for (int j = kFile - 1; j >= 0; j--)
        {
            if ((board[kRank][j] instanceof Rook || board[kRank][j] instanceof Queen) &&
                board[kRank][j].getColor() != toMove)
            {
                return true;
            }
            else if (board[kRank][j] != null)
            {
                j = -1; // path is blocked
            }
        }
        // Check if rook is attacking king from right
        for (int j = kFile + 1; j < 8; j++)
        {
            if ((board[kRank][j] instanceof Rook || board[kRank][j] instanceof Queen) &&
                board[kRank][j].getColor() != toMove)
            {
                return true;
            }
            else if (board[kRank][j] != null)
            {
                j = 8; // path is blocked
            }
        }
        // Check for bishop attacks (and queen diagonal attacks)
        // Check for bishops up and to the left
        for (int i = kRank + 1, j = kFile - 1; i < 8 && j >= 0; i++, j--)
        {
            if ((board[i][j] instanceof Bishop || board[i][j] instanceof Queen) &&
                board[i][j].getColor() != toMove)
            {
                return true;
            }
            else if (board[i][j] != null)
            {
                j = -1;
            }
        }
        // Check for bishops up and to the right
        for (int i = kRank + 1, j = kFile + 1; i < 8 && j < 8; i++, j++)
        {
            if ((board[i][j] instanceof Bishop || board[i][j] instanceof Queen) &&
                board[i][j].getColor() != toMove)
            {
                return true;
            }
            else if (board[i][j] != null)
            {
                j = -1;
            }
        }
        // Check for bishops down and to the left
        for (int i = kRank - 1, j = kFile - 1; i >= 0 && j >= 0; i--, j--)
        {
            if ((board[i][j] instanceof Bishop || board[i][j] instanceof Queen) &&
                board[i][j].getColor() != toMove)
            {
                return true;
            }
            else if (board[i][j] != null)
            {
                j = -1;
            }
        }
        // Check for bishops down and to the right
        for (int i = kRank - 1, j = kFile + 1; i >= 0 && j < 8; i--, j++)
        {
            if ((board[i][j] instanceof Bishop || board[i][j] instanceof Queen) &&
                board[i][j].getColor() != toMove)
            {
                return true;
            }
            else if (board[i][j] != null)
            {
                j = -1;
            }
        }
        // Check for king attacks
        for (int i = -1; i <= 1; i++)
        {
            for (int j = -1; j <= 1; j++)
            {
                if ((kRank + i) >= 0 && (kRank + i) < 8 && (kFile + j) >= 0 && (kFile + j) < 8 &&
                    board[kRank + i][kFile + j] != null && board[kRank + i][kFile + j] instanceof King && board[kRank + i][kFile + j].getColor() != toMove)
                {
                    return true;
                }
            }
        }
        // Check for knight attacks
        for (int i = -2; i <= 2; i++)
        {
            for (int j = -2; j <= 2; j++)
            {
                if ((i == Math.abs(1) && j == Math.abs(2)) || (i == Math.abs(2) && j == Math.abs(1)))
                {
                    if ((kRank + i) < 8 && (kRank + i) >= 0 && (kFile + j) < 8 && (kFile + j) >= 0 &&
                        board[kRank + i][kFile + j] != null && board[kRank + i][kFile + j] instanceof Knight && board[kRank + i][kFile + j].getColor() != toMove)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Returns true if move would cause king to be checked (and is thus illegal), false otherwise
    // rank and file are current position of piece and r and f are the dist moving
    public boolean causesCheck(int rank, int file, int r, int f)
    {
        Piece movedPiece = board[rank][file];
        board[rank][file] = null;
        Piece movedTo = board[rank + r][file + f];
        board[rank + r][file + f] = movedPiece;
        boolean check = checked();
        board[rank][file] = movedPiece;
        board[rank + r][file + f] = movedTo;
        return check;
    }


    // Adds move to concurrent hashset if it doesn't result in king being put in check
    // Also makes sure you can capture piece e.g. can't if its yours or a king
    // rank and file are where piece currently is, r and f are how its moving
    public void addMove(Set<Move> moves, int rank, int file, int r, int f)
    {
        // Return if trying to move ontop of your own piece or try to capture a king
        if (board[rank + r][file + f] != null && board[rank + r][file + f].getColor() == toMove ||
            board[rank + r][file + f] instanceof King)
        {
            return;
        }
        Move.Builder move = new Move.Builder(board[rank][file], f, r);
        // Check if in check when move is done (return if checked)
        if (rank == kRank && file == kFile)
        {
            // king is the piece being moved (now check if checked)
            kRank += r;
            kFile += f;
            if (causesCheck(rank, file, r, f))
            {
                kRank -= r;
                kFile -= f;
                return;
            }
            kRank -= r;
            kFile -= f;
        }
        else
        {
            if (causesCheck(rank, file, r, f))
            {
                return;
            }
        }
        // See's if move checks other player to be checked
        if (toMove == Piece.Color.LIGHT)
        {
            toMove = Piece.Color.DARK;
        }
        else
        {
            toMove = Piece.Color.LIGHT;
        }
        move.isCheck(causesCheck(rank, file, r, f));
        if (toMove == Piece.Color.LIGHT)
        {
            toMove = Piece.Color.DARK;
        }
        else
        {
            toMove = Piece.Color.LIGHT;
        }
        // See's if move captures a piece
        if (board[rank + r][file + f] != null)
        {
            move.isCapture(true);
        }
        // need to fill out rest of builder (checkmate, castle, and promotion)
        moves.add(move.build());
        return;
    }

    // Adds any moves the king can make to the moves concurrent hashset
    public void generateKingMoves(Set<Move> moves, int rank, int file) {
        for (int r = -1; r <= 1; r++)
        {
            for (int f = -1; f <= 1; f++)
            {
                if ((rank + r) >= 0 && (rank + r) < 8 && (file + f) >= 0 && (file + f) < 8)
                {
                    // Add move
                    addMove(moves, rank, file, r, f);
                }
            }
        }
        // Need to add castling moves here
        return;
    }

    // Sets kRank and kFile to be the position of the king of the player's whose turn it is
    public void findKing()
    {
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if (board[i][j] instanceof King && board[i][j].getColor() == toMove)
                {
                    kRank = i;
                    kFile = j;
                    i = j = 8;
                }
            }
        }
        return;
    }

    // Returns a concurrent hashset containing all the moves available to the player whose turn it is
    public Set<Move> generateMoves() {
        ConcurrentHashMap<Move, Integer> map = new ConcurrentHashMap<>();
        Set<Move> moves = map.newKeySet();
        // Find king
        findKing();
        // Generate possible moves for each piece
        generateKingMoves(moves, kRank, kFile);
        // generate rook moves
        // generate bishop moves
        // generate knight moves
        // generate queen moves
        // generate pawn moves
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Board) {
            Board b = (Board) o;
            for(int i = 0; i < 8; i++) {
                if (!Arrays.equals(b.board[i], this.board[i])) return false;
            }
            return true;
        }
        return false;
    }

}
