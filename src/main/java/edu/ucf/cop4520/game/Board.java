package edu.ucf.cop4520.game;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.Stack;
import edu.ucf.cop4520.game.pieces.*;
import edu.ucf.cop4520.game.Move;

public class Board {
    public static final String startingPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private Piece[][] board = new Piece[8][8];
    private Piece[] kings = new King[2];
    private Piece[] rooks = new Rook[4];
    private Piece[] bishops = new Bishop[4];
    private Piece[] knights = new Knight[4];
    private Piece[] queens = new Queen[2];
    private Piece[] pawns = new Pawn[16];
    private String castlingRights;
    private Piece.Color toMove;
    private String enPassant;
    private int halfMoveClock;
    private int fullMoveClock;
    private int kRank;
    private int kFile;

    public Board(String fen) {
        int characterBeginIndex = 0;
        int colorIndex, lightRookIndex = 0, darkRookIndex = 0, lightBishopIndex = 0, darkBishopIndex = 0, lightKnightIndex = 0, darkKnightIndex = 0, lightPawnIndex = 0, darkPawnIndex = 0;
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
                    if (color == Piece.Color.LIGHT)
                    {
                        colorIndex = 0;
                    }
                    else
                    {
                        colorIndex = 1;
                    }
                    switch(Character.toLowerCase(ch)) {
                        case 'r':
                            board[i][j] = new Rook(color);
                            board[i][j].move((new Move.Builder(board[i][j], j, i)).build());
                            if (colorIndex == 0)
                            {
                                rooks[lightRookIndex] = board[i][j];
                                lightRookIndex++;
                            }
                            else
                            {
                                rooks[darkRookIndex] = board[i][j];
                                darkRookIndex++;
                            }
                            j++;
                            break;
                        case 'n':
                            board[i][j] = new Knight(color);
                            board[i][j].move((new Move.Builder(board[i][j], j, i)).build());
                            if (colorIndex == 0)
                            {
                                knights[lightKnightIndex] = board[i][j];
                                lightKnightIndex++;
                            }
                            else
                            {
                                knights[darkKnightIndex] = board[i][j];
                                darkKnightIndex++;
                            }
                            j++;
                            break;
                        case 'b':
                            board[i][j] = new Bishop(color);
                            board[i][j].move((new Move.Builder(board[i][j], j, i)).build());
                            if (colorIndex == 0)
                            {
                                bishops[lightBishopIndex] = board[i][j];
                                lightBishopIndex++;
                            }
                            else
                            {
                                bishops[darkBishopIndex] = board[i][j];
                                darkBishopIndex++;
                            }
                            j++;
                            break;
                        case 'q':
                            board[i][j] = new Queen(color);
                            board[i][j].move((new Move.Builder(board[i][j], j, i)).build());
                            if (colorIndex == 0)
                            {
                                queens[0] = board[i][j];
                            }
                            else
                            {
                                queens[1] = board[i][j];
                            }
                            j++;
                            break;
                        case 'k':
                            board[i][j] = new King(color);
                            board[i][j].move((new Move.Builder(board[i][j], j, i)).build());
                            kings[colorIndex] = board[i][j];
                            j++;
                            break;
                        case 'p':
                            board[i][j] = new Pawn(color);
                            board[i][j].move((new Move.Builder(board[i][j], j, i)).build());
                            if (colorIndex == 0)
                            {
                                pawns[lightPawnIndex] = board[i][j];
                                lightPawnIndex++;
                            }
                            else
                            {
                                pawns[darkPawnIndex] = board[i][j];
                                darkPawnIndex++;
                            }
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

    // Returns a stack with all possible places king could move to if at rank/file or
    // all possible places a king could attack rank/file from
    // Note: moves are represented as 2 integers so pop rank then pop file
    public Stack<Integer> kingMoves(int rank, int file) {
        Stack<Integer> moves = new Stack<Integer>();
        for (int r = -1; r <= 1; r++)
        {
            for (int f = -1; f <= 1; f++)
            {
                if ((rank + r) >= 0 && (rank + r) < 8 && (file + f) >= 0 && (file + f) < 8)
                {
                    // Add move
                    moves.push(f);
                    moves.push(r);
                }
            }
        }
        // Adds castling moves
        // Adds queenside castling (black lowercase, white uppercase)
        if ((castlingRights.indexOf('q') != -1 && toMove == Piece.Color.DARK) ||
            (castlingRights.indexOf('Q') != -1 && toMove == Piece.Color.LIGHT))
        {
            moves.push(-2);
            moves.push(0);
        }
        // Adds kingside castling
        if ((castlingRights.indexOf('k') != -1 && toMove == Piece.Color.DARK) ||
            (castlingRights.indexOf('K') != -1 && toMove == Piece.Color.LIGHT))
        {
            moves.push(2);
            moves.push(0);
        }
        return moves;
    }

    // Returns a stack with all possible places a rook could move to if at rank/file or
    // all possible places a rook could attack rank/file from
    // Note: moves are represented as 2 integers so pop rank then pop file
    public Stack<Integer> rookMoves(int rank, int file) {
        Stack<Integer> moves = new Stack<Integer>();
        straightMoves(moves, rank, file);
        return moves;
    }

    // Returns a stack with all possible places a bishop could move to if at rank/file or
    // all possible places a bishop could attack rank/file from
    // Note: moves are represented as 2 integers so pop rank then pop file
    public Stack<Integer> bishopMoves(int rank, int file) {
        Stack<Integer> moves = new Stack<Integer>();
        diagonalMoves(moves, rank, file);
        return moves;
    }

    // Returns a stack with all possible places a queen could move to if at rank/file or
    // all possible places a queen could attack rank/file from
    // Note: moves are represented as 2 integers so pop rank then pop file
    public Stack<Integer> queenMoves(int rank, int file) {
        Stack<Integer> moves = new Stack<Integer>();
        straightMoves(moves, rank, file);
        diagonalMoves(moves, rank, file);
        return moves;
    }

    // Returns a stack with all possible places a knight could move to if at rank/file or
    // all possible places a knight could attack rank/file from
    // Note: moves are represented as 2 integers so pop rank then pop file
    public Stack<Integer> knightMoves(int rank, int file) {
        Stack<Integer> moves = new Stack<Integer>();
        for (int i = -2; i <= 2; i++)
        {
            for (int j = -2; j <= 2; j++)
            {
                if ((Math.abs(i) == 1 && Math.abs(j) == 2) || (Math.abs(i) == 2 && Math.abs(j) == 1))
                {
                    if ((rank + i) < 8 && (rank + i) >= 0 && (file + j) < 8 && (file + j) >= 0)
                    {
                        moves.push(j);
                        moves.push(i);
                    }
                }
            }
        }
        return moves;
    }

    // Returns a stack with all possible places a pawn could move to if at rank/file or
    // all possible places a pawn could attack rank/file from
    // Note: moves are represented as 2 integers so pop rank then pop file
    public Stack<Integer> pawnMoves(int rank, int file) {
        Stack<Integer> moves = new Stack<Integer>();
        if (toMove == Piece.Color.LIGHT)
        {
            // Add one space forward (up) move
            if (rank < 7 && board[rank + 1][file] == null)
            {
                moves.push(0);
                moves.push(1);
            }
            // Add two spaces forward (up) move if haven't moved before i.e. at starting position still
            if (rank == 1 && board[rank + 1][file] == null && board[rank + 2][file] == null)
            {
                moves.push(0);
                moves.push(2);
            }
            // Adds attack on the left (up left)
            if ((rank + 1) < 8 && (file - 1) >= 0 && board[rank + 1][file - 1] != null && board[rank + 1][file - 1].getColor() != toMove)
            {
                moves.push(-1);
                moves.push(1);
            }
            // Adds attack on the right (up right)
            if ((rank + 1) < 8 && (file + 1) < 8 && board[rank + 1][file + 1] != null && board[rank + 1][file + 1].getColor() != toMove)
            {
                moves.push(1);
                moves.push(1);
            }
            // Adds en passant attack
            if (enPassant.charAt(0) != '-')
            {
                int r = Integer.parseInt(enPassant.substring(1)) - 1;
                int f = (int)(enPassant.charAt(0) - 'a');
                if ((rank + 1) == r)
                {
                    if ((file - 1) == f)
                    {
                        moves.push(-1);
                        moves.push(1);
                    }
                    else if ((file + 1) == f)
                    {
                        moves.push(1);
                        moves.push(1);
                    }
                }
            }
        }
        else
        {
            // Add one space forward (down) move
            if (rank >= 0 && board[rank - 1][file] == null)
            {
                moves.push(0);
                moves.push(-1);
            }
            // Add two spaces forward (down) move if haven't moved before i.e. at starting position still
            if (rank == 6 && board[rank - 1][file] == null && board[rank - 2][file] == null)
            {
                moves.push(0);
                moves.push(-2);
            }
            // Adds attack on the left (down left)
            if ((rank - 1) >= 0 && (file - 1) >= 0 && board[rank - 1][file - 1] != null && board[rank - 1][file - 1].getColor() != toMove)
            {
                moves.push(-1);
                moves.push(-1);
            }
            // Adds attack on the right (down right)
            if ((rank - 1) >= 0 && (file + 1) < 8 && board[rank - 1][file + 1] != null && board[rank - 1][file + 1].getColor() != toMove)
            {
                moves.push(1);
                moves.push(-1);
            }
            // Adds en passant attack
            if (enPassant.charAt(0) != '-')
            {
                int r = Integer.parseInt(enPassant.substring(1)) - 1;
                int f = (int)(enPassant.charAt(0) - 'a');
                if ((rank - 1) == r)
                {
                    if ((file - 1) == f)
                    {
                        moves.push(-1);
                        moves.push(-1);
                    }
                    else if ((file + 1) == f)
                    {
                        moves.push(1);
                        moves.push(-1);
                    }
                }
            }
        }
        return moves;
    }

    // Adds to given stack all possible straight attacks (up/down/right/left for rooks/queens)
    // Note: moves are represented as 2 integers so pop rank then pop file
    public void straightMoves(Stack<Integer> moves, int rank, int file) {
        // Add upward moves
        for (int r = 1; (rank + r) < 8; r++)
        {
            moves.push(0);
            moves.push(r);
            // Checks if path is blocked
            if (board[rank + r][file] != null)
            {
                r = 8;
            }
        }
        // Add downward moves
        for (int r = -1; (rank + r) >= 0; r--)
        {
            moves.push(0);
            moves.push(r);
            if (board[rank + r][file] != null)
            {
                r = -8;
            }
        }
        // Add rightward moves
        for (int f = 1; (file + f) < 8; f++)
        {
            moves.push(f);
            moves.push(0);
            if (board[rank][file + f] != null)
            {
                f = 8;
            }
        }
        // Add leftward moves
        for (int f = -1; (file + f) >= 0; f--)
        {
            moves.push(f);
            moves.push(0);
            if (board[rank][file + f] != null)
            {
                f = -8;
            }
        }
    }

    // Adds to give stack all possible diagonal attacks (up left/up right/down left/down right for bishops/queens)
    // Note: moves are represented as 2 integers so pop rank then pop file
    public void diagonalMoves(Stack<Integer> moves, int rank, int file)
    {
        // Add up right moves
        for (int r = 1; (rank + r) < 8 && (file + r) < 8; r++)
        {
            moves.push(r);
            moves.push(r);
            // Checks if path is blocked
            if (board[rank + r][file + r] != null)
            {
                r = 8;
            }
        }
        // Add up left moves
        for (int r = 1; (rank + r) < 8 && (file - r) >= 0; r++)
        {
            moves.push(0 - r);
            moves.push(r);
            // Checks if path is blocked
            if (board[rank + r][file - r] != null)
            {
                r = 8;
            }
        }
        // Add down right moves
        for (int r = 1; (rank - r) >= 0 && (file + r) < 8; r++)
        {
            moves.push(r);
            moves.push(0 - r);
            // Checks if path is blocked
            if (board[rank - r][file + r] != null)
            {
                r = 8;
            }
        }
        // Add down left moves
        for (int r = 1; (rank - r) >= 0 && (file - r) >= 0; r++)
        {
            moves.push(0 - r);
            moves.push(0 - r);
            // Checks if path is blocked
            if (board[rank - r][file - r] != null)
            {
                r = 8;
            }
        }
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
        Stack<Integer> kingAttacks = kingMoves(kRank, kFile);
        int r, f;
        while (!kingAttacks.empty())
        {
            r = kingAttacks.pop();
            f = kingAttacks.pop();
            if ((kRank + r) < 8 && (kRank + r) >= 0 && (kFile + f) < 8 && (kFile + f) >= 0 &&
                board[kRank + r][kFile + f] != null && board[kRank + r][kFile + f] instanceof King
                && board[kRank + r][kFile + f].getColor() != toMove && f != -2 && f != 2)
            {
                return true;
            }
        }
        // Check for knight attacks
        for (int i = -2; i <= 2; i++)
        {
            for (int j = -2; j <= 2; j++)
            {
                if ((Math.abs(i) == 1 && Math.abs(j) == 2) || (Math.abs(i) == 2 && Math.abs(j) == 1))
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
        //System.out.println("testing rank:"+rank+",file:"+file+",r:"+r+",f:"+f);
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
            // Checks if its queenside or kingside castling respectively
            // Then checks if its legal (space unoccupied and king never enters check)
            if (f == -2)
            {
                if (board[rank][file - 1] != null || board[rank][file - 2] != null || board[rank][file - 3] != null)
                {
                    return;
                }
                kFile -= 1;
                if (causesCheck(rank, file, r, -1))
                {
                    kFile += 1;
                    return;
                }
                kFile += 1;
            }
            else if (f == 2)
            {
                if (board[rank][file + 1] != null || board[rank][file + 2] != null)
                {
                    return;
                }
                kFile += 1;
                if (causesCheck(rank, file, r, 1))
                {
                    kFile -= 1;
                    return;
                }
                kFile -= 1;
            }
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

    // Adds moves represented by integers in newMoves stack to the moves set
    public void addMoves(Set<Move> moves, Stack<Integer> newMoves, int rank, int file)
    {
        int r, f;
        while (!newMoves.empty())
        {
            r = newMoves.pop();
            f = newMoves.pop();
            // Add move
            addMove(moves, rank, file, r, f);
        }
        return;
    }

    // Adds any moves the king can make to the moves concurrent hashset
    public void addKingMoves(Set<Move> moves, int rank, int file) {
        Stack<Integer> newMoves = kingMoves(kRank, kFile);
        addMoves(moves, newMoves, rank, file);
        return;
    }

    // Adds any moves the rook can make to the moves concurrent hashset
    public void addRookMoves(Set<Move> moves) {
        int rank, file, r, f;
        for (int i = 0; i < 4; i++)
        {
            if (rooks[i] != null && rooks[i].getRank() != -1 && rooks[i].getColor() == toMove)
            {
                rank = rooks[i].getRank();
                file = rooks[i].getFile();
                Stack<Integer> newMoves = rookMoves(rank, file);
                addMoves(moves, newMoves, rank, file);
            }
        }
        return;
    }

    // Adds any moves the bishop can make to the moves concurrent hashset
    public void addBishopMoves(Set<Move> moves) {
        int rank, file, r, f;
        for (int i = 0; i < 4; i++)
        {
            if (bishops[i] != null && bishops[i].getRank() != -1 && bishops[i].getColor() == toMove)
            {
                rank = bishops[i].getRank();
                file = bishops[i].getFile();
                Stack<Integer> newMoves = bishopMoves(rank, file);
                addMoves(moves, newMoves, rank, file);
            }
        }
        return;
    }

    // Adds any moves the bishop can make to the moves concurrent hashset
    public void addKnightMoves(Set<Move> moves) {
        int rank, file, r, f;
        for (int i = 0; i < 4; i++)
        {
            if (knights[i] != null && knights[i].getRank() != -1 && knights[i].getColor() == toMove)
            {
                rank = knights[i].getRank();
                file = knights[i].getFile();
                Stack<Integer> newMoves = knightMoves(rank, file);
                addMoves(moves, newMoves, rank, file);
            }
        }
        return;
    }

    // Adds any moves the queen can make to the moves concurrent hashset
    public void addQueenMoves(Set<Move> moves) {
        int rank, file, r, f;
        for (int i = 0; i < 2; i++)
        {
            if (queens[i] != null && queens[i].getRank() != -1 && queens[i].getColor() == toMove)
            {
                rank = queens[i].getRank();
                file = queens[i].getFile();
                Stack<Integer> newMoves = queenMoves(rank, file);
                addMoves(moves, newMoves, rank, file);
            }
        }
        return;
    }

    // Adds any moves the pawns can make to the moves concurrent hashset
    public void addPawnMoves(Set<Move> moves) {
        int rank, file, r, f;
        for (int i = 0; i < 16; i++)
        {
            if (pawns[i] != null && pawns[i].getRank() != -1 && pawns[i].getColor() == toMove)
            {
                rank = pawns[i].getRank();
                file = pawns[i].getFile();
                Stack<Integer> newMoves = pawnMoves(rank, file);
                addMoves(moves, newMoves, rank, file);
            }
        }
        return;
    }

    // Sets kRank and kFile to be the position of the king of the player's whose turn it is
    public void findKing()
    {
        if (toMove == Piece.Color.LIGHT)
        {
            kRank = kings[0].getRank();
            kFile = kings[0].getFile();
        }
        else
        {
            kRank = kings[1].getRank();
            kFile = kings[1].getFile();
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
        addKingMoves(moves, kRank, kFile);
        addRookMoves(moves);
        addBishopMoves(moves);
        addKnightMoves(moves);
        addQueenMoves(moves);
        addPawnMoves(moves);
        return moves;
    }

}
