import java.util.HashMap;

public enum Piece {
    //white pieces
    WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP,
    WHITE_QUEEN, WHITE_KING, WHITE_PAWN,

    //black pieces
    BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP,
    BLACK_QUEEN, BLACK_KING, BLACK_PAWN,

    //none
    NONE;

    public static HashMap<Piece, Character> notation = new HashMap<>();
    static {
        //white pieces
        notation.put(Piece.WHITE_ROOK, 'R');
        notation.put(Piece.WHITE_KNIGHT, 'N');
        notation.put(Piece.WHITE_BISHOP, 'B');
        notation.put(Piece.WHITE_QUEEN, 'Q');
        notation.put(Piece.WHITE_KING, 'K');
        notation.put(Piece.WHITE_PAWN, 'P');

        //black pieces
        notation.put(Piece.BLACK_ROOK, 'r');
        notation.put(Piece.BLACK_KNIGHT, 'n');
        notation.put(Piece.BLACK_BISHOP, 'b');
        notation.put(Piece.BLACK_QUEEN, 'q');
        notation.put(Piece.BLACK_KING, 'k');
        notation.put(Piece.BLACK_PAWN, 'p');

        //none
        notation.put(Piece.NONE, ' ');
    }
}
