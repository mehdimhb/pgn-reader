import java.util.HashMap;

public enum Square {
    A1, A2, A3, A4, A5, A6, A7, A8,
    B1, B2, B3, B4, B5, B6, B7, B8,
    C1, C2, C3, C4, C5, C6, C7, C8,
    D1, D2, D3, D4, D5, D6, D7, D8,
    E1, E2, E3, E4, E5, E6, E7, E8,
    F1, F2, F3, F4, F5, F6, F7, F8,
    G1, G2, G3, G4, G5, G6, G7, G8,
    H1, H2, H3, H4, H5, H6, H7, H8;


    public static HashMap<Square, int[]> position = new HashMap<>();
    static {
        Square[] square = Square.values();
        for (Square s : square) {
            String[] arr = s.toString().split("");
            switch (arr[0]) {
                case "A" -> position.put(s, new int[]{Integer.parseInt(arr[1])-1, 0});
                case "B" -> position.put(s, new int[]{Integer.parseInt(arr[1])-1, 1});
                case "C" -> position.put(s, new int[]{Integer.parseInt(arr[1])-1, 2});
                case "D" -> position.put(s, new int[]{Integer.parseInt(arr[1])-1, 3});
                case "E" -> position.put(s, new int[]{Integer.parseInt(arr[1])-1, 4});
                case "F" -> position.put(s, new int[]{Integer.parseInt(arr[1])-1, 5});
                case "G" -> position.put(s, new int[]{Integer.parseInt(arr[1])-1, 6});
                case "H" -> position.put(s, new int[]{Integer.parseInt(arr[1])-1, 7});
            }
        }
    }

    public static HashMap<Square, Piece> piece = new HashMap<>();
    static {
        Square[] square = Square.values();
        for (Square s : square) {
            String[] arr = s.toString().split("");
            switch (arr[1]) {
                case "1":
                    switch (arr[0]) {
                        case "A", "H" -> piece.put(s, Piece.WHITE_ROOK);
                        case "B", "G" -> piece.put(s, Piece.WHITE_KNIGHT);
                        case "C", "F" -> piece.put(s, Piece.WHITE_BISHOP);
                        case "D" -> piece.put(s, Piece.WHITE_QUEEN);
                        case "E" -> piece.put(s, Piece.WHITE_KING);
                    }
                    break;
                case "8":
                    switch (arr[0]) {
                        case "A", "H" -> piece.put(s, Piece.BLACK_ROOK);
                        case "B", "G" -> piece.put(s, Piece.BLACK_KNIGHT);
                        case "C", "F" -> piece.put(s, Piece.BLACK_BISHOP);
                        case "D" -> piece.put(s, Piece.BLACK_QUEEN);
                        case "E" -> piece.put(s, Piece.BLACK_KING);
                    }
                    break;
                case "2":
                    piece.put(s, Piece.WHITE_PAWN);
                    break;
                case "7":
                    piece.put(s, Piece.BLACK_PAWN);
                    break;
                default:
                    piece.put(s, Piece.NONE);
                    break;
            }
        }
    }
}
